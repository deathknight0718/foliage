package com.example.foliage.page.device.view;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.widget.SwitchCompat;

import com.example.foliage.R;
import com.example.foliage.base.BaseFragmentActivity;
import com.example.foliage.infrastructure.roundview.RoundTextView;
import com.example.foliage.infrastructure.utils.IntentTool;
import com.example.foliage.page.device.contract.DeviceContract;
import com.example.foliage.page.device.presenter.DeviceDetailPresenter;
import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import butterknife.BindView;

public class DeviceDetailActivity extends BaseFragmentActivity implements DeviceContract.DetailView {

    @BindView(R.id.tv_device_front)
    RoundTextView vTvDeviceFront;
    @BindView(R.id.tv_device_back)
    RoundTextView vTvDeviceBack;
    @BindView(R.id.tv_device_stop)
    RoundTextView vTvDeviceStop;

    // =========== 运行仪表 =============== //
    @BindView(R.id.engine)
    PointerSpeedometer vEngine;
    @BindView(R.id.water)
    PointerSpeedometer vWater;
    @BindView(R.id.oilpressure)
    PointerSpeedometer vOilpressure;
    @BindView(R.id.speed)
    PointerSpeedometer vSpeed;
    @BindView(R.id.shake)
    PointerSpeedometer vShake;

    // =========== 报警状态 =============== //
    @BindView(R.id.water_high_warning)
    SwitchCompat vWaterHighWarning;
    @BindView(R.id.oil_pressure_warning)
    SwitchCompat vOilPressureWarning;
    @BindView(R.id.filter_block_warning)
    SwitchCompat vFilterBlockWarning;
    @BindView(R.id.hydraulic_pressure_warning)
    SwitchCompat vHydraulicPressureWarning;

    // =========== 控制参数仪表 =============== //
    @BindView(R.id.front_electricity)
    PointerSpeedometer vFrontElectricity;
    @BindView(R.id.back_electricity)
    PointerSpeedometer vBackElectricity;
    @BindView(R.id.front_shake_electricity)
    PointerSpeedometer vFrontShakeElectricity;
    @BindView(R.id.back_shake_electricity)
    PointerSpeedometer vBackShakeElectricity;

    private DeviceContract.DetailPresenter mPresenter;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_device_detail;
    }

    @Override
    protected String getActionBarTitle() {
        return "设备信息";
    }

    @Override
    protected boolean hasBindEventBus() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();
        mPresenter = new DeviceDetailPresenter(this);
        Intent intent = this.getIntent();
        String id = intent.getStringExtra("id");
        mPresenter.fetchDeviceById(id);
    }

    @Override
    public void setPresenter(DeviceContract.DetailPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showDetailView(JsonObject retDatBean) {
        JsonElement data = retDatBean.get("data");
        JsonElement device = retDatBean.get("device");

        vTvDeviceFront.setPressed(data.getAsJsonObject().get("手柄前进开关输入状态").getAsInt() != 0);
        vTvDeviceBack.setPressed(data.getAsJsonObject().get("手柄后退开关输入状态").getAsInt() != 0);
        vTvDeviceStop.setPressed(data.getAsJsonObject().get("手柄中位开关输入状态").getAsInt() != 0);

        vEngine.setMaxSpeed(5000);
        vWater.setMaxSpeed(100);
        vOilpressure.setMaxSpeed(2);
        vSpeed.setMaxSpeed(150);
        vShake.setMaxSpeed(200);

        vEngine.speedTo(data.getAsJsonObject().get("发动机转速").getAsFloat());
        vWater.speedTo(data.getAsJsonObject().get("水温").getAsFloat());
        vOilpressure.speedTo(data.getAsJsonObject().get("机油压力").getAsFloat());
        vSpeed.speedTo(data.getAsJsonObject().get("行车速度").getAsFloat());
        vShake.speedTo(data.getAsJsonObject().get("振动频率").getAsFloat());

        vWaterHighWarning.setChecked(data.getAsJsonObject().get("发动机水温高报警").getAsFloat() != 0);
        vOilPressureWarning.setChecked(data.getAsJsonObject().get("补油压力报警").getAsFloat() != 0);
        vFilterBlockWarning.setChecked(data.getAsJsonObject().get("空滤阻塞报警").getAsFloat() != 0);
        vHydraulicPressureWarning.setChecked(data.getAsJsonObject().get("液压真空度报警").getAsFloat() != 0);

        vFrontElectricity.speedTo(data.getAsJsonObject().get("行走泵前进比例阀输出").getAsFloat());
        vBackElectricity.speedTo(data.getAsJsonObject().get("行走泵后退比例阀输出").getAsFloat());
        vFrontShakeElectricity.speedTo(data.getAsJsonObject().get("振动泵正转比例阀输出").getAsFloat());
        vBackShakeElectricity.speedTo(data.getAsJsonObject().get("振动泵反转比例阀输出").getAsFloat());
    }

    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, DeviceDetailActivity.class);
        intent.putExtra("id", id);
        IntentTool.startActivity(context, intent);
    }

}