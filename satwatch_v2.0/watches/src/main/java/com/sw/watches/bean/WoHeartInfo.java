package com.sw.watches.bean;

import java.util.List;

public class WoHeartInfo {
    public String WoHeartDate;

    public int WoHeartSleepMax;

    public int WoHeartSleepMin;

    public int WoHeartSleepAvg;

    public int WoHeartDayMax;

    public int WoHeartDayMin;

    public int WoHeartDayAvg;

    public int WoHeartRecent;

    public List<Integer> WoHeartData;

    public WoHeartInfo(String woHeartDate, int woHeartSleepMax, int woHeartSleepMin, int woHeartSleepAvg, int woHeartDayMax, int woHeartDayMin,
                       int woHeartDayAvg, int woHeartRecent, List<Integer> woHeartData) {
        setWoHeartDate(woHeartDate);
        setWoHeartSleepMax(woHeartSleepMax);
        setWoHeartSleepMin(woHeartSleepMin);
        setWoHeartSleepAvg(woHeartSleepAvg);
        setWoHeartDayMax(woHeartDayMax);
        setWoHeartDayMin(woHeartDayMin);
        setWoHeartDayAvg(woHeartDayAvg);
        setWoHeartRecent(woHeartRecent);
        setWoHeartData(woHeartData);
    }

    public String getWoHeartDate() {
        return this.WoHeartDate;
    }

    public void setWoHeartDate(String WoHeartDate) {
        this.WoHeartDate = WoHeartDate;
    }

    public int getWoHeartSleepMax() {
        return this.WoHeartSleepMax;
    }

    public void setWoHeartSleepMax(int WoHeartSleepMax) {
        this.WoHeartSleepMax = WoHeartSleepMax;
    }

    public int getWoHeartSleepMin() {
        return this.WoHeartSleepMin;
    }

    public void setWoHeartSleepMin(int WoHeartSleepMin) {
        this.WoHeartSleepMin = WoHeartSleepMin;
    }

    public int getWoHeartSleepAvg() {
        return this.WoHeartSleepAvg;
    }

    public void setWoHeartSleepAvg(int WoHeartSleepAvg) {
        this.WoHeartSleepAvg = WoHeartSleepAvg;
    }

    public int getWoHeartDayMax() {
        return this.WoHeartDayMax;
    }

    public void setWoHeartDayMax(int WoHeartDayMax) {
        this.WoHeartDayMax = WoHeartDayMax;
    }

    public int getWoHeartDayMin() {
        return this.WoHeartDayMin;
    }

    public void setWoHeartDayMin(int WoHeartDayMin) {
        this.WoHeartDayMin = WoHeartDayMin;
    }

    public int getWoHeartDayAvg() {
        return this.WoHeartDayAvg;
    }

    public void setWoHeartDayAvg(int WoHeartDayAvg) {
        this.WoHeartDayAvg = WoHeartDayAvg;
    }

    public int getWoHeartRecent() {
        return this.WoHeartRecent;
    }

    public void setWoHeartRecent(int WoHeartRecent) {
        this.WoHeartRecent = WoHeartRecent;
    }

    public List<Integer> getWoHeartData() {
        return this.WoHeartData;
    }

    public void setWoHeartData(List<Integer> WoHeartData) {
        this.WoHeartData = WoHeartData;
    }
}