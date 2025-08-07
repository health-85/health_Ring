package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/14 10:46
 * @UpdateRemark:
 */
@Entity
public class SleepDBEntity {
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
     * 睡眠的日期 是哪一天
     */
    public long sleepDay = 0;

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    /**
     * 睡眠开始时间
     */
    public long startDateTime = 0;

    /**
     * 睡眠时长
     */
    public long sleeplength = 0;


    /**
     * sleeptype 1深睡 2浅睡 3清醒 4入睡 5 熬夜  starttime睡眠开始  endtime 睡眠结束 long 型  微秒
     * 睡眠json 数据 [{"sleeptype":1,"starttime":12345,"endtime",12345}]
     */
    public String sleepJsonData = "[]";


    @Generated(hash = 1673210887)
    public SleepDBEntity(Long id, long uid, int isupLoadToServer, long sleepDay,
            String deviceName, String deviceMacAddress, long startDateTime,
            long sleeplength, String sleepJsonData) {
        this.id = id;
        this.uid = uid;
        this.isupLoadToServer = isupLoadToServer;
        this.sleepDay = sleepDay;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.startDateTime = startDateTime;
        this.sleeplength = sleeplength;
        this.sleepJsonData = sleepJsonData;
    }


    @Generated(hash = 922536488)
    public SleepDBEntity() {
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


    public long getSleepDay() {
        return this.sleepDay;
    }


    public void setSleepDay(long sleepDay) {
        this.sleepDay = sleepDay;
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


    public long getStartDateTime() {
        return this.startDateTime;
    }


    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }


    public long getSleeplength() {
        return this.sleeplength;
    }


    public void setSleeplength(long sleeplength) {
        this.sleeplength = sleeplength;
    }


    public String getSleepJsonData() {
        return this.sleepJsonData;
    }


    public void setSleepJsonData(String sleepJsonData) {
        this.sleepJsonData = sleepJsonData;
    }
}
