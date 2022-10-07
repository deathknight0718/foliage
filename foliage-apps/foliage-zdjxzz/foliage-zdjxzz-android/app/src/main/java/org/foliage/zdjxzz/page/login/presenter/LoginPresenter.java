package org.foliage.zdjxzz.page.login.presenter;

import android.text.TextUtils;

import org.foliage.zdjxzz.infrastructure.cache.CacheManage;
import org.foliage.zdjxzz.infrastructure.cache.CacheModel;
import org.foliage.zdjxzz.infrastructure.utils.IntentTool;
import org.foliage.zdjxzz.infrastructure.utils.ToastUtil;
import org.foliage.zdjxzz.page.login.contract.LoginContract;
import org.foliage.zdjxzz.page.login.model.LoginModel;
import org.foliage.zdjxzz.page.main.view.MainActivity;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Description：登陆present
 * Created by  liang.qfzc@gmail.com on 2018/6/12.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;

    private CompositeSubscription mSubscription;

    public LoginPresenter(LoginContract.View mView) {
        this.mView = mView;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void login(String userName, String password) {
        if (TextUtils.isEmpty(userName)) {
            ToastUtil.showTextViewPromptShort("手机号码不能为空");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtil.showTextViewPromptShort("密码不能为空");
            return;
        }
        mView.showLoadingDialog(true, "登录中...");
        Subscription subscription = LoginModel.getInstance().login(userName, password)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        mView.dismissLoadingDialog();
                        System.out.println("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissLoadingDialog();
                        mView.showErrorText(e);
                    }

                    @Override
                    public void onNext(String basic) {
                        CacheManage.getInstance().saveCache(CacheModel.LOGIN_KEY, basic);
                        IntentTool.startActivity(mView.getContext(), MainActivity.class,true);
                    }
                });
        mSubscription.add(subscription);
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
}
