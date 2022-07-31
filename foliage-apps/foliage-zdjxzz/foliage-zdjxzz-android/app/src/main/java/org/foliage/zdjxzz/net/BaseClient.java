package org.foliage.zdjxzz.net;

import org.foliage.zdjxzz.constant.UrlConstants;
import org.foliage.zdjxzz.infrastructure.utils.TimeManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Description：网络接口基类 retrofit2 & RxJava & Http
 * Created by liang.qfzc@gmail.com on 2018/6/14.
 */

public class BaseClient {

    private HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    private Interceptor headInterceptor;
    protected OkHttpClient client;

    protected BaseClient() {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        headInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("timestamp", String.valueOf(TimeManager.getInstance().getCurTimeMillis())).addHeader("Authorization", "Basic dG9tY2F0OnNlY3JldA==").build();
                return chain.proceed(request);
            }
        };

        client = new OkHttpClient.Builder().addInterceptor(logging).addInterceptor(headInterceptor).readTimeout(UrlConstants.HTTP_TIMEOUT, TimeUnit.SECONDS).connectTimeout(UrlConstants.HTTP_TIMEOUT, TimeUnit.SECONDS).build();
    }

}
