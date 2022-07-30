package com.example.foliage.net.interfaces;


import com.example.foliage.page.device.dto.DeviceInfoDTO;
import com.example.foliage.page.device.dto.GeographicDTO;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Description：登录模块接口定义
 * Created by liang.qfzc@gmail.com on 2018/6/14.
 */

public interface DeviceService {

    @PUT("/foliage-zdjxzz-web/api/v1/device/register")
    Observable<Void> register(@Body Map<String, String> param);

    @DELETE("foliage-zdjxzz-web/api/v1/device/{id}")
    Observable<Void> unRegister(@Path("id") String id);

    @GET("/foliage-zdjxzz-web/api/v1/devices")
    Observable<List<DeviceInfoDTO>> devices(@QueryMap Map<String, String> param);

    @GET("/foliage-zdjxzz-web/api/v1/specification/{id}")
    Observable<JsonObject> detailById(@Path("id") String id);

    @GET("/foliage-zdjxzz-web/api/v1/geographic/{id}")
    Observable<GeographicDTO> geographic(@Path("id") String id);

}
