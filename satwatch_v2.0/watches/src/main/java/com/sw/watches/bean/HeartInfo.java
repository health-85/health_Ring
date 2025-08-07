package com.sw.watches.bean;

public class HeartInfo {
    public int HeartInfoHR;

    public int HeartInfoSBP;

    public int HeartInfoDBP;

    public long day;

    public String time;

    public long heartTime;

    public HeartInfo(int HeartInfoHR, int HeartInfoSBP, int HeartInfoDBP) {
        setHeartInfoHR(HeartInfoHR);
        setHeartInfoSBP(HeartInfoSBP);
        setHeartInfoDBP(HeartInfoDBP);
    }

    public HeartInfo(int HeartInfoHR, int HeartInfoSBP, int HeartInfoDBP, long day) {
        setHeartInfoHR(HeartInfoHR);
        setHeartInfoSBP(HeartInfoSBP);
        setHeartInfoDBP(HeartInfoDBP);
        setDay(day);
    }

    public HeartInfo(int HeartInfoHR, int HeartInfoSBP, int HeartInfoDBP, String time) {
        setHeartInfoHR(HeartInfoHR);
        setHeartInfoSBP(HeartInfoSBP);
        setHeartInfoDBP(HeartInfoDBP);
        setTime(time);
    }

    public HeartInfo(int HeartInfoHR, int HeartInfoSBP, int HeartInfoDBP, long day, long heartTime) {
        setHeartInfoHR(HeartInfoHR);
        setHeartInfoSBP(HeartInfoSBP);
        setHeartInfoDBP(HeartInfoDBP);
        setDay(day);
        setHeartTime(heartTime);
    }

    public int getHeartInfoHR() {
        return this.HeartInfoHR;
    }

    public void setHeartInfoHR(int HeartInfoHR) {
        this.HeartInfoHR = HeartInfoHR;
    }

    public int getHeartInfoSBP() {
        return this.HeartInfoSBP;
    }

    public void setHeartInfoSBP(int HeartInfoSBP) {
        this.HeartInfoSBP = HeartInfoSBP;
    }

    public int getHeartInfoDBP() {
        return this.HeartInfoDBP;
    }

    public void setHeartInfoDBP(int HeartInfoDBP) {
        this.HeartInfoDBP = HeartInfoDBP;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(long heartTime) {
        this.heartTime = heartTime;
    }
}
