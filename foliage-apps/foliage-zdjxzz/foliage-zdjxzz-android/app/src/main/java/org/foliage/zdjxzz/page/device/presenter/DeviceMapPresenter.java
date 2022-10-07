package org.foliage.zdjxzz.page.device.presenter;

import org.foliage.zdjxzz.page.device.model.DeviceModel;
import org.foliage.zdjxzz.page.device.contract.DeviceContract;
import org.foliage.zdjxzz.page.device.dto.GeographicDTO;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public class DeviceMapPresenter implements DeviceContract.MapGeographicPresenter {

    private DeviceContract.MapGeographicView mView;

    private CompositeSubscription mSubscription;

    public DeviceMapPresenter(DeviceContract.MapGeographicView mView) {
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
    public void geographic(String id) {
        mView.showLoadingDialog(false);
        Subscription subscription = DeviceModel.getInstance().geographic(id).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<>() {
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
            public void onNext(GeographicDTO geographic) {
                mView.showMapMaker(geographic);
            }
        });
        mSubscription.add(subscription);
    }

}