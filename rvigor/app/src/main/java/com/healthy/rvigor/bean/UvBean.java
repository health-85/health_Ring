package com.healthy.rvigor.bean;

import java.util.List;

public class UvBean {

    public float lastUv;             
    public String date;           // 时间
    public List<Integer> list;

    public float getLastUv() {
        return lastUv;
    }

    public void setLastUv(float lastUv) {
        this.lastUv = lastUv;
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
