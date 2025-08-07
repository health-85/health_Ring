package com.sw.watches.bean;

import java.util.List;

/**
 * 压力数据
 */
public class PressureInfo {

    private int TimeGap;

    private String pressureDate;

    private List<Integer> pressureList;

    public PressureInfo(int TimeGap,String pressureDate, List<Integer> pressureList){
        this.TimeGap=TimeGap;
        this.pressureDate = pressureDate;
        this.pressureList = pressureList;
    }

    public int getPressureTimeGap() {
        return TimeGap;
    }

    public String getPressureDate() {
        return pressureDate;
    }

    public void setPressureDate(String pressureDate) {
        this.pressureDate = pressureDate;
    }

    public List<Integer> getPressureList() {
        return pressureList;
    }

    public void setPressureList(List<Integer> pressureList) {
        this.pressureList = pressureList;
    }
}
