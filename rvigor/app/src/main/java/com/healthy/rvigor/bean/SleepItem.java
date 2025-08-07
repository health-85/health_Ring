package com.healthy.rvigor.bean;

import android.graphics.Rect;

public class SleepItem implements Comparable<SleepItem> {

    public static int DEEP_SLEEP_TYPE = 1;   //1 深睡
    public static int LIGHT_SLEEP_TYPE = 2;  //2 浅睡
    public static int WAKE_SLEEP_TYPE = 3;   //3 清醒
    public static int FALL_SLEEP_TYPE = 4;   //4 入睡
    public static int AUYELEN_SLEEP_TYPE = 5;  //5 熬夜

    public static int REM_SLEEP_TYPE = 7;  //7 REM
    public static int END_SLEEP_TYPE = 6;  //6 结束睡眠

    //起始时间
    public long startTime;
    //结束时间
    public long endTime;
    //起始时间
    public String startTimeS;
    //结束时间
    public String endTimeS;
    //颜色
    public int color;

    public int lightColor;
    //睡眠状态
    public int sleepType; //1 深睡 //2 浅睡 //3 清醒 //4 入睡 //5 熬夜
    //当前绘制的地方
    public Rect currentDrawRect = new Rect(0, 0, 0, 0);



    public SleepItem(long startTime, long endTime, int color, int sleep_type) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
        this.sleepType = sleep_type;
    }

    public SleepItem(long startTime, long endTime, int color, int sleep_type, String startTimeS, String endTimeS) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTimeS = startTimeS;
        this.endTimeS = endTimeS;
        this.color = color;
        this.sleepType = sleep_type;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getSleepType() {
        return sleepType;
    }

    public void setSleepType(int sleepType) {
        this.sleepType = sleepType;
    }

    public Rect getCurrentDrawRect() {
        return currentDrawRect;
    }

    public void setCurrentDrawRect(Rect currentDrawRect) {
        this.currentDrawRect = currentDrawRect;
    }

    @Override
    public int compareTo(SleepItem o) {
        return this.getStartTime() - o.getStartTime() > 0 ? 1 : -1;
    }
}
