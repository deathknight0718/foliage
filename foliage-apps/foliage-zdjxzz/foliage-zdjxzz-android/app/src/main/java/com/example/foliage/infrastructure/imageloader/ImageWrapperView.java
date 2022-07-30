package com.example.foliage.infrastructure.imageloader;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Description：兼容5.0及以上版本的imageview
 * Created by liang.qfzc@gmail.com on 2018/7/11.
 */
public class ImageWrapperView extends AppCompatImageView {

    public ImageWrapperView(Context context) {
        super(context);
    }

    public ImageWrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageWrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public ImageWrapperView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
}
