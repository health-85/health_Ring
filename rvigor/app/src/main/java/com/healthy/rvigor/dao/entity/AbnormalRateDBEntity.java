package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class AbnormalRateDBEntity {

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
     * 心率哪一天
     */
    public long HeartRateDay = 0;

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    /**
     * 一分钟心率
     */
//    public boolean isOneMin;

    /**
     * [{"rate":234,"datetime":dfffff}]  心率   时间
     */
    public String heartJsonData = "[]";

    @Generated(hash = 1185145958)
    public AbnormalRateDBEntity(Long id, long uid, int isupLoadToServer,
            long HeartRateDay, String deviceName, String deviceMacAddress,
            String heartJsonData) {
        this.id = id;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.HeartRateDay = HeartRateDay;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.heartJsonData = heartJsonData;
    }

    @Generated(hash = 111442241)
    public AbnormalRateDBEntity() {
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

    public long getHeartRateDay() {
        return this.HeartRateDay;
    }

    public void setHeartRateDay(long HeartRateDay) {
        this.HeartRateDay = HeartRateDay;
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

    public String getHeartJsonData() {
        return this.heartJsonData;
    }

    public void setHeartJsonData(String heartJsonData) {
        this.heartJsonData = heartJsonData;
    }


}
