package com.healthy.rvigor.dao.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.healthbit.framework.util.ToastUtil;
import com.healthy.rvigor.Constants;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.R;
import com.healthy.rvigor.bean.BoBean;
import com.healthy.rvigor.bean.BpBean;
import com.healthy.rvigor.bean.StepBean;
import com.healthy.rvigor.dao.executor.InsertAbnormalHeartExecutor;
import com.healthy.rvigor.dao.executor.InsertBreatheExecutor;
import com.healthy.rvigor.dao.executor.InsertEmotionExecutor;
import com.healthy.rvigor.dao.executor.InsertHeartRateExecutor;
import com.healthy.rvigor.dao.executor.InsertPressureExecutor;
import com.healthy.rvigor.dao.executor.InsertSiestaExecutor;
import com.healthy.rvigor.dao.executor.InsertSleepExecutor;
import com.healthy.rvigor.dao.executor.InsertSnoreExecutor;
import com.healthy.rvigor.dao.executor.InsertSpoExecutor;
import com.healthy.rvigor.dao.executor.InsertSpoExecutorSIATWatch;
import com.healthy.rvigor.dao.executor.InsertStepExecutor;
import com.healthy.rvigor.dao.executor.InsertStrengthExecutor;
import com.healthy.rvigor.dao.executor.InsertTireExecutor;
import com.healthy.rvigor.dao.executor.InsertXueYaXinLvManridyWatchExecutor;
import com.healthy.rvigor.event.WatchBindEvent;
import com.healthy.rvigor.event.WatchDataEvent;
import com.healthy.rvigor.event.WatchSyncEvent;
import com.healthy.rvigor.util.AppUtils;
import com.healthy.rvigor.util.BleUtils;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.DeviceInfoUtil;
import com.healthy.rvigor.util.LogUtils;
import com.healthy.rvigor.util.NumberUtils;
import com.healthy.rvigor.util.SPUtil;
import com.healthy.rvigor.util.SpConfig;
import com.healthy.rvigor.util.ValidRule;
import com.healthy.rvigor.watch.IBleScanCallBack;
import com.healthy.rvigor.watch.IWatchConnectingListener;
import com.healthy.rvigor.watch.IWatchFunctionDataCallBack;
import com.healthy.rvigor.watch.WatchBase;
import com.sw.watches.bean.AbnormalHeartListInfo;
import com.sw.watches.bean.BreatheInfo;
import com.sw.watches.bean.DeviceInfo;
import com.sw.watches.bean.EmotionInfo;
import com.sw.watches.bean.HeartInfo;
import com.sw.watches.bean.HeartListInfo;
import com.sw.watches.bean.MotionInfo;
import com.sw.watches.bean.PoHeartInfo;
import com.sw.watches.bean.PressureInfo;
import com.sw.watches.bean.SiestaInfo;
import com.sw.watches.bean.SleepInfo;
import com.sw.watches.bean.SleepOxInfo;
import com.sw.watches.bean.SnoreInfo;
import com.sw.watches.bean.SpoData;
import com.sw.watches.bean.SpoInfo;
import com.sw.watches.bean.StrengthInfo;
import com.sw.watches.bean.TireInfo;
import com.sw.watches.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 手机同步
 */
public class WatchSyncUtils {

    private static final String TAG = "WatchSyncUtils";

    private String mDeviceName = "";
    private String mDeviceAddress = "";

    //手表链接状态
    private int mWatchStatus;

    public WatchSyncUtils() {
        MyApplication.Companion.instance().getBleUtils().registerScanCallback(iBleScanCallBack);
        MyApplication.Companion.instance().getBleUtils().registryConnectingListener(connectingListener);
        MyApplication.Companion.instance().getBleUtils().registryWatchFunctionDataCallBack(watchFunctionDataCallBack);

//        MyApplication.Companion.instance().getTimerRepeatExecutor().AddTask(new Runnable() {
//            @Override
//            public void run() {
//                MyApplication.Companion.instance().getUiHandler().PostAndWait(TickRunnableInUI);
//            }
//        }, 60000 * 10);
//
//        MyApplication.Companion.instance().getTimerRepeatExecutor().AddTask(new Runnable() {
//            @Override
//            public void run() {
//                MyApplication.Companion.instance().getUiHandler().PostAndWait(syncTimeTickUIRun);
//            }
//        }, 1000);

    }

    /**
     * 扫描设备监听
     */
    private IBleScanCallBack iBleScanCallBack = new IBleScanCallBack() {

        @Override
        public void scanStarted() {
            LogUtils.i("scanStarted 扫描中");
            mDeviceName = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.DEVICE_NAME, "");
            mDeviceAddress = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.DEVICE_ADDRESS, "");
            mWatchStatus = Constants.WATCH_SCANNING;
            EventBus.getDefault().post(new WatchBindEvent(Constants.WATCH_SCANNING));
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null && ((TextUtils.equals(device.getName(), mDeviceName) && TextUtils.equals(device.getAddress(), mDeviceAddress))
                    || AppUtils.isEqualErrorMac(device.getName(), device.getAddress()))) {
                MyApplication.Companion.instance().getBleUtils().stopScan();
                if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() == null) {
                    LogUtils.i(" WatchSyncUtils onLeScan connect ");
                    MyApplication.Companion.instance().getBleUtils().connect(device, device.getName());
                }
            }
        }

        @Override
        public void scanStop() {
            LogUtils.i("scanStarted 扫描停止");
            mWatchStatus = Constants.WATCH_SCAN_STOP;
            EventBus.getDefault().post(new WatchBindEvent(Constants.WATCH_SCAN_STOP));
        }
    };

    private IWatchConnectingListener connectingListener = new IWatchConnectingListener() {
        @Override
        public void onConnectingStart(WatchBase watch) {
            LogUtils.i("onConnectingStart 链接中");
            mWatchStatus = Constants.WATCH_CONNECTING;
            EventBus.getDefault().post(new WatchBindEvent(Constants.WATCH_CONNECTING));
//            isDeviceConnectionOrConnected = true;
        }

        @Override
        public void onConnectedAndWrite(WatchBase watch) {
//            MyApplication.Companion.instance().setBle_app_status(1);
//            isCheckedNewVersion = false;
            LogUtils.i("onConnectedAndWrite 已链接");
            mWatchStatus = Constants.WATCH_CONNECTED;
            EventBus.getDefault().post(new WatchBindEvent(Constants.WATCH_CONNECTED));
            watchSync();
        }


        @Override
        public void onDisconnect(WatchBase watch) {
            LogUtils.i(" onDisconnect 断开链接");
            mWatchStatus = Constants.WATCH_DISCONNECT;
            EventBus.getDefault().post(new WatchBindEvent(Constants.WATCH_DISCONNECT));
//            if (isSyncing) {
//                MyApplication.Companion.instance().getBleUtils().performWatchDataArrived(watch, "ResponseComplete", new Object());
//            }
//            if (MyApplication.Companion.instance().getBle_app_status() == 1) {
//            MyApplication.Companion.instance().setBle_app_status(0);
//            }
//            clearCached();//如果断开链接
//            isDeviceConnectionOrConnected = false;
        }

        @Override
        public void onConnectFailed(WatchBase watch) {
            LogUtils.i(" onDisconnect 链接失败");
            mWatchStatus = Constants.WATCH_CONNECT_FAIL;
            EventBus.getDefault().post(new WatchBindEvent(Constants.WATCH_CONNECT_FAIL));
//            if (MyApplication.Companion.instance().getBle_app_status() == 1) {
//            MyApplication.Companion.instance().setBle_app_status(0);
//            }
//            device_state = MyApplication.Companion.instance().getResources().getString(R.string.my_connect_failed);
//            isDeviceConnectionOrConnected = false;
        }

        @Override
        public void onReConnect(WatchBase watch) {
            mWatchStatus = Constants.WATCH_CONNECTING;
            EventBus.getDefault().post(new WatchBindEvent(Constants.WATCH_CONNECTING));
        }

        @Override
        public void onConnectSuccess(WatchBase watch) {
            LogUtils.i(" onDisconnect 链接成功");
            mWatchStatus = Constants.WATCH_CONNECTED;
            EventBus.getDefault().post(new WatchBindEvent(Constants.WATCH_CONNECTED));
        }
    };

    public int getWatchStatus(){
        return mWatchStatus;
    }

    /**
     * 手表同步计时
     */
    private int syncTimeTick = 0;

    private Runnable syncTimeTickUIRun = new Runnable() {
        @Override
        public void run() {
            WatchBase watchBase = MyApplication.Companion.instance().getBleUtils().getConnectionWatch();
            if (watchBase != null) {
                if (isSyncing) {//如果正在同步
                    syncTimeTick++;
                    if (syncTimeTick >= 160) {
                        MyApplication.Companion.instance().getBleUtils()
                                .performWatchDataArrived(watchBase, "ResponseComplete", new Object());
                    }
                } else {
                    syncTimeTick = 0;
                }
            }
        }
    };

    /**
     * 获取已经绑定的设备名
     *
     * @return
     */
    public String getSavedDeviceName1() {
        return SPUtil.getData(MyApplication.Companion.instance(), SpConfig.DEVICE_NAME, "").toString();
    }




    private Runnable TickRunnableInUI = new Runnable() {
        @Override
        public void run() {
            // WatchSync();
        }
    };


    /**
     * 是否正在连接中或者设备已经连接
     */
//    private boolean isDeviceConnectionOrConnected = false;




    /**
     * 是否正在同步
     *
     * @return
     */
    public boolean isSyncing() {
        validateUIThread();
        return isSyncing;
    }

    /**
     * 是否正在同步
     */
    private boolean isSyncing = false;

    /**
     * 同步时间
     */
    private long syncTime = 0;

    /**
     * 同步手表数据
     */
    public void watchSync() {
        WatchBase watchBase = MyApplication.Companion.instance().getBleUtils().getConnectionWatch();
        if (watchBase != null) {
//            if (watchBase instanceof CavoWatch) isSyncing = false;
//            if (!isSyncing) {
                syncTimeTick = 0;
            EventBus.getDefault().post(new WatchSyncEvent(true, 0));
                watchBase.syncWatch();
                LogUtils.i(" WatchSyncUtils syncWatch");
                syncTime = (new Date()).getTime();
//                BloodHeartMeasureActivity.isTesting = false;
                isSyncing = true;
//                //触发事件
                for (int i = 0; i < events.size(); i++) {
                    events.get(i).OnStart();
                }
//            }
        } else {//如果设备未连接
            reStartScanDevice();
        }
    }


    /**
     * 以前链接的设备对象
     */
    private BluetoothDevice oldConnectedDevice = null;

    /**
     * 设置旧的链接对象
     *
     * @param oldConnectedDevice
     */
    public void setOldConnectedDevice(BluetoothDevice oldConnectedDevice) {
        this.oldConnectedDevice = oldConnectedDevice;
    }

    /**
     * 如果设备断开则  重连设备
     */
    public void reConnectDevice() {

        String smart_device = SPUtil.getData(MyApplication.Companion.instance(), SpConfig.DEVICE_NAME, "").toString();
        String smart_device_address = SPUtil.getData(MyApplication.Companion.instance(), SpConfig.DEVICE_ADDRESS, "").toString();

        if (TextUtils.isEmpty(smart_device)) {//如果没有名字
            return;
        }

        if (TextUtils.isEmpty(smart_device_address)) {//如果没有存储设备地址
            return;
        }
        BleUtils adapetUtils = MyApplication.Companion.instance().getBleUtils();
        if (!adapetUtils.isSupportBle()) {
            return;
        }

        if (!adapetUtils.isBleEnable()) {
            return;
        }
//        UpdateDeviceUtils updateDeviceUtils = MyApplication.Companion.instance().getUpdateDeviceUtils();
//        if (updateDeviceUtils != null) {
//            if (updateDeviceUtils.isIsdeviceUpgrade()) {//如果设备正在升级 则不连接
//                return;
//            }
//        }
        if (!AppUtils.isGpsAndNetworkEnable(MyApplication.Companion.instance())) {
            ToastUtil.showToast(MyApplication.Companion.instance(), R.string.turn_on_location_service);
            return;
        }

        if (!AppUtils.checkSelfPermissions(MyApplication.Companion.instance(), AppUtils.getBluetoothPerm())) {
//            RxToast.showToast("请先打开蓝牙服务");
            return;
        }
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            if (!TextUtils.isEmpty(smart_device) && !TextUtils.isEmpty(smart_device_address)) {
                MyApplication.Companion.instance().getSiatDeviceService().tryConnectDevice();
            } else {
                if (!AppUtils.isGpsAndNetworkEnable(MyApplication.Companion.instance())) {
                    ToastUtil.showToast(MyApplication.Companion.instance(), R.string.turn_on_location_service);
                    return;
                }
                adapetUtils.startScan();
            }
        } else {
            if (!AppUtils.isGpsAndNetworkEnable(MyApplication.Companion.instance())) {
                ToastUtil.showToast(MyApplication.Companion.instance(), R.string.turn_on_location_service);
                return;
            }
            adapetUtils.startScan();
        }
    }

    /**
     * 如果设备断开则  重新扫描设备
     */
    public void reStartScanDevice() {
        String smart_device = SPUtil.getData(MyApplication.Companion.instance(), SpConfig.DEVICE_NAME, "").toString();
        String smart_device_address = SPUtil.getData(MyApplication.Companion.instance(), SpConfig.DEVICE_ADDRESS, "").toString();

        if (TextUtils.isEmpty(smart_device)) {//如果没有名字
            return;
        }

        if (TextUtils.isEmpty(smart_device_address)) {//如果没有存储设备地址
            return;
        }
        BleUtils adapetUtils = MyApplication.Companion.instance().getBleUtils();
        if (!adapetUtils.isSupportBle()) {
            return;
        }

        if (!adapetUtils.isBleEnable()) {
            return;
        }

//        UpdateDeviceUtils updateDeviceUtils = MyApplication.Companion.instance().getUpdateDeviceUtils();
//        if (updateDeviceUtils != null) {
//            if (updateDeviceUtils.isIsdeviceUpgrade()) {//如果设备正在升级 则不连接
//                return;
//            }
//        }
        if (!AppUtils.isGpsAndNetworkEnable(MyApplication.Companion.instance())) {
            ToastUtil.showToast(MyApplication.Companion.instance(), R.string.turn_on_location_service);
            return;
        }
        if (!AppUtils.checkSelfPermissions(MyApplication.Companion.instance(), AppUtils.getBluetoothPerm())) {
//            RxToast.showToast("请先打开蓝牙服务");
            return;
        }
        adapetUtils.startScan();
    }


    /**
     *
     */
    private void validateUIThread() {
//        if (!MyApplication.Companion.instance().IsUIThread()) {
//            throw new RuntimeException("必须在UI线程中操作");
//        }
    }

    /**
     * 同步事件
     */
    public static interface ISyncEvent {
        /**
         * 开始
         */
        public void OnStart();

        /**
         * 结束
         */
        public void OnComplate();
    }

    /**
     * 事件集合
     */
    private final List<ISyncEvent> events = new ArrayList<>();


    /**
     * 注册事件
     *
     * @param event
     */
    public void RegistryEvent(ISyncEvent event) {
        validateUIThread();
        if (event != null) {
            if (!events.contains(event)) {
                events.add(event);
            }
        }
    }

    /**
     * 注销事件
     *
     * @param event
     */
    public void unRegistryEvent(ISyncEvent event) {
        validateUIThread();
        if (event != null) {
            events.remove(event);
        }
    }

    public void setSyncing(boolean syncing) {
        isSyncing = syncing;
    }

    /**
     * 获取数据回调
     */
    private IWatchFunctionDataCallBack watchFunctionDataCallBack = new IWatchFunctionDataCallBack() {
        /**
         * @param watch
         * @param functionName
         * @param bean
         */
        @Override
        public void WatchDataArrived(WatchBase watch, String functionName, Object bean) {
            LogUtils.i(TAG, " functionName == " + functionName + " bean " + bean);
            if (watch == null) return;
            if ("MotionInfo".equals(functionName)) {//siat设备的运动信息
                if (bean instanceof MotionInfo) {
                    MotionInfo motionInfo = (MotionInfo) bean;
                    if (motionInfo.totalStep > 0) {
                        InsertStepExecutor executorWatch = new InsertStepExecutor(motionInfo, watch.getDeviceMacAddress(), watch.getDeviceName());
                        Objects.requireNonNull(MyApplication.Companion.instance().getAppDaoManager()).ExecuteDBAsync(executorWatch);
                    }
                }
            }
            if ("SleepInfo".equals(functionName)) {//siat设备的睡眠信息
                if (bean instanceof SleepInfo) {
                    SleepInfo sleepInfo = (SleepInfo) bean;
                    if ((sleepInfo.getSleepData() != null)
                            && (sleepInfo.getSleepData().size() > 0)
                            && (sleepInfo.SleepTotalTime > 0)) {
                        InsertSleepExecutor sleepExecutor = new InsertSleepExecutor(sleepInfo, watch.getDeviceMacAddress(), watch.getDeviceName());
                        MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(sleepExecutor);
                    }
                }
            }
            if ("PoHeartInfo".equals(functionName)) {//同步的心率数据
                if (bean instanceof PoHeartInfo) {
                    PoHeartInfo poHeartInfo = (PoHeartInfo) bean;
                    if ((poHeartInfo.getPoHeartData() != null) && (poHeartInfo.getPoHeartData().size() > 0)) {
                        int hr = getLastData(poHeartInfo.getPoHeartData());
                        if (hr > 0) {
//                            LogUtils.i(TAG, "心率 == " + hr + " 是否一分钟心率 " + poHeartInfo.isOneMinRate() + " time " + poHeartInfo.PoHeartDate + " \n "
//                                    + new Gson().toJson(poHeartInfo.PoHeartData));
//                            watch.setOneMinRate(poHeartInfo.isOneMinRate());
                            SPUtil.saveData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.IS_ONE_HEART_RATE, false);
                            InsertHeartRateExecutor insertHeartRateExecutor
                                    = new InsertHeartRateExecutor(watch.getDeviceName(), watch.getDeviceMacAddress(), poHeartInfo);
                            MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(insertHeartRateExecutor);
                        }
                    }
                }
            }
            if ("version".equals(functionName)) {//小手表版本信息
                if (bean instanceof DeviceInfo) {
                    DeviceInfo deviceInfo = (DeviceInfo) bean;
                    DeviceInfoUtil.Companion.saveDeviceInfo(deviceInfo.deviceVersionName, deviceInfo.deviceVersionNumber, deviceInfo.deviceBattery);
                }
            }
            if ("Battery".equals(functionName)) {//大手表版本信息
                if (bean instanceof Integer) {
                    DeviceInfoUtil.Companion.saveDeviceInfo("", 0, (Integer) bean);
                }
            }
            if ("DeviceInfo".equals(functionName)) {//大手表版本信息
                if (bean instanceof DeviceInfo) {
                    LogUtils.i(" DeviceInfo 大手表版本信息");
                    DeviceInfo deviceInfo = (DeviceInfo) bean;
                    if (!isCheckedNewVersion) {
                        isCheckedNewVersion = true;
                    }
                    DeviceInfoUtil.Companion.saveDeviceInfo(deviceInfo.deviceVersionName, deviceInfo.deviceVersionNumber, deviceInfo.deviceBattery);
                }
            }
            if ("SpoInfo".equals(functionName)) {//sait手表 血氧
                if (bean instanceof SpoInfo) {
                    SpoInfo spoInfo = (SpoInfo) bean;
                    if (spoInfo.spoList == null || spoInfo.spoList.isEmpty()) {
                        return;
                    }
                    BoBean boBean = getBoBeanFrom(spoInfo);
                    StringBuilder builder = new StringBuilder();
                    builder.append("日期 = " + boBean.boDate + " ");
                    builder.append("血氧 = " + boBean.boRate + "\n");
                    LogUtils.i(TAG, builder.toString() + " spoInfo " + new Gson().toJson(spoInfo));
                    EventBus.getDefault().post(boBean);
                    if (NumberUtils.fromStringToDouble(boBean.boRate, 0) != 0) {
                        InsertSpoExecutor executorSIATWatch = new InsertSpoExecutor(spoInfo, watch.getDeviceName()
                                , watch.getDeviceMacAddress());
                        MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(executorSIATWatch);
                    }
                }
            }
            if ("SiestaInfo".equals(functionName)) {//午睡数据
                if (bean instanceof SiestaInfo) {
                    SiestaInfo info = (SiestaInfo) bean;
                    if (info.getSleepTime() > 0) {
                        long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                        InsertSiestaExecutor insertSiestaExecutor
                                = new InsertSiestaExecutor(info, watch.getDeviceMacAddress(), watch.getDeviceName(), uid);
                        MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(insertSiestaExecutor);
                    }
                }
            }
            if ("TireInfo".equals(functionName)) {//同步的环境温度
                TireInfo info = (TireInfo) bean;
                if ((info.getList() != null) && (info.getList().size() > 0)) {
                    int last = getLastTireData(info.getList());
                    if (last > 0) {
//                    LogUtils.i(" 疲劳数据 == " + new Gson().toJson(info));
                        long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                        InsertTireExecutor insertTireExecutor
                                = new InsertTireExecutor(uid, watch.getDeviceName(), watch.getDeviceMacAddress(), info);
                        MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(insertTireExecutor);
                    }
                }
            }
            if ("PressureInfo".equals(functionName)) {//同步的压力温度
                PressureInfo info = (PressureInfo) bean;
                if ((info.getPressureList() != null) && (info.getPressureList().size() > 0)) {
                    int last = getLastIntegerData(info.getPressureList());
                    if (last > 0) {
                        LogUtils.i(" 压力数据 == " + new Gson().toJson(info.getPressureList()));
                        long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                        InsertPressureExecutor insertPressureExecutor
                                = new InsertPressureExecutor(watch.getDeviceName(), watch.getDeviceMacAddress(), info);
                        MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(insertPressureExecutor);
                    }
                }
            }
            if ("EmotionInfo".equals(functionName)) {//同步的压力温度
                EmotionInfo info = (EmotionInfo) bean;
                if ((info.getEmotionList() != null) && (info.getEmotionList().size() > 0)) {
                    int last = getLastIntegerData(info.getEmotionList());
                    if (last > 0) {
//                        LogUtils.i(" 情绪数据 == " + new Gson().toJson(info.getEmotionList()));
                        long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                        InsertEmotionExecutor executor = new InsertEmotionExecutor(uid, watch.getDeviceName(),
                                watch.getDeviceMacAddress(), info);
                        MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(executor);
                    }
                }
            }
            if ("HeartListInfo".equals(functionName)) {//同步的血压
                HeartListInfo info = (HeartListInfo) bean;
                if ((info.getList() != null) && (info.getList().size() > 0)) {
                    HeartInfo heartInfo = info.getList().get(info.getList().size() - 1);
                    BpBean bpBean = new BpBean();
                    bpBean.bpHp = heartInfo.HeartInfoSBP;
                    bpBean.bpLp = heartInfo.HeartInfoDBP;
                    bpBean.bpHr = heartInfo.HeartInfoHR;
                    long time = 0;
                    if (!TextUtils.isEmpty(heartInfo.getTime())) {
                        time = DateTimeUtils.s_str_to_long(heartInfo.getTime(), DateTimeUtils.day_hm_format);
                        bpBean.bpDate = DateTimeUtils.s_long_2_str(time, DateTimeUtils.month_day_format_1);
                        bpBean.bpTime = time;
                    }
                    if (time == 0) time = System.currentTimeMillis();
                    EventBus.getDefault().post(bpBean);
                    LogUtils.i(" 血压数据 " + new Gson().toJson(bpBean));
                    long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                    if (bpBean.bpHp > 0 && bpBean.bpLp > 0) {
                        InsertXueYaXinLvManridyWatchExecutor xueYaXinLvManridyWatchExecutor
                                = new InsertXueYaXinLvManridyWatchExecutor(watch.getDeviceMacAddress(),
                                watch.getDeviceName(), uid
                                , DateTimeUtils.getDateTimeDatePart(new Date(time)).getTime()
                                , time, bpBean.bpHp, bpBean.bpLp, bpBean.bpHr);
                        MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(xueYaXinLvManridyWatchExecutor);
                    }
                }
            }
            if ("BreatheInfo".equals(functionName)) {
                BreatheInfo info = (BreatheInfo) bean;
                if ((info.getList() != null) && (info.getList().size() > 0)) {
                    long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                    InsertBreatheExecutor insertBreatheExecutor = new InsertBreatheExecutor(uid, watch.getDeviceMacAddress(), watch.getDeviceName(), info);
                    MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(insertBreatheExecutor);
                }
            }

            if ("SnoreInfo".equals(functionName)) {
                SnoreInfo info = (SnoreInfo) bean;
                if (!TextUtils.isEmpty(info.getDate())) {
                    long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                    InsertSnoreExecutor executor = new InsertSnoreExecutor(uid, watch.getDeviceMacAddress(), watch.getDeviceName(), info);
                    MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(executor);
                }
            }
            if ("StrengthInfo".equals(functionName)) {
                StrengthInfo info = (StrengthInfo) bean;
                long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                InsertStrengthExecutor executor = new InsertStrengthExecutor(uid, watch.getDeviceName(), watch.getDeviceMacAddress(), info);
                MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(executor);
            }

            if ("AbnormalHeartInfo".equals(functionName)) {
                AbnormalHeartListInfo info = (AbnormalHeartListInfo) bean;
                long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                if (info != null) LogUtil.i(" AbnormalHeartInfo " + new Gson().toJson(info));
                InsertAbnormalHeartExecutor executor = new InsertAbnormalHeartExecutor(info, uid, watch.getDeviceName(), watch.getDeviceMacAddress());
                MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(executor);
            }

            if ("SleepOxInfo".equals(functionName)) {
                mSleepOxInfo = (SleepOxInfo) bean;
            }

            if ("ResponseComplete".equals(functionName)) {
                syncTimeTick = 0;
                isSyncing = false;
                LogUtils.i(" ResponseComplete isSyncing " + isSyncing);
                //触发事件
                for (int i = 0; i < events.size(); i++) {
                    events.get(i).OnComplate();
                }

                EventBus.getDefault().post(new WatchSyncEvent(true, 100));

                WatchDataEvent event = new WatchDataEvent();
                event.setTime(DateTimeUtils.getDateTimeDatePart(new Date(System.currentTimeMillis())).getTime());
                event.setType(Constants.ALL_TYPE);
                EventBus.getDefault().post(event);

                if (mSleepOxInfo != null) {
                    try {
                        LogUtil.i(" SleepOxInfo " + new Gson().toJson(mSleepOxInfo));
                        long sleepStartTime = 0;
                        long endSleepStartTime = 0;
//                        if (mSleepInfo != null && mSleepInfo.getSleepData() != null && mSleepInfo.getSleepDate().length() > 0) {
//                            Date sleepDate = DateTimeUtils.ConvertStrToDate(mSleepInfo.getSleepDate());
//                            long sleepDay = DateTimeUtils.getDateTimeDatePart(sleepDate).getTime();
//                            sleepStartTime = makeSleepItemTime(mSleepInfo.getSleepData().get(0).startTime, new Date(sleepDay)).getTime();
//                            endSleepStartTime = makeSleepItemTime(mSleepInfo.getSleepData().get(mSleepInfo.getSleepData().size() - 1)
//                                    .startTime, new Date(sleepDay)).getTime();
//                        }
                        if (mSleepOxInfo.getList() != null && mSleepOxInfo.getList().size() > 0) {
                            long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(mSleepOxInfo.getTime());
//                            if ((calendar.getTimeInMillis() >= sleepStartTime && calendar.getTimeInMillis() <= endSleepStartTime) || BuildConfig.DEBUG) {
                            Date startDate = calendar.getTime();
                            List<SpoData> spoDataList = new ArrayList<>();
                            SpoInfo spoInfo = new SpoInfo(spoDataList);
                            if (mSleepOxInfo.getList() != null && mSleepOxInfo.getList().size() > 0) {
                                for (int data : mSleepOxInfo.getList()) {
                                    SpoData spoData = new SpoData(DateTimeUtils.s_long_2_str(calendar.getTimeInMillis(), DateTimeUtils.f_format), data, 0);
                                    spoDataList.add(spoData);
                                    calendar.add(Calendar.MINUTE, 1);
                                }
                            }
                            spoInfo.setSpoList(spoDataList);
                            InsertSpoExecutorSIATWatch executorSIATWatch
                                    = new InsertSpoExecutorSIATWatch(spoInfo, uid, watch.getDeviceName()
                                    , watch.getDeviceMacAddress(), true);
                            MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(executorSIATWatch);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    /**
     * 睡眠血氧
     */
    private SleepOxInfo mSleepOxInfo;


    /**
     * 心率查询
     */
    private static class HeartRateQueryResultClass implements AppDaoManager.DBExecutor.IResult {
        /**
         * 用户id
         */
        private long uid = 0;

        public HeartRateQueryResultClass(long uid) {
            this.uid = uid;
        }

        @Override
        public void OnSucceed(Object result) {
//            NetUploadApi.UploadHeartRate(uid, (List<HeartRateDBEntity>) result);
        }

        @Override
        public void OnError(Exception ex) {

        }
    }

    /**
     * 查询结果回调
     */
    private static class SpoInfoQueryResultClass implements AppDaoManager.DBExecutor.IResult {

        public SpoInfoQueryResultClass(long uid) {
            this.uid = uid;
        }

        private long uid = 0;

        @Override
        public void OnSucceed(Object result) {
//            NetUploadApi.UploadSpoInfo(uid, (List<SpoDBEntity>) result);
        }

        @Override
        public void OnError(Exception ex) {

        }
    }

    /**
     * 查询血压结果回调
     */
    private static class QueryBloodPressureTimeRangeExecutorResultClass implements AppDaoManager.DBExecutor.IResult {

        private long uid = 0;

        public QueryBloodPressureTimeRangeExecutorResultClass(long uid) {
            this.uid = uid;
        }

        @Override
        public void OnSucceed(Object result) {
//            List<XueYaXinLvDBEntity> xueYaXinLvDBEntities = (List<XueYaXinLvDBEntity>) result;
//            NetUploadApi.UploadXueYaXinLv(uid, xueYaXinLvDBEntities);
        }

        @Override
        public void OnError(Exception ex) {

        }
    }


    /**
     * 是否已经检测了新版本
     */
    private boolean isCheckedNewVersion = false;

    private BoBean getBoBeanFrom(SpoInfo spoInfo) {
        BoBean boBean = new BoBean();
        boBean.boRate = "0";
        boBean.boDate = DateTimeUtils.toDateString(new Date(), "yyyy-MM-dd HH:mm:ss");
        SpoData spoDatag = null;
        if ((spoInfo.spoList != null) && (spoInfo.spoList.size() > 0)) {
            for (int i = 0; i < spoInfo.spoList.size(); i++) {
                SpoData spoData = spoInfo.spoList.get(i);
                if (spoData.spoValue > 0 && ValidRule.getInstance().isValidOx(spoData.spoValue)) {
                    if (spoDatag == null) {
                        spoDatag = spoData;
                    } else {
                        long oldtime = DateTimeUtils.getNonNullDate(DateTimeUtils.convertStrToDateForThisProject(spoDatag.spoTime)).getTime();
                        long newtime = DateTimeUtils.getNonNullDate(DateTimeUtils.convertStrToDateForThisProject(spoData.spoTime)).getTime();
                        if (newtime > oldtime) {
                            spoDatag = spoData;
                        }
                    }
                }
            }
        }
        if (spoDatag != null) {
            boBean.boRate = spoDatag.spoValue + "";
            boBean.boDate = spoDatag.spoTime;
        }
        return boBean;
    }

    private BoBean getOldBoBeanFrom(SpoInfo spoInfo) {
        BoBean boBean = new BoBean();
        boBean.boRate = "0";
        boBean.boDate = DateTimeUtils.toDateString(new Date(), "yyyy-MM-dd HH:mm:ss");
        SpoData spoDatag = null;
        if ((spoInfo.spoList != null) && (spoInfo.spoList.size() > 0)) {
            for (int i = 0; i < spoInfo.spoList.size(); i++) {
                SpoData spoData = spoInfo.spoList.get(i);
                if (spoData.spoValue > 0 && ValidRule.getInstance().isValidOx(spoData.spoValue)) {
                    if (spoDatag == null) {
                        spoDatag = spoData;
                    } else {
                        long oldtime = DateTimeUtils.getNonNullDate(DateTimeUtils.convertStrToDateForThisProject(spoDatag.spoTime)).getTime();
                        long newtime = DateTimeUtils.getNonNullDate(DateTimeUtils.convertStrToDateForThisProject(spoData.spoTime)).getTime();
                        if (newtime < oldtime) {
                            spoDatag = spoData;
                        }
                    }
                }
            }
        }
        if (spoDatag != null) {
            boBean.boRate = spoDatag.spoValue + "";
            boBean.boDate = spoDatag.spoTime;
        }
        return boBean;
    }

    /**
     * 获取最后的数据
     *
     * @param datas
     * @return
     */
    private int getLastData(List<Integer> datas) {
        if ((datas != null) && (datas.size() > 0)) {
            for (int i = datas.size() - 1; i >= 0; i--) {
                int curr = datas.get(i);
                if (curr > 0 && curr < 1000) {
                    return curr;
                }
            }
        }
        return 0;
    }

    /**
     * 获取最后的数据
     *
     * @param datas
     * @return
     */
    private float getLastFloatData(List<Float> datas) {
        if ((datas != null) && (datas.size() > 0)) {
            for (int i = datas.size() - 1; i >= 0; i--) {
                float curr = datas.get(i);
                if (curr > 0 && curr < 1000) {
                    return curr;
                }
            }
        }
        return 0;
    }

    private float getLasHeatData(List<Float> datas) {
        if ((datas != null) && (datas.size() > 0)) {
            for (int i = datas.size() - 1; i >= 0; i--) {
                float curr = datas.get(i);
                if (curr > 0 && curr < 1000 && ValidRule.getInstance().isValidHeat(curr)) {
                    return curr;
                }
            }
        }
        return 0;
    }

    private int getLastIntegerData(List<Integer> datas) {
        if ((datas != null) && (datas.size() > 0)) {
            for (int i = datas.size() - 1; i >= 0; i--) {
                int curr = datas.get(i);
                if (curr > 0 && curr < 100) {
                    return curr;
                }
            }
        }
        return 0;
    }

    private int getLastTireData(List<Integer> datas) {
        if ((datas != null) && (datas.size() > 0)) {
            for (int i = datas.size() - 1; i >= 0; i--) {
                int curr = datas.get(i);
                if (curr > 0 && curr < 100 && ValidRule.getInstance().isValidTire(curr)) {
                    return curr;
                }
            }
        }
        return 0;
    }

    /**
     * 获取最后的时间
     *
     * @param date
     * @param datas
     * @return
     */
    private long getLastTimeData(String date, List<Float> datas) {
        if (TextUtils.isEmpty(date) || datas == null || datas.size() <= 0) return 0;
        Calendar calendar = Calendar.getInstance();
        long time = DateTimeUtils.convertStrToDateForThisProject(date).getTime();
        calendar.setTimeInMillis(time);
        for (int i = datas.size() - 1; i >= 0; i--) {
            float curr = datas.get(i);
            if (curr > 0 && curr < 1000) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
//                LogUtils.i(TAG, " getLastTimeData " + DateTimeUtils.s_long_2_str(calendar.getTimeInMillis(), DateTimeUtils.f_format));
                return calendar.getTimeInMillis();
            }
        }
        return 0;
    }

    private long getLastTime(String date, List<Integer> datas) {
        if (TextUtils.isEmpty(date) || datas == null || datas.size() <= 0) return 0;
        Calendar calendar = Calendar.getInstance();
        long time = DateTimeUtils.convertStrToDateForThisProject(date).getTime();
        calendar.setTimeInMillis(time);
        for (int i = datas.size() - 1; i >= 0; i--) {
            float curr = datas.get(i);
            if (curr > 0 && curr < 1000) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                return calendar.getTimeInMillis();
            }
        }
        return 0;
    }

    /**
     * 生成SIAT设备的运动时间
     *
     * @param oldDate
     * @return
     */
    private Date makeSIATDeviceStepDateTime(Date oldDate, List<Integer> stepdatas) {
        Date newdate = oldDate;
        if (stepdatas != null && stepdatas.size() > 0) {//size为96
            for (int i = stepdatas.size() - 1; i >= 0; i--) {
                if (stepdatas.get(i) > 0) {
                    int minute = 15 * i;
                    if (minute >= (24 * 60)) {
                        minute = 24 * 60;
                    }
                    newdate = DateTimeUtils.AddMinute(oldDate, minute);
                    break;
                }
            }
        }
        return newdate;
    }

    private Date makeSleepItemTime(String time, Date sleepday) {
        Date sleepdaydatepart = DateTimeUtils.getDateTimeDatePart(sleepday);
        Date date = DateTimeUtils.ConvertStrToDate(time, "HH:mm");
        if (date != null) {
            sleepdaydatepart = DateTimeUtils.AddHours(sleepdaydatepart, DateTimeUtils.getHour(date));
            sleepdaydatepart = DateTimeUtils.AddMinute(sleepdaydatepart, DateTimeUtils.getMinute(date));
        }
        return sleepdaydatepart;
    }

    private void updateSaveMotion(WatchBase watch, MotionInfo motionInfo, StepBean stepBean, long uid) {
        try {
//            Date timeDate = DateTimeUtils.convertStrToDateForThisProject(motionInfo.getMotionDate());
//            long startTime = DateTimeUtils.getDateTimeDatePart(timeDate).getTime();
//            long endTime = DateTimeUtils.AddDay(new Date(startTime), 1).getTime();
//            LogUtils.i(" motion startTime " + DateTimeUtils.s_long_2_str(startTime, DateTimeUtils.f_format) + " "
//                    + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format));
//            float sg = 170;
//            if (AppUserInfo.getInstance().isLogin()) {
//                sg = NumberUtils.fromStringToFloat(AppUserInfo.getInstance().userInfo.height, 170);
//            }
//            float finalSg = sg;
//            QueryStepExecutor stepExecutor = new QueryStepExecutor(startTime, endTime
//                    , watch == null ? "" : watch.getDeviceMacAddress(), uid, ""
//                    , new AppDaoManager.DBExecutor.IResult() {
//                @Override
//                public void OnSucceed(Object result) {
//                    if (result != null) {
//                        List<StepDBEntity> dbEntities = (List<StepDBEntity>) result;
//                        if (dbEntities.isEmpty()) return;
//                        StepDBEntity dbEntity = dbEntities.get(0);
//                        List<Integer> list = motionInfo.getStepData();
//                        JsonArrayUtils saveJsonUtils = new JsonArrayUtils(dbEntity.stepDataJsonArrayForTime);
//                        for (int timeMode = 1; timeMode <= 24; timeMode++) {
//                            JsonUtils curr = saveJsonUtils.getJsonObject(timeMode - 1);
//                            long saveStep = curr.getLong("step", 0);
//                            int start = ((timeMode - 1) * 60) / 15;//起始点
//                            int tick = timeMode * 60 / 15;
//                            long step = 0;
//                            for (int i = start; i < tick; i++) {
//                                if (list.size() > i) {
//                                    step += list.get(i);
//                                }
//                            }
//                            if (step == 0 && saveStep > 0) {
//                                list.set(start, (int) saveStep);
//                            }
//                        }
//                        motionInfo.setTotalStep((int) dbEntity.getTotalStep());
//                        motionInfo.setStepData(list);
//                        motionInfo.setCalorie(WatchBeanUtil.byteToCalorie((int) dbEntity.getTotalStep()) + "千卡");
//                        stepBean.stepMileage = NumberUtils.StepToDistanceM(finalSg, motionInfo.totalStep);
//                        stepBean.stepNum = motionInfo.totalStep;
//                        stepBean.stepCalorie = (int) (NumberUtils.fromStringToDouble(NumberUtils.delNumberUnitStr(motionInfo.getCalorie(), "千卡"), 0));
//                        cacheStepBean = stepBean;//缓存步数信息
//                        EventBus.getDefault().post(stepBean);
//                        NetUploadApi.UploadStepSIATWatch(motionInfo);//上传siat运动数据
////                        LogUtils.i(TAG, new Gson().toJson(motionInfo));
//                    }
//                }
//
//                @Override
//                public void OnError(Exception ex) {
//
//                }
//            });
//            MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(stepExecutor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void bindDayStep(List<HeartRateDBEntity> dbEntities) {
//        if (dbEntities == null || dbEntities.isEmpty()) {
//            return;
//        }
//        HeartRangeBean bean = new HeartRangeBean();
//        WatchBase watchBase =  MyApplication.Companion.instance().getBleUtils().getConnectionWatch();
//        for (int i = (dbEntities.size() - 1); i >= 0; i--) {
//            HeartRateDBEntity entity = dbEntities.get(i);
//            if ((watchBase != null && watchBase.isOneMinRate())) {
//                make15List(entity, tongEndTime);
//            } else {
//                makeList(entity, tongEndTime);
//            }
//            bean.setHeartRateDay(entity.HeartRateDay);
//        }
//    }
//
//    private void makeList(HeartRateDBEntity dbEntity, long tongEndTime) {
//        mMaxRate = 0;
//        mMinRate = 0;
//        mAverageRate = 0;
//        if (heartChart.datas != null) {
//            heartChart.datas.clear();
//        }
//        JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(dbEntity.heartJsonData);
//        if (jsonArrayUtils.length() > 0) {
//            for (int i = 0; i < jsonArrayUtils.length(); i++) {
//                JsonUtils curr = jsonArrayUtils.getJsonObject(i);
//                int rate = curr.getInt("rate", 0);
//                long time = curr.getLong("datetime", 0);
//                if ((tongEndTime > 0 && time > tongEndTime)) continue;
//                if (heartChart != null) {
//                    heartChart.datas.add(new HeartRateTongJiChartView.DataItem(rate, time));
//                }
//                mAverageRate = mAverageRate == 0 ? rate : (mAverageRate + rate) / 2;
//                mMinRate = (mMinRate == 0 || mMinRate > rate) ? rate : mMinRate;
//                mMaxRate = (mMaxRate == 0 || mMaxRate < rate) ? rate : mMaxRate;
//            }
//        }
//        heartChart.postInvalidate();
//    }
//
//    private void make15List(HeartRateDBEntity dbEntity, long tongEndTime) {
//        mMaxRate = 0;
//        mMinRate = 0;
//        mAverageRate = 0;
//        if (heartChart.datas != null){
//            heartChart.datas.clear();
//        }
//        JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(dbEntity.heartJsonData);
//        if (jsonArrayUtils.length() > 0) {
//            int i = 0;
//            int j = 0;
//            Calendar calendar = Calendar.getInstance();
//            long startTime = jsonArrayUtils.getJsonObject(0).getLong("datetime", 0);
//            long endTime = jsonArrayUtils.getJsonObject(jsonArrayUtils.length() - 1).getLong("datetime", 0);
//            calendar.setTimeInMillis(startTime);
//            List<Integer> list = new ArrayList<>();
////            LogUtil.i(TAG, " time == " + DateTimeUtils.s_long_2_str(calendar.getTimeInMillis(), DateTimeUtils.f_format));
//            while (startTime <= endTime && (tongEndTime > 0 && startTime < tongEndTime)) {
//                JsonUtils curr = jsonArrayUtils.getJsonObject(j);
//                int temp = curr.getInt("rate", 0);
//                long time = curr.getLong("datetime", 0);
//                mAverageRate = mAverageRate == 0 ? temp : (mAverageRate + temp) / 2;
//                mMinRate = (mMinRate == 0 || mMinRate > temp) ? temp : mMinRate;
//                mMaxRate = (mMaxRate == 0 || mMaxRate < temp) ? temp : mMaxRate;
//                if (time == startTime) {
//                    list.add(temp);
//                    j++;
//                }
//                if ((i % 5 == 4 || startTime == endTime) && list.size() > 0) {
//                    Collections.sort(list);
//                    int rate = list.get(list.size() / 2);
//                    list.clear();
//                    if (heartChart != null) {
//                        heartChart.datas.add(new HeartRateTongJiChartView.DataItem(rate, calendar.getTimeInMillis()));
//                    }
//                }
//                calendar.add(Calendar.MINUTE, 1);
//                startTime = calendar.getTimeInMillis();
//                i++;
//            }
//        }
//        heartChart.postInvalidate();
//    }

}
