package org.foliage.zdjxzz.page.device.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.foliage.zdjxzz.R;
import org.foliage.zdjxzz.base.BaseFragmentActivity;
import org.foliage.zdjxzz.infrastructure.widget.VerticalSwipeRefreshLayout;
import org.foliage.zdjxzz.page.device.adapter.DeviceAdapter;
import org.foliage.zdjxzz.page.device.contract.DeviceContract;
import org.foliage.zdjxzz.page.device.dto.DeviceInfoDTO;
import org.foliage.zdjxzz.page.device.presenter.DeviceListPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DeviceListActivity extends BaseFragmentActivity implements DeviceContract.DeviceListView, OnItemClickListener, OnItemChildClickListener {

    private DeviceContract.DeviceListPresenter mPresenter;

    @BindView(R.id.fab)
    FloatingActionButton vFab;
    @BindView(R.id.swipeRefreshLayout)
    VerticalSwipeRefreshLayout vSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView vRecyclerview;
    @BindView(R.id.emptyView)
    View emptyView;

    View vProgressRegister;
    AlertDialog vRegisterDialog;
    AlertDialog vQueryDialog;

    private DeviceAdapter mDeviceAdapter;

    private List<DeviceInfoDTO> mDevices;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_device_list;
    }

    @Override
    protected String getActionBarTitle() {
        return "设备监控";
    }

    @Override
    protected boolean hasBindEventBus() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        vRecyclerview.setLayoutManager(layoutManager);

        mDeviceAdapter = new DeviceAdapter(mContext);
        vRecyclerview.setAdapter(mDeviceAdapter);

        mDeviceAdapter.addChildClickViewIds(R.id.tv_device_status, R.id.tv_device_local, R.id.tv_device_exit);

        mPresenter = new DeviceListPresenter(this);
        mPresenter.fetchDevices();
    }

    @Override
    protected void setListener() {
        super.setListener();
        vSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.fetchDevices();
        });
        // 设置子控件点击监听
        mDeviceAdapter.setOnItemChildClickListener(this);
        mDeviceAdapter.setOnItemClickListener(this);
        vFab.setOnClickListener(view -> {
            displayRegisterDialog();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_query) {
            displayQueryDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayQueryDialog() {
        View queryView = LayoutInflater.from(this).inflate(R.layout.dialog_register, null);
        vProgressRegister = ButterKnife.findById(queryView, R.id.progress_register);
        ButterKnife.findById(queryView, R.id.l_dev_as).setVisibility(View.GONE);
        TextInputEditText teDevCode = ButterKnife.findById(queryView, R.id.dev_code);

        vQueryDialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("设备查询")
                .setView(queryView)
                .setNegativeButton("取消", (dialogInterface, i) -> {
                }).setCancelable(false).setPositiveButton("查询", null).create();
        vQueryDialog.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) vQueryDialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String inputCode = teDevCode.getText().toString();

                    if (TextUtils.isEmpty(inputCode)) {
                        mDeviceAdapter.setList(mDevices);
                    } else {
                        List<DeviceInfoDTO> filterList = new ArrayList<>();
                        for (DeviceInfoDTO info : mDevices) {
                            if (info.getDevcode().contains(inputCode)) {
                                filterList.add(info);
                            }
                        }
                        mDeviceAdapter.setList(filterList);
                    }
                }
            });
        });
        vQueryDialog.show();
    }

    public void displayRegisterDialog() {
        View registerView = LayoutInflater.from(this).inflate(R.layout.dialog_register, null);
        vProgressRegister = ButterKnife.findById(registerView, R.id.progress_register);
        TextInputEditText teDevAs = ButterKnife.findById(registerView, R.id.dev_as);
        TextInputEditText teDevCode = ButterKnife.findById(registerView, R.id.dev_code);
        vRegisterDialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme).setTitle("设备注册").setView(registerView).setNegativeButton("取消", (dialogInterface, i) -> {
        }).setCancelable(false).setPositiveButton("注册", null).create();
        vRegisterDialog.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) vRegisterDialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> mPresenter.registerDevice(teDevAs.getText().toString(), teDevCode.getText().toString()));
        });
        vRegisterDialog.show();
    }

    @Override
    public void setPresenter(DeviceContract.DeviceListPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showLineProgress(boolean show) {
        if (vProgressRegister != null) {
            vProgressRegister.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void showDeviceList(List<DeviceInfoDTO> devices) {
        mDevices = devices;
        if (vSwipeRefreshLayout.isRefreshing()) {
            vSwipeRefreshLayout.setRefreshing(false);
        }

        if (vRegisterDialog != null) {
            vRegisterDialog.dismiss();
        }

        if (devices != null && devices.size() > 0) {
            vRecyclerview.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            mDeviceAdapter.setList(devices);
        } else {
            vRecyclerview.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        String id = mDeviceAdapter.getItem(position).getId();
        if (view.getId() == R.id.tv_device_status) {
            DeviceDetailActivity.startActivity(mContext, id);
        } else if (view.getId() == R.id.tv_device_local) {
            DeviceMapsActivity.startActivity(mContext, id);
        } else if (view.getId() == R.id.tv_device_exit) {
            mPresenter.unRegisterDevice(id);
        }
    }

}