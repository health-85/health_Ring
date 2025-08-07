package com.sw.watches.bean;

import java.util.List;

public class HeartListInfo {

    private List<HeartInfo> list;

    public HeartListInfo(){

    }

    public HeartListInfo(List<HeartInfo> list){
        this.list = list;
    }

    public List<HeartInfo> getList() {
        return list;
    }

    public void setList(List<HeartInfo> list) {
        this.list = list;
    }
}
