package org.foliage.zdjxzz.base;

/**
 * Description :Presenter的基类
 * Created by liang.qfzc@gmail.com on 2018/6/12.
 */
public interface IBasePresenter<T> {

    /**
     * 清理Presenter加载的数据
     */
    void onDestroyPresenter();

}
