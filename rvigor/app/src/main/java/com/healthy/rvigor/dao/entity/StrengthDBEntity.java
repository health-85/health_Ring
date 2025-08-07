package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class StrengthDBEntity {

    @Id(autoincrement = true)
    private Long id;

    public long uid = 0;

    /**
     * 是否已经同步到服务器了 0未同步  1已同步
     */
    public int isupLoadToServer = 0;

    /**
     * 环境温度哪一天
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

    public int inLow;

    public int inCentre;

    public int inHigh;

    public String jsonData = "[]";

    @Generated(hash = 1504057770)
    public StrengthDBEntity(Long id, long uid, int isupLoadToServer, long day,
            String deviceName, String deviceMacAddress, int inLow, int inCentre,
            int inHigh, String jsonData) {
        this.id = id;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.day = day;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.inLow = inLow;
        this.inCentre = inCentre;
        this.inHigh = inHigh;
        this.jsonData = jsonData;
    }

    @Generated(hash = 1616557318)
    public StrengthDBEntity() {
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

    public int getInLow() {
        return this.inLow;
    }

    public void setInLow(int inLow) {
        this.inLow = inLow;
    }

    public int getInCentre() {
        return this.inCentre;
    }

    public void setInCentre(int inCentre) {
        this.inCentre = inCentre;
    }

    public int getInHigh() {
        return this.inHigh;
    }

    public void setInHigh(int inHigh) {
        this.inHigh = inHigh;
    }

    public String getJsonData() {
        return this.jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }




}
