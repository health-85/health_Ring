package com.healthy.rvigor.watch

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/7 19:24
 * @UpdateRemark:   设备链接监听
 */
interface IWatchConnectingListener {

    fun onConnectingStart(watch: WatchBase?)

    /**
     * 手表链接成功
     *
     * @param watch
     */
    fun onConnectedAndWrite(watch: WatchBase?)

    /**
     * 设备断开链接
     *
     * @param watch
     */
    fun onDisconnect(watch: WatchBase?)

    /**
     * 设备链接失败
     *
     * @param watch
     */
    fun onConnectFailed(watch: WatchBase?)

    /**
     * 设备重新进行连接F38使用
     *
     * @param watch
     */
    fun onReConnect(watch: WatchBase?)

    /**
     * 设备链接成功
     *
     * @param watch
     */
    fun onConnectSuccess(watch: WatchBase?)
}