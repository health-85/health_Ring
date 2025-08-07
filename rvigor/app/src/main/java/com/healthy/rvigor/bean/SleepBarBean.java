package com.healthy.rvigor.bean;

import android.graphics.Rect;

import java.util.List;

public class SleepBarBean {

    public static int EMPTY = Integer.MIN_VALUE;
    //睡眠时长
    public long sleepLength;
    //底部文字
    private String bottomString;
    //显示时间
    private String showTime;

    private int wakeCount;

    private long deepLength;

    private long remLength;

    private long fallLength;

    private long lightLength;

    private long wakeLength;

    private long siestaLength;

    private int siestaCount;

    private long startTime;

    private long endTime;

    private long auyelenTime;

    private long sleepDay;

    private long earlyTime;

    private int totalScore;

    private String startSiestaTime;

    private String endSiestaTime;

    private int totalLen;

    private String endMonth;

    private int sign; // 0 睡眠数据 1 空白  2 文字

    private int reachDay; //达标天数

    //高血压风险
    private int highScore;
    //脱发风险
    private int hairScore;
    //皮肤老化指数
    private int skinScore;
    //情绪指数
    private int emotionScore;

    public String showTimeString;

    private List<SleepItem> list;

    private List<SleepDayBean> dayBeanList;

    public Rect currentDrawRect = new Rect(0, 0, 0, 0);

    public static class Size {

        public int width = 0;
        public int height = 0;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public SleepBarBean(){

    }

    public SleepBarBean(long sleepLength, long deepLength, long lightLength, long wakeLength, long siestaLength,
                        String bottomString, String showTime){
        this.sleepLength = sleepLength;
        this.deepLength = deepLength;
        this.lightLength = wakeLength;
        this.wakeLength = sleepLength;
        this.siestaLength = siestaLength;
        this.bottomString = bottomString;
        this.showTime = showTime;
    }

    public SleepBarBean(long sleepLength, long deepLength, long lightLength, long wakeLength, long siestaLength,
                        String bottomString, String showTime, List<SleepItem> list){
        this.sleepLength = sleepLength;
        this.deepLength = deepLength;
        this.lightLength = wakeLength;
        this.wakeLength = sleepLength;
        this.siestaLength = siestaLength;
        this.bottomString = bottomString;
        this.showTime = showTime;
        this.list = list;
    }

    public long getSleepLength() {
        return sleepLength;
    }

    public void setSleepLength(long sleepLength) {
        this.sleepLength = sleepLength;
    }

    public String getBottomString() {
        return bottomString;
    }

    public void setBottomString(String bottomString) {
        this.bottomString = bottomString;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public long getDeepLength() {
        return deepLength;
    }

    public void setDeepLength(long deepLength) {
        this.deepLength = deepLength;
    }

    public long getLightLength() {
        return lightLength;
    }

    public void setLightLength(long lightLength) {
        this.lightLength = lightLength;
    }

    public long getWakeLength() {
        return wakeLength;
    }

    public void setWakeLength(long wakeLength) {
        this.wakeLength = wakeLength;
    }

    public long getSiestaLength() {
        return siestaLength;
    }

    public void setSiestaLength(long siestaLength) {
        this.siestaLength = siestaLength;
    }

    public List<SleepItem> getList() {
        return list;
    }

    public void setList(List<SleepItem> list) {
        this.list = list;
    }

    public int getWakeCount() {
        return wakeCount;
    }

    public void setWakeCount(int wakeCount) {
        this.wakeCount = wakeCount;
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

    public long getAuyelenTime() {
        return auyelenTime;
    }

    public void setAuyelenTime(long auyelenTime) {
        this.auyelenTime = auyelenTime;
    }

    public long getSleepDay() {
        return sleepDay;
    }

    public void setSleepDay(long sleepDay) {
        this.sleepDay = sleepDay;
    }

    public long getEarlyTime() {
        return earlyTime;
    }

    public void setEarlyTime(long earlyTime) {
        this.earlyTime = earlyTime;
    }

    public String getStartSiestaTime() {
        return startSiestaTime;
    }

    public void setStartSiestaTime(String startSiestaTime) {
        this.startSiestaTime = startSiestaTime;
    }

    public String getEndSiestaTime() {
        return endSiestaTime;
    }

    public void setEndSiestaTime(String endSiestaTime) {
        this.endSiestaTime = endSiestaTime;
    }

    public long getFallLength() {
        return fallLength;
    }

    public void setFallLength(long fallLength) {
        this.fallLength = fallLength;
    }

    public int getTotalLen() {
        return totalLen;
    }

    public void setTotalLen(int totalLen) {
        this.totalLen = totalLen;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getSiestaCount() {
        return siestaCount;
    }

    public void setSiestaCount(int siestaCount) {
        this.siestaCount = siestaCount;
    }

    public List<SleepDayBean> getDayBeanList() {
        return dayBeanList;
    }

    public void setDayBeanList(List<SleepDayBean> dayBeanList) {
        this.dayBeanList = dayBeanList;
    }

    public long getRemLength() {
        return remLength;
    }

    public void setRemLength(long remLength) {
        this.remLength = remLength;
    }

    public String getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public int getReachDay() {
        return reachDay;
    }

    public void setReachDay(int reachDay) {
        this.reachDay = reachDay;
    }

    public String getShowTimeString() {
        return showTimeString;
    }

    public void setShowTimeString(String showTimeString) {
        this.showTimeString = showTimeString;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getHairScore() {
        return hairScore;
    }

    public void setHairScore(int hairScore) {
        this.hairScore = hairScore;
    }

    public int getSkinScore() {
        return skinScore;
    }

    public void setSkinScore(int skinScore) {
        this.skinScore = skinScore;
    }

    public int getEmotionScore() {
        return emotionScore;
    }

    public void setEmotionScore(int emotionScore) {
        this.emotionScore = emotionScore;
    }
}
