package com.example.foliage.infrastructure.imageloader;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.foliage.DzApplication;

/**
 * Description：imageloader实现类
 * Created by liang.qfzc@gmail.com on 2018/7/11.
 */

public class DzImageLoader implements ImageLoader {

    private static DzImageLoader instance;

    public static DzImageLoader getInstance() {
        if (instance == null) {
            synchronized (DzImageLoader.class) {
                if (instance == null) {
                    instance = new DzImageLoader();
                }
            }
        }
        return instance;
    }

    @Override
    public void displayImage(ImageLoaderBuilder imageBuilder) {

    }

    @Override
    public void displayImage(Context activity, Object path, ImageView imageView) {
        displayImage(activity, path, imageView, 0, false, true, true);
    }

    @Override
    public void displayImage(Context activity, Object path, final ImageView imageView, int defaultDrawable) {
        displayImage(activity, path, imageView, defaultDrawable, false, true, true);
    }

    @Override
    public void displayImage(Context activity, Object path, final ImageView imageView, int defaultDrawable, boolean fitCenter, boolean memoryCache, boolean diskCache) {
        if (isActDestroyed(activity)) return;

        Glide.with(activity).load(path).into(imageView);
//        DrawableRequestBuilder builder = Glide.with(activity).load(path);
//        builder.dontAnimate();
//        if (defaultDrawable != 0) {
//            builder.placeholder(defaultDrawable).error(defaultDrawable);
//        } else {
//            builder.placeholder(R.drawable.shape_placeholder).error(R.drawable.shape_placeholder);
//        }
//
//        if (!memoryCache) {//不要缓存到memory
//            builder.skipMemoryCache(true);
//        }
//        if (!diskCache) {//不缓存到磁盘
//            builder.diskCacheStrategy(DiskCacheStrategy.NONE);
//        }
//
//        if (fitCenter) {
//            builder.fitCenter();
//        }
//
//        builder.into(new ImageViewTarget<GlideDrawable>(imageView) {
//            @Override
//            protected void setResource(GlideDrawable resource) {
//                imageView.setImageDrawable(resource);
//            }
//
//            @Override
//            public void setRequest(Request request) {
//                imageView.setTag(R.id.adapter_item_tag_key, request);
//            }
//
//            @Override
//            public Request getRequest() {
//                return (Request) imageView.getTag(R.id.adapter_item_tag_key);
//            }
//        });
    }

    @Override
    public void clearMemoryCache() {
        Glide.get(DzApplication.getInstance()).clearMemory();
    }

    public boolean isActDestroyed(Context context) {
        if (context instanceof Activity) {
            Activity act = (Activity) context;
            if (act.isFinishing() || (Build.VERSION.SDK_INT > 17 ? act.isDestroyed() : false)) {
                return true;
            }
        }
        return false;
    }
}
