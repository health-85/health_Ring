package com.healthy.rvigor.net.http;

/**
 * 用户接口管理
 */
public interface IActivityManagerCallBase {
    /**
     * 获取唯一UUID
     *
     * @return
     */
    public String getUUID();

    /**
     * 移除指定UUID的对话框
     */
    public void CloseProgressByUUID(String ProgressUUID);


    /**
     * 主动关闭ui
     */
    public void close();
}
