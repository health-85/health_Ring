package com.sw.watches.bleUtil;

import android.content.Context;
import android.content.SharedPreferences;

public class SpDeviceTools {

    public static String WATCHES_DEVICE_TOOLS = "zjw_zhbracelet_device_tools";;

    public Context mContext;

    public SpDeviceTools(Context context) {
        mContext = context;
    }

    private SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    public SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(WATCHES_DEVICE_TOOLS, 0);
    }

    /**
     * 保存Mac地址
     * @param s
     */
    public void putBleMac(String s) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("ble_mac", s);
        editor.commit();
    }

    /**
     * 获取Mac地址
     * @return
     */
    public String getBleMac() {
        return getSharedPreferences().getString("ble_mac", "");
    }

    /**
     * 保存Mac地址
     * @param s
     */
    public void putBleName(String s) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("ble_name", s);
        editor.commit();
    }

    /**
     * 获取Mac地址
     * @return
     */
    public String getBleName() {
        return getSharedPreferences().getString("ble_name", "");
    }


    /**
     * 保存电话提醒
     * @param b
     */
    public void putRemindCall(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_call", b);
        editor.commit();
    }

    /**
     * 获取电话提醒
     * @return
     */
    public boolean getRemindCall() {
        return getSharedPreferences().getBoolean("reminde_call", false);
    }

    /**
     * 保存消息提醒
     * @param reminde_mms
     */
    public void putRemindMms(boolean reminde_mms) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_mms", reminde_mms);
        editor.commit();
    }

    /**
     * 获取消息提醒
     * @return
     */
    public boolean getRemindMms() {
        return getSharedPreferences().getBoolean("reminde_mms", false);
    }

    /**
     * 保存QQ提醒
     */
    public void putRemindQQ(boolean reminde_qq) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_qq", reminde_qq);
        editor.commit();
    }

    /**
     * 获取QQ提醒
     */
    public boolean getRemindQQ() {
        return getSharedPreferences().getBoolean("reminde_qq", false);
    }

    /**
     * 保存微信提醒
     * @param reminde_wx
     */
    public void puyRemindWx(boolean reminde_wx) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_wx", reminde_wx);
        editor.commit();
    }

    /**
     * 获取微信提醒
     * @return
     */
    public boolean getRemindWx() {
        return getSharedPreferences().getBoolean("reminde_wx", false);
    }

    /**
     * 保存Skype提醒
     * @param reminde_skype
     */
    public void putRemindSkype(boolean reminde_skype) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_skype", reminde_skype);
        editor.commit();
    }

    /**
     * 获取Skype提醒
     * @return
     */
    public boolean getRemindSkype() {
        return getSharedPreferences().getBoolean("reminde_skype", false);
    }

    /**
     * 保存Face_book提醒
     * @param reminde_facebook
     */
    public void putRemindFacebook(boolean reminde_facebook) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_facebook", reminde_facebook);
        editor.commit();
    }

    /**
     * 获取FaceBook提醒
     * @return
     */
    public boolean getRemindFacebook() {
        return getSharedPreferences().getBoolean("reminde_facebook", false);
    }

    /**
     * 保存WhatApp提醒
     * @param b
     */
    public void putRemindWhatsapp(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_whatsapp", b);
        editor.commit();
    }

    /**
     * 获取RemindWhatsApp提醒
     * @return
     */
    public boolean getRemindWhatsapp() {
        return getSharedPreferences().getBoolean("reminde_whatsapp", false);
    }

    /**
     * 保存LinedIn提醒
     */
    public void putRemindLinkedin(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_linkedin", b);
        editor.commit();
    }

    /**
     * 获取LinedIn提醒
     */
    public boolean getRemindLinkedin() {
        return getSharedPreferences().getBoolean("reminde_linkedin", false);
    }
    /**
     * 保存Twitter提醒
     */
    public void putRemindTwitter(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_twitter", b);
        editor.commit();
    }
    /**
     * 获取Twitter提醒
     */
    public boolean getRemindTwitter() {
        return getSharedPreferences().getBoolean("reminde_twitter", false);
    }
    /**
     * 保存Viber提醒
     */
    public void putRemindViber(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_viber", b);
        editor.commit();
    }
    /**
     * 获取Viber提醒
     */
    public boolean getRemindViber() {
        return getSharedPreferences().getBoolean("reminde_viber", false);
    }
    /**
     * 保存Line提醒
     */
    public void putRemindLine(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_line", b);
        editor.commit();
    }
    /**
     * 获取Line提醒
     */
    public boolean getRemindLine() {
        return getSharedPreferences().getBoolean("reminde_line", false);
    }

    public void putRemindIosmail(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_iosmail", b);
        editor.commit();
    }

    public boolean getRemindIosmail() {
        return getSharedPreferences().getBoolean("reminde_iosmain", false);
    }
    /**
     * 保存Snapchat提醒
     */
    public void putRemindSnapchat(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_snapchat", b);
        editor.commit();
    }
    /**
     * 获取Snapchat提醒
     */
    public boolean getRemindSnapchat() {
        return getSharedPreferences().getBoolean("reminde_snapchat", false);
    }
    /**
     * 保存Instagram提醒
     */
    public void putRemindInstagram(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_instagram", b);
        editor.commit();
    }
    /**
     * 获取Instagram提醒
     */
    public boolean getRemindInstagram() {
        return getSharedPreferences().getBoolean("reminde_instagram", false);
    }
    /**
     * 保存Outlook提醒
     */
    public void putRemindOutlook(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_outlook", b);
        editor.commit();
    }
    /**
     * 获取Outlook提醒
     */
    public boolean getRemindOutlook() {
        return getSharedPreferences().getBoolean("reminde_outlook", false);
    }
    /**
     * 保存Gmail提醒
     */
    public void putRemindGmail(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("reminde_gmail", b);
        editor.commit();
    }
    /**
     * 获取Gmail提醒
     */
    public boolean getRemindGmail() {
        return getSharedPreferences().getBoolean("reminde_gmail", false);
    }
    /**
     * 保存抬腕提醒
     */
    public void putTaiwan(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("taiwan", b);
        editor.commit();
    }
    /**
     * 获取抬腕提醒
     */
    public boolean getTaiwan() {
        return getSharedPreferences().getBoolean("taiwan", true);
    }
    /**
     * 保存转腕提醒
     */
    public void putZhuanwan(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("zhuanwan", b);
        editor.commit();
    }
    /**
     * 获取转腕提醒
     */
    public boolean getZhuanwan() {
        return getSharedPreferences().getBoolean("zhuanwan", false);
    }

    /**
     * 保存打鼾提醒
     */
    public void putSnoreMonitor(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("snore_monitor", b);
        editor.commit();
    }
    /**
     * 获取打鼾提醒
     */
    public boolean getSnoreMonitor() {
        return getSharedPreferences().getBoolean("snore_monitor", false);
    }

    /**
     * 获取是否设置整点心率
     * @return
     */
    public boolean getHighHeartRemind() {
        return getSharedPreferences().getBoolean("high_heart_remind", false);
    }

    /**
     * 设置整点心率
     * @param b
     */
    public void putHighHeartRemind(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("high_heart_remind", b);
        editor.commit();
    }

    /**
     * 获取是否设置整点心率
     * @return
     */
    public boolean getPointMeasurementHeart() {
        return getSharedPreferences().getBoolean("point_measurement_heart", false);
    }

    /**
     * 设置整点心率
     * @param b
     */
    public void putPointMeasurementHeart(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("point_measurement_heart", b);
        editor.commit();
    }

    /**
     * 获取是否设置连续心率
     * @return
     */
    public boolean getWointMeasurementHeart() {
        return getSharedPreferences().getBoolean("woint_measurement_heart", false);
    }



    /**
     * 设置连续心率
     */
    public void putWointMeasurementHeart(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("woint_measurement_heart", b);
        editor.commit();
    }

    /**
     * 获取语言
     * @return
     */
    public int getLanguage() {
        return getSharedPreferences().getInt("language", 1);
    }

    /**
     * 设置语言
     * @param language
     */
    public void putLanguage(int language) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("language", language);
        editor.commit();
    }

    /**
     * 获取是否设置未打扰
     * @return
     */
    public boolean getNotDisturb() {
        return getSharedPreferences().getBoolean("not_disturb", false);
    }

    /**
     * 设置未打扰
     * @param b
     */
    public void putNotDisturb(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("not_disturb", b);
        editor.commit();
    }

    public void putColockType(boolean colock_type) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("colock_type", colock_type);
        editor.commit();
    }

    public boolean getColockType() {
        return getSharedPreferences().getBoolean("colock_type", true);
    }
    /**
     * 设置单位
     * @param device_unit
     */
    public void putDeviceUnit(boolean device_unit) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("device_unit", device_unit);
        editor.commit();
    }

    /**
     * 获取单位
     * @return
     */
    public boolean getDeviceUnit() {
        return getSharedPreferences().getBoolean("device_unit", true);
    }

    public void putIsSupportEcg(int is_support_ecg) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("is_support_ecg", is_support_ecg);
        editor.commit();
    }

    public void putIsSupportPpg(int is_support_ppg) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("is_support_ppg", is_support_ppg);
        editor.commit();
    }

    public void putStepAlgorithmType(int step_algorithm_type) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("step_algorithm_type", step_algorithm_type);
        editor.commit();
    }

    public int getStepAlgorithmType() {
        return getSharedPreferences().getInt("step_algorithm_type", 0);
    }

    public void putNotifaceType(int notiface_type) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("notiface_type", notiface_type);
        editor.commit();
    }

    public int getNotifaceType() {
        return getSharedPreferences().getInt("notiface_type", 0);
    }

    /**
     * 设置身高
     * @param user_height
     */
    public void putUserHeight(int user_height) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("user_height", user_height);
        editor.commit();
    }

    public int getUserHeight() {
        return getSharedPreferences().getInt("user_height", 170);
    }
    /**
     * 设置体重
     */
    public void putUserWeight(int user_weight) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("user_weight", user_weight);
        editor.commit();
    }

    public int getUserWeight() {
        return getSharedPreferences().getInt("user_weight", 65);
    }
    /**
     * 设置性别
     */
    public void putUserSex(boolean var1) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("user_sex", var1);
        editor.commit();
    }

    public boolean getUserSex() {
        return getSharedPreferences().getBoolean("user_sex", true);
    }
    /**
     * 设置年龄
     */
    public void putUserAge(int user_age) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("user_age", user_age);
        editor.commit();
    }

    public int getUserAge() {
        return getSharedPreferences().getInt("user_age", 24);
    }
    /**
     * 设置佩戴方式
     */
    public void putWearWay(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("wear_way", b);
        editor.commit();
    }

    public boolean getWearWay() {
        return getSharedPreferences().getBoolean("wear_way", true);
    }

    public void putUserCalibrationHr(int user_calibration_hr) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("user_calibration_hr", user_calibration_hr);
        editor.commit();
    }

    public int getUserCalibrationHr() {
        return getSharedPreferences().getInt("user_calibration_hr", 70);
    }

    public void putUserCalibrationSbp(int user_calibration_sbp) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("user_calibration_sbp", user_calibration_sbp);
        editor.commit();
    }

    public int getUserCalibrationSbp() {
        return getSharedPreferences().getInt("user_calibration_sbp", 120);
    }

    public void putUserCalibrationDbp(int user_calibration_dbp) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("user_calibration_dbp", user_calibration_dbp);
        editor.commit();
    }

    public int getUserCalibrationDbp() {
        return getSharedPreferences().getInt("user_calibration_dbp", 70);
    }

    public int getUserStpe() {
        return getSharedPreferences().getInt("user_stpe", 70);
    }

    public void putUserStpe(int user_stpe) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("user_stpe", user_stpe);
        editor.commit();
    }

    public void putControlPhoto(boolean b) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("control_photo", b);
        editor.commit();
    }

    public boolean getControlPhoto() {
        return getSharedPreferences().getBoolean("control_photo", false);
    }

    public void putAlarmData(String alarm_data) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("alarm_data", alarm_data);
        editor.commit();
    }

    public String getAlarmData() {
        return getSharedPreferences().getString("alarm_data", "");
    }

    public void puthNumber(int h_number) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("h_number", h_number);
        editor.commit();
    }

    public int gethNumber() {
        return getSharedPreferences().getInt("h_number", 0);
    }

    public void putLightTime(int light_time) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("light_time", light_time);
        editor.commit();
    }

    public int getLightTime() {
        return getSharedPreferences().getInt("light_time", 0);
    }

    public void putBrightness(int brightness) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("brightness", brightness);
        editor.commit();
    }

    public int getBrightness() {
        return getSharedPreferences().getInt("brightness", 0);
    }

    public void putUiType(int ui_type) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("ui_type", ui_type);
        editor.commit();
    }

    public int getUiType() {
        return getSharedPreferences().getInt("ui_type", 0);
    }

    public void putSkin(int skin) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("skin", skin);
        editor.commit();
    }

    public int getSkin() {
        return getSharedPreferences().getInt("skin", 0);
    }
}
