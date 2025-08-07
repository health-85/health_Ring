package com.sw.watches.bean;


import java.util.List;

public class SpoInfo {
    public List<SpoData> spoList;

    public SpoInfo(List<SpoData> spoList) {
        this.spoList = spoList;
    }

    public List<SpoData> getSpoList() {
        return this.spoList;
    }

    public void setSpoList(List<SpoData> spoList) {
        this.spoList = spoList;
    }
}
