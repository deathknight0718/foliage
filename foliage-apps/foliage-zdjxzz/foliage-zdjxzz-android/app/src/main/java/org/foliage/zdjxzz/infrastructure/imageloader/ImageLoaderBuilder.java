package org.foliage.zdjxzz.infrastructure.imageloader;

import android.content.Context;

/**
 * Description：ImageLoaderBuilder
 * Created by liang.qfzc@gmail.com on 2018/7/11.
 */
public class ImageLoaderBuilder {

    private Context context;
    private String path;
    private ImageWrapperView imageView;
    private int defaultDrawable;
    private int width;
    private int height;
    private boolean fitCenter;
    private boolean memoryCache;
    private boolean diskCache;

    public ImageLoaderBuilder context(Context context) {
        this.context = context;
        return this;
    }

    public ImageLoaderBuilder path(String path) {
        this.path = path;
        return this;
    }

    public ImageLoaderBuilder view(ImageWrapperView view) {
        this.imageView = view;
        return this;
    }

    public ImageLoaderBuilder defDrawable(int defaultDrawable) {
        this.defaultDrawable = defaultDrawable;
        return this;
    }

    public ImageLoaderBuilder width(int width) {
        this.width = width;
        return this;
    }

    public ImageLoaderBuilder height(int height) {
        this.height = height;
        return this;
    }

    public ImageLoaderBuilder fitCenter(boolean fitCenter) {
        this.fitCenter = fitCenter;
        return this;
    }

    public ImageLoaderBuilder memoryCache(boolean memoryCache) {
        this.memoryCache = memoryCache;
        return this;
    }

    public ImageLoaderBuilder diskCache(boolean diskCache) {
        this.diskCache = diskCache;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ImageWrapperView getImageView() {
        return imageView;
    }

    public void setImageView(ImageWrapperView imageView) {
        this.imageView = imageView;
    }

    public int getDefaultDrawable() {
        return defaultDrawable;
    }

    public void setDefaultDrawable(int defaultDrawable) {
        this.defaultDrawable = defaultDrawable;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isFitCenter() {
        return fitCenter;
    }

    public void setFitCenter(boolean fitCenter) {
        this.fitCenter = fitCenter;
    }

    public boolean isMemoryCache() {
        return memoryCache;
    }

    public void setMemoryCache(boolean memoryCache) {
        this.memoryCache = memoryCache;
    }

    public boolean isDiskCache() {
        return diskCache;
    }

    public void setDiskCache(boolean diskCache) {
        this.diskCache = diskCache;
    }
}
