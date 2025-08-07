package com.healthy.rvigor.bean;

import java.util.List;

public class EmotionBean {

    public int lastEmotion;              // 最新压力
    public long emotionTime;
    public String date;           // 时间
    public List<Integer> list;

    public int getLastEmotion() {
        return lastEmotion;
    }

    public void setLastEmotion(int lastEmotion) {
        this.lastEmotion = lastEmotion;
    }

    public long getEmotionTime() {
        return emotionTime;
    }

    public void setEmotionTime(long emotionTime) {
        this.emotionTime = emotionTime;
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
}
