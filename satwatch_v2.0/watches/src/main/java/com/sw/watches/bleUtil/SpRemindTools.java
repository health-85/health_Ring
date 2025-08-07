package com.sw.watches.bleUtil;

import android.content.Context;
import android.content.SharedPreferences;

public class SpRemindTools {

    public static String WATCHES_REMIND_TOOLS  = "zjw_zhbracelet_remin_tools";;

    public Context mContext;

    public SpRemindTools(Context context) {
        mContext = context;
    }

    public SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(WATCHES_REMIND_TOOLS, 0);
    }

    /**
     * 保存吃药开始小时
     */
    public void putMedicalStartHour(int medical_start_hour) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("medical_start_hour", medical_start_hour);
        editor.commit();
    }

    public int getMedicalStartHour() {
        return getSharedPreferences().getInt("medical_start_hour", 8);
    }
    /**
     * 保存吃药开始分钟
     */
    public void putMedicalStartMin(int medical_start_min) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("medical_start_min", medical_start_min);
        editor.commit();
    }

    public int getMedicalStartMin() {
        return getSharedPreferences().getInt("medical_start_min", 0);
    }
    /**
     * 保存吃药结束小时
     */
    public void putMedicalEndHour(int medical_end_hour) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("medical_end_hour", medical_end_hour);
        editor.commit();
    }

    public int getMedicalEndHour() {
        return getSharedPreferences().getInt("medical_end_hour", 20);
    }
    /**
     * 保存吃药结束分钟
     */
    public void putMedicalEndMin(int medical_end_min) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("medical_end_min", medical_end_min);
        editor.commit();
    }

    public int getMedicalEndMin() {
        return getSharedPreferences().getInt("medical_end_min", 0);
    }
    /**
     * 保存吃药间隔
     */
    public void putMedicalPeriod(int medical_period) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("medical_period", medical_period);
        editor.commit();
    }

    public int getMedicalPeriod() {
        return getSharedPreferences().getInt("medical_period", 4);
    }
    /**
     * 是否开启吃药提醒
     */
    public void putMedicalEnable(boolean medical_enable) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean("medical_enable", medical_enable);
        editor.commit();
    }

    public boolean getMedicalEnable() {
        return getSharedPreferences().getBoolean("medical_enable", false);
    }
    /**
     * 保存久座开始小时
     */
    public void putSitStartHour(int sit_start_hour) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("sit_start_hour", sit_start_hour);
        editor.commit();
    }

    public int getSitStartHour() {
        return getSharedPreferences().getInt("sit_start_hour", 8);
    }
    /**
     * 保存久座开始分钟
     */
    public void putSitStartMin(int sit_start_min) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("sit_start_min", sit_start_min);
        editor.commit();
    }

    public int getSitStartMin() {
        return getSharedPreferences().getInt("sit_start_min", 0);
    }
    /**
     * 保存久座结束小时
     */
    public void putSitEndHour(int sit_end_hour) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("sit_end_hour", sit_end_hour);
        editor.commit();
    }

    public int getSitEndHour() {
        return getSharedPreferences().getInt("sit_end_hour", 20);
    }
    /**
     * 保存久座结束分钟
     */
    public void putSitEndMin(int sit_end_min) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("sit_end_min", sit_end_min);
        editor.commit();
    }

    public int getSitEndMin() {
        return getSharedPreferences().getInt("sit_end_min", 0);
    }
    /**
     * 保存久座间隔
     */
    public void putSitPeriod(int sit_period) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("sit_period", sit_period);
        editor.commit();
    }

    public int getSitPeriod() {
        return getSharedPreferences().getInt("sit_period", 4);
    }
    /**
     * 设置久座是否可用
     */
    public void putSitEnable(boolean sit_enable) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean("sit_enable", sit_enable);
        editor.commit();
    }

    public boolean getSitEnable() {
        return getSharedPreferences().getBoolean("sit_enable", false);
    }

    public void putDrinkStartHour(int drink_start_hour) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("drink_start_hour", drink_start_hour);
        editor.commit();
    }

    public int getDrinkStartHour() {
        return getSharedPreferences().getInt("drink_start_hour", 8);
    }

    public void putDrinkStartMin(int drink_start_min) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("drink_start_min", drink_start_min);
        editor.commit();
    }

    public int getDrinkStartMin() {
        return getSharedPreferences().getInt("drink_start_min", 0);
    }

    public void putDrinkEndHour(int drink_end_hour) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("drink_end_hour", drink_end_hour);
        editor.commit();
    }

    public int getDrinkEndHour() {
        return getSharedPreferences().getInt("drink_end_hour", 20);
    }

    public void putDrinkEndMin(int drink_end_min) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("drink_end_min", drink_end_min);
        editor.commit();
    }

    public int getDrinkEndMin() {
        return getSharedPreferences().getInt("drink_end_min", 0);
    }

    public void putDrinkPeriod(int drink_period) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("drink_period", drink_period);
        editor.commit();
    }

    public int getDrinkPeriod() {
        return getSharedPreferences().getInt("drink_period", 4);
    }

    public void putDrinkEnable(boolean drink_enable) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean("drink_enable", drink_enable);
        editor.commit();
    }

    public boolean getDrinkEnable() {
        return getSharedPreferences().getBoolean("drink_enable", false);
    }

    public void putMeedingYear(int meeding_year) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("meeding_year", meeding_year);
        editor.commit();
    }

    public int getMeedingYear() {
        return getSharedPreferences().getInt("meeding_year", 17);
    }

    public void putMeedingMonth(int meeding_month) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("meeding_month", meeding_month);
        editor.commit();
    }

    public int getMeedingMonth() {
        return getSharedPreferences().getInt("meeding_month", 10);
    }

    public void putMeedingDay(int meeding_day) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("meeding_day", meeding_day);
        editor.commit();
    }

    public int getMeedingDay() {
        return getSharedPreferences().getInt("meeding_day", 7);
    }

    public void putMeedingHour(int meeding_hour) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("meeding_hour", meeding_hour);
        editor.commit();
    }

    public int getMeedingHour() {
        return getSharedPreferences().getInt("meeding_hour", 10);
    }

    public void putMeedingMin(int meeding_min) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("meeding_min", meeding_min);
        editor.commit();
    }

    public int getMeedingMin() {
        return getSharedPreferences().getInt("meeding_min", 20);
    }

    public void putMeedingEnable(boolean meeding_enable) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean("meeding_enable", meeding_enable);
        editor.commit();
    }

    public boolean getMeedingEnable() {
        return getSharedPreferences().getBoolean("meeding_enable", false);
    }
}