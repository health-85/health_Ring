package com.sw.watches.bean;

import java.util.List;

public class SymptomListInfo {

    public List<SymptomInfo> list;

    public List<SymptomInfo2> symptomInfo2;

    public int symptomCount;

    public SymptomListInfo(List<SymptomInfo> list, int symptomCount){
        this.symptomCount = symptomCount;
        this.list = list;
    }

    public SymptomListInfo(){

    }

    public List<SymptomInfo> getList() {
        return list;
    }

    public void setList(List<SymptomInfo> list) {
        this.list = list;
    }

    public int getSymptomCount() {
        return symptomCount;
    }

    public void setSymptomCount(int symptomCount) {
        this.symptomCount = symptomCount;
    }

    public List<SymptomInfo2> getSymptomInfo2() {
        return symptomInfo2;
    }

    public void setSymptomInfo2(List<SymptomInfo2> symptomInfo2) {
        this.symptomInfo2 = symptomInfo2;
    }
}
