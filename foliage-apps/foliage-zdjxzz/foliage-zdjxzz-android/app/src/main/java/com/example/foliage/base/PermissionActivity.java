
package com.example.foliage.base;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foliage.R;
import com.example.foliage.infrastructure.permissions.PermissionsMgr;
import com.example.foliage.infrastructure.permissions.PermissionsResultAction;

import java.util.Arrays;
import java.util.List;

/**
 * Description : 主要是为了需要权限的可继承此类
 */
public abstract class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private final String TAG = this.getClass().getSimpleName();

    /**
     * 当权限被授权
     */
    public void onPermissionGranted(List<String> permissions) {
    }

    /**
     * 当权限被拒绝
     *
     * @param permissions 申请的权限
     */
    public void onPermissionDenied(List<String> permissions) {
    }

    /**
     * 是否有某项权限
     */
    public boolean hasPermission(String... permission) {
        return PermissionsMgr.getInstance().hasAllPermissions(this, permission);
    }

    /**
     * 校验是否有某项权限，没有返回是哪种权限
     */
    public String checkPermission(String... permission) {
        return PermissionsMgr.getInstance().checkPermissions(this, permission);
    }

    /**
     * 申请权限
     *
     * @param permissions 需要的权限
     */
    public void requestPermissions(String... permissions) {
        if (!PermissionsMgr.getInstance().hasAllPermissions(this, permissions)) {
            PermissionsMgr.getInstance().requestPermissionsIfNecessaryForResult(this, permissions,
                    new PermissionsResultAction() {

                        @Override
                        public void onGranted(List<String> perms) {
                            Log.d(TAG, "Permission is Granted:" + perms);
                            onPermissionGranted(perms);
                        }

                        @Override
                        public void onDenied(List<String> perms) {
                            Log.d(TAG, "Permission is Denied" + perms);
                            onPermissionDenied(perms);
                        }
                    });
        } else {
            onPermissionGranted(Arrays.asList(permissions));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsMgr.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
