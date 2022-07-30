package com.example.foliage.infrastructure.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.foliage.R;
import com.example.foliage.base.BaseDialog;
import com.example.foliage.infrastructure.utils.CornerUtil;

/**
 * Description：'●●●'加载进度框
 * Created by liang.qfzc@gmail.com on 2018/6/12.
 */

public class RippleLoadingDialog extends BaseDialog<RippleLoadingDialog> {

    private TextView vTitle;
    private String title = "加载中...";

    public RippleLoadingDialog(Context context) {
        super(context);
    }

    public RippleLoadingDialog title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
//        interceptDismiss(true);
        showAnim(new BaseAnimatorSet() {
            @Override
            public void setAnimation(View view) {

            }
        });

        View inflate = View.inflate(getContext(), R.layout.dialog_ripple, null);
        vTitle = (TextView) inflate.findViewById(R.id.title);
        inflate.setPadding(dp2px(15), dp2px(15), dp2px(15), dp2px(15));
        inflate.setBackgroundDrawable(
                CornerUtil.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));
        return inflate;
    }

    /**
     * 设置是否拦截返回
     */
    public void setInterceptDismiss(boolean interceptDismiss) {
        interceptDismiss(interceptDismiss);
    }

    @Override
    public void setUiBeforShow() {
        vTitle.setText(title);
    }
}
