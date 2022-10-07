package org.foliage.zdjxzz.page.device.presenter;

import org.foliage.zdjxzz.page.device.model.DeviceModel;
import org.foliage.zdjxzz.page.device.contract.DeviceContract;
import org.foliage.zdjxzz.page.device.dto.DeviceInfoDTO;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public class DeviceListPresenter implements DeviceContract.DeviceListPresenter {

    private DeviceContract.DeviceListView mView;

    private CompositeSubscription mSubscription;

    public DeviceListPresenter(DeviceContract.DeviceListView mView) {
        this.mView = mView;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroyPresenter() {
        if (mView != null) mView = null;
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    @Override
    public void fetchDevices() {
        Subscription subscription = DeviceModel.getInstance().devices().subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mView.dismissLoadingDialog();
                mView.showErrorText(e);
            }

            @Override
            public void onNext(List<DeviceInfoDTO> devices) {
                mView.showDeviceList(devices);
            }
        });
        mSubscription.add(subscription);
    }

    @Override
    public void registerDevice(String name, String devCode) {
        mView.showLineProgress(true);
        Subscription subscription = DeviceModel.getInstance().register(name, devCode).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Void>() {
            @Override
            public void onCompleted() {
                mView.showLineProgress(false);
            }

            @Override
            public void onError(Throwable e) {
                mView.showLineProgress(false);
                mView.showErrorText(e);
            }

            @Override
            public void onNext(Void unused) {
                fetchDevices();
            }
        });
        mSubscription.add(subscription);
    }

    @Override
    public void unRegisterDevice(String id) {
        mView.showLoadingDialog(false);
        Subscription subscription = DeviceModel.getInstance().unRegister(id).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Void>() {
            @Override
            public void onCompleted() {
                mView.dismissLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                mView.dismissLoadingDialog();
                mView.showErrorText(e);
            }

            @Override
            public void onNext(Void unused) {
                fetchDevices();
            }

        });
        mSubscription.add(subscription);
    }

}
