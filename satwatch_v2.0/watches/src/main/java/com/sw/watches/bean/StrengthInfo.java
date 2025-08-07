package com.sw.watches.bean;

public class StrengthInfo {

    private long date;
    private int lowTime;
    private int middleTime;
    private int highTime;

    public StrengthInfo(long date, int lowTime, int middleTime, int highTime){
        this.date = date;
        this.lowTime = lowTime;
        this.middleTime = middleTime;
        this.highTime = highTime;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getLowTime() {
        return lowTime;
    }

    public void setLowTime(int lowTime) {
        this.lowTime = lowTime;
    }

    public int getMiddleTime() {
        return middleTime;
    }

    public void setMiddleTime(int middleTime) {
        this.middleTime = middleTime;
    }

    public int getHighTime() {
        return highTime;
    }

    public void setHighTime(int highTime) {
        this.highTime = highTime;
    }
}
