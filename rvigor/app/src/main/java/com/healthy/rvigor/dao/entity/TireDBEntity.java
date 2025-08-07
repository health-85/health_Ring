package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class TireDBEntity {

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
     * 疲劳哪一天
     */
    public long tireDay = 0;

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    public String tireJsonData="[]";

    @Generated(hash = 42949092)
    public TireDBEntity(Long id, long uid, int isupLoadToServer, long tireDay,
            String deviceName, String deviceMacAddress, String tireJsonData) {
        this.id = id;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.tireDay = tireDay;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.tireJsonData = tireJsonData;
    }

    @Generated(hash = 716011649)
    public TireDBEntity() {
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

    public long getTireDay() {
        return this.tireDay;
    }

    public void setTireDay(long tireDay) {
        this.tireDay = tireDay;
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

    public String getTireJsonData() {
        return this.tireJsonData;
    }

    public void setTireJsonData(String tireJsonData) {
        this.tireJsonData = tireJsonData;
    }


}
