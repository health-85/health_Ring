package com.sw.watches.listener;

/**
 * 蓝牙连接监听
 */
public interface ConnectorListener {

    /**
     * 连接成功
     */
    void onConnectAndWrite();

    /**
     * 断开连接
     */
    void onDisconnect();

    /**
     * 连接失败
     */
    void onConnectFailed();

    /**
     * 重新连接
     */
    void onReConnect();
    /**
     * 连接成功
     */
    void onConnectSuccess();
}

