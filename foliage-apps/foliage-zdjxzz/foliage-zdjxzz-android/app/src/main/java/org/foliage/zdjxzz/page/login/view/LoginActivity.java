package org.foliage.zdjxzz.page.login.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


import com.google.android.material.textfield.TextInputEditText;

import org.foliage.zdjxzz.R;
import org.foliage.zdjxzz.base.BaseFragmentActivity;
import org.foliage.zdjxzz.page.login.contract.LoginContract;
import org.foliage.zdjxzz.page.login.presenter.LoginPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description：登陆页
 */
public class LoginActivity extends BaseFragmentActivity implements LoginContract.View {

    @BindView(R.id.et_user_name)
    TextInputEditText mUserName;
    @BindView(R.id.et_password)
    TextInputEditText mPassword;

    private LoginContract.Presenter mPresenter;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected String getActionBarTitle() {
        return null;
    }

    @Override
    protected void initView() {
        super.initView();
        mPresenter = new LoginPresenter(this);
    }

    @OnClick({R.id.login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                String userName = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                mPresenter.login(userName, password);
                break;
            default:
                break;
        }
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    protected boolean hasBindEventBus() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroyPresenter();
            mPresenter = null;
        }
    }
}
