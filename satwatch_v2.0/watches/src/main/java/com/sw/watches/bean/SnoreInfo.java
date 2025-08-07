package com.sw.watches.bean;

public class SnoreInfo {

    private String date;
    private int snoreLen;
    private int maxDbF;
    private int averageDb;
    private int minDb;
    private float snoreIndex;
    private int snoreFrequency;
    private int snoreNormal;
    private int snoreMild;
    private int snoreMiddle;
    private int snoreSerious;

    public SnoreInfo(String date, int snoreLen, int maxDb, int averageDb, int minDb, float snoreIndex, int snoreFrequency, int snoreNormal, int snoreMild, int snoreMiddle, int snoreSerious) {
        this.date = date;
        this.snoreLen = snoreLen;
        this.maxDbF = maxDb;
        this.averageDb = averageDb;
        this.minDb = minDb;
        this.snoreIndex = snoreIndex;
        this.snoreFrequency = snoreFrequency;
        this.snoreNormal = snoreNormal;
        this.snoreMild = snoreMild;
        this.snoreMiddle = snoreMiddle;
        this.snoreSerious = snoreSerious;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSnoreLen() {
        return snoreLen;
    }

    public void setSnoreLen(int snoreLen) {
        this.snoreLen = snoreLen;
    }

    public int getMaxDbF() {
        return maxDbF;
    }

    public void setMaxDbF(int maxDbF) {
        this.maxDbF = maxDbF;
    }

    public int getAverageDb() {
        return averageDb;
    }

    public void setAverageDb(int averageDb) {
        this.averageDb = averageDb;
    }

    public float getSnoreIndex() {
        return snoreIndex;
    }

    public void setSnoreIndex(float snoreIndex) {
        this.snoreIndex = snoreIndex;
    }

    public int getSnoreFrequency() {
        return snoreFrequency;
    }

    public void setSnoreFrequency(int snoreFrequency) {
        this.snoreFrequency = snoreFrequency;
    }

    public int getSnoreNormal() {
        return snoreNormal;
    }

    public void setSnoreNormal(int snoreNormal) {
        this.snoreNormal = snoreNormal;
    }

    public int getSnoreMild() {
        return snoreMild;
    }

    public void setSnoreMild(int snoreMild) {
        this.snoreMild = snoreMild;
    }

    public int getSnoreMiddle() {
        return snoreMiddle;
    }

    public void setSnoreMiddle(int snoreMiddle) {
        this.snoreMiddle = snoreMiddle;
    }

    public int getSnoreSerious() {
        return snoreSerious;
    }

    public void setSnoreSerious(int snoreSerious) {
        this.snoreSerious = snoreSerious;
    }

    public int getMinDb() {
        return minDb;
    }

    public void setMinDb(int minDb) {
        this.minDb = minDb;
    }
}
