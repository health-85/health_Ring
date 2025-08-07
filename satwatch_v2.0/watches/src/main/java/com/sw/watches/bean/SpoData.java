package com.sw.watches.bean;


public class SpoData {
    public String spoTime;

    public int spoValue;

    public int heartValue;

    public SpoData(String spoTime, int spoValue, int heartValue) {
        this.spoTime = spoTime;
        this.spoValue = spoValue;
        this.heartValue = heartValue;
    }

    public String getSpoTime() {
        return this.spoTime;
    }

    public void setSpoTime(String spoTime) {
        this.spoTime = spoTime;
    }

    public int getSpoValue() {
        return this.spoValue;
    }

    public void setSpoValue(int spoValue) {
        this.spoValue = spoValue;
    }

    public int getHeartValue() {
        return this.heartValue;
    }

    public void setHeartValue(int heartValue) {
        this.heartValue = heartValue;
    }
}
