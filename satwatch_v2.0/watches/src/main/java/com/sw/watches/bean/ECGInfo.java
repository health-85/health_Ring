package com.sw.watches.bean;

import java.util.List;

public class ECGInfo {

    public List<ECGData> ecgList;
    public byte[] data;

    public ECGInfo(List<ECGData> ecgList) {
        this.ecgList = ecgList;
    }

    public List<ECGData> getEcgList() {
        return this.ecgList;
    }

    public void setEcgList(List<ECGData> ecgList) {
        this.ecgList = ecgList;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
