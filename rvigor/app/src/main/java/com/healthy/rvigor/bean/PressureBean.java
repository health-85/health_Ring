package com.healthy.rvigor.bean;

import java.util.List;

public class PressureBean {

    public int lastPressure;              // 最新压力
    public long pressureTime;
    public String date;           // 时间
    public List<Integer> list;

    public int getLastPressure() {
        return lastPressure;
    }

    public void setLastPressure(int lastPressure) {
        this.lastPressure = lastPressure;
    }

    public long getPressureTime() {
        return pressureTime;
    }

    public void setPressureTime(long pressureTime) {
        this.pressureTime = pressureTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }
}
