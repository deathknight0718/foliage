package com.example.foliage.net.client;

import com.example.foliage.constant.UrlConstants;
import com.example.foliage.net.BaseClient;
import com.example.foliage.net.interfaces.DeviceService;
import com.example.foliage.page.device.dto.DeviceInfoDTO;
import com.example.foliage.page.device.dto.GeographicDTO;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public class DeviceClient extends BaseClient {

    private static DeviceClient mInstance;

    public static DeviceClient getInstance() {
        if (mInstance == null) {
            synchronized (DeviceClient.class) {
                if (mInstance == null) {
                    mInstance = new DeviceClient();
                }
            }
        }
        return mInstance;
    }

    private DeviceService service;

    private DeviceClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConstants.getBaseUrl())
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(DeviceService.class);
    }

    /**
     * 获取设备列表
     */
    public Observable<List<DeviceInfoDTO>> devices() {
        Map<String, String> param = new HashMap<>();
        param.put("offset", "0");
        param.put("limit", "500");
        return service.devices(param);
    }

    /**
     * 注册
     */
    public Observable<Void> register(String name, String devCode) {
        Map<String, String> param = new HashMap<>();
        param.put("name", name);
        param.put("devcode", devCode);
        return service.register(param);
    }

    /**
     * 注销
     */
    public Observable<Void> unRegister(String id) {
        return service.unRegister(id);
    }

    /**
     * 查询设备详情
     */
    public Observable<JsonObject> detailById(String id) {
        return service.detailById(id);
    }

    /**
     * 查询设备详情
     */
    public Observable<GeographicDTO> geographic(String id) {
        return service.geographic(id);
    }

}
