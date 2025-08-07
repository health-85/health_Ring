package com.sw.watches.bean;

public class DrinkInfo {
    public static final int DrinkPU1 = 1;

    public static final int DrinkPU2 = 2;

    public static final int DrinkPU3 = 3;

    public static final int DrinkPU4 = 4;

    public int DrinkStartHour;

    public int DrinkStartMin;

    public int DrinkEndHour;

    public int DrinkEndMin;

    public int DrinkPeriod;

    public boolean DrinkEnable;

    public DrinkInfo(int DrinkStartHour, int DrinkStartMin, int DrinkEndHour, int DrinkEndMin, int DrinkPeriod, boolean DrinkEnable) {
        setDrinkStartHour(DrinkStartHour);
        setDrinkStartMin(DrinkStartMin);
        setDrinkEndHour(DrinkEndHour);
        setDrinkEndMin(DrinkEndMin);
        setDrinkPeriod(DrinkPeriod);
        setDrinkEnable(DrinkEnable);
    }

    public int getDrinkStartHour() {
        return this.DrinkStartHour;
    }

    public void setDrinkStartHour(int DrinkStartHour) {
        this.DrinkStartHour = DrinkStartHour;
    }

    public int getDrinkStartMin() {
        return this.DrinkStartMin;
    }

    public void setDrinkStartMin(int DrinkStartMin) {
        this.DrinkStartMin = DrinkStartMin;
    }

    public int getDrinkEndHour() {
        return this.DrinkEndHour;
    }

    public void setDrinkEndHour(int DrinkEndHour) {
        this.DrinkEndHour = DrinkEndHour;
    }

    public int getDrinkEndMin() {
        return this.DrinkEndMin;
    }

    public void setDrinkEndMin(int DrinkEndMin) {
        this.DrinkEndMin = DrinkEndMin;
    }

    public int getDrinkPeriod() {
        return this.DrinkPeriod;
    }

    public void setDrinkPeriod(int DrinkPeriod) {
        this.DrinkPeriod = DrinkPeriod;
    }

    public boolean getDrinkEnable() {
        return this.DrinkEnable;
    }

    public void setDrinkEnable(boolean DrinkEnable) {
        this.DrinkEnable = DrinkEnable;
    }
}
