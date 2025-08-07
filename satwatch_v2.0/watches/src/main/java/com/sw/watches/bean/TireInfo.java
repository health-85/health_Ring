package com.sw.watches.bean;

import java.util.List;

/**
 * 疲劳数据
 */
public class TireInfo {

    private int TimeGap;
    private String date;
    private List<Integer> list;

    public TireInfo(int TimeGap,String date, List<Integer> list){
        this.TimeGap=TimeGap;
        this.date = date;
        this.list = list;
    }

    public int getTireTimeGap() {
        return TimeGap;
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
