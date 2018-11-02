package org.lym.image.select.imageloader;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Doc  默认为Glide加载图片
 *
 * @author ym.li
 * @version 2.9.0
 * @since 2018/11/2/002
 */
public class GlideImageLoader implements UIImageLoader {
    @Override
    public void imageLoader(ImageView imageView, String path, int size) {
        Glide.with(imageView.getContext()).asBitmap().load(path).apply(new RequestOptions().override(size).centerCrop()).into(imageView);
    }
}
