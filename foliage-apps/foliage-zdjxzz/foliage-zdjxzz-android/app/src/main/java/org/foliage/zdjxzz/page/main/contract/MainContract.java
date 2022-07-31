package org.foliage.zdjxzz.page.main.contract;

import org.foliage.zdjxzz.base.IBaseExtraView;
import org.foliage.zdjxzz.base.IBasePresenter;
import org.foliage.zdjxzz.page.main.entity.BannerEntity;

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
