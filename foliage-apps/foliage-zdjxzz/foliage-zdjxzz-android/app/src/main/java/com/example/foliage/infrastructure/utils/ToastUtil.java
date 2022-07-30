package com.example.foliage.infrastructure.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.example.foliage.R;

/**
 * Description :Toast的工具类
 * Created by liang.qfzc@gmail.com on 2018/6/12.
 */
public class ToastUtil {

    private static Toast sToast;

    /**
     * 原始的toast
     * @param str
     */
    public static void show(String str) {
        Toast.makeText(AppContextUtil.getAppContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast显示一个View
     * @param view
     */
    public static void show(View view) {
        Toast toast = getToast(view);
        toast.show();
    }

    /**
     * 显示一个View为TextView的Toast
     * @param textResId
     */
    public static void showTextViewPrompt(@StringRes int textResId) {
        showTextViewPrompt(AppContextUtil.getAppContext(), AppContextUtil.getAppContext().getString(textResId));
    }

    /**
     * 重载的方法，同上
     * @param name
     */
    public static void showTextViewPrompt(String name) {
//        showTextViewPrompt(AppContextUtil.getAppContext(), name);
        show(name);
    }

    public static void showTextViewPrompt(Context context, String name) {
//        showVerboseToast(name);
        show(name);
    }

    public static void showTextViewPromptShort(String name) {
//        showVerboseToast(name);
        show(name);
    }

    public static void showImageViewPromptShort(String showContent) {
        showOkToast(showContent);
    }

    public static void showImageViewPromptLong(String showContent) {
        showOkToast(showContent);
    }

    public static void showTextViewPromptLong(String name) {
//        showVerboseToast(name);
        show(name);
    }

    /**
     * 显示正确的Toast
     * @param text
     */
    public static void showOkToast(String text) {

        View view = View.inflate(AppContextUtil.getAppContext(), R.layout.toast_view_prompt, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_toast_prompt);
        tv.setText(text);

        Toast toast = getToast(view);
        toast.show();
    }

    /**
     * 显示错误的Toast
     * @param text
     */
    public static void showErrorToast(String text) {
        View view = View.inflate(AppContextUtil.getAppContext(), R.layout.toast_view_prompt, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_toast_prompt);
        tv.setText(text);
        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.toast_show_error, 0, 0, 0);

        Toast toast = getToast(view);
        toast.show();
    }

    /**
     * 显示警告的Toast
     * @param text
     */
    public static void showWarnToast(String text) {
        View view = View.inflate(AppContextUtil.getAppContext(), R.layout.toast_view_prompt, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_toast_prompt);
        tv.setText(text);
        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.toast_show_warn, 0, 0, 0);

        Toast toast = getToast(view);
        toast.show();
    }

    /**
     * 显示View为TextView 的Toast
     * @param text
     */
    public static void showVerboseToast(String text) {
        View view = View.inflate(AppContextUtil.getAppContext(), R.layout.toast_view_prompt, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_toast_prompt);
        tv.setText(text);
        tv.setCompoundDrawables(null, null, null, null);

        Toast toast = getToast(view);
        toast.show();
    }

    private static Toast getToast(View view) {

        if (sToast == null) {
            Toast toast = new Toast(AppContextUtil.getAppContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            sToast = toast;
        }
        sToast.setView(view);
        return sToast;
    }
}
