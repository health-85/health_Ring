package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EmotionDBEntity {

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
     * 情绪温度哪一天
     */
    public long tempDay = 0;

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    public String tempJsonData="[]";

    @Generated(hash = 513399331)
    public EmotionDBEntity(Long id, long uid, int isupLoadToServer, long tempDay,
            String deviceName, String deviceMacAddress, String tempJsonData) {
        this.id = id;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.tempDay = tempDay;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.tempJsonData = tempJsonData;
    }

    @Generated(hash = 1024503548)
    public EmotionDBEntity() {
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

    public long getTempDay() {
        return this.tempDay;
    }

    public void setTempDay(long tempDay) {
        this.tempDay = tempDay;
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

    public String getTempJsonData() {
        return this.tempJsonData;
    }

    public void setTempJsonData(String tempJsonData) {
        this.tempJsonData = tempJsonData;
    }


}
