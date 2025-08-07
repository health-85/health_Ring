package com.sdk.satwatch.listener;

import android.bluetooth.BluetoothDevice;

/**
 * 扫描监听
 */
public interface ScannerListener {

    /**
     * 重新连接设备
     */
    public void reconnectDevice();

    /**
     * 扫描开始
     */
    public void scanStarted();

    /**
     * 扫描收到的设备
     *
     * @param device
     * @param rssi
     * @param scanRecord
     */
    public void onFoundScan(BluetoothDevice device, int rssi, byte[] scanRecord);

    /**
     * 扫描结束
     */
    public void scanStoped();
}
