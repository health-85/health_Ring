package com.sw.watches.bean;

import java.util.List;

/**
 * 呼吸数据
 */
public class BreatheInfo {

    private int TimeGap;

    private String date;
    private List<Integer> list;

    int hypopnea; //低通气指数;
    int blockLen; //累计阻塞时长;
    int chaosIndex; //呼吸紊乱指数;
    int pauseCount; //呼吸暂停次数;

    public BreatheInfo(String date, List<Integer> list){
        this.date = date;
        this.list = list;
    }

    public BreatheInfo(int TimeGap,String date, List<Integer> list, int hypopnea, int blockLen, int chaosIndex, int pauseCount){
        this.TimeGap=TimeGap;
        this.date = date;
        this.list = list;
        this.hypopnea = hypopnea;
        this.blockLen = blockLen;
        this.chaosIndex = chaosIndex;
        this.pauseCount = pauseCount;
    }

    public int getBreathTimeGap() {
        return TimeGap;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public int getHypopnea() {
        return hypopnea;
    }

    public void setHypopnea(int hypopnea) {
        this.hypopnea = hypopnea;
    }

    public int getBlockLen() {
        return blockLen;
    }

    public void setBlockLen(int blockLen) {
        this.blockLen = blockLen;
    }

    public int getChaosIndex() {
        return chaosIndex;
    }

    public void setChaosIndex(int chaosIndex) {
        this.chaosIndex = chaosIndex;
    }

    public int getPauseCount() {
        return pauseCount;
    }

    public void setPauseCount(int pauseCount) {
        this.pauseCount = pauseCount;
    }
}
