package com.example.foliage.page.device.view;

import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.animation.Transformation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.foliage.R;
import com.example.foliage.base.BaseFragmentActivity;
import com.example.foliage.infrastructure.utils.IntentTool;
import com.example.foliage.page.device.adapter.GeoAdapter;
import com.example.foliage.page.device.contract.DeviceContract;
import com.example.foliage.page.device.dto.GeographicDTO;
import com.example.foliage.page.device.presenter.DeviceMapPresenter;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.List;

import butterknife.BindView;

public class DeviceMapsActivity extends BaseFragmentActivity implements DeviceContract.MapGeographicView, BaiduMap.OnMarkerClickListener {

    private DeviceContract.MapGeographicPresenter mPresenter;
    private GeoAdapter mGeoAdapter;
    private GeographicDTO mGeographicDTO;

    @BindView(R.id.map_view)
    MapView vMapView;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_devices_map;
    }

    @Override
    protected String getActionBarTitle() {
        return "设备位置";
    }

    @Override
    protected boolean hasBindEventBus() {
        return false;
    }

    @Override
    protected void initData() {
        super.initData();
        mGeoAdapter = new GeoAdapter(this);
    }

    @Override
    protected void initView() {
        super.initView();
        mPresenter = new DeviceMapPresenter(this);
        Intent intent = this.getIntent();
        String id = intent.getStringExtra("id");
        BaiduMap map = vMapView.getMap();
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        map.setOnMarkerClickListener(this);
        mPresenter.geographic(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        vMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vMapView != null) {
            vMapView.onDestroy();
        }
    }

    @Override
    public void setPresenter(DeviceContract.MapGeographicPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showMapMaker(final GeographicDTO retDatBean) {
        this.mGeographicDTO = retDatBean;
        GeographicDTO.CurrentBean.CoordinateBean coordinate = retDatBean.getCurrent().getCoordinate();

        List<GeographicDTO.GeographicsByProvinceBean> allGeo = retDatBean.getGeographicsByProvince();
        for (GeographicDTO.GeographicsByProvinceBean geographicsBean : allGeo) {
            LatLng point = new LatLng(geographicsBean.getCoordinate().getLatitude(), geographicsBean.getCoordinate().getLongitude());
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
            OverlayOptions option = new MarkerOptions().position(point).animateType(MarkerOptions.MarkerAnimateType.jump).icon(bitmap);
            BaiduMap map = vMapView.getMap();
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()), 13);
            map.animateMapStatus(msu);
            map.addOverlay(option);
        }
    }

    @Override
    public void showGeoDialog(GeographicDTO geographicDTO) {
        mGeoAdapter.setData(geographicDTO.getGeographicsByProvince());
        DialogPlus dialog = DialogPlus.newDialog(getContext()).setAdapter(mGeoAdapter).setHeader(R.layout.view_dialogplus_header_map).setOnItemClickListener((dialog1, item, view, position) -> {
                    if (position < 0) return;
                    GeographicDTO.GeographicsByProvinceBean data = mGeoAdapter.getItem(position);
                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(new LatLng(data.getCoordinate().getLatitude(), data.getCoordinate().getLongitude()), 13);
                    vMapView.getMap().animateMapStatus(msu);
                }).setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }

    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, DeviceMapsActivity.class);
        intent.putExtra("id", id);
        IntentTool.startActivity(context, intent);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showGeoDialog(mGeographicDTO);
        return false;
    }

}