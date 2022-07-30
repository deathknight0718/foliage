package com.example.foliage.page.device.Model;

import com.example.foliage.net.client.DeviceClient;
import com.example.foliage.page.device.dto.DeviceInfoDTO;
import com.example.foliage.page.device.dto.GeographicDTO;
import com.google.gson.JsonObject;

import java.util.List;

import rx.Observable;

/**
 * Created by liang.qfzc@gmail.com on 2018/7/2.
 */

public class DeviceModel {

    private static volatile DeviceModel mInstance;

    private DeviceModel() {
        if (mInstance != null) {
            throw new IllegalStateException("Already initialized.");
        }
    }

    public static DeviceModel getInstance() {
        DeviceModel result = mInstance;
        if (result == null) {
            synchronized (DeviceModel.class) {
                result = mInstance;
                if (result == null) {
                    mInstance = result = new DeviceModel();
                }
            }
        }
        return result;
    }

    public Observable<List<DeviceInfoDTO>> devices() {
        return DeviceClient.getInstance().devices();
    }

    public Observable<Void> register(String name, String devCode) {
        return DeviceClient.getInstance().register(name, devCode);
    }

    public Observable<Void> unRegister(String id) {
        return DeviceClient.getInstance().unRegister(id);
    }

    public Observable<JsonObject> detailById(String id) {
        return DeviceClient.getInstance().detailById(id);
    }

    public Observable<GeographicDTO> geographic(String id) {
        return DeviceClient.getInstance().geographic(id);
    }

}
