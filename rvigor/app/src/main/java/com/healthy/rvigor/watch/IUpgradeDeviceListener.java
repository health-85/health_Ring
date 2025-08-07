package com.healthy.rvigor.watch;

/**
 * 设备升级接口
 */
public interface IUpgradeDeviceListener {

    /**
     * 升级完成
     */
    void onUpgradeDeviceCompleted();

    /**
     * 升级错误
     * @param paramInt1
     * @param paramInt2
     * @param paramString
     */
    void onUpgradeDeviceError(int paramInt1, int paramInt2, String paramString);

    /**
     * 升级进度
     * @param paramInt
     */
    void onUpgradeDeviceProgress(int paramInt);

    /**
     * 升级开始
     * @param paramInt
     */
    void onUpgradeDeviceStarting(int paramInt);

    /**
     * 升级提示
     * @param tip
     */
    void onUpgradeDeviceTip(String tip);

    /**
     * 搜索设备
     * @param s
     */
    void onReConnectUpdateDevice(String s, boolean start);
}
