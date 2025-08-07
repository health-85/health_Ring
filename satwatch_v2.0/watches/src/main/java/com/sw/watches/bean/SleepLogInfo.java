package com.sw.watches.bean;

import java.util.List;

public class SleepLogInfo {

    private String date;
    private List<Integer> list;

    public SleepLogInfo(String date, List<Integer> list){
        this.date = date;
        this.list = list;
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
