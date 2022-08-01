package org.foliage.zdjxzz.page.login.contract;


import org.foliage.zdjxzz.base.IBaseExtraView;
import org.foliage.zdjxzz.base.IBasePresenter;

/**
 * Description：登陆contract
 * Created by  liang.qfzc@gmail.com on 2018/6/12.
 */

public interface LoginContract {

    interface View extends IBaseExtraView<Presenter> {

    }

    interface Presenter extends IBasePresenter {

        /**
         * 登录
         */
        void login(String userName, String password);

    }
}
