package org.foliage.zdjxzz.constant;

import org.foliage.zdjxzz.BuildConfig;

/**
 * Description：网络接口
 * Created by liang.qfzc@gmail.com on 2018/6/12.
 */

public class UrlConstants {

    /**
     * 生产环境
     */
    public static final String BASE_URL = "http://47.97.35.147:7080";

    public static final int HTTP_TIMEOUT = 30;

    /**
     * 获取服务器地址
     *
     * @return 地址
     */
    public static String getBaseUrl() {
        if (BuildConfig.DEBUG) {
            return BASE_URL;
        }
        return BASE_URL;
    }

}