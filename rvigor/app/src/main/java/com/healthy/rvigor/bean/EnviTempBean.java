package com.healthy.rvigor.bean;

import java.util.List;

public class EnviTempBean {

    public int lastTemp;              // 体温
    public String date;           // 时间
    public long enviTime;           // 时间
    public List<Integer> list;

    public int getLastTemp() {
        return lastTemp;
    }

    public void setLastTemp(int lastTemp) {
        this.lastTemp = lastTemp;
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

    public long getEnviTime() {
        return enviTime;
    }

    public void setEnviTime(long enviTime) {
        this.enviTime = enviTime;
    }
}
