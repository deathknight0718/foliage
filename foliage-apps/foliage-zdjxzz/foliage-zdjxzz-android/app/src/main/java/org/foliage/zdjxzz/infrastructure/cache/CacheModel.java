package org.foliage.zdjxzz.infrastructure.cache;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description: 各接口或者自定义缓存数据Key
 */
public class CacheModel {

    @StringDef({LOGIN_KEY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Model {
    }


    /**
     * 登录信息
     */
    public static final String LOGIN_KEY = "login";

    private String model;

    public void setModel(String model) {
        this.model = model;
    }
}

