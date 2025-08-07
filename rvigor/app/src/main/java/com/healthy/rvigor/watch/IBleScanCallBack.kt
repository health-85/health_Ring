package com.healthy.rvigor.watch

import android.bluetooth.BluetoothDevice

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/7 19:23
 * @UpdateRemark:
 */
interface IBleScanCallBack {

    /**
     * 扫描开始
     */
    fun scanStarted()

    /**
     * 扫描收到的设备
     *
     * @param device
     * @param rssi
     * @param scanRecord
     */
    fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?)

    /**
     * 扫描结束
     */
    fun scanStop()
}