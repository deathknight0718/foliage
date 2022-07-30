package com.example.foliage.infrastructure.banner.transformer;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * Description：设置ViewPager切换时的动画效果
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public class CustomTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.9F;
    @Override
    public void transformPage(View page, float position) {

        if(position < -1){
            page.setScaleY(MIN_SCALE);
        }else if(position<= 1){
            float scale = Math.max(MIN_SCALE,1 - Math.abs(position));
            page.setScaleY(scale);
//            page.setScaleX(scale);
//            if(position<0){
//                page.setTranslationX(width * (1 - scale) /2);
//            }else{
//                page.setTranslationX(-width * (1 - scale) /2);
//            }

        }else{
            page.setScaleY(MIN_SCALE);
        }
    }

}
