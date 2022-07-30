package com.example.foliage.page.device.presenter;

import com.example.foliage.page.device.Model.DeviceModel;
import com.example.foliage.page.device.contract.DeviceContract;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Description：主界面presenter
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public class DeviceDetailPresenter implements DeviceContract.DetailPresenter {

    private DeviceContract.DetailView mView;

    private CompositeSubscription mSubscription;

    public DeviceDetailPresenter(DeviceContract.DetailView mView) {
        this.mView = mView;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroyPresenter() {
        if (mView != null)
            mView = null;
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    @Override
    public void fetchDeviceById(String id) {
        mView.showLoadingDialog(false);
        Subscription subscription = DeviceModel.getInstance().detailById(id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<>() {
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
                    public void onNext(JsonObject devices) {
                        mView.showDetailView(devices);
                    }
                });
        mSubscription.add(subscription);
    }

}
