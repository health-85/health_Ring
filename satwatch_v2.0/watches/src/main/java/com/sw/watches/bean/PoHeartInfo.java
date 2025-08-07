package com.sw.watches.bean;


import java.util.List;

public class PoHeartInfo {

    /**
     * 是否是1分钟心率
     */
    private int TimeGap;

    public String PoHeartDate;

    public List<Integer> PoHeartData;

    public PoHeartInfo(String PoHeartDate, List<Integer> PoHeartData,int TimeGap) {
        this.PoHeartDate = PoHeartDate;
        this.PoHeartData = PoHeartData;
        this.TimeGap = TimeGap;
    }

    public PoHeartInfo(String PoHeartDate, List<Integer> PoHeartData) {
        this.PoHeartDate = PoHeartDate;
        this.PoHeartData = PoHeartData;
    }

    public PoHeartInfo() {}

    public String getPoHeartDate() {
        return this.PoHeartDate;
    }

    public void setPoHeartDate(String PoHeartDate) {
        this.PoHeartDate = PoHeartDate;
    }

    public List<Integer> getPoHeartData() {
        return this.PoHeartData;
    }

    public void setPoHeartData(List<Integer> PoHeartData) {
        this.PoHeartData = PoHeartData;
    }

    public int getHrTimeGap() {
        return TimeGap;
    }

    public void setOneMinRate(int TimeGap) {
        this.TimeGap = TimeGap;
    }
}
