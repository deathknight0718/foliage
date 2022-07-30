package com.example.foliage.infrastructure.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foliage.R;

/**
 * Description：下拉刷新组件
 * Created by liang.qfzc@gmail.com on 2018/7/18.
 */
public class VerticalSwipeRefreshLayout extends SwipeRefreshLayout {
    private int mTouchSlop;
    private float mPrevX;
    private boolean mInterceptHorizontal;
    private View mInterceptHorizontalView;

    public VerticalSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setColorSchemeColors(ContextCompat.getColor(context, R.color.color_base));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mInterceptHorizontalView != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPrevX = MotionEvent.obtain(event).getX();
                    int location[] = new int[2];
                    mInterceptHorizontalView.getLocationOnScreen(location);
                    Rect rect = new Rect(location[0], location[1], location[0] + mInterceptHorizontalView.getWidth(),
                            location[1] + mInterceptHorizontalView.getHeight());
                    mInterceptHorizontal = rect.contains((int) event.getRawX(), (int) event.getRawY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float eventX = event.getX();
                    float xDiff = Math.abs(eventX - mPrevX);
                    if (mInterceptHorizontal && xDiff > mTouchSlop) {
                        return false;
                    }
            }
        }

        return super.onInterceptTouchEvent(event);
    }

    public void setInterceptHorizontalView(View view) {
        mInterceptHorizontalView = view;
    }

}
