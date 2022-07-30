package com.example.foliage.infrastructure.eventbus;

/**
 * Description：eventbus基类
 * Created by liang.qfzc@gmail.com on 2018/6/11.
 */
public class BaseEvent<T> {

    private String eventType;
    private T values;

    public T getValues() {
        return values;
    }

    public void setValues(T values) {
        this.values = values;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
