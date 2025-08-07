package com.healthy.rvigor.bean;

import java.util.List;

public class StepBean {
//    public int hisCount;
//    public int hisLength;
//    public int id;
    public String stepDate;
    public int sportMode;              // 0x0 步行(跑步、步行) 0x1 骑车； 0x2 游泳； 0x3 跳绳 0x4 俯卧撑 0x5 爬山 0x6 羽毛球 0x7 冰球 0x8 棒球  0x9 拳击  0xa 竞走，0xb 体操，0xc足球，0xd篮球,0xe 划船机
    public int stepCalorie;            // 卡路里
    public String stepDay;             // 日期（yyyy-MM-dd)
    public double stepMileage;            // 里程
    public int stepNum;                // 步数
    public int stepTime;               // stepTime
    public int stepType;               // 0代表当前 1代表分段计步 2代表运动
    public List<Integer> stepData;
}
