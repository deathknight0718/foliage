package com.example.foliage.infrastructure.eventbus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liang.qfzc@gmail.com on 2018/8/27.
 */

public class FilterEvent implements Parcelable {

    private String title;
    private String transactionType;
    private String startTime;
    private String endTime;
    private String month;
    private String timeType;
    private String startMoney;
    private String endMoney;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getStartMoney() {
        return startMoney;
    }

    public void setStartMoney(String startMoney) {
        this.startMoney = startMoney;
    }

    public String getEndMoney() {
        return endMoney;
    }

    public void setEndMoney(String endMoney) {
        this.endMoney = endMoney;
    }

    public FilterEvent() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.transactionType);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.month);
        dest.writeString(this.timeType);
        dest.writeString(this.startMoney);
        dest.writeString(this.endMoney);
    }

    protected FilterEvent(Parcel in) {
        this.title = in.readString();
        this.transactionType = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.month = in.readString();
        this.timeType = in.readString();
        this.startMoney = in.readString();
        this.endMoney = in.readString();
    }

    public static final Creator<FilterEvent> CREATOR = new Creator<FilterEvent>() {
        @Override
        public FilterEvent createFromParcel(Parcel source) {
            return new FilterEvent(source);
        }

        @Override
        public FilterEvent[] newArray(int size) {
            return new FilterEvent[size];
        }
    };
}
