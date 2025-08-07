package com.sw.watches.bluetooth;

import android.content.Context;
import android.util.Log;

import com.sw.watches.bean.AbnormalHeartInfo;
import com.sw.watches.bean.AbnormalHeartListInfo;
import com.sw.watches.bean.AlarmInfo;
import com.sw.watches.bean.BreatheInfo;
import com.sw.watches.bean.EmotionInfo;
import com.sw.watches.bean.HeartListInfo;
import com.sw.watches.bean.ECGData;
import com.sw.watches.bean.ECGDateTime;
import com.sw.watches.bean.ECGInfo;
import com.sw.watches.bean.EnviTempInfo;
import com.sw.watches.bean.HeartInfo;
import com.sw.watches.bean.HeatInfo;
import com.sw.watches.bean.HrvInfo;
import com.sw.watches.bean.MotionInfo;
import com.sw.watches.bean.PPGData;
import com.sw.watches.bean.PPGDateTime;
import com.sw.watches.bean.PPGInfo;
import com.sw.watches.bean.PoHeartInfo;
import com.sw.watches.bean.PressureInfo;
import com.sw.watches.bean.SiestaInfo;
import com.sw.watches.bean.SleepData;
import com.sw.watches.bean.SleepInfo;
import com.sw.watches.bean.SleepLogInfo;
import com.sw.watches.bean.SleepOxInfo;
import com.sw.watches.bean.SnoreInfo;
import com.sw.watches.bean.SpoData;
import com.sw.watches.bean.SpoInfo;
import com.sw.watches.bean.StrengthInfo;
import com.sw.watches.bean.SwitchInfo;
import com.sw.watches.bean.SymptomInfo;
import com.sw.watches.bean.SymptomInfo2;
import com.sw.watches.bean.SymptomListInfo;
import com.sw.watches.bean.TireInfo;
import com.sw.watches.bean.UvInfo;
import com.sw.watches.bean.WatchSaveInfo;
import com.sw.watches.bean.WoHeartInfo;
import com.sw.watches.bleUtil.ByteToStringUtil;
import com.sw.watches.bleUtil.SpDeviceTools;
import com.sw.watches.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SIATCommand {

    private static final String TAG = "SIATCommand";
    //AB标签头
    public static final byte SIGN_AB_HEAD = (byte) 0xab;
    public static final String SIGN_AB_HEAD_TAG = "ab";
    //AA标签头
    public static final byte SIGN_AA_HEAD = (byte) 0xaa;
    public static final String SIGN_AA_HEAD_TAG = "aa";
    //AC标签头
    public static final byte SIGN_AC_HEAD = (byte) 0xac;
    public static final String SIGN_AC_HEAD_TAG = "ac";
    //睡眠时间
    public static final int SLEEP_TIME = 80;

    //命令开头
    public static byte SIGN_HEAD = SIGN_AC_HEAD;
    public static String SIGN_HEAD_TAG = SIGN_AC_HEAD_TAG;

    /**
     * 字符串转byte
     *
     * @param c
     * @return
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 同步数据
     *
     * @return
     */
    public static byte[] getSyncTimeCom() {
        byte sign = SIGN_HEAD;

        byte[] byteArray = new byte[17];
        byteArray[0] = sign;
        byteArray[3] = 9;
        byteArray[8] = 1;
        byteArray[10] = 1;
        byteArray[12] = 4;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String str = simpleDateFormat.format(date);
        char[] chatArray = str.toCharArray();
        int year = charToByte(chatArray[2]) * 10 + charToByte(chatArray[3]);
        int month = charToByte(chatArray[4]) * 10 + charToByte(chatArray[5]);
        int day = charToByte(chatArray[6]) * 10 + charToByte(chatArray[7]);
        int hour = charToByte(chatArray[8]) * 10 + charToByte(chatArray[9]);
        int min = charToByte(chatArray[10]) * 10 + charToByte(chatArray[11]);
        int second = charToByte(chatArray[12]) * 10 + charToByte(chatArray[13]);
        byteArray[13] = (byte) (year << 2 | month >> 2);
        byteArray[14] = (byte) ((month & 0x3) << 6 | day << 1 | hour >> 4);
        byteArray[15] = (byte) ((hour & 0xF) << 4 | min >> 2);
        byteArray[16] = (byte) ((min & 0x3) << 6 | second);

        return byteArray;
    }

    public static byte[] getSaveAlarmDataCom(List<AlarmInfo> alarmInfos) {
        byte sign = SIGN_HEAD;
        byte[] alarmByteArray = new byte[alarmInfos.size() * 5 + 13];

        alarmByteArray[0] = sign;
        alarmByteArray[3] = (byte) (alarmInfos.size() * 5 + 5);
        alarmByteArray[8] = 1;
        alarmByteArray[10] = 2;
        alarmByteArray[12] = (byte) (alarmInfos.size() * 5);

        for (byte i = 0; i < alarmInfos.size(); i = (byte) (i + 1)) {

            AlarmInfo info = alarmInfos.get(i);

            int alarmId = info.getAlarmId();
            int alarmMin = info.getAlarmMin();
            int alarmHour = info.getAlarmHour();
            int alarmData = info.getAlarmData();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
            calendar.set(Calendar.MINUTE, alarmMin);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(calendar.getTimeInMillis());
            String str = simpleDateFormat.format(date);
            char[] chatArray = str.toCharArray();
            int l = charToByte(chatArray[2]) * 10 + charToByte(chatArray[3]);
            int j = charToByte(chatArray[4]) * 10 + charToByte(chatArray[5]);
            int k = charToByte(chatArray[6]) * 10 + charToByte(chatArray[7]);
            int m = charToByte(chatArray[8]) * 10 + charToByte(chatArray[9]);
            int n = charToByte(chatArray[10]) * 10 + charToByte(chatArray[11]);
            int p = charToByte(chatArray[12]) * 10 + charToByte(chatArray[13]);
            alarmByteArray[13] = (byte) (l << 2 | j >> 2);
            alarmByteArray[14] = (byte) ((j & 0x3) << 6 | k << 1 | m >> 4);
            alarmByteArray[15] = (byte) ((m & 0xF) << 4 | n >> 2);
            alarmByteArray[16] = (byte) ((n & 0x3) << 6 | alarmId << 3);
            alarmByteArray[17] = (byte) alarmData;

        }

        return alarmByteArray;
    }

    public static byte[] getSaveRepeatAlarmDataCom(List<AlarmInfo> alarmInfos) {
        byte sign = SIGN_HEAD;
        byte[] alarmByteArray = new byte[alarmInfos.size() * 5 + 13];

        alarmByteArray[0] = sign;
        alarmByteArray[3] = (byte) (alarmInfos.size() * 5 + 5);
        alarmByteArray[8] = 1;
        alarmByteArray[10] = 2;
        alarmByteArray[12] = (byte) (alarmInfos.size() * 5);

        for (byte i = 0; i < alarmInfos.size(); i = (byte) (i + 1)) {

            AlarmInfo info = alarmInfos.get(i);

            int alarmId = info.getAlarmId();
            int alarmMin = info.getAlarmMin();
            int alarmHour = info.getAlarmHour();
            int alarmData = info.getAlarmData();

            String currentHourMin = new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));
            int hour = Integer.parseInt(currentHourMin.split(":")[0]);
            int min = Integer.parseInt(currentHourMin.split(":")[1]);

            SimpleDateFormat yearDayFormat = new SimpleDateFormat("yyyyMMdd");
            char[] arrayOfChar = ((alarmHour != hour || alarmMin >= min) && alarmHour >= hour ?
                    yearDayFormat.format(new Date(System.currentTimeMillis())) :
                    yearDayFormat.format(new Date(System.currentTimeMillis() + 86400000L))).toCharArray();

            int tempData = charToByte(arrayOfChar[2]) * 10 + charToByte(arrayOfChar[3]) - 1;
            int var10 = charToByte(arrayOfChar[6]) * 10 + charToByte(arrayOfChar[7]);

            tempData = i * 5;
            hour = tempData + 13;
            alarmByteArray[hour] = (byte) ((charToByte(arrayOfChar[2]) * 10 + charToByte(arrayOfChar[3]) - 1) << 2 | min >> 2);

            hour = tempData + 14;
            alarmByteArray[hour] = (byte) ((charToByte(arrayOfChar[4]) * 10 + charToByte(arrayOfChar[5]) & 3) << 6 | var10 << 1 | alarmHour >> 4);

            var10 = tempData + 15;
            alarmByteArray[var10] = (byte) ((alarmHour & 15) << 4 | alarmMin >> 2);

            var10 = tempData + 16;
            alarmByteArray[var10] = (byte) ((alarmMin & 3) << 6 | alarmId << 3);

            var10 = tempData + 17;
            alarmByteArray[var10] = (byte) alarmData;

        }

        Log.i(TAG, " array " + ByteToStringUtil.ByteToString(alarmByteArray));

        return alarmByteArray;
    }

    public static byte[] getSaveAlarmDataCom(AlarmInfo info) {
        byte sign = SIGN_HEAD;
        byte[] alarmByte = new byte[5 + 13];

        alarmByte[0] = sign;
        alarmByte[3] = (byte) (5 + 5);
        alarmByte[8] = 1;
        alarmByte[10] = 2;
        alarmByte[12] = (byte) (5);

        int alarmId = info.getAlarmId();
        int alarmMin = info.getAlarmMin();
        int alarmHour = info.getAlarmHour();
        int alarmData = info.getAlarmData();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, alarmMin);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(calendar.getTimeInMillis());
        String str = simpleDateFormat.format(date);
        char[] chatArray = str.toCharArray();
        int l = charToByte(chatArray[2]) * 10 + charToByte(chatArray[3]);
        int j = charToByte(chatArray[4]) * 10 + charToByte(chatArray[5]);
        int k = charToByte(chatArray[6]) * 10 + charToByte(chatArray[7]);
        int m = charToByte(chatArray[8]) * 10 + charToByte(chatArray[9]);
        int n = charToByte(chatArray[10]) * 10 + charToByte(chatArray[11]);
        int p = charToByte(chatArray[12]) * 10 + charToByte(chatArray[13]);
        alarmByte[13] = (byte) (l << 2 | j >> 2);
        alarmByte[14] = (byte) ((j & 0x3) << 6 | k << 1 | m >> 4);
        alarmByte[15] = (byte) ((m & 0xF) << 4 | n >> 2);
        alarmByte[16] = (byte) ((n & 0x3) << 6 | alarmId << 3);
        alarmByte[17] = (byte) alarmData;

        LogUtil.i(TAG, " array " + ByteToStringUtil.ByteToString(alarmByte));

        return alarmByte;
    }

    public static byte[] getStepNumberCom(int h_number) {
        byte sign = SIGN_HEAD;
        byte[] var1;
        byte[] var10000 = var1 = new byte[17];
        var1[0] = sign;
        var1[3] = 8;
        var1[8] = 1;
        var1[10] = 3;
        var1[12] = 3;
        var1[13] = (byte) (h_number >> 24);
        var1[14] = (byte) (h_number >> 16);
        var1[15] = (byte) (h_number >> 8);
        var10000[16] = (byte) h_number;
        return var10000;
    }

    public static byte[] getUserInfoCom(int sex, int age, int userHeight, int userWeight) {
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[17];
        bytes[0] = sign;
        bytes[3] = 8;
        bytes[8] = 1;
        bytes[10] = 4;
        bytes[12] = 4;
        bytes[13] = (byte) (sex << 7 | age);
        bytes[14] = (byte) userHeight;
        bytes[15] = (byte) userWeight;
        return bytes;
    }

    public static byte[] getRestoreFactoryCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 6, 0, 0};
    }

    public static byte[] getSitInfoCom(int startHour, int startMin, int endHour, int endMin, int period, boolean bool) {
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[17];
        bytes[0] = sign;
        bytes[3] = 9;
        bytes[8] = 1;
        bytes[10] = 7;
        bytes[12] = 4;
        bytes[13] = (byte) ((bool ? 1 : 0) << 7 | period << 4 | startHour >> 1);
        bytes[14] = (byte) ((startHour & 0x1) << 7 | startMin << 1 | endHour >> 4);
        bytes[15] = (byte) ((endHour & 0xF) << 4 | endMin >> 2);
        bytes[16] = (byte) ((endMin & 0x3) << 6);

//        byte[] bytes = new byte[17];
//        bytes[0] = sign;
//        bytes[3] = 9;
//        bytes[8] = 1;
//        bytes[10] = 7;
//        bytes[12] = 4;
//        bytes[13] = (byte) ((bool ? 1 : 0) << 7 | startHour >> 1);
//        bytes[14] = (byte) ((startHour & 0x1) << 7 | startMin << 1 | endHour >> 4);
//        bytes[15] = (byte) ((endHour & 0xF) << 4 | endMin >> 2);
//        bytes[16] = (byte) ((endMin & 0x3) << 6 | period);
        return bytes;
    }

    public static byte[] getUnitCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 8, 0, 1, (byte) b};
    }

    public static byte[] getTimeFormatCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 9, 0, 1, (byte) b};
    }

    public static byte[] getTaiWanCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 10, 0, 1, (byte) b};
    }

    public static byte[] getPhoneCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 14, 0, 1, (byte) b};
    }

    public static byte[] getSmsCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 15, 0, 1, (byte) b};
    }

    public static byte[] getQQCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 16, 0, 1, (byte) b};
    }

    public static byte[] getWeiXinCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 17, 0, 1, (byte) b};
    }

    public static byte[] getSkypeCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 18, 0, 1, (byte) b};
    }

    public static byte[] getWhatsappCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 19, 0, 1, (byte) b};
    }

    public static byte[] getFacebookCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 20, 0, 1, (byte) b};
    }

    public static byte[] getLinkedlnCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 36, 0, 1, (byte) b};
    }

    public static byte[] getTwitterCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 37, 0, 1, (byte) b};
    }

    public static byte[] getViberCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 38, 0, 1, (byte) b};
    }

    public static byte[] getLineCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 40, 0, 1, (byte) b};
    }

    public static byte[] getMailCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 64, 0, 1, (byte) b};
    }

    public static byte[] getOutlookCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 65, 0, 1, (byte) b};
    }

    public static byte[] getInstagramCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 66, 0, 1, (byte) b};
    }

    public static byte[] getSnapchatCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 67, 0, 1, (byte) b};
    }

    public static byte[] getGmailCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 68, 0, 1, (byte) b};
    }

    public static byte[] getZhuanWanCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 11, 0, 1, (byte) b};
    }

    /**
     * 设置整点心率
     *
     * @param b
     */
    public static byte[] getPoHeartCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 13, 0, 1, (byte) b};
    }

    /**
     * 打鼾监测
     *
     * @param b
     * @return
     */
    public static byte[] getSnoreMonitorCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 11, 0, 1, (byte) b};
    }

    /**
     * 心率过高提醒
     *
     * @param b
     * @return
     */
    public static byte[] getHighHeartRemindCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 13, 0, 1, (byte) b};
    }

    public static byte[] getMedicalInfoCom(int startHour, int startMin, int endHour, int endMin, int period, boolean bool) {
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[17];
        bytes[0] = sign;
        bytes[3] = 9;
        bytes[8] = 1;
        bytes[10] = 21;
        bytes[12] = 4;
        bytes[13] = (byte) ((bool ? 1 : 0) << 7 | startHour >> 1);
        bytes[14] = (byte) ((startHour & 0x1) << 7 | startMin << 1 | endHour >> 4);
        bytes[15] = (byte) ((endHour & 0xF) << 4 | endMin >> 2);
        bytes[16] = (byte) ((endMin & 0x3) << 6 | period);
        return bytes;
    }

    public static byte[] getDrinkInfoCom(int startHour, int startMin, int endHour, int endMin, int drinkPeriod, boolean bool) {
        byte sign = SIGN_HEAD;
        --drinkPeriod;
        byte[] bytes = new byte[17];
        bytes[0] = sign;
        bytes[3] = 9;
        bytes[8] = 1;
        bytes[10] = 22;
        bytes[12] = 4;
        bytes[13] = (byte) ((bool ? 1 : 0) << 7 | drinkPeriod + 1 << 4 | startHour >> 1);
        bytes[14] = (byte) ((startHour & 1) << 7 | startMin << 1 | endHour >> 4);
        bytes[15] = (byte) ((endHour & 0xF) << 4 | endMin >> 2);
        bytes[16] = (byte) ((endMin & 3) << 6);
        return bytes;
    }

    public static byte[] getMeetingInfoCom(int year, int month, int day, int hour, int min, boolean bool) {
        byte sign = SIGN_HEAD;
        byte[] arrayOfByte = new byte[18];
        arrayOfByte[0] = sign;
        arrayOfByte[3] = 10;
        arrayOfByte[8] = 1;
        arrayOfByte[10] = 23;
        arrayOfByte[12] = 5;
        arrayOfByte[13] = (byte) (year << 2 | month >> 2);
        arrayOfByte[14] = (byte) ((month & 0x3) << 6 | day << 1 | hour >> 4);
        arrayOfByte[15] = (byte) ((hour & 0xF) << 4 | min >> 2);
        arrayOfByte[16] = (byte) ((min & 0x3) << 6);
        arrayOfByte[17] = (byte) ((bool ? 1 : 0) << 7);
        return arrayOfByte;
    }

    /**
     * 查找设备
     */
    public static byte[] getFindDeviceCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 24, 0, 0};
    }

    /**
     * 设置采样间隔时间
     */
    public static byte[] getCollectGapTime(int gaptime) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign,0,0, 8, 0 ,0 ,0, 0 ,1 ,0 ,0x5E, 0, 3, (byte) 0xFF, (byte) (gaptime>>8), (byte) (gaptime&0x00FF)};
    }

    /**
     * 主动测量心率数据
     *
     * @return
     */
    public static byte[] getOpenMeasurementCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 6, 0, 0, 0, 0, 1, 0, 25, 0, 1, 1};
    }

    /**
     * 关闭测量心率数据
     *
     * @return
     */
    public static byte[] getCloseMeasurementCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 6, 0, 0, 0, 0, 1, 0, 25, 0, 1, 0};
    }

    public static byte[] getMeasureInfoCom(int infoHR, int infoSBP, int infoDBP) {
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[16];
        bytes[0] = sign;
        bytes[3] = 8;
        bytes[8] = 1;
        bytes[10] = 26;
        bytes[11] = 0;
        bytes[12] = 3;
        bytes[13] = (byte) infoHR;
        bytes[14] = (byte) infoSBP;
        bytes[15] = (byte) infoDBP;
        return bytes;
    }

    public static byte[] getLanguagenCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 7, 0, 0, 0, 0, 1, 0, 27, 0, 2, 0, (byte) b};
    }

    public static byte[] getDeviceInfoCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 28, 0, 0};
    }

    public static byte[] getRemindCom(int colockType, int deviceUnit, int taiWan, int zhuanWan, int measurementHeart, int notDisturb) {
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[14];

        bytes[0] = sign;
        bytes[3] = 5;
        bytes[8] = 1;
        bytes[10] = 30;
        bytes[12] = 1;

        int temp = 0;
        if (colockType == 1) {
            temp = 128;
        }
        if (deviceUnit == 1) {
            temp |= 64;
        }
        if (taiWan == 1) {
            temp |= 32;
        }
        if (zhuanWan == 1) {
            temp |= 16;
        }
        if (measurementHeart == 1) {
            temp |= 8;
        }
        if (notDisturb == 1) {
            temp |= 4;
        }
        bytes[13] = (byte) temp;
        return bytes;
    }

    public static byte[] getUserCalibrationCom(int user_calibration_sbp, int user_calibration_hr) {
        byte sign = SIGN_HEAD;
        if (user_calibration_sbp > 250) {
            user_calibration_sbp = 250;
        } else if (user_calibration_sbp <= 0) {
            user_calibration_sbp = 0;
        }

        if (user_calibration_hr > 250) {
            user_calibration_hr = 250;
        } else if (user_calibration_hr <= 0) {
            user_calibration_hr = 0;
        }

        byte[] byteData = new byte[15];
        byteData[0] = sign;
        byteData[3] = 5;
        byteData[8] = 1;
        byteData[10] = 31;
        byteData[12] = 2;
        byteData[13] = (byte) user_calibration_sbp;
        byteData[14] = (byte) user_calibration_hr;
        return byteData;
    }

    /**
     * 设置勿扰模式
     *
     * @param i
     * @return
     */
    public static byte[] getNotDisturbCom(int i) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 10, 0, 1, (byte) i};
    }

    public static byte[] h() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 33, 0, 0};
    }

    public static byte[] getHardwareStatueCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 34, 0, 0};
    }

    public static byte[] getWoHeartCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 41, 0, 1, (byte) b};
    }

    public static byte[] getCameraCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 48, 0, 1, (byte) b};
    }

    public static byte[] getResponePhoneCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 49, 0, 0};
    }

    public static byte[] getPhoneStateCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 6, 0, 0, 0, 0, 1, 0, 49, 0, 1, 1};
    }

    public static byte[] getImageInfoCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 51, 0, 0};
    }

    public static byte[] getSkinCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 52, 0, 1, (byte) b};
    }

    public static byte[] getUITypeCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 53, 0, 1, (byte) b};
    }

    public static byte[] getBrightnessCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 54, 0, 1, (byte) b};
    }

    public static byte[] getBrightScreenTimeCom(int b) {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, 55, 0, 1, (byte) b};
    }

    public static byte[] getCloseDeviceCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, -128, 0, 0};
    }

    public static byte[] getDeviceFullShowCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 5, 0, 0, 0, 0, 1, 0, -127, 0, 0};
    }

    public static byte[] getUpdateDeviceCom() {
        byte sign = SIGN_HEAD;
//        return new byte[]{sign, 0, 0, 6, 0, 0, 0, 0, 1, 0, -126, 0, 0, 0};
        return new byte[]{sign, 0, 0, 6, 0, 0, 0, 0, 1, 0, -126, 0, 1, 0};
    }

    public static byte[] getCompletePreparationCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 6, 0, 0, 0, 0, 1, 0, -126, 0, 1, 2};
    }

    public static byte[] getPreparationFailCancelUpgradeCom() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 6, 0, 0, 0, 0, 1, 0, -126, 0, 1, 4};
    }

    /**
     * 设置手表语言为中文
     *
     * @return
     */
    public static byte[] getChinaLocalCommand() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 7, 0, 0, 0, 0, 1, 0, 27, 0, 2, 0, 1};
    }

    /**
     * 获取提醒命令
     *
     * @param msg     内容
     * @param context
     * @param id      ID
     * @return
     */
    public static byte[] getNotifyCom(String msg, Context context, int id) {
        byte sign = SIGN_HEAD;
        int notifaceType = new SpDeviceTools(context).getNotifaceType();
        byte[] bytes = (notifaceType == 1 ? ParseWatchesData.parseNotifyMsg(msg) : ParseWatchesData.parseUnNotifyMsg(msg)).getBytes();
        int len = bytes.length + 14;
        byte[] resultByte = new byte[len];

        resultByte[0] = sign;
        resultByte[3] = (byte) (bytes.length + 5 + 1);
        resultByte[8] = 2;
        resultByte[10] = (byte) id;
        resultByte[12] = (byte) (bytes.length + 1);
        resultByte[13] = (byte) bytes.length;

        int i;
        for (i = 0; i < bytes.length; ++i) {
            resultByte[i + 14] = bytes[i];
        }

        String str = "";
        for (i = 0; i < len; ++i) {
            String s = Integer.toHexString(resultByte[i]);
            if (s.length() > 2) {
                s = s.substring(s.length() - 2);
            } else if (s.length() < 2) {
                s = "0" + s;
            }
            str = str + s;
        }
        return resultByte;
    }

    //运动数据
    public static MotionInfo byteToMotionInfo(byte[] data, Context context) {
        if (data == null || data.length < 17) return null;
        SpDeviceTools spDeviceTools = new SpDeviceTools(context);
        //时间 年月日
        String motionDate = ParseWatchesData.byteToDate(data);
        int temp = (data[15] & 255) << 8 | data[16] & 255;
        int motionTime = (temp & '￠') >> 5;
        int motionCount = temp & 31;
        //步数 列表
        List<Integer> list = new ArrayList<>();
        for (int i = 17; i < data.length && (i + 1) < data.length; i += 2) {
            list.add((data[i] & 255) << 8 | data[i + 1] & 255);
        }
        //总步数
        int totalStep = (Integer) list.remove(list.size() - 1);
        if (totalStep == 0 && list.size() > 0) {
            for (int j = 0; j < list.size() - 1; j++) {
                totalStep += list.get(j);
            }
        }
        //卡路里
        String calorie = ParseWatchesData.byteToCalorie((float) spDeviceTools.getUserHeight(), (float) spDeviceTools.getUserWeight(), totalStep) + "千卡";
        //里程
        String distance;
        if (spDeviceTools.getStepAlgorithmType() == 1) {
            //公里
            distance = ParseWatchesData.byteToKm((float) spDeviceTools.getUserHeight(), totalStep) + "Km";
        } else {
            //英里
            distance = ParseWatchesData.byteToMile((float) spDeviceTools.getUserHeight(), totalStep) + "Mile";
        }
        return new MotionInfo(motionDate, motionCount, motionTime, calorie, distance, list, totalStep);
    }

    public static PPGDateTime byteToPpgDate(byte[] data) {

        StringBuilder builder = new StringBuilder();

        int temp = (data[13] & 0xFF) << 8 | data[14] & 0xFF;
        Log.e("time", "time=" + temp);
        int year = temp >> 9;
        Log.e("time", "year=" + year);

        String month = intToAdd0String((temp & 480) >> 5);
        Log.e("time", "month=480");

        String day = intToAdd0String(temp & 31);

        Log.e("time", "n=" + data[15]);
        String hour = intToAdd0String(data[15] / 2);

        String min;
        if (data[15] % 2 == 0) {
            min = ":00";
        } else {
            min = ":30";
        }

        builder.append("20" + year).append("-").append(month).append("-").append(day).append("  ").append(hour + min);
        return new PPGDateTime(builder.toString());
    }

    public static ECGDateTime byteToEcgDate(byte[] data) {
        StringBuilder builder = new StringBuilder();
        int temp = (data[13] & 0xFF) << 24 | (data[14] & 0xFF) << 16 | (data[15] & 0xFF) << 8 | data[16] & 0xFF;
        int year = temp >> 26;
        String month = intToAdd0String((temp & 0x3C00000) >> 22);
        String day = intToAdd0String((temp & 0x3E0000) >> 17);
        String hour = intToAdd0String((temp & 0xF000) >> 12);
        String min = intToAdd0String((temp & 0xFC0) >> 6);
        String second = intToAdd0String(temp & 0x3F);
        builder.append("20" + year).append("-").append(month).append("-").append(day).append("  ").append(hour).append(":").append(min).append(":").append(second);
        return new ECGDateTime(builder.toString());
    }

    public static PPGInfo parseByteToPPGInfo(PPGDateTime ppgDateTime, byte[] data) {
//        Log.e("AppRunService", "ArrayOfbyte=" + data.length);
        int k;
        List<PPGData> ppgDataList = new ArrayList<>();
        for (int i = 0; i < data.length; i = k) {
            PPGData ppgData = new PPGData();
            if (ppgDateTime != null) {
                ppgData.setPpgDateTime(ppgDateTime);
            }
            List<Integer> dataList = new ArrayList<>();
            for (int j = i; j < (k = i + 20); j += 2) {
                if (j < data.length) {
                    k = (data[j] & 255) * 256 + (data[j + 1] & 255);
//                    if (j < i + 18) {
                    dataList.add(k);
                    ppgData.setDataList(dataList);
//                    } else {
//                        ppgData.setCode(k);
//                    }
//                    Log.i(" parseByteToPPGInfo ", j + " " + data[j] + " " + (j + 1) + " " + data[j + 1]);
                }
            }
            ppgDataList.add(ppgData);
        }
        return new PPGInfo(ppgDataList);
    }

    public static ECGInfo parseByteToEcgInfo(ECGDateTime ecgDateTime, byte[] bytes) {
        List<ECGData> ecgDataList = new ArrayList<>();
        ECGData ecgData = new ECGData();
        if (ecgDateTime != null) {
            ecgData.setEcgDateTime(ecgDateTime);
        }
        List<Integer> dataList = new ArrayList<>();
        for (int i = 0; i < 20; i += 2) {
            if (i < bytes.length) {
                int status = (bytes[i] & 0xFF) * 256 + (bytes[i + 1] & 0xFF);
                if (i < 18) {
                    dataList.add(status);
                    ecgData.setDataList(dataList);
                } else {
                    status = (bytes[i] & 0xFF) >> 7;
                    ecgData.setCode(status & 0x7FFF);
                    ecgData.setStatue(status);
//                    Log.d("onCharacteristicChanged", " getCode=" + ecgData.getCode() + ",statue=" + status);
                }
            }
        }
        ecgDataList.add(ecgData);
        return new ECGInfo(ecgDataList);
    }

    public static SpoInfo byteToSpoInfo(byte[] data) {

        if (data == null || data.length < 16) return null;

        List<SpoData> list = new ArrayList<>();

        for (int i = 16; i < data.length && i + 5 < data.length; i += 6) {

            int temp = (data[i] & 0xFF) << 24 | (data[i + 1] & 0xFF) << 16 | (data[i + 2] & 0xFF) << 8 | data[i + 3] & 0xFF;

            int year = temp >> 26;
            int month = (temp & 0x3C00000) >> 22;
            int day = (temp & 0x3E0000) >> 17;
            int hour = (temp & 0x1F000) >> 12;
            int min = (temp & 0xFC0) >> 6;
            int second = temp & 0x3F;

            //时间 年月日时分秒
            StringBuilder builder = new StringBuilder();
            builder.append("20" + year).append("-")
                    .append(intToAdd0String(month)).append("-")
                    .append(intToAdd0String(day)).append(" ")
                    .append(intToAdd0String(hour)).append(":")
                    .append(intToAdd0String(min)).append(":")
                    .append(intToAdd0String(second));
            //血氧
            byte spoValue = data[i + 4];
            //心率
            byte heartValue = data[i + 5];

            list.add(new SpoData(builder.toString(), spoValue, heartValue));
        }

        return new SpoInfo(list);
    }

    public static SleepInfo byteToSleepInfo(byte[] data) {

        if (data == null || data.length < 17) return null;

        String sleepDate = ParseWatchesData.byteToDate(data);

        List<SleepData> list = new ArrayList<>();

        int sleepTotalTime = 0;
        int sleepDeepTime = 0;
        int sleepLightTime = 0;
        int sleepStayupTime = 0;
        int sleepWakingNumber = 0;
        int sleepFallTime = 0;
        int sleepRemTime = 0;

        int endTime = 0;
        int totalTime = 0;

        int oldHourTime = 0;
        int oldMinTime = 0;

        int old_sleep_type = -1;

        int sleepOrder = 0;

        int temp;
        for (int i = 16; i < data.length && (i + 1) < data.length; sleepTotalTime = temp) {

            int singleData = (data[i] & 0xFF) << 8 | data[i + 1] & 0xFF;

            int sleep_type = singleData & 15;
            int startHourTime = (singleData & 0xF800) >> 11;
            int startMinTime = (singleData & 0x7E0) >> 5;

            int sleepTime;
            if (old_sleep_type == 0) { //熬夜
                if ((sleepTime = startHourTime * 60 + startMinTime - (oldHourTime * 60 + oldMinTime)) < 0) {
                    sleepTime += 1440;
                }
                sleepStayupTime += sleepTime;
            } else if (old_sleep_type == 2) { //浅睡
                if ((sleepTime = startHourTime * 60 + startMinTime - (oldHourTime * 60 + oldMinTime)) < 0) {
                    sleepTime += 1440;
                }
                sleepLightTime += sleepTime;
            } else if (old_sleep_type == 3) { //熟睡
                if ((sleepTime = startHourTime * 60 + startMinTime - (oldHourTime * 60 + oldMinTime)) < 0) {
                    sleepTime += 1440;
                }
                sleepDeepTime += sleepTime;
            } else if (old_sleep_type == 6) { //熟睡
                if ((sleepTime = startHourTime * 60 + startMinTime - (oldHourTime * 60 + oldMinTime)) < 0) {
                    sleepTime += 1440;
                }
                sleepRemTime += sleepTime;
            }
            if (old_sleep_type == 1) { //入睡
                if ((sleepTime = startHourTime * 60 + startMinTime - (oldHourTime * 60 + oldMinTime)) < 0) {
                    sleepTime += 1440;
                }
                sleepFallTime += sleepTime;
            }

            if (sleep_type == 1) { //入睡
                sleepOrder++;
                endTime = startHourTime * 60 + startMinTime;
                temp = sleepTotalTime;
                sleepTotalTime = totalTime;
                totalTime = temp;
            } else if (sleep_type == 4) { //清醒
                ++sleepWakingNumber;
                temp = sleepTotalTime;
                sleepTotalTime = totalTime;
                totalTime = temp;
            } else if (sleep_type == 5) { //退出睡眠
                if ((sleepTotalTime = startHourTime * 60 + startMinTime - endTime) < 0) {
                    sleepTotalTime += 1440;
                }
                totalTime = sleepFallTime + sleepLightTime + sleepDeepTime + sleepRemTime;
            } else {
                temp = sleepTotalTime;
                sleepTotalTime = totalTime;
                totalTime = temp;
            }

            list.add(new SleepData(intToAdd0String(sleep_type), intToAdd0String(startHourTime) + ":" + intToAdd0String(startMinTime), sleepOrder));


            i += 2;
            temp = totalTime;
            oldMinTime = startMinTime;
            oldHourTime = startHourTime;
            old_sleep_type = sleep_type;
            totalTime = sleepTotalTime;
        }

        return new SleepInfo(addDay(sleepDate, 1), sleepTotalTime, sleepDeepTime, sleepLightTime, sleepStayupTime, sleepWakingNumber, sleepFallTime, sleepRemTime, list, totalTime);
    }

    public static String addDay(String sleepDate, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(sleepDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return sdf.format(calendar.getTime());
    }

    public static String intToAdd0String(int i) {
        return i < 10 ? "0" + i : "" + i;
    }

    public static PoHeartInfo byteToHeartInfo(byte[] data) {
        if (data == null || data.length < 17) return null;
        String heartDate = ParseWatchesData.byteToDate(data);
        List<Integer> list = new ArrayList<>();
//        if (data[5] == 1 || data.length > 500) {
            for (int i = 17; i < data.length; i++) {
                list.add(data[i] & 0xFF);
            }
//        } else {
//            for (int i = 17; i < data.length; i += 2) {
//                list.add(data[i] & 0xFF00 | data[i + 1] & 0xFF);
//            }
//        }
        int isOneMinRate = data[5]+data[6]*256;
        return new PoHeartInfo(heartDate, list, isOneMinRate);
    }

    public static WoHeartInfo byteToWoHeartInfo(byte[] data) {

        if (data == null || data.length < 17) return null;

        String date = ParseWatchesData.byteToDate(data);

        byte start = 17;
        short len = 288;

        List<Integer> woHeartDataList = new ArrayList<>();
        List<Integer> woHeartDayList = new ArrayList<>();
        List<Integer> woHeartSleepList = new ArrayList<>();

        int woHeartDayMax = 0;
        int woHeartDayMin = 0;
        int woHeartSleepMax = 0;
        int woHeartSleepMin = 0;
        int woHeartRecent = 0;

        int i3;
        for (byte i = 0; i < len && (start + i) < data.length; woHeartDayMin = i3) {
            int temp = i3 = data[start + i] & 0xFF;
            woHeartDataList.add(i3);
            if (temp != 0) {
                if (i < 96) {
                    woHeartSleepMin = i3;
                }
                woHeartDayMin = i3;
            } else {
                temp = woHeartDayMin;
                woHeartDayMin = woHeartRecent;
                i3 = temp;
            }
            ++i;
            woHeartRecent = woHeartDayMin;
        }

        int woHeartSleepAvg;
        for (byte j = 0; j < woHeartDataList.size(); ++j) {
            if ((woHeartSleepAvg = Integer.valueOf((Integer) woHeartDataList.get(j))) > 0) {
                if (j < 96) {
                    woHeartSleepList.add(woHeartSleepAvg);
                    if (woHeartSleepAvg > woHeartSleepMax) {
                        woHeartSleepMax = woHeartSleepAvg;
                    }

                    if (woHeartSleepAvg < woHeartSleepMin) {
                        woHeartSleepMin = woHeartSleepAvg;
                    }
                }

                if (woHeartSleepAvg > woHeartDayMax) {
                    woHeartDayMax = woHeartSleepAvg;
                }

                if (woHeartSleepAvg < woHeartDayMin) {
                    woHeartDayMin = woHeartSleepAvg;
                }

                woHeartDayList.add(woHeartSleepAvg);
            }
        }

        int woHeartDayAvg = ParseWatchesData.getAvgData((List<Integer>) woHeartDayList);
        woHeartSleepAvg = ParseWatchesData.getAvgData((List<Integer>) woHeartSleepList);
        return new WoHeartInfo(date, woHeartSleepMax, woHeartSleepMin, woHeartSleepAvg, woHeartDayMax, woHeartDayMin, woHeartDayAvg, woHeartRecent, woHeartDataList);
    }

    public static HeartInfo byteToHeartInfo(byte[] data, int heartInfoHR, int heartInfoSBP, int heartInfoDBP) {
        int infoHR = data[13] & 0xFF;
        int infoSBP = ParseWatchesData.getInfoSBP(infoHR, heartInfoHR, heartInfoSBP);
        int infoDBP = ParseWatchesData.getInfoDBP(infoHR, heartInfoHR, heartInfoSBP);
        return new HeartInfo(infoHR, infoSBP, infoDBP);
    }

    public static HeartInfo byteToHeartInfo(int infoHR, int heartInfoHR, int heartInfoSBP, int heartInfoDBP) {
        int infoSBP = ParseWatchesData.getInfoSBP(infoHR, heartInfoHR, heartInfoSBP);
        int infoDBP = ParseWatchesData.getInfoDBP(infoHR, heartInfoHR, heartInfoSBP);
        return new HeartInfo(infoHR, infoSBP, infoDBP);
    }

    public static SiestaInfo byteToSiestaInfo(byte[] data) {

        if (data == null || data.length < 20) return null;

        int startYear = data[12] * 100 + data[13];
        int startMonth = data[14];
        int startDay = data[15];
        int startHour = data[16];
        int startMin = data[17];
        int endHour = data[18];
        int endMin = data[19];
        int sleepTime = data[20];

        return new SiestaInfo(startYear, startMonth, startDay, startHour, startMin, endHour, endMin, sleepTime);
    }

    public static byte[] getEncryptCom(byte[] data) {
        byte sign = SIGN_HEAD;
//        data[28]={0xAB,0,0,0x14,0,0,0,0,1,0,0x84,0,code[0],code[1],code[2],…,code[15]}
        byte[] comByte = new byte[29];
        comByte[0] = sign;
        comByte[3] = 20;
        comByte[8] = 1;
        comByte[10] = -124;
        comByte[12] = 15;
        for (int i = 0; i < 16; i++) {
            comByte[13 + i] = data[i];
        }
        return comByte;
    }

    public static PressureInfo byteToPressureInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        String date = ParseWatchesData.byteToDate(data);
        List<Integer> list = new ArrayList<>();
        for (int i = 17; i < data.length; i += 2) {
            byte[] b = new byte[1];
            b[0] = data[i];
            list.add(ByteToStringUtil.byteToInt(b));
        }
        return new PressureInfo(data[5]+data[6]*256,date, list);
    }

    public static EmotionInfo byteToEmotionInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        String date = ParseWatchesData.byteToDate(data);
        List<Integer> list = new ArrayList<>();
        for (int i = 18; i < data.length; i += 2) {
            byte[] b = new byte[1];
            b[0] = data[i];
            list.add(ByteToStringUtil.byteToInt(b));
        }
        return new EmotionInfo(data[5]+data[6]*256,date, list);
    }

    public static TireInfo byteToTireInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        String date = ParseWatchesData.byteToDate(data);
        List<Integer> list = new ArrayList<>();
        for (int i = 17; i < data.length; i++) {
            byte[] b = new byte[1];
            b[0] = data[i];
            list.add(ByteToStringUtil.byteToInt(b));
        }
        return new TireInfo(data[5]+data[6]*256,date, list);
    }

    public static HeatInfo byteToHeatInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        String date = ParseWatchesData.byteToDate(data);
        List<Float> list = new ArrayList<>();
        for (int i = 19; i < data.length && (i + 1) < data.length; i += 5) {
            int byte0 = ByteToStringUtil.byteToInt(data[i]);
            int byte1 = ByteToStringUtil.byteToInt(data[i + 1]);
            float temp = byte0 + 10 * byte1 / 256 * 0.1f;
//            LogUtil.i(TAG, " HeatInfo " + temp + " " + byte0 + " " + byte1);
            list.add(temp);
        }
        return new HeatInfo(date, list);
    }

    public static EnviTempInfo byteToEnviHeatInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        String date = ParseWatchesData.byteToDate(data);
        List<Integer> list = new ArrayList<>();
        for (int i = 17; i < data.length && (i + 1) < data.length; i += 5) {
            int byte0 = ByteToStringUtil.byteTo7Int(data[i]);
            int byte1 = ByteToStringUtil.byteToInt(data[i + 1]);
            int temp;
            if (byte0 < 0) {
                temp = byte0 /*- 10 * byte1 / 256 * 0.1f*/;
            } else {
                temp = byte0 /*+ 10 * byte1 / 256 * 0.1f*/;
            }
//            LogUtil.i(TAG, " EnviHeatInfo " + temp + " " + byte0 + " " + byte1);
            list.add(temp);
        }
        return new EnviTempInfo(date, list);
    }

    public static UvInfo byteToUVInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        String date = ParseWatchesData.byteToDate(data);
        List<Integer> list = new ArrayList<>();
        for (int i = 21; i < data.length; i += 5) {
            int byte0 = ByteToStringUtil.byteToInt(data[i]);
//            LogUtil.i(TAG, " UVInfo " + " " + byte0);
            list.add(byte0);
        }
        return new UvInfo(date, list);
    }

    public static HeartListInfo byteToHeartListInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        List<HeartInfo> list = new ArrayList<>();
        for (int i = 16; i < data.length && (i + 6) < data.length; i += 7) {
            int temp = (data[i] & 0xFF) << 24 | (data[i + 1] & 0xFF) << 16 | (data[i + 2] & 0xFF) << 8 | data[i + 3] & 0xFF;

            int year = temp >> 26;
            int month = (temp & 0x3C00000) >> 22;
            int day = (temp & 0x3E0000) >> 17;
            int hour = (temp & 0x1F000) >> 12;
            int min = (temp & 0xFC0) >> 6;
            int second = temp & 0x3F;

            StringBuilder builder = new StringBuilder();
            builder.append("20" + year).append("-")
                    .append(intToAdd0String(month)).append("-")
                    .append(intToAdd0String(day)).append(" ")
                    .append(intToAdd0String(hour)).append(":")
                    .append(intToAdd0String(min)).append(":")
                    .append(intToAdd0String(second));

            HeartInfo info = new HeartInfo(ByteToStringUtil.byteToInt(data[i + 4]), ByteToStringUtil.byteToInt(data[i + 5]), ByteToStringUtil.byteToInt(data[i + 6]), builder.toString());
            list.add(info);
        }
        return new HeartListInfo(list);
    }

    public static SleepLogInfo byteToSleepLogInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        String date = ParseWatchesData.byteToDate(data);
        List<Integer> list = new ArrayList<>();
        for (int i = 17; i < data.length; i++) {
            byte[] b = new byte[1];
            b[0] = data[i];
            list.add(ByteToStringUtil.byteToInt(b));
        }
        return new SleepLogInfo(date, list);
    }

    /**
     * 发送生命力数值
     *
     * @return
     */
    public static byte[] getLifeCom(List<Integer> list) {
        byte sign = SIGN_HEAD;
        if (list == null || list.isEmpty()) return null;
        byte[] lifeByte = new byte[20];
        lifeByte[0] = sign;
        lifeByte[3] = 12;
        lifeByte[8] = 1;
        lifeByte[10] = 80;
        lifeByte[12] = 7;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > 0) {
                lifeByte[13 + i] = (byte) (list.get(i) & 0xff);
            } else {
                lifeByte[13 + i] = (byte) ((list.get(i) + 256) & 0xff);
            }
            ByteToStringUtil.byteTo7Int(lifeByte[13 + i]);
        }
        return lifeByte;
    }

    //睡眠高心率,睡眠低心率，睡眠高血氧，睡眠低血氧，全天最高心率，全天最低心率，全天最高血氧，全天最低血氧
    public static byte[] getOxHeartCom(int sleepMaxHeart, int sleepMinHeart, int sleepMaxOx, int sleepMinOx, int maxHeart, int minHeart, int maxOx, int minOx) {
        byte sign = SIGN_HEAD;
        byte[] oxHeartByte = new byte[21];
        oxHeartByte[0] = sign;
        oxHeartByte[3] = 13;
        oxHeartByte[8] = 1;
        oxHeartByte[10] = 81;
        oxHeartByte[12] = 8;
        oxHeartByte[13] = (byte) (sleepMaxHeart & 0xFF);
        oxHeartByte[14] = (byte) (sleepMinHeart & 0xFF);
        oxHeartByte[15] = (byte) (sleepMaxOx & 0xFF);
        oxHeartByte[16] = (byte) (sleepMinOx & 0xFF);
        oxHeartByte[17] = (byte) (maxHeart & 0xFF);
        oxHeartByte[18] = (byte) (minHeart & 0xFF);
        oxHeartByte[19] = (byte) (maxOx & 0xFF);
        oxHeartByte[20] = (byte) (minOx & 0xFF);
        return oxHeartByte;
    }

    public static BreatheInfo byteToBreatheInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        String date = ParseWatchesData.byteToDate(data);
        List<Integer> list = new ArrayList<>();
        for (int i = 17; i < data.length-4; i++) {
            list.add(data[i] & 0xFF);
        }

        int hypopnea = 0; //低通气指数;
        int blockLen = 0; //累计阻塞时长;
        int chaosIndex = 0; //呼吸紊乱指数;
        int pauseCount = 0; //呼吸暂停次数;
        try {
                hypopnea = data[data.length-4] & 0xFF;
                blockLen = data[data.length-3] & 0xFF;
                chaosIndex = data[data.length-2] & 0xFF;
                pauseCount = data[data.length-1] & 0xFF;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BreatheInfo(data[5]*data[6]*256,date, list, hypopnea, blockLen, chaosIndex, pauseCount);
    }

    public static SymptomListInfo byteToSymptomInfo(byte[] data) {

        if (data == null || data.length < 16) return null;

        List<SymptomInfo> list = new ArrayList<>();

        try {
            for (int i = 16; i < data.length && (i + 5) < data.length; i += 6) {

                int temp = (data[i] & 0xFF) << 24 | (data[i + 1] & 0xFF) << 16 | (data[i + 2] & 0xFF) << 8 | data[i + 3] & 0xFF;

                int year = temp >> 26;
                int month = (temp & 0x3C00000) >> 22;
                int day = (temp & 0x3E0000) >> 17;
                int hour = (temp & 0x1F000) >> 12;
                int min = (temp & 0xFC0) >> 6;
                int second = temp & 0x3F;

                StringBuilder builder = new StringBuilder();
                builder.append("20" + year).append("-")
                        .append(intToAdd0String(month)).append("-")
                        .append(intToAdd0String(day)).append(" ")
                        .append(intToAdd0String(hour)).append(":")
                        .append(intToAdd0String(min)).append(":")
                        .append(intToAdd0String(second));

                SymptomInfo info = new SymptomInfo(builder.toString(), data[i + 4] & 0xFF);
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SymptomListInfo(list, data[15] & 0xFF);
    }

    public static SnoreInfo byteToSnoreInfo(byte[] data) {

        if (data == null || data.length < 14) return null;

        String date = ParseWatchesData.byteToDate(data);

        int snoreHour = 0;
        int snoreMin = 0;
        int snoreLen = 0;

        int maxDb = 0;
        int averageDb = 0;
        int minDb = 0;
        float snoreIndex = 0;
        int snoreFrequency = 0;

        int snoreNormalHour = 0;
        int snoreNormalMin = 0;
        int snoreNormal = 0;

        int snoreMildHour = 0;
        int snoreMildMin = 0;
        int snoreMild = 0;

        int snoreMiddleHour = 0;
        int snoreMiddleMin = 0;
        int snoreMiddle = 0;

        int snoreSeriousHour = 0;
        int snoreSeriousMin = 0;
        int snoreSerious = 0;

        try {
            if (data.length > 16) {
                snoreHour = data[15] & 0xFF;
                snoreMin = data[16] & 0xFF;
                snoreLen = snoreHour * 60 + snoreMin;
            }
            if (data.length > 17) {
                maxDb = data[17] & 0xFF;
            }
            if (data.length > 18) {
                minDb = data[18] & 0xFF;
            }
            if (data.length > 19) {
                averageDb = data[19] & 0xFF;
            }
            if (data.length > 20) {
                snoreIndex = (float) (data[20] & 0xFF) * 0.1f;
            }
            if (data.length > 21) {
                snoreFrequency = data[21] & 0xFF;
            }
            if (data.length > 23) {
                snoreNormalHour = data[22] & 0xFF;
                snoreNormalMin = data[23] & 0xFF;
                snoreNormal = snoreNormalHour * 60 + snoreNormalMin;
            }
            if (data.length > 25) {
                snoreMildHour = data[24] & 0xFF;
                snoreMildMin = data[25] & 0xFF;
                snoreMild = snoreMildHour * 60 + snoreMildMin;
            }
            if (data.length > 27) {
                snoreMiddleHour = data[26] & 0xFF;
                snoreMiddleMin = data[27] & 0xFF;
                snoreMiddle = snoreMiddleHour * 60 + snoreMiddleMin;
            }
            if (data.length > 29) {
                snoreSeriousHour = data[28] & 0xFF;
                snoreSeriousMin = data[29] & 0xFF;
                snoreSerious = snoreSeriousHour * 60 + snoreSeriousMin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SnoreInfo(addDay(date, 1), snoreLen, maxDb, averageDb, minDb, snoreIndex, snoreFrequency, snoreNormal, snoreMild, snoreMiddle, snoreSerious);
    }

    /**
     * 解压运动强度
     *
     * @param data
     * @return
     */
    public static StrengthInfo byteToStrengthInfo(byte[] data) {
        if (data == null || data.length < 18) return null;
        try {
            int lowTime = data[13] * 60 + data[14];
            int middleTime = data[15] * 60 + data[16];
            int highTime = data[17] * 60 + data[18];
            return new StrengthInfo(System.currentTimeMillis(), lowTime, middleTime, highTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送运动强度到手表
     *
     * @param highHour
     * @param hourMin
     * @param midHour
     * @param midMin
     * @param lowHour
     * @param lowMin
     * @return
     */
    public static byte[] getMotionStrength(int highHour, int hourMin, int midHour, int midMin, int lowHour, int lowMin) {
        byte sign = SIGN_HEAD;
        byte[] strengthByte = new byte[20];
        strengthByte[0] = sign;
        strengthByte[3] = 0x0B;
        strengthByte[8] = 1;
        strengthByte[10] = 0x52;
        strengthByte[12] = 0x06;
        strengthByte[13] = (byte) lowHour;
        strengthByte[14] = (byte) lowMin;
        strengthByte[15] = (byte) midHour;
        strengthByte[16] = (byte) midMin;
        strengthByte[17] = (byte) highHour;
        strengthByte[18] = (byte) hourMin;
        return strengthByte;
    }

    public static byte[] getWatchSaveCom() {
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[13];
        bytes[0] = sign;
        bytes[3] = 0x05;
        bytes[8] = 0x01;
        bytes[10] = 0x53;
        return bytes;
    }

    public static WatchSaveInfo byteToWatchSaveInfo(byte[] data) {
        if (data == null || data.length != 20) return null;
        int temp = (data[16] & 0xFF) << 24 | (data[17] & 0xFF) << 16 | (data[18] & 0xFF) << 8 | data[19] & 0xFF;
        int year = temp >> 26;
        int month = (temp & 0x3C00000) >> 22;
        int day = (temp & 0x3E0000) >> 17;
        int hour = (temp & 0x1F000) >> 12;
        int min = (temp & 0xFC0) >> 6;
        int second = temp & 0x3F;

        StringBuilder builder = new StringBuilder();
        builder.append("20" + intToAdd0String(year)).append(".")
                .append(intToAdd0String(month)).append(".")
                .append(intToAdd0String(day)).append(" ")
                .append(intToAdd0String(hour)).append(":")
                .append(intToAdd0String(min)).append(":")
                .append(intToAdd0String(second));

        int i = 0;
        int A = (data[i] & 0xFF) << 24 | (data[i + 1] & 0xFF) << 16 | (data[i + 2] & 0xFF) << 8 | data[i + 3] & 0xFF;
        i = 4;
        int B = (data[i] & 0xFF) << 24 | (data[i + 1] & 0xFF) << 16 | (data[i + 2] & 0xFF) << 8 | data[i + 3] & 0xFF;
        i = 8;
        int C = (data[i] & 0xFF) << 24 | (data[i + 1] & 0xFF) << 16 | (data[i + 2] & 0xFF) << 8 | data[i + 3] & 0xFF;
        i = 12;
        int D = (data[i] & 0xFF) << 24 | (data[i + 1] & 0xFF) << 16 | (data[i + 2] & 0xFF) << 8 | data[i + 3] & 0xFF;

//        Log.i("", " A " + A + " B " + B + " C " + C + " D " + D  + " time " + builder.toString());

        return new WatchSaveInfo(builder.toString(), A, B, C, D);
    }

    //开始跑步
    public static byte[] getStartRunCommand() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 6, 0, 0, 0, 0, 1, 0, 84, 0, 1, 0};
    }

    //结束跑步
    public static byte[] getEndRunCommand() {
        byte sign = SIGN_HEAD;
        return new byte[]{sign, 0, 0, 6, 0, 0, 0, 0, 1, 0, 84, 0, 1, 1};
    }

    public static AbnormalHeartListInfo byteToAbnormalHeartListInfo(byte[] data) {
        if (data == null || data.length == 0 || data.length < 22) return null;

        List<AbnormalHeartInfo> list = new ArrayList<>();

        try {
            for (int i = 16; i < data.length && (i + 5) < data.length; i += 6) {

                int temp = (data[i] & 0xFF) << 24 | (data[i + 1] & 0xFF) << 16 | (data[i + 2] & 0xFF) << 8 | data[i + 3] & 0xFF;

                int year = temp >> 26;
                int month = (temp & 0x3C00000) >> 22;
                int day = (temp & 0x3E0000) >> 17;
                int hour = (temp & 0x1F000) >> 12;
                int min = (temp & 0xFC0) >> 6;
                int second = temp & 0x3F;

                StringBuilder builder = new StringBuilder();
                builder.append("20" + year).append("-")
                        .append(intToAdd0String(month)).append("-")
                        .append(intToAdd0String(day)).append(" ")
                        .append(intToAdd0String(hour)).append(":")
                        .append(intToAdd0String(min)).append(":")
                        .append(intToAdd0String(second));

                AbnormalHeartInfo info = new AbnormalHeartInfo(builder.toString(), data[i + 5] & 0xFF);
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AbnormalHeartListInfo(list);
    }

    public static int byteToRunStepInfo(byte[] data) {
        if (data.length < 16) {
            return 0;
        }
        int step = (data[13] & 0xFF) << 16 | (data[14] & 0xFF) << 8 | data[15] & 0xFF;
        return step;
    }

    public static SwitchInfo byteToSwitchInfo(byte[] data){
        SwitchInfo info = new SwitchInfo();
        if (data.length < 18) {
            return info;
        }
        info.setHeartRemind(data[12] == 1);
        info.setSitRemind(data[13] == 1);
        info.setSleepRemind(data[14] == 1);
        info.setLowOxRemind(data[15] == 1);
        info.setDisturbRemind(data[16] == 1);
        info.setLanguageRemind(data[17] == 1);
        return info;
    }

    //发送传图指令
    public static byte[] getSendImgCom(byte[] addr, byte[] size) {
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[21];
        bytes[0] = sign;
        bytes[3] = 0x0D;
        bytes[8] = 0x01;
        bytes[10] = 0x56;
        bytes[12] = 0x08;
        if (addr != null && addr.length == 4) {
            bytes[13] = addr[0];
            bytes[14] = addr[1];
            bytes[15] = addr[2];
            bytes[16] = addr[3];
        }
        if (size != null && size.length == 4) {
            bytes[17] = size[0];
            bytes[18] = size[1];
            bytes[19] = size[2];
            bytes[20] = size[3];
        }
        return bytes;
    }

    //发送睡眠平均心率和血氧
    public static byte[] getSendHeartAndOx( int sleepAverageHeart, int sleepAverageOx) {
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[19];
        bytes[0] = sign;
        bytes[3] = 0x0B;
        bytes[8] = 0x01;
        bytes[10] = 0x58;
        bytes[12] = 0x06;
        bytes[13] = (byte) sleepAverageHeart;
        bytes[14] = (byte) sleepAverageOx;
        return bytes;
    }

    //开始心率测量
    public static byte[] getTestHeartCom(){
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[14];
        bytes[0] = sign;
        bytes[3] = 0x06;
        bytes[8] = 0x01;
        bytes[10] = 0x59;
        bytes[12] = 0x01;
//        LogUtil.i(" getTestHeartCom " + ByteToStringUtil.ByteToString(bytes));
        return bytes;
    }

    //开始血氧测量
    public static byte[] getTestOxCom(){
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[14];
        bytes[0] = sign;
        bytes[3] = 0x06;
        bytes[8] = 0x01;
        bytes[10] = 0x5A;
        bytes[12] = 0x01;
//        LogUtil.i(" getTestOxCom " + ByteToStringUtil.ByteToString(bytes));
        return bytes;
    }

    //开始体温、环境温度测量
    public static byte[] getTestHeatCom(){
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[14];
        bytes[0] = sign;
        bytes[3] = 0x06;
        bytes[8] = 0x01;
        bytes[10] = 0x5B;
        bytes[12] = 0x01;
        bytes[13] = 0x01;
//        LogUtil.i(" getTestHeatCom " + ByteToStringUtil.ByteToString(bytes));
        return bytes;
    }

    public static byte[] getTestTempCom(){
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[14];
        bytes[0] = sign;
        bytes[3] = 0x06;
        bytes[8] = 0x01;
        bytes[10] = 0x5B;
        bytes[12] = 0x01;
//        LogUtil.i(" getTestHeatCom " + ByteToStringUtil.ByteToString(bytes));
        return bytes;
    }

    //开始疲劳、压力测量
    public static byte[] getTestTirePressureCom(){
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[14];
        bytes[0] = sign;
        bytes[3] = 0x06;
        bytes[8] = 0x01;
        bytes[10] = 0x5C;
        bytes[12] = 0x01;
//        LogUtil.i(" getTestTirePressureCom " + ByteToStringUtil.ByteToString(bytes));
        return bytes;
    }

    //发送开关
    //0x5D:设置模式，APP向手表发送设置模式指令data[21]={0xAB 0 0 0x0D 0 0 0 0 1 0 0x5D 0 8 心率过高开关 坐久开关 睡眠提醒开关 血氧偏低开关 勿扰开关 0 0 0}，
    public static byte[] sendSwitchCom(boolean heartSwitchRemind, boolean sitSwitchRemind, boolean sleepSwitchRemind, boolean oxSwitchRemind, boolean disturbSwitchRemind, boolean sleepOxRemind){
        byte sign = SIGN_HEAD;
        byte[] bytes = new byte[21];
        bytes[0] = sign;
        bytes[3] = 0x0D;
        bytes[8] = 0x01;
        bytes[10] = 0x5D;
        bytes[12] = 0x08;
        if (heartSwitchRemind){
            bytes[13] = 0x01;
        }else {
            bytes[13] = 0x00;
        }
        if (sitSwitchRemind){
            bytes[14] = 0x01;
        }else {
            bytes[14] = 0x00;
        }
        if (sleepSwitchRemind){
            bytes[15] = 0x01;
        }else {
            bytes[15] = 0x00;
        }
        if (oxSwitchRemind){
            bytes[16] = 0x01;
        }else {
            bytes[16] = 0x00;
        }
        if (disturbSwitchRemind){
            bytes[17] = 0x01;
        }else {
            bytes[17] = 0x00;
        }
        if (sleepOxRemind){
            bytes[18] = 0x01;
        }else {
            bytes[18] = 0x00;
        }
        return bytes;
    }

    public static int byteToHRV(byte[] data) {
        if (data.length < 15) return 0;
        try {
            return (data[13] & 0xFF) * 256 + (data[14] & 0xFF);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    //睡眠血氧
    public static SleepOxInfo byteToSleepOxInfo(byte[] data) {
        if (data.length < 18) return new SleepOxInfo();
        int temp = (data[14] & 0xFF) << 24 | (data[15] & 0xFF) << 16 | (data[16] & 0xFF) << 8 | data[17] & 0xFF;
        int year = temp >> 26;
        int month = (temp & 0x3C00000) >> 22;
        int day = (temp & 0x3E0000) >> 17;
        int hour = (temp & 0x1F000) >> 12;
        int min = (temp & 0xFC0) >> 6;
        int second = temp & 0x3F;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year + 2000);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);

        LogUtil.i(" byteToSleepOxInfo year " + year + " month " + month + " day " + day + " hour " + hour + " min " + min + " second " + second);

        List<Integer> list = new ArrayList<>();
        for (int i = 18; i < data.length; i++){
            list.add(ByteToStringUtil.byteToInt(data[i]));
        }
        SleepOxInfo sleepOxInfo = new SleepOxInfo();
        sleepOxInfo.setNum(data[12] >> 8 | data[13]);
        sleepOxInfo.setList(list);
        sleepOxInfo.setTime(calendar.getTimeInMillis());
        return sleepOxInfo;
    }

    public static HrvInfo byteToHrvInfo(byte[] data){
        if (data == null || data.length < 17) return null;
        //日期
        String hrvDate = ParseWatchesData.byteToDate(data);
        //数据
        List<Integer> list = new ArrayList<>();
        for (int i = 17; i < data.length && (i + 1) < data.length; i += 2) {
            list.add((data[i] & 255) << 8 | data[i + 1] & 255);
        }
        return new HrvInfo(hrvDate, list);
    }

    public static SymptomListInfo byteToSymptomInfo2(byte[] data) {
        if (data == null || data.length < 16) return null;
        SymptomListInfo symptomListInfo = new SymptomListInfo();
        List<SymptomInfo2> list = new ArrayList<>();
        try {
            for (int i = 16; i < data.length && (i + 12) < data.length; i += 13) {
                int temp = (data[i] & 0xFF) << 24 | (data[i + 1] & 0xFF) << 16 | (data[i + 2] & 0xFF) << 8 | data[i + 3] & 0xFF;
                int year = temp >> 26;
                int month = (temp & 0x3C00000) >> 22;
                int day = (temp & 0x3E0000) >> 17;
                int hour = (temp & 0x1F000) >> 12;
                int min = (temp & 0xFC0) >> 6;
                int second = temp & 0x3F;
                StringBuilder builder = new StringBuilder();
                builder.append("20" + year).append("-")
                        .append(intToAdd0String(month)).append("-")
                        .append(intToAdd0String(day)).append(" ")
                        .append(intToAdd0String(hour)).append(":")
                        .append(intToAdd0String(min)).append(":")
                        .append(intToAdd0String(second));
                SymptomInfo2 info = new SymptomInfo2();
                info.setSymptomTime(builder.toString());
                //选择的症状 1选择 0未选择
                int[] symptomArray = new int[8];
                symptomArray[0] = data[i + 4] & 0xFF;
                symptomArray[1] = data[i + 5] & 0xFF;
                symptomArray[2] = data[i + 6] & 0xFF;
                symptomArray[3] = data[i + 7] & 0xFF;
                symptomArray[4] = data[i + 8] & 0xFF;
                symptomArray[5] = data[i + 9] & 0xFF;
                symptomArray[6] = data[i + 10] & 0xFF;
                symptomArray[7] = data[i + 11] & 0xFF;
                info.setSymptomArray(symptomArray);
                info.setScore(data[i + 12] & 0xFF);
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        symptomListInfo.setSymptomInfo2(list);
        symptomListInfo.setSymptomCount(data[15] & 0xFF);
        return symptomListInfo;
    }

}