package com.example.foliage.page.main.contract;

import com.example.foliage.base.IBaseExtraView;
import com.example.foliage.base.IBasePresenter;
import com.example.foliage.page.main.entity.BannerEntity;

import java.util.List;

/**
 * Description：主界面 Contract
 * Created by liang.qfzc@gmail.com on 2018/7/9.
 */

public interface MainContract {

    interface View extends IBaseExtraView<Presenter> {

        void initBanner(List<BannerEntity> retDatBean);

        void initGrid();

    }

    interface Presenter extends IBasePresenter {

        void init();

    }

}
