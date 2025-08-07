package com.healthy.rvigor.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class SnoreNewDBEntity {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 用户id
     */
    public long uid = 0;

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

    /**
     * 打鼾时长
     */
    public int snoreLen;
    /**
     * 打鼾最大分贝
     */
    public int maxDbF;
    /**
     * 打鼾平均分贝
     */
    public int averageDb;
    /**
     * 打鼾最小分贝
     */
    public int minDbF;
    /**
     * 鼾声指数
     */
    public float snoreIndex;
    /**
     * 打鼾频次
     */
    public int snoreFrequency;
    /**
     * 正常鼾声
     */
    public int snoreNormal;
    /**
     * 轻度鼾声
     */
    public int snoreMild;
    /**
     * 中度鼾声
     */
    public int snoreMiddle;
    /**
     * 重度鼾声
     */
    public int snoreSerious;

    public String snoreJsonData="[]";

    @Generated(hash = 1331030618)
    public SnoreNewDBEntity(Long id, long uid, long day, String deviceName,
            String deviceMacAddress, int snoreLen, int maxDbF, int averageDb,
            int minDbF, float snoreIndex, int snoreFrequency, int snoreNormal,
            int snoreMild, int snoreMiddle, int snoreSerious,
            String snoreJsonData) {
        this.id = id;
        this.uid = uid;
        this.day = day;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.snoreLen = snoreLen;
        this.maxDbF = maxDbF;
        this.averageDb = averageDb;
        this.minDbF = minDbF;
        this.snoreIndex = snoreIndex;
        this.snoreFrequency = snoreFrequency;
        this.snoreNormal = snoreNormal;
        this.snoreMild = snoreMild;
        this.snoreMiddle = snoreMiddle;
        this.snoreSerious = snoreSerious;
        this.snoreJsonData = snoreJsonData;
    }

    @Generated(hash = 1801800812)
    public SnoreNewDBEntity() {
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

    public int getSnoreLen() {
        return this.snoreLen;
    }

    public void setSnoreLen(int snoreLen) {
        this.snoreLen = snoreLen;
    }

    public int getMaxDbF() {
        return this.maxDbF;
    }

    public void setMaxDbF(int maxDbF) {
        this.maxDbF = maxDbF;
    }

    public int getAverageDb() {
        return this.averageDb;
    }

    public void setAverageDb(int averageDb) {
        this.averageDb = averageDb;
    }

    public int getMinDbF() {
        return this.minDbF;
    }

    public void setMinDbF(int minDbF) {
        this.minDbF = minDbF;
    }

    public float getSnoreIndex() {
        return this.snoreIndex;
    }

    public void setSnoreIndex(float snoreIndex) {
        this.snoreIndex = snoreIndex;
    }

    public int getSnoreFrequency() {
        return this.snoreFrequency;
    }

    public void setSnoreFrequency(int snoreFrequency) {
        this.snoreFrequency = snoreFrequency;
    }

    public int getSnoreNormal() {
        return this.snoreNormal;
    }

    public void setSnoreNormal(int snoreNormal) {
        this.snoreNormal = snoreNormal;
    }

    public int getSnoreMild() {
        return this.snoreMild;
    }

    public void setSnoreMild(int snoreMild) {
        this.snoreMild = snoreMild;
    }

    public int getSnoreMiddle() {
        return this.snoreMiddle;
    }

    public void setSnoreMiddle(int snoreMiddle) {
        this.snoreMiddle = snoreMiddle;
    }

    public int getSnoreSerious() {
        return this.snoreSerious;
    }

    public void setSnoreSerious(int snoreSerious) {
        this.snoreSerious = snoreSerious;
    }

    public String getSnoreJsonData() {
        return this.snoreJsonData;
    }

    public void setSnoreJsonData(String snoreJsonData) {
        this.snoreJsonData = snoreJsonData;
    }






}
