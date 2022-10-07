package org.foliage.zdjxzz.page.login.model;

import android.util.Pair;

import org.foliage.zdjxzz.net.client.LoginClient;
import org.json.JSONObject;

import rx.Observable;

/**
 * Description：登录模块model
 */

public class LoginModel {

    private static volatile LoginModel mInstance;

    private LoginModel() {
        if (mInstance != null) {
            throw new IllegalStateException("Already initialized.");
        }
    }

    public static LoginModel getInstance() {
        LoginModel result = mInstance;
        if (result == null) {
            synchronized (LoginModel.class) {
                result = mInstance;
                if (result == null) {
                    mInstance = result = new LoginModel();
                }
            }
        }
        return result;
    }

    /**
     * 登录接口
     *
     * @param accountName 用户名
     * @param password    密码
     * @return
     */
    public Observable<String> login(String accountName, String password) {
        return LoginClient.getInstance().login(accountName, password);
    }

}
