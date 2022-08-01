package org.foliage.zdjxzz.net.client;

import android.util.Base64;
import android.util.Pair;

import com.google.gson.JsonArray;

import org.foliage.zdjxzz.constant.UrlConstants;
import org.foliage.zdjxzz.net.BaseClient;
import org.foliage.zdjxzz.net.interfaces.LoginService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Description：main模块接口
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public class LoginClient extends BaseClient {

    private static LoginClient mInstance;

    public static LoginClient getInstance() {
        if (mInstance == null) {
            synchronized (LoginClient.class) {
                if (mInstance == null) {
                    mInstance = new LoginClient();
                }
            }
        }
        return mInstance;
    }

    private LoginService service;

    private LoginClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConstants.getBaseUrl())
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(LoginService.class);
    }

    public Observable<String> login(String username, String password) {
        String credentials = username + ":" + password;
        String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return callMap(service.login(basic), basic);
    }


    /**
     * response处理
     *
     * @param observable 对象
     */
    public Observable<String> callMap(Observable<JsonArray> observable, String basic) {
        return observable.map(jsonObject -> basic);
    }
}
