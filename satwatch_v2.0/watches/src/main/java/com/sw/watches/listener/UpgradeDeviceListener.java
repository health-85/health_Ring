package com.sw.watches.listener;

/**
 * 更新设备监听
 */
public interface UpgradeDeviceListener {

    void onUpgradeDeviceError(int i1, int i2, String str);

    void onUpgradeDeviceProgress(int progress);

    void onUpgradeDeviceCompleted();

    void onUpgradeDeviceStarting(int start);
    void onUpgradeDeviceTip(String message);

    void onReConnectUpdateDevice(String s, boolean start);

}
