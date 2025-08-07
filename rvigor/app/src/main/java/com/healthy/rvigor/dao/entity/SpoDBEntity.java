package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 血氧数据
 */
@Entity
public class SpoDBEntity {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 用户id
     */
    public   long  uid=0;

    /**
     * 血氧哪一天
     */
    public  long  SpoDay=0;

    /**
     * 设备名称
     */
    public  String  deviceName="";

    /**
     * 设备地址
     */
    public  String  deviceMacAddress="";


    /**
     * [{"spo":"234","datetime":dfffff}]  血氧  时间
     */
    public String  spoJsonData="[]";


    @Generated(hash = 577158587)
    public SpoDBEntity(Long id, long uid, long SpoDay, String deviceName,
            String deviceMacAddress, String spoJsonData) {
        this.id = id;
        this.uid = uid;
        this.SpoDay = SpoDay;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.spoJsonData = spoJsonData;
    }


    @Generated(hash = 1882288756)
    public SpoDBEntity() {
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


    public long getSpoDay() {
        return this.SpoDay;
    }


    public void setSpoDay(long SpoDay) {
        this.SpoDay = SpoDay;
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


    public String getSpoJsonData() {
        return this.spoJsonData;
    }


    public void setSpoJsonData(String spoJsonData) {
        this.spoJsonData = spoJsonData;
    }




    


}
