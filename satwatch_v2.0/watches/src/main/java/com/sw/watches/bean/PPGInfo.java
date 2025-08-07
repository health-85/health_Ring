package com.sw.watches.bean;

import java.util.List;

public class PPGInfo {

    public byte[] data;
    public List<PPGData> ppgList;

    public PPGInfo(List<PPGData> ppgList) {
        this.ppgList = ppgList;
    }

    public List<PPGData> getPpgList() {
        return this.ppgList;
    }

    public void setPpgList(List<PPGData> ppgList) {
        this.ppgList = ppgList;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
