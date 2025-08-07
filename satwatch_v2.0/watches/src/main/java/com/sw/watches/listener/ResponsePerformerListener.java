package com.sw.watches.listener;

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

import java.util.ArrayList;

public class ResponsePerformerListener {
    
    public ResponsePerformerListener() {
        
    }

    public static void onResponseComplete(ArrayList<SimplePerformerListener> listeners) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseComplete();
        }
    }

    public static void onResponsePhoto(ArrayList<SimplePerformerListener> listeners) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponsePhoto();
        }
    }

    public static void onResponseFindPhone(ArrayList<SimplePerformerListener> listeners) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseFindPhone();
        }
    }

    public static void onResponsePPGInfo(ArrayList<SimplePerformerListener> listeners, PPGInfo ppgInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponsePPGInfo(ppgInfo);
        }
    }

    public static void onResponseDeviceMac(ArrayList<SimplePerformerListener> listeners, String s) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseDeviceMac(s);
        }
    }

    public static void onResponseECGInfo(ArrayList<SimplePerformerListener> listeners, ECGInfo ecgInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseECGInfo(ecgInfo);
        }
    }

    public static void onResponseSpoInfo(ArrayList<SimplePerformerListener> listeners, SpoInfo spoInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseSpoInfo(spoInfo);
        }
    }

    public static void onResponsePPGDateTime(ArrayList<SimplePerformerListener> listeners, PPGDateTime ppgDateTime) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponsePPGDateTime(ppgDateTime);
        }
    }

    public static void onResponseECGGDateTime(ArrayList<SimplePerformerListener> listeners, ECGDateTime ecgDateTime) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseECGGDateTime(ecgDateTime);
        }
    }

    public static void onResponseDeviceInfo(ArrayList<SimplePerformerListener> listeners, DeviceInfo deviceInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseDeviceInfo(deviceInfo);
        }
    }

    public static void onResponseMotionInfo(ArrayList<SimplePerformerListener> listeners, MotionInfo motionInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseMotionInfo(motionInfo);
        }
    }

    public static void onResponseSleepInfo(ArrayList<SimplePerformerListener> listeners, SleepInfo sleepInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseSleepInfo(sleepInfo);
        }
    }

    public static void onResponseSiestaInfo(ArrayList<SimplePerformerListener> listeners, SiestaInfo siestaInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseSiestaInfo(siestaInfo);
        }
    }

    public static void onResponseEncryp(ArrayList<SimplePerformerListener> listeners, byte[] bytes) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseEncryp(bytes);
        }
    }

    public static void onResponsePressureAndEmotionInfo(ArrayList<SimplePerformerListener> listeners, PressureInfo pressureInfo, EmotionInfo emotionInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponsePressureAndEmotionInfo(pressureInfo, emotionInfo);
        }
    }

    public static void onResponseTireInfo(ArrayList<SimplePerformerListener> listeners, TireInfo tireInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseTireInfoInfo(tireInfo);
        }
    }

    public static void onResponseTempInfo(ArrayList<SimplePerformerListener> listeners, HeatInfo heatInfo, EnviTempInfo enviTempInfo, UvInfo uvInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseTempInfoInfo(heatInfo, enviTempInfo, uvInfo);
        }
    }

    public static void onResponsePoHeartInfo(ArrayList<SimplePerformerListener> listeners, PoHeartInfo poHeartInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponsePoHeartInfo(poHeartInfo);
        }
    }

    public static void onResponseWoHeartInfo(ArrayList<SimplePerformerListener> listeners, WoHeartInfo woHeartInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseWoHeartInfo(woHeartInfo);
        }
    }

    public static void onResponseHeartInfo(ArrayList<SimplePerformerListener> listeners, HeartInfo heartInfo) {
        for (byte i = 0; i < listeners.size(); ++i) {
             listeners.get(i).onResponseHeartInfo(heartInfo);
        }
    }

    public static void onResponseByteArray(ArrayList<SimplePerformerListener> listeners, byte[] bytes) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseByteArray(bytes);
        }
    }

    public static void onResponseHeartListInfo(ArrayList<SimplePerformerListener> listeners, HeartListInfo info) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseHeartListInfo(info);
        }
    }

    public static void onResponseSleepLogInfo(ArrayList<SimplePerformerListener> listeners, SleepLogInfo info) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseSleepLogInfo(info);
        }
    }

    public static void onResponseBreatheInfo(ArrayList<SimplePerformerListener> listeners, BreatheInfo info) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseBreatheInfo(info);
        }
    }

    public static void onResponseSymptomListInfo(ArrayList<SimplePerformerListener> listeners, SymptomListInfo info) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseSymptomListInfo(info);
        }
    }

    public static void onResponseSymptomListInfo2(ArrayList<SimplePerformerListener> listeners, SymptomListInfo info) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseSymptomListInfo2(info);
        }
    }

    public static void onResponseSnoreInfo(ArrayList<SimplePerformerListener> listeners, SnoreInfo info) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseSnoreInfo(info);
        }
    }

    public static void onResponseStrengthInfo(ArrayList<SimplePerformerListener> listeners, StrengthInfo info) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseStrengthInfo(info);
        }
    }

    public static void onResponseStepInfo(ArrayList<SimplePerformerListener> listeners, int runStep){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseRunStep(runStep);
        }
    }

    public static void onResponseECGByteArray(ArrayList<SimplePerformerListener> listeners, byte[] bytes) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseECGByteArray(bytes);
        }
    }

    public static void onResponsePCGByteArray(ArrayList<SimplePerformerListener> listeners, byte[] bytes) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponsePCGByteArray(bytes);
        }
    }

    public static void onResponseTest(ArrayList<SimplePerformerListener> listeners, String s) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseTest(s);
        }
    }

    public static void onResponseWatchSaveInfo(ArrayList<SimplePerformerListener> listeners, WatchSaveInfo info, String log) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseWatchSaveInfo(info, log);
        }
    }

    public static void onResponseReceiveImgInfo(ArrayList<SimplePerformerListener> listeners, boolean isSendImgAgain, String msg) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseReceiveImgInfo(isSendImgAgain, msg);
        }
    }

    public static void onResponseStartSendImgInfo(ArrayList<SimplePerformerListener> listeners, String msg) {
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseStartSendImgInfo(msg);
        }
    }

    public static void onResponseLogInfo(ArrayList<SimplePerformerListener> listeners, String log){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseLogInfo(log);
        }
    }

    public static void onResponseAbnormalHeartListInfo(ArrayList<SimplePerformerListener> listeners, AbnormalHeartListInfo info){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseAbnormalHeartListInfo(info);
        }
    }

    public static void onResponseTestOx(ArrayList<SimplePerformerListener> listeners, int ox){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseTestOx(ox);
        }
    }

    public static void onResponseMeasureTemp(ArrayList<SimplePerformerListener> listeners, float heat, int temp){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseMeasureTemp(heat, temp);
        }
    }

    public static void onResponseMeasureTireAndPressure(ArrayList<SimplePerformerListener> listeners, int tire, int pressure){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseMeasureTireAndPressure(tire, pressure);
        }
    }

    public static void onResponseSwitchInfo(ArrayList<SimplePerformerListener> listeners, SwitchInfo info){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseSwitchInfo(info);
        }
    }

    public static void onResponseHRV(ArrayList<SimplePerformerListener> listeners, int hrv){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseHRVInfo(hrv);
        }
    }

    public static void onResponseSleepOxInfo(ArrayList<SimplePerformerListener> listeners, SleepOxInfo info){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseSleepOxInfo(info);
        }
    }

    public static void onResponseHrvInfo(ArrayList<SimplePerformerListener> listeners, HrvInfo info){
        for (byte i = 0; i < listeners.size(); ++i) {
            listeners.get(i).onResponseHrvInfo(info);
        }
    }
}
