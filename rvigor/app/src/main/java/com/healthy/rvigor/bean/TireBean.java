package com.healthy.rvigor.bean;

import java.util.List;

public class TireBean {

    public int lastTire;              // 最新疲劳
    public long tireTime;
    public String date;           // 时间
    public List<Integer> list;
    public List<Integer> intList;

    public int getLastTire() {
        return lastTire;
    }

    public void setLastTire(int lastTire) {
        this.lastTire = lastTire;
    }

    public long getTireTime() {
        return tireTime;
    }

    public void setTireTime(long tireTime) {
        this.tireTime = tireTime;
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

    public List<Integer> getIntList() {
        return intList;
    }

    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }
}
