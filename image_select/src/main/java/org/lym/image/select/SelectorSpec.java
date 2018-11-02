package org.lym.image.select;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import org.lym.image.select.imageloader.GlideImageLoader;
import org.lym.image.select.imageloader.UIImageLoader;
import org.lym.image.select.ui.SelectImageActivity;

/**
 * Doc  对选中的属性进行包装
 *
 * @author ym.li
 * @version 2.9.0
 * @since 2018/11/2/002
 */
public final class SelectorSpec {
    private int mMaxSelectImage;
    private int mSpanCount;
    private UIImageLoader mImageLoader;
    private PictureSelector mPictureSelector;

    private SelectorSpec() {

    }

    public static SelectorSpec getCleanInstance() {
        SelectorSpec selectorSpec = getInstance();
        selectorSpec.resetSpec();
        return selectorSpec;
    }

    public static SelectorSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void resetSpec() {
        this.mSpanCount = 3;
        this.mMaxSelectImage = 1;
        this.mImageLoader = new GlideImageLoader();
    }

    public SelectorSpec withPictureSelector(PictureSelector pictureSelector) {
        this.mPictureSelector = pictureSelector;
        return this;
    }

    public int getMaxSelectImage() {
        return mMaxSelectImage;
    }

    public SelectorSpec setMaxSelectImage(int maxSelectImage) {
        this.mMaxSelectImage = maxSelectImage;
        return this;
    }

    public int getSpanCount() {
        return mSpanCount;
    }

    public SelectorSpec setSpanCount(int spanCount) {
        this.mSpanCount = spanCount;
        return this;
    }

    public UIImageLoader getImageLoader() {
        return mImageLoader;
    }

    public SelectorSpec setImageLoader(UIImageLoader imageLoader) {
        mImageLoader = imageLoader;
        return this;
    }

    public void startForResult(int request) {
        Activity activity = mPictureSelector.getActivity();
        if (null == activity) {
            return;
        }
        Intent intent = new Intent(activity, SelectImageActivity.class);
        Fragment fragment = mPictureSelector.getFragment();
        if (null != fragment) {
            fragment.startActivityForResult(intent, request);
        } else {
            activity.startActivityForResult(intent, request);
        }
    }

    private static final class InstanceHolder {
        private static final SelectorSpec INSTANCE = new SelectorSpec();
    }

}
