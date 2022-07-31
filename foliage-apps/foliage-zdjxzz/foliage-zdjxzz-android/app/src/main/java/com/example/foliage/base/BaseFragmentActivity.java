package com.example.foliage.base;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.foliage.R;
import com.example.foliage.infrastructure.dialog.RippleLoadingDialog;
import com.example.foliage.infrastructure.utils.AppContextUtil;
import com.example.foliage.infrastructure.utils.ToastUtil;
import com.example.foliage.net.RxError;
import com.githang.statusbar.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.HttpException;

/**
 * Description：FragmentActivity基类
 * Created by liang.qfzc@gmail.com on 2018/8/3.
 */

public abstract class BaseFragmentActivity extends AppCompatActivity {

    private RippleLoadingDialog loadingDialog;

    private Unbinder unbinder;

    protected Context mContext;

    protected Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        mContext = this;
        setContentView(getLayoutResID());
        this.savedInstanceState = savedInstanceState;
        unbinder = ButterKnife.bind(this);
        AppContextUtil.pushActivity(this);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
        String actionBarTitle = getActionBarTitle();
        if (!TextUtils.isEmpty(actionBarTitle)) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getActionBarTitle());
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        initView();
        setListener();
        initData();
        if (hasBindEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
        unbinder.unbind();
        AppContextUtil.popActivity(this);
        if (hasBindEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected abstract int getLayoutResID();

    protected abstract String getActionBarTitle();

    protected void initView() {
    }

    protected void initData() {
    }

    protected void setListener() {
    }

    /**
     * 当前页面是否有EventBus。 默认为false
     *
     * @return
     */
    protected abstract boolean hasBindEventBus();

    /**
     * 显示加载数据进度条
     */
    public void showLoadingDialog(boolean cancelable) {
        showLoadingDialog(cancelable, "正在加载中...");
    }

    /**
     * 显示加载数据进度条
     */
    public void showLoadingDialog(boolean cancelable, String content) {
        if (loadingDialog == null) {
            loadingDialog = new RippleLoadingDialog(this);
        } else {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
        loadingDialog.setCancelable(cancelable);
        if (!TextUtils.isEmpty(content)) ;
        loadingDialog.title(content);
        loadingDialog.show();
    }

    /**
     * 取消正在显示的dialog
     */
    public void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * 接口回调error提示错误信息
     *
     * @param e
     */
    public void showErrorText(Throwable e) {
        if (e instanceof RxError) {
            RxError error = (RxError) e;
            ToastUtil.showTextViewPrompt(error.getMessage());
        } else if (e instanceof SocketTimeoutException) {
            ToastUtil.showTextViewPrompt("网络连接超时!");
        } else if (e instanceof ConnectException) {
            ToastUtil.showTextViewPrompt("网络连接异常!");
        } else if (e instanceof HttpException) {
            try {
                String message = ((HttpException) e).response().errorBody().string();
                ToastUtil.showTextViewPrompt(message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            ToastUtil.showTextViewPrompt(e.getMessage());
        }
    }

}
