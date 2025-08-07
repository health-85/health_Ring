package com.sw.watches.bean;

import java.util.List;

public class EmotionInfo {

    private int TimeGap;

    private String emotionDate;

    private List<Integer> emotionList;

    public EmotionInfo(int TimeGap,String emotionDate, List<Integer> emotionList){
        this.TimeGap=TimeGap;
        this.emotionDate = emotionDate;
        this.emotionList = emotionList;
    }

    public int getEmotionTimeGap() {
        return TimeGap;
    }

    public String getEmotionDate() {
        return emotionDate;
    }

    public void setEmotionDate(String emotionDate) {
        this.emotionDate = emotionDate;
    }

    public List<Integer> getEmotionList() {
        return emotionList;
    }

    public void setEmotionList(List<Integer> emotionList) {
        this.emotionList = emotionList;
    }
}
