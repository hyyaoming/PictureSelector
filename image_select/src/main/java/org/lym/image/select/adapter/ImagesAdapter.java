package org.lym.image.select.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.lym.image.select.R;
import org.lym.image.select.SelectorSpec;
import org.lym.image.select.bean.ImageItem;
import org.lym.image.select.weight.SuperCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Doc  相册图片Adapter
 *
 * @author ym.li
 * @version 2.9.0
 * @since 2018/11/2/002
 */
public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ImageItem> mImageItems;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private int mImageResize;
    private OnItemClickListener mOnItemClickListener;
    private OnImageSelectListener mOnImageSelectListener;
    private SelectorSpec mSelectorSpec;

    public ImagesAdapter(List<ImageItem> imageItems, RecyclerView recyclerView) {
        this.mImageItems = imageItems == null ? new ArrayList<ImageItem>() : imageItems;
        this.mRecyclerView = recyclerView;
        this.mContext = recyclerView.getContext();
        mSelectorSpec = SelectorSpec.getInstance();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public void setOnImageSelectListener(OnImageSelectListener imageSelectListener) {
        this.mOnImageSelectListener = imageSelectListener;
    }

    public void setNewData(List<ImageItem> list) {
        this.mImageItems = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImagesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_images_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ImagesViewHolder imagesViewHolder = (ImagesViewHolder) viewHolder;
        if (null != mOnImageSelectListener) {
            imagesViewHolder.mSuperCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getSelectImageItem().size() >= mSelectorSpec.getMaxSelectImage() && imagesViewHolder.mSuperCheckBox.isChecked()) {
                        imagesViewHolder.mSuperCheckBox.setChecked(false);
                        String maxSelect = String.format(mContext.getString(R.string.max_select_image), String.valueOf(mSelectorSpec.getMaxSelectImage()));
                        Toast.makeText(mContext, maxSelect, Toast.LENGTH_LONG).show();
                    } else {
                        mOnImageSelectListener.onImageSelect(imagesViewHolder.mSuperCheckBox, i);
                    }
                }
            });
        }
        if (null != mOnItemClickListener) {
            imagesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(i);
                }
            });
        }
        ImageItem item = getItem(i);
        imagesViewHolder.mSuperCheckBox.setChecked(item.checked);
        imagesViewHolder.mSuperCheckBox.setVisibility(mSelectorSpec.getMaxSelectImage() == 1 ? View.GONE : View.VISIBLE);
        imagesViewHolder.mMaskView.setVisibility(item.checked ? View.VISIBLE : View.GONE);
        mSelectorSpec.getImageLoader().imageLoader(imagesViewHolder.mImage, item.path, getImageResize(mContext));
    }

    @Override
    public int getItemCount() {
        return mImageItems.size();
    }

    public List<ImageItem> getSelectImageItem() {
        List<ImageItem> mSelectImages = new ArrayList<>();
        for (ImageItem imageItem : mImageItems) {
            if (imageItem.checked) {
                mSelectImages.add(imageItem);
            }
        }
        return mSelectImages;
    }

    public ImageItem getItem(int position) {
        return mImageItems.get(position);
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            if (null != lm) {
                int spanCount = ((GridLayoutManager) lm).getSpanCount();
                int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
                int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(R.dimen.image_grid_spacing) * (spanCount - 1);
                mImageResize = availableWidth / spanCount;
            }
        }
        return mImageResize;
    }

    public int getSelectImageCount() {
        return getSelectImageItem().size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnImageSelectListener {
        void onImageSelect(SuperCheckBox checkBox, int position);
    }

    private static class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        SuperCheckBox mSuperCheckBox;
        View mMaskView;

        ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            mSuperCheckBox = itemView.findViewById(R.id.checkbox);
            mImage = itemView.findViewById(R.id.image);
            mMaskView = itemView.findViewById(R.id.mask);
        }
    }
}
