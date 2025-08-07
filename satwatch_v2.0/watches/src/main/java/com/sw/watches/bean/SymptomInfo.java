package com.sw.watches.bean;

import java.util.List;

/**
 * 症状
 */
public class SymptomInfo {

    private String symptomTime;  //症状时间

    private int number;  //症状编号

    public SymptomInfo(String symptomTime, int number){
        this.symptomTime = symptomTime;
        this.number = number;
    }

    public String getSymptomTime() {
        return symptomTime;
    }

    public void setSymptomTime(String symptomTime) {
        this.symptomTime = symptomTime;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
