package com.sw.watches.bean;

public class SitInfo {
    public static final int SitPU1 = 1;

    public static final int SitPU2 = 2;

    public static final int SitPU3 = 3;

    public static final int SitPU4 = 4;

    public int SitStartHour;

    public int SitStartMin;

    public int SitEndHour;

    public int SitEndMin;

    public int SitPeriod;

    public boolean SitEnable;

    public SitInfo(int SitStartHour, int SitStartMin, int SitEndHour, int SitEndMin, int SitPeriod, boolean SitEnable) {
        setSitStartHour(SitStartHour);
        setSitStartMin(SitStartMin);
        setSitEndHour(SitEndHour);
        setSitEndMin(SitEndMin);
        setSitPeriod(SitPeriod);
        setSitEnable(SitEnable);
    }

    public int getSitStartHour() {
        return this.SitStartHour;
    }

    public void setSitStartHour(int SitStartHour) {
        this.SitStartHour = SitStartHour;
    }

    public int getSitStartMin() {
        return this.SitStartMin;
    }

    public void setSitStartMin(int SitStartMin) {
        this.SitStartMin = SitStartMin;
    }

    public int getSitEndHour() {
        return this.SitEndHour;
    }

    public void setSitEndHour(int SitEndHour) {
        this.SitEndHour = SitEndHour;
    }

    public int getSitEndMin() {
        return this.SitEndMin;
    }

    public void setSitEndMin(int SitEndMin) {
        this.SitEndMin = SitEndMin;
    }

    public int getSitPeriod() {
        return this.SitPeriod;
    }

    public void setSitPeriod(int SitPeriod) {
        this.SitPeriod = SitPeriod;
    }

    public boolean isSitEnable() {
        return this.SitEnable;
    }

    public void setSitEnable(boolean SitEnable) {
        this.SitEnable = SitEnable;
    }
}