package com.sw.watches.bean;

import java.util.List;

public class HrvInfo {

    public String hrvDate;

    public List<Integer> hrvList;

    public HrvInfo(String hrvDate, List<Integer> hrvList){
        this.hrvDate = hrvDate;
        this.hrvList = hrvList;
    }

    public String getHrvDate() {
        return hrvDate;
    }

    public void setHrvDate(String hrvDate) {
        this.hrvDate = hrvDate;
    }

    public List<Integer> getHrvList() {
        return hrvList;
    }

    public void setHrvList(List<Integer> hrvList) {
        this.hrvList = hrvList;
    }
}
