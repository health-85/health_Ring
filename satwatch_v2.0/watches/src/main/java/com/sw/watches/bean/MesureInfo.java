package com.sw.watches.bean;

public class MesureInfo {
    public int MesureInfoHR;

    public int MesureInfoSBP;

    public int MesureInfoDBP;

    public MesureInfo(int MesureInfoHR, int MesureInfoSBP, int MesureInfoDBP) {
        setMesureInfoHR(MesureInfoHR);
        setMesureInfoSBP(MesureInfoSBP);
        setMesureInfoDBP(MesureInfoDBP);
    }

    public MesureInfo() {}

    public int getMesureInfoHR() {
        return this.MesureInfoHR;
    }

    public void setMesureInfoHR(int MesureInfoHR) {
        this.MesureInfoHR = MesureInfoHR;
    }

    public int getMesureInfoSBP() {
        return this.MesureInfoSBP;
    }

    public void setMesureInfoSBP(int MesureInfoSBP) {
        this.MesureInfoSBP = MesureInfoSBP;
    }

    public int getMesureInfoDBP() {
        return this.MesureInfoDBP;
    }

    public void setMesureInfoDBP(int MesureInfoDBP) {
        this.MesureInfoDBP = MesureInfoDBP;
    }
}