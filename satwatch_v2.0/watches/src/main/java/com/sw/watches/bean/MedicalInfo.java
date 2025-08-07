package com.sw.watches.bean;

public class MedicalInfo {
    public static final int MedicalPU4 = 4;

    public static final int MedicalPU6 = 6;

    public static final int MedicalPU8 = 8;

    public static final int MedicalPU12 = 12;

    public int MedicalStartHour;

    public int MedicalStartMin;

    public int MedicalEndHour;

    public int MedicalEndMin;

    public int MedicalPeriod;

    public boolean MedicalEnable;

    public MedicalInfo(int MedicalStartHour, int MedicalStartMin, int MedicalEndHour, int MedicalEndMin, int MedicalPeriod, boolean MedicalEnable) {
        setMedicalStartHour(MedicalStartHour);
        setMedicalStartMin(MedicalStartMin);
        setMedicalEndHour(MedicalEndHour);
        setMedicalEndMin(MedicalEndMin);
        setMedicalPeriod(MedicalPeriod);
        setMedicalEnable(MedicalEnable);
    }

    public int getMedicalStartHour() {
        return this.MedicalStartHour;
    }

    public void setMedicalStartHour(int MedicalStartHour) {
        this.MedicalStartHour = MedicalStartHour;
    }

    public int getMedicalStartMin() {
        return this.MedicalStartMin;
    }

    public void setMedicalStartMin(int MedicalStartMin) {
        this.MedicalStartMin = MedicalStartMin;
    }

    public int getMedicalEndHour() {
        return this.MedicalEndHour;
    }

    public void setMedicalEndHour(int MedicalEndHour) {
        this.MedicalEndHour = MedicalEndHour;
    }

    public int getMedicalEndMin() {
        return this.MedicalEndMin;
    }

    public void setMedicalEndMin(int MedicalEndMin) {
        this.MedicalEndMin = MedicalEndMin;
    }

    public int getMedicalPeriod() {
        return this.MedicalPeriod;
    }

    public void setMedicalPeriod(int MedicalPeriod) {
        this.MedicalPeriod = MedicalPeriod;
    }

    public boolean getMedicalEnable() {
        return this.MedicalEnable;
    }

    public void setMedicalEnable(boolean MedicalEnable) {
        this.MedicalEnable = MedicalEnable;
    }

    public String toString() {
        return "MedicalInfo{MedicalStartHour=" + this.MedicalStartHour + ", MedicalStartMin=" + this.MedicalStartMin + ", MedicalEndHour=" + this.MedicalEndHour + ", MedicalEndMin=" + this.MedicalEndMin + ", MedicalPeriod=" + this.MedicalPeriod + ", MedicalEnable=" + this.MedicalEnable + '}';
    }
}