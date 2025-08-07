package com.sw.watches.bean;

import java.util.List;

/**
 * 体温
 */
public class HeatInfo {

    public String heatDate;

    public List<Float> list;

    public List<TemperatureInfo> temperatureInfoList;

    public HeatInfo(){

    }

    public HeatInfo(String heatDate, List<Float> list){
        this.heatDate = heatDate;
        this.list = list;
    }

    public String getHeatDate() {
        return heatDate;
    }

    public void setHeatDate(String heatDate) {
        this.heatDate = heatDate;
    }

    public List<Float> getList() {
        return list;
    }

    public void setList(List<Float> list) {
        this.list = list;
    }

    public List<TemperatureInfo> getTemperatureInfoList() {
        return temperatureInfoList;
    }

    public void setTemperatureInfoList(List<TemperatureInfo> temperatureInfoList) {
        this.temperatureInfoList = temperatureInfoList;
    }
}
