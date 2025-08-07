package com.sw.watches.bean;

/**
 * 异常心率
 */
public class AbnormalHeartInfo {

    private String time;

    private int heart;

    public AbnormalHeartInfo(String time, int heart){
        this.time = time;
        this.heart = heart;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }
}
