package com.sw.watches.bean;


import java.util.List;

public class MotionInfo {
    public String motionDate;

    public int motionCount;

    public int motionTime;

    public String calorie;

    public String distance;

    public float calorieData;

    public float distanceData;

    public List<Integer> stepData;

    public int totalStep;

    public MotionInfo(String motionDate, int motionCount, int motionTime, String calorie, String distance, List<Integer> stepData, int totalStep) {
        this.motionDate = motionDate;
        this.motionCount = motionCount;
        this.motionTime = motionTime;
        this.calorie = calorie;
        this.distance = distance;
        this.stepData = stepData;
        this.totalStep = totalStep;
    }

    public MotionInfo() {}

    public String getCalorie() {
        return this.calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getDistance() {
        return this.distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getTotalStep() {
        return this.totalStep;
    }

    public void setTotalStep(int totalStep) {
        this.totalStep = totalStep;
    }

    public String getMotionDate() {
        return this.motionDate;
    }

    public void setMotionDate(String motionDate) {
        this.motionDate = motionDate;
    }

    public int getMotionCount() {
        return this.motionCount;
    }

    public void setMotionCount(int motionCount) {
        this.motionCount = motionCount;
    }

    public int getMotionTime() {
        return this.motionTime;
    }

    public void setMotionTime(int motionTime) {
        this.motionTime = motionTime;
    }

    public List<Integer> getStepData() {
        return this.stepData;
    }

    public void setStepData(List<Integer> stepData) {
        this.stepData = stepData;
    }

    public float getCalorieData() {
        return calorieData;
    }

    public void setCalorieData(float calorieData) {
        this.calorieData = calorieData;
    }

    public float getDistanceData() {
        return distanceData;
    }

    public void setDistanceData(float distanceData) {
        this.distanceData = distanceData;
    }
}