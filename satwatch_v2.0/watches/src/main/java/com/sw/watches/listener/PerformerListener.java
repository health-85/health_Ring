package com.sw.watches.listener;

import com.sw.watches.bean.DeviceInfo;
import com.sw.watches.bean.ECGDateTime;
import com.sw.watches.bean.ECGInfo;
import com.sw.watches.bean.HeartInfo;
import com.sw.watches.bean.MotionInfo;
import com.sw.watches.bean.PPGDateTime;
import com.sw.watches.bean.PPGInfo;
import com.sw.watches.bean.PoHeartInfo;
import com.sw.watches.bean.SleepInfo;
import com.sw.watches.bean.SpoInfo;
import com.sw.watches.bean.WoHeartInfo;

/**
 * 解析数据结果监听
 */
public interface PerformerListener {

    /**
     * 设备信息
     */
    void onResponseDeviceInfo(DeviceInfo deviceInfo);

    /**
     * 运动数据
     */
    void onResponseMotionInfo(MotionInfo motionInfo);

    /**
     * 睡眠数据
     * @param sleepInfo
     */
    void onResponseSleepInfo(SleepInfo sleepInfo);

    /**心脏数据
     * @param poHeartInfo
     */
    void onResponsePoHeartInfo(PoHeartInfo poHeartInfo);

    /**
     * @param woHeartInfo
     */
    void onResponseWoHeartInfo(WoHeartInfo woHeartInfo);
    /**
     * 血压数据
     * @param heartInfo
     */
    void onResponseHeartInfo(HeartInfo heartInfo);

    /**
     * ppg数据
     * @param ppgInfo
     */
    void onResponsePPGInfo(PPGInfo ppgInfo);

    /**
     * ecg数据
     * @param ecgInfo
     */
    void onResponseECGInfo(ECGInfo ecgInfo);

    /**
     * 血氧数据
     * @param spoInfo
     */
    void onResponseSpoInfo(SpoInfo spoInfo);

    void onResponseComplete();

    void onResponsePhoto();

    void onResponseFindPhone();

    void onResponseDeviceMac(String mac);

    void onResponsePPGDateTime(PPGDateTime ppgDateTime);

    void onResponseECGGDateTime(ECGDateTime ecgDateTime);
}
