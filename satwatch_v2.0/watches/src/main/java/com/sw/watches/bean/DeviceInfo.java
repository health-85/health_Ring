package com.sw.watches.bean;

public class DeviceInfo {
    public int deviceBattery;

    public int deviceType;

    public int deviceVersionNumber;

    public int versionRule;

    public String deviceVersionName;

    public void setDeviceBattery(int deviceBattery) {
        this.deviceBattery = deviceBattery;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public void setDeviceVersionName(String deviceVersionName) {
        this.deviceVersionName = deviceVersionName;
    }

    public DeviceInfo(){

    }

    public DeviceInfo(int deviceBattery, int deviceType, int deviceVersionNumber, int versionRule, String deviceVersionName) {
        setDeviceBattery(deviceBattery);
        setDeviceType(deviceType);
        setVersionRule(versionRule);
        setDeviceVersionNumber(deviceVersionNumber);
        setDeviceVersionName(deviceVersionName);
    }

    public int getDeviceBattery() {
        return deviceBattery;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public String getDeviceVersionName() {
        return deviceVersionName;
    }

    public int getDeviceVersionNumber() {
        return deviceVersionNumber;
    }

    public void setDeviceVersionNumber(int deviceVersionNumber) {
        this.deviceVersionNumber = deviceVersionNumber;
    }

    public int getVersionRule() {
        return versionRule;
    }

    public void setVersionRule(int versionRule) {
        this.versionRule = versionRule;
    }
}
