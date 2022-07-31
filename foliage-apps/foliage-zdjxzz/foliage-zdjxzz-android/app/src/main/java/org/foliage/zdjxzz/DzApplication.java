package org.foliage.zdjxzz;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import org.foliage.zdjxzz.infrastructure.utils.AppContextUtil;
import org.foliage.zdjxzz.infrastructure.utils.TimeManager;

/**
 * Description：应用入口
 * Created by liang.qfzc@gmail.com on 2018/6/11.
 */
public class DzApplication extends Application {

    private static DzApplication instance;

    public static DzApplication getInstance() {
        if (null == instance) {
            instance = new DzApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppContextUtil.initApp(this);
        initModule();
    }

    public void initModule() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TimeManager.getInstance().initLoginTime();
            }
        }).start();
        SDKInitializer.setAgreePrivacy(this, true);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

}
