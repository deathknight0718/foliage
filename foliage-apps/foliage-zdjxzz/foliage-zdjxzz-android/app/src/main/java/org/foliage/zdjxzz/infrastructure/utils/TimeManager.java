
package org.foliage.zdjxzz.infrastructure.utils;

import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeManager {

    private long loginElapsedTime;
    private long loginNetworkTime;
    private boolean initNetworkTimeSucceed = false;
    private static TimeManager mTimeManager = null;

    private TimeManager() {
    }

    public static TimeManager getInstance() {
        if (mTimeManager == null) {
            mTimeManager = new TimeManager();
        }
        return mTimeManager;
    }

    public void initLoginTime() {

        if (!initNetworkTimeSucceed) {
            long networkTime = 0;
            try {
                URL url = new URL("http://www.baidu.com");
                URLConnection uc = url.openConnection();
                uc.connect();
                networkTime = uc.getDate();
                Log.d("TimeManager", "initLoginTime networkTime = " + getTimeString(networkTime / 1000));
            } catch (MalformedURLException e) {
                Log.d("TimeManager", "initLoginTime MalformedURLException = " + e.getMessage());
            } catch (IOException e) {
                Log.d("TimeManager", "initLoginTime IOException = " + e.getMessage());
            } finally {
                if (networkTime == 0) {
                    loginNetworkTime = System.currentTimeMillis();
                } else {
                    loginNetworkTime = networkTime;
                    initNetworkTimeSucceed = true;
                }
                loginElapsedTime = SystemClock.elapsedRealtime();
            }
        }
    }

    public long getCurTimeSeconds() {
        return getCurTimeMillis() / 1000;
    }

    public long getCurTimeMillis() {

        if (initNetworkTimeSucceed) {
            return loginNetworkTime
                    + (SystemClock.elapsedRealtime() - loginElapsedTime);
        }

        return System.currentTimeMillis();
    }

    /**
     * 将秒转为时间字符串
     *
     * @param time
     * @return
     */
    public static String getTimeString(long time) {
        long curTime = getInstance().getCurTimeMillis();
        long offset = TimeZone.getDefault().getRawOffset();
        int day = (int) ((curTime + offset) / DateUtils.DAY_IN_MILLIS - (time + offset)
                / DateUtils.DAY_IN_MILLIS);
        String timeString = "";
        String format;
        switch (day) {
            case 0:
                timeString += "今天  ";
                format = "HH:mm";
                break;
            case 1:
                timeString += "昨天  ";
                format = "HH:mm";
                break;
            case 2:
                timeString += "前天  ";
                format = "HH:mm";
                break;
            default:
                format = "MM-dd HH:mm";
                break;
        }

        timeString += new SimpleDateFormat(format).format(new Date(time));
        return timeString;
    }

    public static boolean isToday(long seconds) {
        long time = seconds * 1000;
        long curTime = getInstance().getCurTimeMillis();
        long offset = TimeZone.getDefault().getRawOffset();
        int day = (int) ((curTime + offset) / DateUtils.DAY_IN_MILLIS - (time + offset)
                / DateUtils.DAY_IN_MILLIS);
        return day == 0;
    }

    public static boolean is48Hours(long seconds) {
        long curTime = getInstance().getCurTimeMillis();
        return curTime - seconds * 1000 < 48 * DateUtils.HOUR_IN_MILLIS;
    }

    public static long getTodayStartSeconds() {
        long curTime = getInstance().getCurTimeMillis();
        long offset = TimeZone.getDefault().getRawOffset();
        long day = (curTime + offset) / DateUtils.DAY_IN_MILLIS;
        return day * DateUtils.DAY_IN_MILLIS / 1000;
    }

    public boolean isInitNetworkTimeSucceed() {
        return initNetworkTimeSucceed;
    }

}
