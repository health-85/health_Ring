package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class StepDBEntity {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 用户id
     */
    public long uid = 0;

    /**
     * 是否已经同步到服务器了 0未同步  1已同步
     */
    public int isupLoadToServer = 0;

    /**
     * 跑步的日期 是哪一天
     */
    private long stepDay = 0;

    /**
     * 跑步的总步数
     */
    private long totalStep = 0;

    /**
     * 卡路里
     */
    private double stepCalorie = 0;

    /**
     * 里程
     */
    public double stepMileage = 0;

    /**
     * 步数分布时间段信息  [{timestep:时间段 0  凌晨0-8   1上午8-12  2下午12-18  3晚上18-24,;step:步数},{}] json片段信息
     */
    public String stepDataJsonArrayForTime = "[]";

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";


    /**
     * 步行模式  // 0x0 步行(跑步、步行) 0x1 骑车； 0x2 游泳； 0x3 跳绳 0x4 俯卧撑 0x5 爬山 0x6 羽毛球 0x7 冰球 0x8 棒球  0x9 拳击  0xa 竞走，0xb 体操，0xc足球，0xd篮球,0xe 划船机
     */
    public int sportMode = 0;


    @Generated(hash = 1484128418)
    public StepDBEntity(Long id, long uid, int isupLoadToServer, long stepDay, long totalStep, double stepCalorie, double stepMileage,
            String stepDataJsonArrayForTime, String deviceName, String deviceMacAddress, int sportMode) {
        this.id = id;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.stepDay = stepDay;
        this.totalStep = totalStep;
        this.stepCalorie = stepCalorie;
        this.stepMileage = stepMileage;
        this.stepDataJsonArrayForTime = stepDataJsonArrayForTime;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.sportMode = sportMode;
    }


    @Generated(hash = 1632715664)
    public StepDBEntity() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public long getUid() {
        return this.uid;
    }


    public void setUid(long uid) {
        this.uid = uid;
    }


    public int getIsupLoadToServer() {
        return this.isupLoadToServer;
    }


    public void setIsupLoadToServer(int isupLoadToServer) {
        this.isupLoadToServer = isupLoadToServer;
    }


    public long getStepDay() {
        return this.stepDay;
    }


    public void setStepDay(long stepDay) {
        this.stepDay = stepDay;
    }


    public long getTotalStep() {
        return this.totalStep;
    }


    public void setTotalStep(long totalStep) {
        this.totalStep = totalStep;
    }


    public double getStepCalorie() {
        return this.stepCalorie;
    }


    public void setStepCalorie(double stepCalorie) {
        this.stepCalorie = stepCalorie;
    }


    public double getStepMileage() {
        return this.stepMileage;
    }


    public void setStepMileage(double stepMileage) {
        this.stepMileage = stepMileage;
    }


    public String getStepDataJsonArrayForTime() {
        return this.stepDataJsonArrayForTime;
    }


    public void setStepDataJsonArrayForTime(String stepDataJsonArrayForTime) {
        this.stepDataJsonArrayForTime = stepDataJsonArrayForTime;
    }


    public String getDeviceName() {
        return this.deviceName;
    }


    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    public String getDeviceMacAddress() {
        return this.deviceMacAddress;
    }


    public void setDeviceMacAddress(String deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }


    public int getSportMode() {
        return this.sportMode;
    }


    public void setSportMode(int sportMode) {
        this.sportMode = sportMode;
    }




}
