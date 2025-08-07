package com.sw.watches.bean;

import java.util.List;

public class PPGData {
    public List<Integer> dataList;

    public int code;

    public PPGDateTime ppgDateTime;

    public PPGData(List<Integer> dataList, int code) {
        this.dataList = dataList;
        this.code = code;
    }

    public PPGData() {}

    public PPGDateTime getPpgDateTime() {
        return this.ppgDateTime;
    }

    public void setPpgDateTime(PPGDateTime paramPPGDateTime) {
        this.ppgDateTime = paramPPGDateTime;
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

    public String toString() {
        return "PPGData{dataList=" + this.dataList + ", code=" + this.code + '}';
    }
}
