package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class SiestaDBEntity {

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
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    /**
     * 睡眠的日期 是哪一天
     */
    public long siestaDay = 0;

    /**
     * 睡眠时长
     */
    public long siestaLength = 0;

    public String startTime; //开始时间
    public String endTime; //结束时间
    @Generated(hash = 1491252193)
    public SiestaDBEntity(Long id, long uid, int isupLoadToServer,
            String deviceName, String deviceMacAddress, long siestaDay,
            long siestaLength, String startTime, String endTime) {
        this.id = id;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.siestaDay = siestaDay;
        this.siestaLength = siestaLength;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    @Generated(hash = 1028879502)
    public SiestaDBEntity() {
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
    public long getSiestaDay() {
        return this.siestaDay;
    }
    public void setSiestaDay(long siestaDay) {
        this.siestaDay = siestaDay;
    }
    public long getSiestaLength() {
        return this.siestaLength;
    }
    public void setSiestaLength(long siestaLength) {
        this.siestaLength = siestaLength;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return this.endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }



}
