package com.healthy.rvigor.bean;

import java.util.List;

public class ECGPPGBean implements Comparable<ECGPPGBean>{

    public String date;
    public long time;
    public int heartRate;
    public List<Integer> ecgList;
    public List<Integer> ppgList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public List<Integer> getEcgList() {
        return ecgList;
    }

    public void setEcgList(List<Integer> ecgList) {
        this.ecgList = ecgList;
    }

    public List<Integer> getPpgList() {
        return ppgList;
    }

    public void setPpgList(List<Integer> ppgList) {
        this.ppgList = ppgList;
    }

    @Override
    public int compareTo(ECGPPGBean o) {
        if (this.time - o.time > 0){
            return -1;
        }
        return 1;
    }
}
