package org.lym.image.select.bean;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 存储选中的ImageItem
 * <p>
 * author: ym.li
 * since: 2018/11/3
 */

public class SelectImageHelper {
    private Set<ImageItem> mImageSet;
    private Set<ImageItem> mFolderAllImage;
    public boolean folderAllImage;
    public int selectPosition;
    private onImageSelectUpdateListener mOnImageSelectUpdateListener;

    private SelectImageHelper() {

    }

    public void setOnImageSelectUpdateListener(onImageSelectUpdateListener updateListener) {
        this.mOnImageSelectUpdateListener = updateListener;
    }

    public void notifyImageItem(int position) {
        if (null != mOnImageSelectUpdateListener) {
            mOnImageSelectUpdateListener.notify(position);
        }
    }

    public void resetPosition() {
        this.selectPosition = 0;
    }

    public static SelectImageHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectImageHelper buildInstance() {
        SelectImageHelper bean = InstanceHolder.INSTANCE;
        bean.build();
        return bean;
    }

    private void build() {
        mImageSet = new LinkedHashSet<>();
        mFolderAllImage = new LinkedHashSet<>();
    }

    public ArrayList<ImageItem> getFolderAllImage() {
        return new ArrayList<>(mFolderAllImage);
    }

    public int getFolderAllImageCount() {
        return getFolderAllImage().size();
    }

    public void addImageItem(ImageItem imageItem) {
        if (mImageSet.contains(imageItem)) {
            mImageSet.remove(imageItem);
        } else {
            mImageSet.add(imageItem);
        }
    }

    public void addAllImageItem(List<ImageItem> imageItems) {
        mFolderAllImage.addAll(imageItems);
    }

    public void removeImageItem(ImageItem imageItem) {
        mImageSet.remove(imageItem);
    }

    public boolean contains(ImageItem imageItem) {
        return mImageSet.contains(imageItem);
    }

    public ArrayList<ImageItem> getSelectImageItem() {
        return new ArrayList<>(mImageSet);
    }

    public ArrayList<ImageItem> getAllImageItem() {
        return folderAllImage ? getFolderAllImage() : getSelectImageItem();
    }

    public int getMaxImageCount() {
        return folderAllImage ? getFolderAllImageCount() : getSelectCount();
    }

    public int getSelectCount() {
        return mImageSet.size();
    }

    public interface onImageSelectUpdateListener {
        void notify(int position);
    }

    private static final class InstanceHolder {
        private static final SelectImageHelper INSTANCE = new SelectImageHelper();
    }
}
