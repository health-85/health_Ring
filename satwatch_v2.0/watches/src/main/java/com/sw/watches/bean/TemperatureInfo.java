package com.sw.watches.bean;

public class TemperatureInfo {
    //体表温度
    private float temperatureOriginValue;
    //体温
    private float temperatureValue;
    private long time;

    public float getTemperatureOriginValue() {
        return temperatureOriginValue;
    }

    public void setTemperatureOriginValue(float temperatureOriginValue) {
        this.temperatureOriginValue = temperatureOriginValue;
    }

    public float getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(float temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
