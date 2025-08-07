package com.sw.watches.bean;


public class SleepData {

    public int sleep_order;

    public String sleep_type;

    public String startTime;

    public SleepData(String sleep_type, String startTime) {
        setSleep_type(sleep_type);
        setStartTime(startTime);
    }

    public SleepData(String sleep_type, String startTime, int sleep_order) {
        setSleep_type(sleep_type);
        setStartTime(startTime);
        setSleep_order(sleep_order);
    }

    public String getSleep_type() {
        return this.sleep_type;
    }

    public void setSleep_type(String sleep_type) {
        this.sleep_type = sleep_type;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getSleep_order() {
        return sleep_order;
    }

    public void setSleep_order(int sleep_order) {
        this.sleep_order = sleep_order;
    }

    public String toString() {
        return "SleepData{sleep_type='" + this.sleep_type + '\'' + ", startTime='" + this.startTime + '\'' /*+ ", sleep_order='" + this.sleep_order + '\''*/ +'}';
    }
}
