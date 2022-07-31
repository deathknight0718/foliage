
package org.foliage.zdjxzz.infrastructure.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.lang.reflect.Method;

public class ScreenUtil {

    // 目标模糊半径
    public static final float GBLUR_RADIUS_DEST = 220;

    // 屏幕宽
    public static int widthPixels;

    // 屏幕高
    public static int heightPixels;

    // 屏幕密度
    public static float density;

    // 图片在沟通中显示最大宽度
    public static int maxImageWidth;

    // 图片在沟通中显示最小宽度
    public static int minImageWidth;

    // 图片在沟通中显示最大高度
    public static int maxImageHeight;

    // 图片在沟通中显示最小高度
    public static int minImageHeight;

    public static void init(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;
        density = dm.density;
        maxImageWidth = widthPixels / 2;
        minImageWidth = widthPixels / 4;
        maxImageHeight = heightPixels / 3;
        minImageHeight = heightPixels / 8;
    }

    /**
     * dp转px @param dpValue dp @return int px @throws
     */
    public static int dp2px(float dpValue) {
        return (int) (dpValue * density + 0.5f);
    }

    public static int dp2px(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     *
     * @param context 上下文
     * @param spValue SP值
     * @return 像素值
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px 转 dp @param pxValue px @return int dp @throws
     */
    public static int px2dp(float pxValue) {
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 获取状态栏高度 @return int @throws
     */
    public static int getStatusBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
    }

    /**
     * 根据屏幕的高度计算相应的视图高度
     *
     * @param high
     * @return
     */
    public static int getViewHeight(int high) {
        return high * heightPixels / 1920;
    }

    /**
     * 根据屏幕的高度计算相应的视图高度
     *
     * @param width
     * @return
     */
    public static int getViewWidth(int width) {
        return width * widthPixels / 1080;
    }

    public static int[] getScreenInfo() {
        int info[] = {
                widthPixels, heightPixels
        };
        return info;
    }

    public static int getTextSize(int size) {
        return size * heightPixels / 1920;
    }

    /**
     * 获取底部工具栏高度
     *
     * @return
     */
    public static int getNavigationBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android"));
    }

    /**
     * 检查是否启用底部工具栏
     *
     * @return
     */
    public static boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        int id = Resources.getSystem().getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = Resources.getSystem().getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            hasNavigationBar = "0".equals(navBarOverride);
        } catch (Exception e) {
            Log.e("ScreenUtil", e.getMessage());
        }
        return hasNavigationBar;
    }
}
