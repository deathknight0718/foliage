package com.example.foliage.base;

/**
 * Description :带有网络请求View的基类
 * Created by liang.qfzc@gmail.com on 2018/6/12.
 */
public interface IBaseExtraView<T> extends  IBaseView<T>{

    /**
     * 显示正在加载的dialog
     * @param cancelable 是不是可以取消
     */
    void showLoadingDialog(boolean cancelable);

    /**
     * 显示正在加载的dialog
     * @param cancelable 是不是可以取消
     * @param content 显示文案
     */
    void showLoadingDialog(boolean cancelable, String content);

    /**
     * 正在加载对话框消失
     */
    void dismissLoadingDialog();

    /**
     * 接口回调error提示错误信息
     * @param e 异常信息
     */
    void showErrorText(Throwable e);
}
