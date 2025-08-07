package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class BreatheDBEntity {

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
     * 哪一天
     */
    public long day = 0;

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    public String breatheJsonData="[]";

    public int hypopnea = 0; //低通气指数;

    public int blockLen = 0; //累计阻塞时长;

    public int chaosIndex = 0; //呼吸紊乱指数;

    public int pauseCount = 0; //呼吸暂停次数;

    @Generated(hash = 1906497970)
    public BreatheDBEntity(Long id, long uid, int isupLoadToServer, long day,
            String deviceName, String deviceMacAddress, String breatheJsonData,
            int hypopnea, int blockLen, int chaosIndex, int pauseCount) {
        this.id = id;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.day = day;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.breatheJsonData = breatheJsonData;
        this.hypopnea = hypopnea;
        this.blockLen = blockLen;
        this.chaosIndex = chaosIndex;
        this.pauseCount = pauseCount;
    }

    @Generated(hash = 467489117)
    public BreatheDBEntity() {
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

    public long getDay() {
        return this.day;
    }

    public void setDay(long day) {
        this.day = day;
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

    public String getBreatheJsonData() {
        return this.breatheJsonData;
    }

    public void setBreatheJsonData(String breatheJsonData) {
        this.breatheJsonData = breatheJsonData;
    }

    public int getHypopnea() {
        return this.hypopnea;
    }

    public void setHypopnea(int hypopnea) {
        this.hypopnea = hypopnea;
    }

    public int getBlockLen() {
        return this.blockLen;
    }

    public void setBlockLen(int blockLen) {
        this.blockLen = blockLen;
    }

    public int getChaosIndex() {
        return this.chaosIndex;
    }

    public void setChaosIndex(int chaosIndex) {
        this.chaosIndex = chaosIndex;
    }

    public int getPauseCount() {
        return this.pauseCount;
    }

    public void setPauseCount(int pauseCount) {
        this.pauseCount = pauseCount;
    }



}
