package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class PressureDBEntity {

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
     * 压力哪一天
     */
    public long pressureDay = 0;

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    public String pressureJsonData="[]";

    @Generated(hash = 1248702898)
    public PressureDBEntity(Long id, long uid, int isupLoadToServer,
            long pressureDay, String deviceName, String deviceMacAddress,
            String pressureJsonData) {
        this.id = id;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.pressureDay = pressureDay;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.pressureJsonData = pressureJsonData;
    }

    @Generated(hash = 1303681108)
    public PressureDBEntity() {
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

    public long getPressureDay() {
        return this.pressureDay;
    }

    public void setPressureDay(long pressureDay) {
        this.pressureDay = pressureDay;
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

    public String getPressureJsonData() {
        return this.pressureJsonData;
    }

    public void setPressureJsonData(String pressureJsonData) {
        this.pressureJsonData = pressureJsonData;
    }


}
