package org.foliage.zdjxzz.net.interfaces;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

/**
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public interface LoginService {

    @GET("/foliage-zdjxzz-web/api/v1/devices")
    Observable<JsonArray> login(@Header("Authorization") String basic);
}
