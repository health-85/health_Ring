package com.sw.watches.bean;

import java.util.List;

public class ECGData {
    public List<Integer> dataList;

    public int code;

    public ECGDateTime ecgDateTime;

    public int statue;

    public ECGData(List<Integer> dataList, int code) {
        this.dataList = dataList;
        this.code = code;
    }

    public ECGData() {}

    public ECGDateTime getEcgDateTime() {
        return this.ecgDateTime;
    }

    public void setEcgDateTime(ECGDateTime ecgDateTime) {
        this.ecgDateTime = ecgDateTime;
    }

    public int getStatue() {
        return this.statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public List<Integer> getDataList() {
        return this.dataList;
    }

    public void setDataList(List<Integer> dataList) {
        this.dataList = dataList;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}