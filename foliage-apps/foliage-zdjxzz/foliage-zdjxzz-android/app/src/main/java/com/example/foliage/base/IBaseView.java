
package com.example.foliage.base;

import android.content.Context;

/**
 * Description :所有View的基类，所有的View继承此View
 * Created by liang.qfzc@gmail.com on 2018/6/12.
 */
public interface IBaseView<T> {
    /**
     * 给View设置Presenter
     * @param presenter
     */
    void setPresenter(T presenter);

    /**
     * 获取Context方便P层使用
     * @return
     */
    Context getContext();
}
