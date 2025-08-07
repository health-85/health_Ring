package com.sw.watches.bean;


import java.math.BigInteger;

public class AlarmInfo {
    public int AlarmId;

    public int AlarmtHour;

    public int AlarmtMin;

    public int AlarmtData;

    public AlarmInfo(){

    }

    public AlarmInfo(int AlarmId, int AlarmtHour, int AlarmtMin, int AlarmtData) {
        setAlarmId(AlarmId);
        setAlarmtHour(AlarmtHour);
        setAlarmtMin(AlarmtMin);
        setAlarmtData(AlarmtData);
    }

    public AlarmInfo(int AlarmtHour, int AlarmtMin, int AlarmtData) {
        setAlarmtHour(AlarmtHour);
        setAlarmtMin(AlarmtMin);
        setAlarmtData(AlarmtData);
    }

    private int handleData(int alarmtData) {
        int i = 0;
        byte[] arrayOfByte = new byte[1];
        arrayOfByte[0] = (byte) alarmtData;
        String str = (new BigInteger(1, arrayOfByte)).toString(2);
        int j = str.length();
        int k;
        for (k = 0; k < 8 - j; k = (byte) (k + 1))
            str = "0" + str;
        for (j = 0; j < str.length(); j = (byte) k) {
            if (Integer.valueOf(str.substring(j, k = j + 1)).intValue() == 1)
                if (j == 0) {
                    i += (int) Math.pow(2.0D, 7.0D);
                } else {
                    double d = (j - 1);
                    i += (int) Math.pow(2.0D, d);
                }
        }
        return i;
    }

    public int getAlarmId() {
        return this.AlarmId;
    }

    public void setAlarmId(int alarmId) {
        this.AlarmId = alarmId;
    }

    public int getAlarmHour() {
        return this.AlarmtHour;
    }

    public void setAlarmtHour(int AlarmtHour) {
        this.AlarmtHour = AlarmtHour;
    }

    public int getAlarmMin() {
        return this.AlarmtMin;
    }

    public void setAlarmtMin(int AlarmtMin) {
        this.AlarmtMin = AlarmtMin;
    }

    public int getAlarmData() {
        return this.AlarmtData;
    }

    public void setAlarmtData(int AlarmtData) {
        this.AlarmtData = handleData(AlarmtData);
    }
}
