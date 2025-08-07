package com.healthy.rvigor.bean;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/22 11:02
 * @UpdateRemark:
 */
public class SleepLenItem {

    private long startTime;
    private long endTime;

    public SleepLenItem(){

    }

    public SleepLenItem(long startTime, long endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

}
