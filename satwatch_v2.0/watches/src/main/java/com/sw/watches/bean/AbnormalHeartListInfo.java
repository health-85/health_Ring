package com.sw.watches.bean;

import java.util.List;

public class AbnormalHeartListInfo {

    private List<AbnormalHeartInfo> list;

    public AbnormalHeartListInfo(List<AbnormalHeartInfo> infoList){
        this.list = infoList;
    }

    public List<AbnormalHeartInfo> getList() {
        return list;
    }

    public void setList(List<AbnormalHeartInfo> list) {
        this.list = list;
    }
}
