package com.healthy.rvigor.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.healthy.rvigor.BuildConfig;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.R;
import com.healthy.rvigor.bean.LanguageType;
import com.healthy.rvigor.dao.executor.InsertAbnormalHeartExecutor;
import com.healthy.rvigor.event.WatchSyncEvent;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.LogUtils;
import com.healthy.rvigor.util.SPUtil;
import com.healthy.rvigor.util.SpConfig;
import com.healthy.rvigor.watch.SIATWatch;
import com.healthy.rvigor.watch.WatchBase;
import com.sw.watches.application.ZhbraceletApplication;
import com.sw.watches.bean.AbnormalHeartInfo;
import com.sw.watches.bean.AbnormalHeartListInfo;
import com.sw.watches.bean.BreatheInfo;
import com.sw.watches.bean.DeviceInfo;
import com.sw.watches.bean.DeviceModule;
import com.sw.watches.bean.ECGInfo;
import com.sw.watches.bean.EmotionInfo;
import com.sw.watches.bean.EnviTempInfo;
import com.sw.watches.bean.HeartInfo;
import com.sw.watches.bean.HeartListInfo;
import com.sw.watches.bean.HeatInfo;
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
import com.sw.watches.bean.WoHeartInfo;
import com.sw.watches.listener.ConnectorListener;
import com.sw.watches.listener.SimplePerformerListener;
import com.sw.watches.listener.UpgradeDeviceListener;
import com.sw.watches.service.ZhBraceletService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * SIAT 设备服务
 */
public class SIATDeviceService extends ZhBraceletService {

    private static final String TAG = "SIATDeviceService";

    /**
     * 是否服务已经销毁
     */
    private boolean isDestoryed = false;

    /**
     * 设备芯片类型 0:C(正常芯片) 1:D(VD版本)
     */
    private int deviceChipType = 0;
    //是否在运动中
    private boolean isRun = false;

    private ExecutorService mECGExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        isDestoryed = false;
        MyApplication.Companion.instance().setSiatDeviceService(this);
        ZhbraceletApplication.getInstance().setZhBraceletService(this);
        LogUtils.i(TAG + " addSimplePerformerListenerLis ");
        this.addConnectorListener(deviceConnectorListener);
        this.addSimplePerformerListenerLis(simplePerformerListener);
//        WristbandManager.getInstance(MainApplication.getInstance()).registerCallback(wristbandManagerCallback); //监听Cavo Sdk 回调
        this.addUpgradeDeviceListener(upgradeDeviceListener);//添加设备升级回调
        EventBus.getDefault().post(new SIATDeviceServiceCreatedEvent());
        MyApplication.Companion.instance().getTimerRepeatExecutor().AddTask(looprunnable, 4000);
        setForegroundService("健康管理服务");
        acquireWakeLock();//锁屏不关闭cpu
//        initC100Sdk(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        LogUtils.i(TAG + " onStartCommand ");
//        addSimplePerformerListenerLis(simplePerformerListener);
        return super.onStartCommand(intent, flags, startId);
    }

    private PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    /**
     * 配置服务
     *
     * @param title
     */
    private void setForegroundService(String title) {
        try {
            Notification notification = null;
            if (Build.VERSION.SDK_INT >= 26) {
                createNotificationChannel("channel_watch" + 0, title);
                Notification.Builder builder = getChannelNotification("channel_watch" + 0, R.drawable.icon_app, title, title);
                notification = builder.build();
            } else {
                NotificationCompat.Builder builder = getNotification_25(R.drawable.icon_app, title, title);
                notification = builder.build();
            }
            if (notification != null) {
                startForeground(1, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private NotificationManager manager = null;

    @SuppressLint("NewApi")
    private void createNotificationChannel(String id, String name) {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 200, 200});
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channel);
    }


    @SuppressLint("NewApi")
    private Notification.Builder getChannelNotification(String id, int smallResId, String title, String content) {
        return new Notification.Builder(this, id)
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(smallResId)
                .setAutoCancel(true);
    }


    private NotificationCompat.Builder getNotification_25(int smallResId, String title, String content) {
        return new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallResId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }


    @Override
    public void onDestroy() {
        isDestoryed = true;
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        MyApplication.Companion.instance().getTimerRepeatExecutor().removeTask(looprunnable);
        UnBindDevice();
        this.removeSimplePerformerListenerLis(simplePerformerListener);
//        WristbandManager.getInstance(MainApplication.getInstance()).registerCallback(wristbandManagerCallback);
        this.removeConnectorListener(deviceConnectorListener);
        this.removeUpgradeDeviceListener();//移除设备升级回调
        MyApplication.Companion.instance().setSiatDeviceService(null);
        if (executor != null) {
            executor.shutdown();
        }
        if (ECGExecutor != null) {
            ECGExecutor.shutdown();
        }
        if (PPGExecutor != null) {
            PPGExecutor.shutdown();
        }
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }


    private Runnable looprunnable = new Runnable() {
        @Override
        public void run() {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(LooprunnableInUI);
        }
    };

    private Runnable LooprunnableInUI = new Runnable() {
        @Override
        public void run() {
            if (!isDestoryed) {
                doneConnectionTimeout();
            }
        }
    };

    /**
     * 链接时间
     */
    private List<Long> conntimes = new LinkedList<>();

    /**
     * 处理链接超时
     */
    private void doneConnectionTimeout() {
        if (conntimes.size() > 0) {
            long time = conntimes.get(0);
            if ((System.currentTimeMillis() - time) > 40000) {//如果链接超过20秒
                conntimes.clear();
                istimeout = true;
                UnBindDevice();
            }
        }
    }


    /**
     * 设备升级
     */
    private UpgradeDeviceListener upgradeDeviceListener = new UpgradeDeviceListener() {

        @Override
        public void onUpgradeDeviceError(int i, int i1, String s) {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new UpgradeDeviceErrorUIRun(i, i1, s));
        }

        @Override
        public void onUpgradeDeviceProgress(int i) {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new UpgradeDeviceProgressUIRun(i));
        }

        @Override
        public void onUpgradeDeviceCompleted() {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(UpgradeDeviceCompletedRunable);
        }

        /**
         * 设备更新开始
         * @param i
         */
        @Override
        public void onUpgradeDeviceStarting(int i) {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new UpgradeDeviceStartingUIRun(i));
        }

        @Override
        public void onUpgradeDeviceTip(String s) {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new UpgradeDeviceTipUIRun(s));
        }

        @Override
        public void onReConnectUpdateDevice(String s, boolean start) {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new reConnectUpdateDeviceUIRun(s, start));
        }

    };


    private Runnable UpgradeDeviceCompletedRunable = new Runnable() {
        @Override
        public void run() {
            MyApplication.Companion.instance().getBleUtils().performUpgradeDeviceCompleted();
        }
    };


    private static class UpgradeDeviceErrorUIRun implements Runnable {

        private int i = 0;
        private int a = 0;
        private String errorString = "";

        public UpgradeDeviceErrorUIRun(int i, int a, String errorString) {
            this.i = i;
            this.a = a;
            this.errorString = errorString;
        }

        @Override
        public void run() {
            MyApplication.Companion.instance().getBleUtils().performUpgradeDeviceError(i, a, errorString);
        }
    }


    /**
     * 设备更新开始
     */
    private static class UpgradeDeviceStartingUIRun implements Runnable {

        private int i = 0;

        public UpgradeDeviceStartingUIRun(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            MyApplication.Companion.instance().getBleUtils().performUpgradeDeviceStarting(i);
        }
    }

    /**
     * 设备更新开始
     */
    private static class UpgradeDeviceTipUIRun implements Runnable {

        private String s = "";

        public UpgradeDeviceTipUIRun(String s) {
            this.s = s;
        }

        @Override
        public void run() {
            MyApplication.Companion.instance().getBleUtils().performUpgradeDeviceTip(s);
        }
    }

    /**
     * 设备更新中
     */
    private static class UpgradeDeviceProgressUIRun implements Runnable {

        private int i = 0;

        public UpgradeDeviceProgressUIRun(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            MyApplication.Companion.instance().getBleUtils().performUpgradeDeviceProgress(i);
        }
    }

    /**
     * 搜索设备
     */
    private static class reConnectUpdateDeviceUIRun implements Runnable {

        private String s = "";
        private boolean isStart;

        public reConnectUpdateDeviceUIRun(String s, boolean isStart) {
            this.s = s;
            this.isStart = isStart;
        }

        @Override
        public void run() {
            MyApplication.Companion.instance().getBleUtils().performOnReConnectUpdateDevice(s, isStart);
        }
    }

    /**
     * 服务已启动事件
     */
    public static class SIATDeviceServiceCreatedEvent {

    }

    private SimplePerformerListener simplePerformerListener = new SimplePerformerListener() {
        public void onResponseDeviceInfo(DeviceInfo var1) {
            LogUtils.i("DeviceInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "DeviceInfo", var1));
        }

        public void onResponseMotionInfo(MotionInfo var1) {
            LogUtils.i("MotionInfo onResponseMotionInfo");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "MotionInfo", var1));
        }

        public void onResponseSleepInfo(SleepInfo var1) {
            LogUtils.i("SleepInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SleepInfo", var1));
        }

        public void onResponsePoHeartInfo(PoHeartInfo var1) {
            LogUtils.i("PoHeartInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "PoHeartInfo", var1));
        }

        public void onResponseWoHeartInfo(WoHeartInfo var1) {
            LogUtils.i("WoHeartInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "WoHeartInfo", var1));
        }

        public void onResponseComplete() {
            LogUtils.i(" onResponseComplete ");
            EventBus.getDefault().post(new WatchSyncEvent(true, 100));
//            if (BuildConfig.DEBUG){
//                SymptomListInfo listInfo = new SymptomListInfo();
//                List<SymptomInfo2> symptomInfo2s = new ArrayList<>();
//                SymptomInfo2 symptomInfo2 = new SymptomInfo2();
//                symptomInfo2.setSymptomTime("2024-04-9 10:10:10");
//                symptomInfo2.setScore(new Random().nextInt(6) + 2);
//                int[] array = new int[8];
//                for (int i = 0; i < 2; i++){
//                    array[i] =  i + 1;
//                }
//                symptomInfo2.setSymptomArray(array);
//                symptomInfo2s.add(symptomInfo2);
//                listInfo.setSymptomInfo2(symptomInfo2s);
//                if (listInfo.getSymptomInfo2() != null && listInfo.getSymptomInfo2().size() > 0) {
//                    MyApplication.Companion.instance().getUiHandler()
//                            .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SymptomListInfo2", listInfo));
//                }
//            }

//            if (BuildConfig.DEBUG){
//                String time = DateTimeUtils.s_long_2_str(System.currentTimeMillis(), DateTimeUtils.f_format);
//                List<AbnormalHeartInfo> heartInfoList = new ArrayList<>();
//                AbnormalHeartInfo heartListInfo = new AbnormalHeartInfo(time, 180);
//                heartInfoList.add(heartListInfo);
//                AbnormalHeartListInfo listInfo = new AbnormalHeartListInfo(heartInfoList);
//                InsertAbnormalHeartExecutor abnormalHeartExecutor = new InsertAbnormalHeartExecutor(listInfo,
//                        MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id, "", "");
//                MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(abnormalHeartExecutor);
//            }

            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "ResponseComplete", null));
        }

        public void onResponsePhoto() {
            LogUtils.i("onResponsePhoto onResponse");
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "ResponsePhoto", null));
        }

        public void onResponseFindPhone() {
            LogUtils.i("onResponseFindPhone onResponse");
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "FindPhone", null));
        }

        public void onResponseHeartInfo(HeartInfo var1) {
            LogUtils.i("HeartInfo onResponse ");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "HeartInfo", var1));
        }

        public void onResponsePPGInfo(PPGInfo var1) {
            LogUtils.i("PPGInfo onResponse");
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "PPGInfo", var1));
        }

        public void onResponseDeviceMac(String var1) {
            LogUtils.i("onResponseDeviceMac onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "DeviceMac", var1));
        }

        public void onResponseECGInfo(ECGInfo var1) {
            LogUtils.i("onResponseECGInfo onResponse");
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "ECGInfo", var1));
        }


        public void onResponseSpoInfo(SpoInfo var1) {
            LogUtils.i("onResponseSpoInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SpoInfo", var1));
        }

        public void onResponsePPGDateTime(PPGDateTime var1) {
            LogUtils.i("onResponsePPGDateTime onResponse");
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "PPGDateTime", var1));
        }

        @Override
        public void onResponseSiestaInfo(SiestaInfo siestaInfo) {
            LogUtils.i("SiestaInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            if (siestaInfo != null && siestaInfo.getSleepTime() > 0) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SiestaInfo", siestaInfo));
            }
        }

        @Override
        public void onResponseTireInfoInfo(TireInfo tireInfo) {
            LogUtils.i("TireInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            super.onResponseTireInfoInfo(tireInfo);
            if (tireInfo != null && tireInfo.getList() != null && tireInfo.getList().size() > 0) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "TireInfo", tireInfo));
            }
        }

        @Override
        public void onResponsePressureAndEmotionInfo(PressureInfo pressureInfo, EmotionInfo emotionInfo) {
            super.onResponsePressureAndEmotionInfo(pressureInfo, emotionInfo);
            LogUtils.i("EmotionInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            if (pressureInfo != null && pressureInfo.getPressureList() != null && pressureInfo.getPressureList().size() > 0) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "PressureInfo", pressureInfo));
            }
            if (emotionInfo != null && emotionInfo.getEmotionList() != null && emotionInfo.getEmotionList().size() > 0) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "EmotionInfo", emotionInfo));
            }
        }

        @Override
        public void onResponseTempInfoInfo(HeatInfo heatInfo, EnviTempInfo enviTempInfo, UvInfo uvInfo) {
            LogUtils.i("HeatInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            if (heatInfo != null && heatInfo.list != null && heatInfo.list.size() > 0) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "HeatInfo", heatInfo));
            }
            if (enviTempInfo != null && enviTempInfo.list != null && enviTempInfo.list.size() > 0) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "EnviTempInfo", enviTempInfo));
            }
            if (uvInfo != null && uvInfo.list != null && uvInfo.list.size() > 0) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "UvInfo", uvInfo));
            }
        }

        @Override
        public void onResponseHeartListInfo(HeartListInfo info) {
            LogUtils.i("HeartListInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            super.onResponseHeartListInfo(info);
            if (info != null && info.getList() != null && info.getList().size() > 0) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "HeartListInfo", info));
            }
        }

        @Override
        public void onResponseSleepLogInfo(SleepLogInfo info) {
//            LogUtils.i("SleepLogInfo onResponse");
            super.onResponseSleepLogInfo(info);
//            if (BuildConfig.DEBUG) {
//                String dayMsg = "2024-01-26 02:52:00";
//                long time = DateTimeUtils.s_str_to_long(dayMsg, DateTimeUtils.f_format);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(time);
//                String oxStr = "94,96,95,94,94,98,97,99,96,98,96,96,96,96,96,96,96,96,96,96,94,99,98,95,95,93,93,98,93,99,100,99,99,100,95,98,99,99,98,95,94,99,96,98,99,98,93,98,98,96,97,98,96,96,94,96,97,98,98,98,93,97,98,97,96,95,97,96,97,95,96,96,100,94,.95,97,97,97,97,98,97,96,94,95,98,97,95,98,94,96,98,98,96,96,95,97,97,96,97,96,97,97,96,98,99,96,95,95,99,98,97,98,95,95,99,96,98,96,97,93,99,96,93,100,97,96,95,99,10o,97,98,98,99,97,98,98,97,99,97,98,95,98,97,97,99,96,99,97,98,95,99,98,98,96,98,96,98,100,99,98,98,98,95,99,96,97,96,98,99,98,94,97,97,97,99,100,98,98,96,98,95,97,99,98,98,99,95,98,97,99,97,94,93,94,96,99,93,97,99,98,99,97,99,97,100,95,97,98,98,99,99,97,97,97,99,96,99,96,98,98,97,99,98,98,97,97,99,99,96,98,98,98,96,97,95,96,95,99,97,97,97,97,98,96,99,98,98,93,98,93,97,95,98,99,95,95,98,97,99.99,97,100,100,99,93,97,96,95,95,96,98,99,96,99,93,98,10o,99,93,96,98,94,99,98,94,97,96,97,95,95,97,99,95,98,97,99,96,98,94,93,93,97,97,98,98,95,96,98,98,97,93,99,98,98,98,95,96,96,99,100,99,97,95,97,94,98,95,95,96,97,94,95,96,96,97,96,98,98,96,95,97,97,96,96,99,94,98,99,96,";
//                String[] oxArr = oxStr.split(",");
//                SleepOxInfo sleepOxInfo = new SleepOxInfo();
//                sleepOxInfo.setTime(calendar.getTimeInMillis());
//                List<Integer> list = new ArrayList<>();
//                for (int i = 0; i < oxArr.length; i++) {
//                    if (!TextUtils.isEmpty(oxArr[i])) {
//                        int ox = NumberUtils.stringToInt(oxArr[i].trim(), 0);
//                        if (ox > 0) list.add(ox);
//                    }
//                }
//                sleepOxInfo.setList(list);
//                MyApplication.Companion.instance().getUiHandler()
//                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SleepOxInfo", sleepOxInfo));
//            }
            if (info != null && info.getList() != null && info.getList().size() > 0) {
//                LogUtils.i(" onResponseSleepLogInfo " + new Gson().toJson(info));
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SleepLogInfo", info));
            }
        }

        @Override
        public void onResponseBreatheInfo(BreatheInfo info) {
            LogUtils.i("BreatheInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            super.onResponseBreatheInfo(info);
            if (info != null && info.getList() != null && info.getList().size() > 0) {
//                LogUtils.i(" onResponseBreatheInfo " + new Gson().toJson(info));
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "BreatheInfo", info));
            }
        }

        @Override
        public void onResponseSnoreInfo(SnoreInfo info) {
            LogUtils.i("SnoreInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            super.onResponseSnoreInfo(info);
            if (info == null) return;
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SnoreInfo", info));
        }

        @Override
        public void onResponseSymptomListInfo(SymptomListInfo info) {
            super.onResponseSymptomListInfo(info);
            LogUtils.i("SymptomListInfo onResponse");
            if (info != null && info.getList() != null && info.getList().size() > 0) {
//                LogUtils.i(" SymptomListInfo " + new Gson().toJson(info));
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SymptomListInfo", info));
            }
        }

        @Override
        public void onResponseSymptomListInfo2(SymptomListInfo info) {
            super.onResponseSymptomListInfo2(info);
            if (info != null && info.getSymptomInfo2() != null && info.getSymptomInfo2().size() > 0) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SymptomListInfo2", info));
            }
        }

        @Override
        public void onResponseStrengthInfo(StrengthInfo info) {
            super.onResponseStrengthInfo(info);
            LogUtils.i("StrengthInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            if (info != null) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "StrengthInfo", info));
            }
        }

        @Override
        public void onResponseAbnormalHeartListInfo(AbnormalHeartListInfo infos) {
            super.onResponseAbnormalHeartListInfo(infos);
            LogUtils.i("AbnormalHeartListInfo onResponse");
            EventBus.getDefault().post(new WatchSyncEvent(true, 10));
            if (infos != null && infos.getList() != null && !infos.getList().isEmpty()) {
                MyApplication.Companion.instance().getUiHandler()
                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "AbnormalHeartInfo", infos));
            }
        }

        @Override
        public void onResponseTestOx(int ox) {
            super.onResponseTestOx(ox);
            if (ox > 0) {
                MyApplication.Companion.instance().getUiHandler().PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "MeasureOx", ox));
            }
        }

        @Override
        public void onResponseMeasureTemp(float heat, int temp) {
            super.onResponseMeasureTemp(heat, temp);
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "MeasureHeat", heat));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "MeasureTemp", temp));
        }

        @Override
        public void onResponseMeasureTireAndPressure(int tire, int pressure) {
            super.onResponseMeasureTireAndPressure(tire, pressure);
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "MeasureTire", tire));
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "MeasurePressure", pressure));
        }

        @Override
        public void onResponseRunStep(int step) {
            super.onResponseRunStep(step);
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "RunStep", step));
        }

        @Override
        public void onResponseSwitchInfo(SwitchInfo switchInfo) {
            super.onResponseSwitchInfo(switchInfo);
            if (switchInfo != null) {
                SPUtil.saveData(getApplicationContext(), SpConfig.IS_HIGH_HEART_REMIND, switchInfo.isHeartRemind());
                SPUtil.saveData(getApplicationContext(), SpConfig.IS_SIT_REMIND, switchInfo.isSitRemind());
                SPUtil.saveData(getApplicationContext(), SpConfig.IS_SLEEP_REMIND, switchInfo.isSleepRemind());
                SPUtil.saveData(getApplicationContext(), SpConfig.IS_LOW_OX_REMIND, switchInfo.isLowOxRemind());
                SPUtil.saveData(getApplicationContext(), SpConfig.IS_DISTURB_REMIND, switchInfo.isDisturbRemind());
                SPUtil.saveData(getApplicationContext(), SpConfig.IS_LANGUAGE_REMIND, switchInfo.isLanguageRemind());
                SPUtil.saveData(getApplicationContext(), SpConfig.LANGUAGE_TYPE, switchInfo.isLanguageRemind() ? LanguageType.LANGUAGE_SAMPLE_CHINESE : LanguageType.LANGUAGE_ENGLISH);
            }
        }

        @Override
        public void onResponseReceiveImgInfo(boolean isResult, String msg) {
            super.onResponseReceiveImgInfo(isResult, msg);
        }

        @Override
        public void onResponseStartSendImgInfo(String msg) {
            super.onResponseStartSendImgInfo(msg);
        }

        @Override
        public void onResponseSleepOxInfo(SleepOxInfo sleepOxInfo) {
            super.onResponseSleepOxInfo(sleepOxInfo);
            if (sleepOxInfo == null || sleepOxInfo.getList() == null || sleepOxInfo.getList().isEmpty())
                return;
            MyApplication.Companion.instance().getUiHandler()
                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "SleepOxInfo", sleepOxInfo));
        }
    };

//    //Cavo监听
//    private WristbandManagerCallback wristbandManagerCallback = new WristbandManagerCallback() {
//
//        @Override
//        public void onLoginStateChange(int state) {
//            super.onLoginStateChange(state);
//            if (state == WristbandManager.STATE_WRIST_LOGIN) {
//                Log.i(TAG, "onLoginStateChange 成功");
//                CavoSdkUtil.getInstance().getDeviceSettingInfo();
//            }
//            LogUtils.i(" onLoginStateChange state " + state);
//        }
//
//        @Override
//        public void onBondReqChipType(int type) {
//            super.onBondReqChipType(type);
//            // 在登录成功后会触发此回调，用户需自行记录设备芯片类型
//            // 若没有触发此回调，则是默认芯片类型 0
//            // This callback will be triggered after successful login. The user needs to record the device chip type by himself.
//            // If this callback is not triggered, the default chip type is 0
//            deviceChipType = type;
//            Log.e("SSSS", "deviceChipType = " + deviceChipType);
//        }
//
//        //微信、QQ、电话、信息通知
//        @Override
//        public void onNotifyModeSettingReceive(ApplicationLayerNotifyPacket applicationLayerNotifyPacket) {
//            super.onNotifyModeSettingReceive(applicationLayerNotifyPacket);
//            Log.i(TAG, "reminderFunction = " + applicationLayerNotifyPacket.toString());
//            if (spDeviceTools == null) {
//                spDeviceTools = new SpDeviceTools(SIATDeviceService.this);
//            }
//            spDeviceTools.putRemindQQ(applicationLayerNotifyPacket.getQQ().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindWhatsapp(applicationLayerNotifyPacket.getWhatsApp().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindLinkedin(applicationLayerNotifyPacket.getLinkedIn().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindLine(applicationLayerNotifyPacket.getLine().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindTwitter(applicationLayerNotifyPacket.getTwitter().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindViber(applicationLayerNotifyPacket.getViber().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindGmail(applicationLayerNotifyPacket.getGmail().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindInstagram(applicationLayerNotifyPacket.getInstagram().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindFacebook(applicationLayerNotifyPacket.getFacebook().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindSkype(applicationLayerNotifyPacket.getSkype().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindMms(applicationLayerNotifyPacket.getSms().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.puyRemindWx(applicationLayerNotifyPacket.getWeChat().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindOutlook(applicationLayerNotifyPacket.getOther().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//            spDeviceTools.putRemindSnapchat(applicationLayerNotifyPacket.getOther().equals(DeviceFunctionStatus.SUPPORT_OPEN));
//        }
//
//        //电量
//        @Override
//        public void onBatteryRead(int value) {
//            super.onBatteryRead(value);
//            MyApplication.Companion.instance().getUiHandler()
//                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "Battery", value));
//            Log.i(TAG, "onBatteryRead battery : " + value);
//        }
//
//        @Override
//        public void onBatteryChange(int value) {
//            super.onBatteryChange(value);
//            MyApplication.Companion.instance().getUiHandler()
//                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "Battery", value));
//            Log.i(TAG, "onBatteryChange battery : " + value);
//        }
//
//        //同步数据
//        @Override
//        public void onSyncDataBegin(ApplicationLayerBeginPacket packet) {
//            super.onSyncDataBegin(packet);
//            Log.i(TAG, "sync begin");
//        }
//
//        @Override
//        public void onStepDataReceiveIndication(ApplicationLayerStepPacket packet) {
//            super.onStepDataReceiveIndication(packet);
//            for (ApplicationLayerStepItemPacket item : packet.getStepsItems()) {
//                Log.i(TAG, " onStepDataReceiveIndication " + item.toString());
//            }
//            Log.i(TAG, " onStepDataReceiveIndication size = " + packet.getStepsItems().size());
//        }
//
//        @Override
//        public void onSleepDataReceiveIndication(ApplicationLayerSleepPacket packet) {
//            super.onSleepDataReceiveIndication(packet);
////            for (ApplicationLayerSleepItemPacket item : packet.getSleepItems()) {
////                Log.i(TAG, " onSleepDataReceiveIndication " + item.toString());
////            }
////            Log.i(TAG, " onSleepDataReceiveIndication size = " + packet.getSleepItems().size());
//        }
//
//        //心率数据
//        @Override
//        public void onHrpDataReceiveIndication(ApplicationLayerHrpPacket packet) {
//            super.onHrpDataReceiveIndication(packet);
////            for (ApplicationLayerHrpItemPacket item : packet.getHrpItems()) {
////                HeartInfo heartInfo = (HeartInfo) new HeartInfo(item.getValue(), 0, 0);
////                MyApplication.Companion.instance().getUiHandler()
////                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "HeartInfo", heartInfo));
////                Log.i(TAG, " onHrpDataReceiveIndication Value " + item.getValue() + " " + item.toString());
////            }
////            Log.i(TAG, " onHrpDataReceiveIndication size = " + packet.getHrpItems().size());
//        }
//
//        //心率数据
//        @Override
//        public void onDeviceCancelSingleHrpRead() {
//            super.onDeviceCancelSingleHrpRead();
////            Log.i(TAG, "onDeviceCancelSingleHrpRead measure hr ");
//        }
//
//        @Override
//        public void onRateList(ApplicationLayerRateListPacket packet) {
//            super.onRateList(packet);
////            for (ApplicationLayerRateItemPacket item : packet.getRateList()) {
////                Log.i(TAG, " onRateList " + item.toString());
////            }
////            Log.i(TAG, " onRateList size = " + packet.getRateList().size());
//        }
//
//        @Override
//        public void onSportFunction(ApplicationLayerMultiSportPacket applicationLayerMultiSportPacket) {
//            super.onSportFunction(applicationLayerMultiSportPacket);
//            Log.i(TAG, " onSportFunction applicationLayerMultiSportPacket = ");
//        }
//
//        @Override
//        public void onSportRateStatus(int status) {
//            super.onSportRateStatus(status);
//            Log.i(TAG, " onSportRateStatus status = " + status);
//        }
//
////        @Override
////        public void onSportDataReceiveIndication(ApplicationLayerSportPacket packet) {
////            super.onSportDataReceiveIndication(packet);
////            if (packet != null && packet.getSportItems() != null && packet.getSportItems().size() > 0) {
////                for (ApplicationLayerSportItemPacket item : packet.getSportItems()) {
////                    Log.i(TAG, " onSportDataReceiveIndication " + item.toString());
////                }
////                Log.i(TAG, " onSportDataReceiveIndication size = " + packet.getSportItems().size());
////            }
////        }
//
//        //温度检测回调
//        // temperature measure data call back
//        @Override
//        public void onTemperatureData(ApplicationLayerHrpPacket packet) {
//            super.onTemperatureData(packet);
//            for (ApplicationLayerHrpItemPacket item : packet.getHrpItems()) {
//                MyApplication.Companion.instance().getUiHandler()
//                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "MeasureV101Heat", item));
////                Log.i(TAG, "onTemperatureData temp " + item.getTemperature() + " tempOriginValue " + item.getTempOriginValue() + " data " + item.toString());
//            }
////            Log.i(TAG, "onTemperatureData size = " + packet.getHrpItems().size());
//        }
//
//        //温度历史数据回调
//        // temperature history data call back
//        @Override
//        public void onTemperatureList(ApplicationLayerRateListPacket packet) {
//            super.onTemperatureList(packet);
////            for (ApplicationLayerRateItemPacket item : packet.getRateList()) {
////                Log.i(TAG, "onTemperatureList = " + item.toString());
////            }
////            Log.i(TAG, "onTemperatureList size = " + packet.getRateList().size());
//        }
//
//        @Override
//        public void onSyncDataEnd(ApplicationLayerTodaySumSportPacket packet) {
//            super.onSyncDataEnd(packet);
//            Log.i(TAG, "sync end");
//            if (!isRun) {
//                CavoSdkUtil.getInstance().loadCavoLocalData();
//            }
//        }
//
//        /**
//         * 血压自动检测回调
//         *
//         * bp auto measure callback
//         *
//         * @param packet
//         */
//        @Override
//        public void onBpList(ApplicationLayerBpListPacket packet) {
//            super.onBpList(packet);
//            for (ApplicationLayerBpListItemPacket item : packet.getBpListItemPackets()) {
//                Log.i(TAG, "bpItem = " + item.toString());
//            }
//            CavoSdkUtil.getInstance().parseBloodData(packet);
//            Log.i(TAG, "bp size = " + packet.getBpListItemPackets().size());
//        }
//
//        //血压回调
//        public void onBpDataReceiveIndication(ApplicationLayerBpPacket packet) {
//            super.onBpDataReceiveIndication(packet);
//            for (ApplicationLayerBpItemPacket item : packet.getBpItems()) {
//                BloodMeasure bloodMeasureBean = new BloodMeasure(item.getmHighValue(), item.getmLowValue(), System.currentTimeMillis());
//                Log.i(TAG, "bp high :" + item.getmHighValue() + " low : " + item.getmLowValue() + "  " + item.toString());
//                MyApplication.Companion.instance().getUiHandler()
//                        .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "BloodMeasure", bloodMeasureBean));
//            }
//        }
//
//        @Override
//        public void onDeviceCancelSingleBpRead() {
//            super.onDeviceCancelSingleBpRead();
//            Log.i(TAG, "stop measure bp ");
//        }
//
//        @Override
//        public void onBp2Control(ApplicationLayerBp2ControlPacket packet) {
//            super.onBp2Control(packet);
//            Log.i(TAG, "on bp2 control " + packet.toString());
//        }
//
//        @Override
//        public void onTemperatureMeasureSetting(ApplicationLayerTemperatureControlPacket packet) {
//            super.onTemperatureMeasureSetting(packet);
//            SPUtil.saveData(MainApplication.getInstance(), SpConfig.HEAT_MEASURE_ENABLE, packet.isShow());
//            Log.i(TAG, "temp setting : show = " + packet.isShow() + " adjust = " + packet.isAdjust() + " celsius unit = " + packet.isCelsiusUnit());
//        }
//
//        @Override
//        public void onTemperatureMeasureStatus(int status) {
//            super.onTemperatureMeasureStatus(status);
//            Log.i(TAG, "temp status :" + status);
//        }
//
//        @Override
//        public void onConnectionStateChange(boolean status) {
//            super.onConnectionStateChange(status);
//            if (status) {
//                Log.i(TAG, "连接成功： " + status);
//                MyApplication.Companion.instance().getUiHandler().PostAndWait(new ConnectionUIRunable(SIATDeviceService.this, 0));
//            } else {
//                Log.i(TAG, "连接失败: " + status);
//                MyApplication.Companion.instance().getUiHandler().PostAndWait(new ConnectionUIRunable(SIATDeviceService.this, 1));
//            }
//        }
//
//        @Override
//        public void onDeviceInfo(ApplicationLayerDeviceInfoPacket packet) {
//            super.onDeviceInfo(packet);
//            if (packet == null) return;
//            LogUtils.i(TAG, " onDeviceInfo " + new Gson().toJson(packet));
//            // compare the device version code to your latest version code(you can save it to your app or get it from you web service)
//            DeviceInfo info = new DeviceInfo(0, 0, 0, packet.getVersionCode(), packet.getVersionName());
//            MyApplication.Companion.instance().getUiHandler()
//                    .PostAndWait(new CallBackInUIRunnable(SIATDeviceService.this, "DeviceInfo", info));
//        }
//
//        @Override
//        public void onDeviceFunction(ApplicationLayerFunctionPacket packet) {
//            super.onDeviceFunction(packet);
//            LogUtils.i(TAG, " onDeviceFunction info = " + packet.toString());
//        }
//
//        //勿扰模式
//        @Override
//        public void onDisturb(ApplicationLayerDisturbPacket packet) {
//            super.onDisturb(packet);
//            if (packet == null) return;
//            LogUtils.i(TAG, " onDisturb " + packet.toString());
//            SPUtil.saveData(getApplicationContext(), SpConfig.IS_DISTURB_REMIND, packet.isOpen());
//        }
//
//        @Override
//        public void onScreenLightDuration(int duration) {
//            super.onScreenLightDuration(duration);
//            LogUtils.i(TAG, "duration : " + duration);
//        }
//
//        @Override
//        public void onTurnOverWristSettingReceive(boolean mode) {
//            super.onTurnOverWristSettingReceive(mode);
//            LogUtils.i(TAG, "turn wrist status : " + mode);
//            SPUtil.saveData(getApplicationContext(), SpConfig.IS_TAI_WANG_REMIND, mode);
//        }
//
//        @Override
//        public void onLongSitSettingReceive(ApplicationLayerSitPacket packet) {
//            super.onLongSitSettingReceive(packet);
//            if (packet == null) return;
//            LogUtils.i(TAG, "Sedentary :" + packet.toString());
//            SPUtil.saveData(getApplicationContext(), SpConfig.IS_SIT_REMIND, packet.getmEnable());
//        }
//
//        @Override
//        public void onHrpContinueParamRsp(boolean enable, int interval) {
//            super.onHrpContinueParamRsp(enable, interval);
//            LogUtils.i(TAG, " onHrpContinueParamRsp enable : " + enable + " interval : " + interval);
//            SPUtil.saveData(getApplicationContext(), SpConfig.IS_AUTOMATIC_HEART, enable);
//            SPUtil.saveData(getApplicationContext(), SpConfig.IS_HEART_INTERVAL_MIN, interval);
//        }
//
//        @Override
//        public void onEarStatus(ApplicationLayerEarStatusPacket packet) {
//            super.onEarStatus(packet);
//            LogUtils.i(" ear onEarStatus " + packet);
//            if (packet != null) {
//                SPUtil.saveData(MainApplication.getInstance(), SpConfig.IS_EAR_STATUS, packet.getPairState() == 3);
//            }
//        }
//
//        @Override
//        public void onSportDataReceiveIndication(ApplicationLayerSportPacket packet) {
//            super.onSportDataReceiveIndication(packet);
//            LogUtils.i(" onSportDataReceiveIndication " + new Gson().toJson(packet));
//            try {
//                if (packet != null && AppUserInfo.getInstance().userInfo != null) {
//                    if (packet.getSportItems() != null && packet.getSportItems().size() > 0) {
//                        for (ApplicationLayerSportItemPacket itemPacket : packet.getSportItems()) {
//                            int target = itemPacket.getRespirationRate();
//                            long userTime = itemPacket.getSportMinute() * 60 * 1000L + itemPacket.getSportSecond() * 1000L;
//
//                            int tkMode = MotionMode.Companion.getTkMode(itemPacket.getSportModel());
//                            RespirationBean bean = new RespirationBean(itemPacket.getCalories() / 1000, itemPacket.getDistance() / 1000f, itemPacket.getMinutes(),
//                                    itemPacket.getPauseCount(), itemPacket.getPauseMinute(), itemPacket.getPauseSecond(), itemPacket.getRateAvg(), itemPacket.getRateHigh(),
//                                    itemPacket.getRateLow(), itemPacket.getRespirationRate(), itemPacket.getRespirationRateMinute(), itemPacket.getSeconds(), itemPacket.getSportMinute(),
//                                    tkMode, itemPacket.getSportSecond(), itemPacket.getSteps());
//
//                            MotionMode mode = new MotionMode(tkMode, MotionMode.Companion.getModeImg(tkMode),
//                                    MotionMode.Companion.getModeImg(tkMode), MotionMode.Companion.getHistoryModeName(getApplicationContext(),
//                                    tkMode), false);
//
//                            ArrayList<RunHeartBean> heartList = new ArrayList<>();
//                            if (itemPacket.getRateAvg() > 0) {
//                                Date now = new Date();
//                                Date dayDate = DateTimeUtils
//                                        .NewDate(
//                                                DateTimeUtils.getYear(now),
//                                                DateTimeUtils.getMonth(now),
//                                                DateTimeUtils.getday(now),
//                                                0,
//                                                0,
//                                                0
//                                        );
//                                long heartTime = dayDate.getTime() + userTime;
//                                RunHeartBean heartBean = new RunHeartBean(heartTime, itemPacket.getRateAvg());
//                                heartList.add(heartBean);
//                            }
//
//                            RunBean runBean = new RunBean(AppUserInfo.getInstance().userInfo.id, mode, System.currentTimeMillis(),
//                                    System.currentTimeMillis(), itemPacket.getDistance() / 1000f, itemPacket.getCalories() / 1000, "", itemPacket.getSteps(),
//                                    userTime, target, heartList, bean
//                            );
//                            WatchBase watchBase = MyApplication.Companion.instance().getBleUtils().getConnectionWatch();
//                            InsertRunExecutor runExecutor = new InsertRunExecutor(AppUserInfo.getInstance().userInfo.id, watchBase.getDeviceName(),
//                                    watchBase.getDeviceMacAddress(), System.currentTimeMillis(), runBean);
//                            MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(runExecutor);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onMusicPlay() {
//            super.onMusicPlay();
//            // 音乐播放
//            LogUtils.i("onMusicPlay");
//            MusicControl.getInstance().playMusic(MainApplication.getInstance().getApplicationContext());
//        }
//
//        @Override
//        public void onMusicPause() {
//            super.onMusicPause();
//            // 音乐暂停
//            LogUtils.i("onMusicPause");
//            MusicControl.getInstance().pauseMusic(MainApplication.getInstance().getApplicationContext());
//        }
//
//        @Override
//        public void onMusicNext() {
//            super.onMusicNext();
//            // 音乐下一首
//            LogUtils.i("onMusicNext");
//            MusicControl.getInstance().nextMusic(MainApplication.getInstance().getApplicationContext());
//        }
//
//        @Override
//        public void onMusicPre() {
//            super.onMusicPre();
//            // 音乐上一首
//            LogUtils.i("onMusicPre");
//            MusicControl.getInstance().lastMusic(MainApplication.getInstance().getApplicationContext());
//        }
//
//        @Override
//        public void onMusicToggle() {
//            super.onMusicToggle();
//            // 音乐暂停
//            LogUtils.i("onMusicToggle");
//            MusicControl.getInstance().musicToggle(MainApplication.getInstance().getApplicationContext());
//        }
//
//        @Override
//        public void onMusicVolumeUp() {
//            super.onMusicVolumeUp();
//            // 音量加
//            MusicControl.getInstance().highMusic(MainApplication.getInstance().getApplicationContext());
//        }
//
//        @Override
//        public void onMusicVolumeDown() {
//            super.onMusicVolumeDown();
//            // 音量减
//            MusicControl.getInstance().lowMusic(MainApplication.getInstance().getApplicationContext());
//        }
//    };


    public static class CallBackInUIRunnable implements Runnable {

        private SIATDeviceService deviceService = null;
        private String key = "";
        private Object bean = null;

        public CallBackInUIRunnable(SIATDeviceService deviceService, String key, Object bean) {
            this.deviceService = deviceService;
            this.key = key;
            this.bean = bean;
        }

        @Override
        public void run() {
            deviceService.CallbackInUI(key, bean);
            this.deviceService = null;
            this.bean = null;
        }
    }

    /**
     * @param key
     * @param bean
     */
    private void CallbackInUI(String key, Object bean) {
        if (watch != null) {
            MyApplication.Companion.instance().getBleUtils().performWatchDataArrived(watch, key, bean);
        }
    }


    private ConnectorListener deviceConnectorListener = new ConnectorListener() {

        @Override
        public void onConnectAndWrite() {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new ConnectionUIRunable(SIATDeviceService.this, 0));
        }

        @Override
        public void onDisconnect() {
            LogUtils.i(" onDisconnect ");
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new ConnectionUIRunable(SIATDeviceService.this, 2));
        }

        @Override
        public void onConnectFailed() {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new ConnectionUIRunable(SIATDeviceService.this, 1));
        }

        @Override
        public void onReConnect() {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new ConnectionUIRunable(SIATDeviceService.this, 4));
        }

        @Override
        public void onConnectSuccess() {
            MyApplication.Companion.instance().getUiHandler().PostAndWait(new ConnectionUIRunable(SIATDeviceService.this, 5));
        }

    };


    /**
     * 链接到设备
     */
    private static class ConnectionUIRunable implements Runnable {

        private SIATDeviceService siatDeviceService = null;

        private int connectState = 0;  //0 链接成功  1 链接失败  2 断开链接

        public ConnectionUIRunable(SIATDeviceService siatDeviceService, int connectState) {
            this.siatDeviceService = siatDeviceService;
            this.connectState = connectState;
        }

        @Override
        public void run() {
            siatDeviceService.doneConnectState(connectState);
            siatDeviceService = null;
        }
    }

    /**
     * 是否已经链接成功
     */
    private boolean connected = false;

    /**
     * //0 链接成功  1 链接失败  2 断开链接
     *
     * @param connectState //0 链接成功  1 链接失败  2 断开链接
     */
    private void doneConnectState(int connectState) {
        try {
            if (watch == null) {
                String address = getBleMac();
                if (TextUtils.isEmpty(address)) {
                    address = remoteAddress;
                }
                if (!TextUtils.isEmpty(address)) {
                    BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
                    if (bluetoothAdapter != null) {
                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                        if (device != null) {
                            @SuppressLint("MissingPermission") String devName = device.getName();
                            if (TextUtils.isEmpty(devName)) {
                                devName = remoteDeviceName;
                            }
//                            if (WatchBeanUtil.isV101Watch(devName) || WatchBeanUtil.isTK12Watch(devName)) {
//                                watch = new CavoWatch(device.getAddress(), devName);
//                            } else {
                            watch = new SIATWatch(device.getAddress(), devName);
//                            }
                        }
                    }
                }
            }
            if (connectState == 0) {//链接成功
                if (istimeout) {
                    conntimes.clear();
                    istimeout = false;
                    try {
                        super.disconnect();
                    } catch (Exception ex) {
                    }
                } else {
                    if (!connected) {
                        connected = true;
                        if (watch != null) {
                            LogUtils.i(" perfermConnectingEvent connected 1 " + connected);
                            MyApplication.Companion.instance().getBleUtils().perfermConnectingEvent(watch, connectState);
                        } else {
                            try {
                                super.disconnect();
                            } catch (Exception ex) {
                            }
                        }
                    }
                }
            } else {
                if (watch != null) {
                    if (connectState == 2) {
                        if (connected) {
                            LogUtils.i(" perfermConnectingEvent connected 2 " + connected);
                            MyApplication.Companion.instance().getBleUtils().perfermConnectingEvent(watch, connectState);
                        } else {
                            if (connecting) {
                                MyApplication.Companion.instance().getBleUtils().perfermConnectingEvent(watch, 1);//如果链接没成功  则报告失败
                            } else {
                                LogUtils.i(" perfermConnectingEvent connected 3 " + connected);
                                MyApplication.Companion.instance().getBleUtils().perfermConnectingEvent(watch, connectState);//如果链接没成功  则报告失败
                            }
                        }
                    } else {
                        LogUtils.i(" perfermConnectingEvent connected 4 " + connected);
                        MyApplication.Companion.instance().getBleUtils().perfermConnectingEvent(watch, connectState);
                    }
                    watch.Release();
                    watch = null;
                }
                if (connected) {
                    connected = false;
                }
            }
            conntimes.clear();
            connecting = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 当前链接的设备
     */
    private WatchBase watch = null;

    /**
     * 设备是否正在链接
     */
    private boolean connecting = false;


    private String remoteAddress = "";


    private String remoteDeviceName = "";

    /**
     * 是否超时
     */
    private boolean istimeout = false;


    /**
     * @param device
     * @return
     */
    public boolean Connection(BluetoothDevice device, String deviceName) {
        if (watch != null) {
            if (TextUtils.equals(watch.getDeviceMacAddress(), device.getAddress())) {
                return true;
            } else {
                UnBindDevice();
            }
        }
        istimeout = false;
        conntimes.clear();
        conntimes.add(System.currentTimeMillis());
        remoteAddress = device.getAddress();
        remoteDeviceName = deviceName;
//            if (WatchBeanUtil.isV101Watch(deviceName) || WatchBeanUtil.isTK12Watch(deviceName)) {
//                watch = new CavoWatch(device.getAddress(), deviceName);
//                WristbandManager.getInstance(MainApplication.getInstance()).connect(device.getAddress());
//            } else {
        watch = new SIATWatch(device.getAddress(), deviceName);
        BindDevice(new DeviceModule(deviceName, device));
//            }
        MyApplication.Companion.instance().getBleUtils().perfermConnectingEvent(watch, 3);//开始连接
        connecting = true;
//        } else {
//            commonApplication.showToast("设备正在链接");
//        }
        return true;
    }

    @Override
    public void UnBindDevice() {
        super.UnBindDevice();
        LogUtils.i(" UnBindDevice ");
//        mECGExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                if (WristbandManager.getInstance(MainApplication.getInstance()).sendRemoveBondCommand()) {
//                    WristbandManager.getInstance(MainApplication.getInstance()).close();
//                    LogUtils.i(" UnBindDevice sendRemoveBondCommand 成功");
//                } else {
//                    LogUtils.i(" UnBindDevice sendRemoveBondCommand 失败");
//                }
//                WristbandManager.getInstance(MainApplication.getInstance()).close();
//            }
//        });
        MyApplication.Companion.instance().getUiHandler().PostAndWait(new ConnectionUIRunable(SIATDeviceService.this, 2));
    }

    public int getDeviceChipType() {
        return deviceChipType;
    }

    public void setRun(boolean run) {
        isRun = run;
    }
}
