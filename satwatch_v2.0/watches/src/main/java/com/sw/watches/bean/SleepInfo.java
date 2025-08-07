package com.sw.watches.bean;


import java.util.List;

public class SleepInfo {
    public String SleepDate;

    public int SleepTotalTime;

    public int SleepDeepTime;

    public int SleepLightTime;

    public int SleepRemTime;

    public int SleepStayupTime;

    public int SleepWakingNumber;

    private int SleepFallTime;

    public List<SleepData> SleepData;

    public int TotalTime;

    public SleepInfo(String SleepDate, int SleepTotalTime, int SleepDeepTime, int SleepLightTime, int SleepStayupTime, int SleepWakingNumber, int SleepFallTime, int SleepRemTime, List<SleepData> SleepData, int TotalTime) {
        setSleepDate(SleepDate);
        setSleepTotalTime(SleepTotalTime);
        setSleepDeepTime(SleepDeepTime);
        setSleepLightTime(SleepLightTime);
        setSleepStayupTime(SleepStayupTime);
        setSleepWakingNumber(SleepWakingNumber);
        setSleepFallTime(SleepFallTime);
        setSleepData(SleepData);
        setTotalTime(TotalTime);
        setSleepRemTime(SleepRemTime);
    }

    public String getSleepDate() {
        return this.SleepDate;
    }

    public void setSleepDate(String SleepDate) {
        this.SleepDate = SleepDate;
    }

    public int getSleepTotalTime() {
        return this.SleepTotalTime;
    }

    public void setSleepTotalTime(int SleepTotalTime) {
        this.SleepTotalTime = SleepTotalTime;
    }

    public int getSleepDeepTime() {
        return this.SleepDeepTime;
    }

    public void setSleepDeepTime(int SleepDeepTime) {
        this.SleepDeepTime = SleepDeepTime;
    }

    public int getSleepLightTime() {
        return this.SleepLightTime;
    }

    public void setSleepLightTime(int SleepLightTime) {
        this.SleepLightTime = SleepLightTime;
    }

    public int getSleepStayupTime() {
        return this.SleepStayupTime;
    }

    public void setSleepStayupTime(int SleepStayupTime) {
        this.SleepStayupTime = SleepStayupTime;
    }

    public int getSleepWakingNumber() {
        return this.SleepWakingNumber;
    }

    public void setSleepWakingNumber(int SleepWakingNumber) {
        this.SleepWakingNumber = SleepWakingNumber;
    }

    public List<SleepData> getSleepData() {
        return this.SleepData;
    }

    public void setSleepData(List<SleepData> SleepData) {
        this.SleepData = SleepData;
    }

    public int getTotalTime() {
        return this.TotalTime;
    }

    public void setTotalTime(int TotalTime) {
        this.TotalTime = TotalTime;
    }

    public int getSleepFallTime() {
        return SleepFallTime;
    }

    public void setSleepFallTime(int sleepFallTime) {
        SleepFallTime = sleepFallTime;
    }

    public int getSleepRemTime() {
        return SleepRemTime;
    }

    public void setSleepRemTime(int sleepRemTime) {
        SleepRemTime = sleepRemTime;
    }
}