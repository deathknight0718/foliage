package com.example.foliage.infrastructure.imageloader;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

/**
 * Description：imageloader接口
 * Created by liang.qfzc@gmail.com on 2018/7/11.
 */
public interface ImageLoader {
    void displayImage(ImageLoaderBuilder builder);

    void displayImage(Context activity, Object path, ImageView imageView);

    void displayImage(Context activity, Object path, ImageView imageView, @DrawableRes int defaultDrawable);

    void displayImage(Context activity, Object path, ImageView imageView, @DrawableRes int defaultDrawable, boolean fitCenter, boolean memoryCache, boolean diskCache);

    void clearMemoryCache();
}
