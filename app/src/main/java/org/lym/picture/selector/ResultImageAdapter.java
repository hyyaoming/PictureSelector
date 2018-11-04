package org.lym.picture.selector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * author: ym.li
 * since: 2018/11/4
 */
public class ResultImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<String> mPaths;
    private LayoutInflater mInflater;

    public ResultImageAdapter(Context context, List<String> paths) {
        this.mContext = context;
        this.mPaths = paths == null ? new ArrayList<String>() : paths;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setNewData(List<String> paths) {
        this.mPaths = paths;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_image_rv, viewGroup, false);
        return new ImageHolder(view);
    }

    private int getImageSize() {
        return mContext.getResources().getDisplayMetrics().widthPixels / 3;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ImageHolder holder = (ImageHolder) viewHolder;
        String path = mPaths.get(viewHolder.getAdapterPosition());
        Glide.with(mContext).asBitmap().load(path).apply(new RequestOptions().override(getImageSize(), getImageSize())).into(holder.mImage);

    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }

    private static class ImageHolder extends RecyclerView.ViewHolder {
        ImageView mImage;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.iv_image_item);
        }
    }

}
