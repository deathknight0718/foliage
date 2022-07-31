package com.example.foliage.page.device.contract;

import com.anychart.scales.Geo;
import com.example.foliage.base.IBaseExtraView;
import com.example.foliage.base.IBasePresenter;
import com.example.foliage.page.device.dto.DeviceInfoDTO;
import com.example.foliage.page.device.dto.GeographicDTO;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Description：主界面Contract
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public interface DeviceContract {

    interface DeviceListView extends IBaseExtraView<DeviceListPresenter> {

        /**
         * 设备列表显示
         *
         * @param retDatBean
         */
        void showDeviceList(List<DeviceInfoDTO> retDatBean);

        void showLineProgress(boolean show);

    }

    interface DetailView extends IBaseExtraView<DetailPresenter> {

        void showDetailView(JsonObject retDatBean);

    }

    interface MapGeographicView extends IBaseExtraView<MapGeographicPresenter> {

        void showMapMaker(GeographicDTO retDatBean);

        void showGeoDialog(GeographicDTO geographicDTO);

    }

    interface DeviceListPresenter extends IBasePresenter {

        /**
         * 获取列表接口
         */
        void fetchDevices();

        /**
         * 注册
         */
        void registerDevice(String name, String devCode);

        /**
         * 注册
         */
        void unRegisterDevice(String id);

    }

    interface DetailPresenter extends IBasePresenter {

        /**
         * 获取列表接口
         */
        void fetchDeviceById(String id);

    }

    interface MapGeographicPresenter extends IBasePresenter {

        void geographic(String id);

    }

}
