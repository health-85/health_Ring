package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 血压 心率数据
 */
@Entity
public class XueYaXinLvDBEntity {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 测量的UUID
     */
    public String testUUID = "";

    /**
     * 测量是哪一天
     */
    public long testDay = 0;
    /**
     * 测量的具体时间
     */
    public long testDate = 0;

    /**
     * 用户id
     */
    public long uid = 0;

    /**
     * 是否已经同步到服务器了 0未同步  1已同步
     */
    public int isupLoadToServer = 0;

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 测量的血压 心率数据集合  [{"HR"：心率，"HBP":高压，"LBP":低压}]
     */
    public String XueYaXinLvJsonArrayData = "[]";

    @Generated(hash = 1435756981)
    public XueYaXinLvDBEntity(Long id, String testUUID, long testDay, long testDate,
            long uid, int isupLoadToServer, String deviceMacAddress,
            String deviceName, String XueYaXinLvJsonArrayData) {
        this.id = id;
        this.testUUID = testUUID;
        this.testDay = testDay;
        this.testDate = testDate;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.deviceMacAddress = deviceMacAddress;
        this.deviceName = deviceName;
        this.XueYaXinLvJsonArrayData = XueYaXinLvJsonArrayData;
    }

    @Generated(hash = 1766495979)
    public XueYaXinLvDBEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestUUID() {
        return this.testUUID;
    }

    public void setTestUUID(String testUUID) {
        this.testUUID = testUUID;
    }

    public long getTestDay() {
        return this.testDay;
    }

    public void setTestDay(long testDay) {
        this.testDay = testDay;
    }

    public long getTestDate() {
        return this.testDate;
    }

    public void setTestDate(long testDate) {
        this.testDate = testDate;
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

    public String getDeviceMacAddress() {
        return this.deviceMacAddress;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getXueYaXinLvJsonArrayData() {
        return this.XueYaXinLvJsonArrayData;
    }

    public void setXueYaXinLvJsonArrayData(String XueYaXinLvJsonArrayData) {
        this.XueYaXinLvJsonArrayData = XueYaXinLvJsonArrayData;
    }



}
