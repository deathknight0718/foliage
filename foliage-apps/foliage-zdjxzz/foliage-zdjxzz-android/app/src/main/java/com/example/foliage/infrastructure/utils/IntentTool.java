package com.example.foliage.infrastructure.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Description：intent工具类
 * Created by liang.qfzc@gmail.com on 2018/6/12.
 */

public class IntentTool {

    public static void startActivity(Context mContext, Class activity) {
        mContext.startActivity(new Intent(mContext, activity));
    }

    public static void startActivity(Context mContext, Class activity, boolean isFinish) {
        mContext.startActivity(new Intent(mContext, activity));
        if (isFinish && mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

    public static void startActivity(Context mContext, Intent intent) {
        mContext.startActivity(intent);
    }

    public static void startActivity(Context mContext, Intent intent, boolean isFinish) {
        mContext.startActivity(intent);
        if (isFinish && mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

    public static void startActivityForResult(Context mContext, Class activity, int requestCode) {
        if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(new Intent(mContext, activity), requestCode);
        }
    }

    public static void startActivityForResult(Context mContext, Intent intent, int requestCode) {
        if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(intent, requestCode);
        }
    }

    public static void finishActivity(Context mContext) {
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }
}
