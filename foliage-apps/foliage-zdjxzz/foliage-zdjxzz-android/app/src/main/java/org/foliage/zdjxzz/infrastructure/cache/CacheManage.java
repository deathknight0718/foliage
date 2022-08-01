package org.foliage.zdjxzz.infrastructure.cache;

import com.orhanobut.hawk.Hawk;

/**
 * Description: 缓存管理
 */
public class CacheManage {

    private static CacheManage instance;

    public static CacheManage getInstance() {
        if (instance == null) {
            instance = new CacheManage();
        }
        return instance;
    }

    public <T> T saveCache(@CacheModel.Model String model, T data) {
        Hawk.put(model, data);
        return data;
    }

    public boolean delete(@CacheModel.Model String model) {
        try {
            return Hawk.delete(model);
        } catch (Exception e) {
        }
        return false;
    }

    public <T> T getCache(@CacheModel.Model String key) {
        return Hawk.get(key);
    }

    public <T> T getCache(@CacheModel.Model String model, T t) {
        return Hawk.get(model, t);
    }

    public void clear() {
        Hawk.delete(CacheModel.LOGIN_KEY);
    }

}
