
package org.foliage.zdjxzz.infrastructure.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Description : 程序工具类
 */
public final class AppContextUtil {

    private static Stack<WeakReference<Activity>> mActivityStack = new Stack<>();

    private static Context mContext;

    public static Context getAppContext() {
        if (mContext == null) {
            throw new IllegalStateException("AppContextUtil 's initApp not called!!!");
        }
        return mContext;
    }

    public static void initApp(Application app) {
        mContext = app.getApplicationContext();
        ScreenUtil.init(mContext);
    }

    /**
     * 将activity加入到栈中
     *
     * @param act activity
     */
    public static void pushActivity(Activity act) {
        try {
            if (mActivityStack == null) {
                mActivityStack = new Stack<>();
            }
            mActivityStack.add(new WeakReference<>(act));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将activity从栈中弹出
     *
     * @param act activity
     */
    public static void popActivity(Activity act) {
        try {
            Iterator<WeakReference<Activity>> iter = mActivityStack.iterator();
            while (iter.hasNext()) {
                Activity activity = iter.next().get();
                if (activity != null && activity == act) {
                    iter.remove();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭activity
     */
    public static void finishActivity(String... activityNames) {
        try {
            Iterator<WeakReference<Activity>> iter = mActivityStack.iterator();
            while (iter.hasNext()) {
                Activity activity = iter.next().get();
                for (String activityName : activityNames) {
                    if (activity != null && activity.getClass().getName().equals(activityName)) {
                        if (!activity.isFinishing()) {
                            activity.finish();
                        }
                        iter.remove();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        try {
            Iterator<WeakReference<Activity>> iter = mActivityStack.iterator();
            while (iter.hasNext()) {
                Activity activity = iter.next().get();
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
                iter.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前activity
     *
     * @return Activity
     */
    public static Activity getCurrentActivity() {
        if (mActivityStack.size() > 0) {
            return mActivityStack.get(mActivityStack.size() - 1).get();
        }
        return null;
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllExclByActivity(String activityName) {
        try {
            Iterator<WeakReference<Activity>> iter = mActivityStack.iterator();
            while (iter.hasNext()) {
                Activity activity = iter.next().get();
                if (activity != null && !activity.getClass().getName().equals(activityName)) {
                    if (!activity.isFinishing()) {
                        activity.finish();
                    }
                    iter.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取绑定该服务的包名
     *
     * @param mContext  上下文
     * @param className 类名
     * @return String
     */
    public static String getBindPackageName(Context mContext, String className) {

        try {
            ActivityManager activityManager = (ActivityManager)

                    mContext.getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningServiceInfo> serviceList

                    = activityManager.getRunningServices(Integer.MAX_VALUE);

            if (serviceList.size() <= 0) {
                return mContext.getPackageName();
            }

            for (int i = 0; i < serviceList.size(); i++) {
                if (serviceList.get(i).service.getClassName().equals(className)) {
                    return serviceList.get(i).service.getPackageName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mContext.getPackageName();
    }

    /**
     * 服务是否存活
     *
     * @param mContext  上下文
     * @param className 类名
     * @return boolean
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        try {
            ActivityManager activityManager = (ActivityManager)

                    mContext.getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningServiceInfo> serviceList

                    = activityManager.getRunningServices(Integer.MAX_VALUE);

            if (serviceList.size() <= 0) {
                return false;
            }

            for (int i = 0; i < serviceList.size(); i++) {
                if (serviceList.get(i).service.getClassName().equals(className)) {
                    isRunning = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRunning;

    }

    /**
     * 当前应用中指定的服务是否存活
     *
     * @param mContext    上下文
     * @param className   类名
     * @param packageName 包名
     * @return boolean
     */
    public static boolean isServiceRunningInContext(Context mContext, String className,
                                                    String packageName) {

        boolean isRunning = false;
        try {
            ActivityManager activityManager = (ActivityManager)

                    mContext.getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningServiceInfo> serviceList

                    = activityManager.getRunningServices(Integer.MAX_VALUE);

            if (serviceList.size() <= 0) {
                return false;
            }
            for (int i = 0; i < serviceList.size(); i++) {
                if (serviceList.get(i).service.getClassName().equals(className)
                        && TextUtils.equals(mContext.getPackageName(), packageName)) {
                    isRunning = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRunning;

    }

    /**
     * 获取当前进程名字
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断应用是否存活
     */
    public static boolean isAppAlive() {
        ActivityManager activityManager = (ActivityManager) getAppContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getAppContext().getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * app是否在前台运行
     */
    public static boolean isAppOnForeground() {
        try {
            ActivityManager mActivityManager = (ActivityManager) getAppContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            String mPackageName = getAppContext().getPackageName();
            List<ActivityManager.RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                // 应用程序位于堆栈的顶层
                if (mPackageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取包名
     */
    public static String getPackageName(Context context) {
        try {
            return context.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取包管理类
     */
    public static synchronized PackageManager getPackageManager(Context context) {
        try {
            return context.getPackageManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用信息
     */
    public static ApplicationInfo getApplicationInfo(Context context) {
        try {
            PackageManager manager = getPackageManager(context);
            if (manager == null)
                return null;
            return manager.getApplicationInfo(getPackageName(context),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取包信息
     */
    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager manager = getPackageManager(context);
            if (manager == null)
                return null;
            return manager.getPackageInfo(getPackageName(context), PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 当前应用的版本号
     */
    public static String getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return String.valueOf(packageInfo.versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取清单文件中的元数据
     *
     * @param name the key of value
     */
    public static boolean getBooleanMetaData(String name) {
        try {
            ApplicationInfo info = getApplicationInfo(mContext);
            assert info != null;
            return info.metaData.getBoolean(name);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取清单文件中的元数据
     *
     * @param name the key of value
     */
    public static String getStringMetaData(String name) {
        try {
            ApplicationInfo info = getApplicationInfo(mContext);
            assert info != null;
            return info.metaData.getString(name);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取清单文件中的元数据
     *
     * @param name the key of value
     */
    public static int getIntMetaData(String name) {
        return getIntMetaData(name, 0);
    }

    /**
     * 获取清单文件中的元数据
     *
     * @param name the key of value
     */
    public static int getIntMetaData(String name, int defaultValue) {
        try {
            ApplicationInfo info = getApplicationInfo(mContext);
            assert info != null;
            return info.metaData.getInt(name, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    /**
     * 退出应用程序
     */
    public static void exit(Context context) {
        try {
            finishAllActivity();
       /*     Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, LoginActivity.class);
            context.startActivity(intent);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
