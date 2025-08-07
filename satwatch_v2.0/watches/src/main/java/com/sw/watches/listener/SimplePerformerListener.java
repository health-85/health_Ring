package com.sw.watches.listener;

import com.sw.watches.bean.AbnormalHeartInfo;
import com.sw.watches.bean.AbnormalHeartListInfo;
import com.sw.watches.bean.BreatheInfo;
import com.sw.watches.bean.EmotionInfo;
import com.sw.watches.bean.HeartListInfo;
import com.sw.watches.bean.DeviceInfo;
import com.sw.watches.bean.ECGDateTime;
import com.sw.watches.bean.ECGInfo;
import com.sw.watches.bean.EnviTempInfo;
import com.sw.watches.bean.HeartInfo;
import com.sw.watches.bean.HeatInfo;
import com.sw.watches.bean.HrvInfo;
import com.sw.watches.bean.MotionInfo;
import com.sw.watches.bean.PPGDateTime;
import com.sw.watches.bean.PPGInfo;
import com.sw.watches.bean.PoHeartInfo;
import com.sw.watches.bean.PressureInfo;
import com.sw.watches.bean.SiestaInfo;
import com.sw.watches.bean.SleepInfo;
import com.sw.watches.bean.SleepLogInfo;
import com.sw.watches.bean.SleepOxInfo;
import com.sw.watches.bean.SnoreInfo;
import com.sw.watches.bean.SpoInfo;
import com.sw.watches.bean.StrengthInfo;
import com.sw.watches.bean.SwitchInfo;
import com.sw.watches.bean.SymptomListInfo;
import com.sw.watches.bean.TireInfo;
import com.sw.watches.bean.UvInfo;
import com.sw.watches.bean.WatchSaveInfo;
import com.sw.watches.bean.WoHeartInfo;

import java.util.List;

/**
 * 解析数据监听实现类
 */
public class SimplePerformerListener implements PerformerListener {

    public void onResponseDeviceInfo(DeviceInfo deviceInfo) {}

    public void onResponseMotionInfo(MotionInfo motionInfo) {}

    public void onResponseSleepInfo(SleepInfo sleepInfo) {}

    public void onResponseSiestaInfo(SiestaInfo siestaInfo) {}

    public void onResponsePoHeartInfo(PoHeartInfo poHeartInfo) {}

    public void onResponseWoHeartInfo(WoHeartInfo woHeartInfo) {}

    public void onResponseComplete() {}

    public void onResponsePhoto() {}

    public void onResponseFindPhone() {}

    public void onResponseHeartInfo(HeartInfo heartInfo) {}

    public void onResponsePPGInfo(PPGInfo ppgInfo) {}

    public void onResponseDeviceMac(String s) {}

    public void onResponseECGInfo(ECGInfo ecgInfo) {}

    public void onResponseSpoInfo(SpoInfo spoInfo) {}

    public void onResponsePPGDateTime(PPGDateTime ppgDateTime) {}

    public void onResponseECGGDateTime(ECGDateTime ecgDateTime) {}

    public void onResponseEncryp(byte[] bytes) {}

    public void onResponseByteArray(byte[] bytes) {}

    public void onResponseHeartListInfo(HeartListInfo info) {}
    public void onResponseSleepLogInfo(SleepLogInfo info) {}
    public void onResponseBreatheInfo(BreatheInfo info) {}
    public void onResponseSymptomListInfo(SymptomListInfo info) {}
    public void onResponseSymptomListInfo2(SymptomListInfo info) {}
    public void onResponseSnoreInfo(SnoreInfo info) {}
    public void onResponseStrengthInfo(StrengthInfo info) {}
    public void onResponseWatchSaveInfo(WatchSaveInfo info, String log) {}
    public void onResponseLogInfo(String log) {}
    public void onResponseStartSendImgInfo(String msg) {}
    public void onResponseReceiveImgInfo(boolean isSendImgAgain, String msg) {}
    public void onResponseAbnormalHeartListInfo(AbnormalHeartListInfo infos) {}

    public void onResponseECGByteArray(byte[] bytes) {}

    public void onResponsePCGByteArray(byte[] bytes) {}

    public void onResponseTest(String s) {}

    public void onResponsePressureAndEmotionInfo(PressureInfo pressureInfo, EmotionInfo emotionInfo) {}

    public void onResponseTireInfoInfo(TireInfo tireInfo) {}
    public void onResponseRunStep(int step) {}

    public void onResponseTempInfoInfo(HeatInfo heatInfo, EnviTempInfo enviTempInfo, UvInfo uvInfo) {}
    //返回的血氧
    public void onResponseTestOx(int ox) {}
    //返回的体温和环境温度
    public void onResponseMeasureTemp(float heat, int temp) {}
    //返回的疲劳和压力
    public void onResponseMeasureTireAndPressure(int tire, int pressure) {}

    public void onResponseSwitchInfo(SwitchInfo info){}

    public void onResponseHRVInfo(int hrv){}
    public void onResponseSleepOxInfo(SleepOxInfo sleepOxInfo){}
    public void onResponseHrvInfo(HrvInfo hrvInfo){}
}