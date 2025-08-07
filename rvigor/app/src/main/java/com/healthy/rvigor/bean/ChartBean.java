package com.healthy.rvigor.bean;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * 血氧报表
 */
public class ChartBean {

    //空数据
    public static final int EMPTY = Integer.MIN_VALUE;
    //开始
    public static final int START_TYPE = 1;
    //结束
    public static final int END_TYPE = 2;

    public float data;

    public long time;
    public float averageData;

    public float minData;

    public float maxData;

    public int color;

    private float line1Data;

    private float line2Data;

    //底端的文字
    public String bottomString = "";
    //显示的文字
    public String showTimeString = "";

    //数据列表
    public List<Float> list = new ArrayList<>();

    public List<DataItem> dataList = new ArrayList<>();

    public Rect currentDrawRect = new Rect(0, 0, 0, 0);

    public int ahi = 0; //低通气指数;

    public int blockLen = 0; //累计阻塞时长;

    public int rdi = 0; //呼吸紊乱指数;

    public int pauseCount = 0; //呼吸暂停次数;

    public static class DataItem {

        /**
         * 数据
         */
        public double data = 0;
        /**
         * 时间
         */
        public long dateTime = 0;

        //1 开始 2 结束
        public int type;

        public Rect currentDrawRect = new Rect(0, 0, 0, 0);

        public DataItem() {

        }

        public DataItem(double data, long dateTime) {
            this.data = data;
            this.dateTime = dateTime;
        }

        public DataItem(double data, long dateTime, int type) {
            this.data = data;
            this.type = type;
            this.dateTime = dateTime;
        }
    }

    public ChartBean() {

    }

    public ChartBean(float average, long time, float max, float min) {
        this.averageData = average;
        this.time = time;
        this.maxData = max;
        this.minData = min;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getAverageData() {
        return averageData;
    }

    public void setAverageData(float averageData) {
        this.averageData = averageData;
    }

    public float getMinData() {
        return minData;
    }

    public void setMinData(float minData) {
        this.minData = minData;
    }

    public float getMaxData() {
        return maxData;
    }

    public void setMaxData(float maxData) {
        this.maxData = maxData;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getBottomString() {
        return bottomString;
    }

    public void setBottomString(String bottomString) {
        this.bottomString = bottomString;
    }

    public String getShowTimeString() {
        return showTimeString;
    }

    public void setShowTimeString(String showTimeString) {
        this.showTimeString = showTimeString;
    }

    public List<Float> getList() {
        return list;
    }

    public void setList(List<Float> list) {
        this.list = list;
    }

    public float getLine1Data() {
        return line1Data;
    }

    public void setLine1Data(float line1Data) {
        this.line1Data = line1Data;
    }

    public float getLine2Data() {
        return line2Data;
    }

    public void setLine2Data(float line2Data) {
        this.line2Data = line2Data;
    }

    public List<DataItem> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataItem> dataList) {
        this.dataList = dataList;
    }

    public int getAhi() {
        return ahi;
    }

    public void setAhi(int ahi) {
        this.ahi = ahi;
    }

    public int getBlockLen() {
        return blockLen;
    }

    public void setBlockLen(int blockLen) {
        this.blockLen = blockLen;
    }

    public int getRdi() {
        return rdi;
    }

    public void setRdi(int rdi) {
        this.rdi = rdi;
    }

    public int getPauseCount() {
        return pauseCount;
    }

    public void setPauseCount(int pauseCount) {
        this.pauseCount = pauseCount;
    }
}
