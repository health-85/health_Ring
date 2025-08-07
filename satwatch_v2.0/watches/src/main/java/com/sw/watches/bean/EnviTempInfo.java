package com.sw.watches.bean;

import java.util.List;

/**
 * 环境温度
 */
public class EnviTempInfo {

    public String date;

    public List<Integer> list;

    public EnviTempInfo(String date, List<Integer> list){
        this.date = date;
        this.list = list;
    }
}
