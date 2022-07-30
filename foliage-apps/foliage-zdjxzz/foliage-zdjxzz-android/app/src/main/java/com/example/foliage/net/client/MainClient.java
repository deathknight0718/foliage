package com.example.foliage.net.client;

import com.example.foliage.constant.UrlConstants;
import com.example.foliage.net.BaseClient;
import com.example.foliage.net.interfaces.MainService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description：main模块接口
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public class MainClient extends BaseClient {

    private static MainClient mInstance;

    public static MainClient getInstance() {
        if (mInstance == null) {
            synchronized (MainClient.class) {
                if (mInstance == null) {
                    mInstance = new MainClient();
                }
            }
        }
        return mInstance;
    }

    private MainService service;

    private MainClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConstants.getBaseUrl())
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MainService.class);
    }

//    /**
//     * 获取个人信息
//     */
//    public Observable<UserCenterOutput> userCenter() {
//        return callMapAfterLogin(service.userCenter(new RequestMap().getReqParams(true)))
//                .flatMap(new Func1<Pair<MetaBean, String>, Observable<UserCenterOutput>>() {
//                    @Override
//                    public Observable<UserCenterOutput> call(Pair<MetaBean, String> pair) {
//                        if (pair == null) {
//                            return toObservable(null);
//                        }
//                        UserCenterOutput output = null;
//                        if (!TextUtils.isEmpty(pair.second)) {
//                            output = new Gson().fromJson(pair.second, new TypeToken<UserCenterOutput>() {
//                            }.getType());
//                        }
//                        return toObservable(new Pair<>(pair.first, output));
//                    }
//                });
//    }

}
