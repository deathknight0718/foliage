package com.example.foliage.page.main.presenter;

import com.example.foliage.page.main.contract.MainContract;
import com.example.foliage.page.main.entity.BannerEntity;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;

/**
 * Description：主界面presenter
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;

    private CompositeSubscription mSubscription;

    public MainPresenter(MainContract.View mView) {
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
    public void init() {
        ArrayList<BannerEntity> list = new ArrayList<>();
        BannerEntity bean1 = new BannerEntity();
        bean1.setName("中大设备助建巴基斯坦PKM高速公路");
        bean1.setUrl("http://www.zdjxzz.cn/uploadfile/2018/11/20/1542694533713294.jpg");
        list.add(bean1);
        BannerEntity bean2 = new BannerEntity();
        bean2.setName("传承匠心成就中国公路摊铺专家");
        bean2.setUrl("http://www.zdjxzz.cn/uploadfile/61821542694054.png");
        list.add(bean2);
        BannerEntity bean3 = new BannerEntity();
        bean3.setName("湖南中大机械助力成都天府国际机场");
        bean3.setUrl("http://www.zdjxzz.cn/uploadfile/2018/01/22/1516606274413707.jpg");
        list.add(bean3);
        mView.initBanner(list);

        mView.initGrid();
    }

}
