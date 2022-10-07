
package org.foliage.zdjxzz.infrastructure.permissions;

import android.Manifest;
import android.annotation.TargetApi;

/**
 * Description : 运行时权限常量 Created by wxh on 2016/3/10 16:14. Job number：138746
 * Phone ：15233620521 Email：wangxiaohui@syswin.com Person in charge : 王晓辉
 * Leader：王晓辉
 */
@TargetApi(19)
public interface PermissionsConstant {
    /**
     * 权限被允许
     */
    int GRANTED = 0;

    /**
     * 权限被拒绝
     */
    int DENIED = -1;

    /**
     * 权限没找到
     */
    int NOT_FOUND = -2;

    /**
     * 相机权限
     */
    String CAMERA = Manifest.permission.CAMERA;

    /**
     * 录音权限
     */
    String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;

    /**
     * 读取联系人
     */
    String READ_CONTACT = Manifest.permission.READ_CONTACTS;

    /**
     * 修改联系人
     */
    String WRITE_CONTACT = Manifest.permission.WRITE_CONTACTS;

    /**
     * 定位1
     */
    String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    /**
     * 定位2
     */
    String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    /**
     * 读取设备信息
     */
    String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

    /**
     * 打电话
     */
    String CALL_PHONE = Manifest.permission.CALL_PHONE;

    /**
     * 发短信
     */
    String SEND_SMS = Manifest.permission.SEND_SMS;

    /**
     * 读SD卡
     */
    String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    /**
     * 写SD卡
     */
    String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /**
     * 创建桌面快捷方式
     */

    String INSTALL_SHORTCUT = Manifest.permission.INSTALL_SHORTCUT;

    /**
     * 弹出系统浮层
     */
    String SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW;

    /**
     * 修改系统设置
     */
    String WRITE_SETTINGS = Manifest.permission.WRITE_SETTINGS;
}
