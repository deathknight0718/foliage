package com.example.foliage.infrastructure.eventbus;

/**
 * Description：登录失效
 * Created by liang.qfzc@gmail.com on 2018/6/11.
 */
public class OfflineEvent extends BaseEvent {

    public OfflineEvent(String values) {
        setValues(values);
    }
}
