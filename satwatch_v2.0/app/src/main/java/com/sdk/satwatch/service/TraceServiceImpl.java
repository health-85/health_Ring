package com.sdk.satwatch.service;

import android.content.Intent;
import android.os.IBinder;

import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.util.RxUtil;
import com.sw.watches.activity.MainActivity;
import com.sw.watches.util.LogUtil;

import java.util.concurrent.TimeUnit;

import cn.qqtheme.framework.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class TraceServiceImpl /*extends AbsWorkService*/ {

//    private static final String TAG = "TraceServiceImpl";
//
//    //是否 任务完成, 不再需要服务运行?
//    public static boolean sShouldStopService;
//    public static Disposable sDisposable;
//
//    //设备地址与名称
//    private String smart_device;
//    private String smart_device_address;
//
//    public static void stopService() {
//        //我们现在不再需要服务运行了, 将标志位置为 true
//        sShouldStopService = true;
//        //取消对任务的订阅
//        if (sDisposable != null) sDisposable.dispose();
//        //取消 Job / Alarm / Subscription
//        cancelJobAlarmSub();
//    }
//
//    /**
//     * 是否 任务完成, 不再需要服务运行?
//     *
//     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
//     */
//    @Override
//    public Boolean shouldStopService(Intent intent, int flags, int startId) {
//        return sShouldStopService;
//    }
//
//    @Override
//    public void startWork(Intent intent, int flags, int startId) {
//        LogUtil.i(TAG + " startWork ");
////        MainApplication.getInstance().getAdapetUtils().registryScanCallback(iBleScanCallBack);//注册扫描回调
////        MainApplication.getInstance().getAdapetUtils().registryConnectingListener(connectingListener);
//        sDisposable = Observable
//                .interval(1, TimeUnit.MINUTES)
//                .compose(RxUtil.IoToMainObserve())
//                //取消任务时取消定时唤醒
//                .doOnDispose(() -> {
//                    LogUtil.i(TAG + "取消任务时取消定时唤醒 ");
//                    cancelJobAlarmSub();
//                })
//                .subscribe(count -> {
//                    LogUtil.i(TAG + " reconnect device " + count);
//                    if (MyApplication.getZhBraceletService() != null && !MyApplication.getZhBraceletService().isConnectState()) {
//                        LogUtil.i(TAG + " reconnect device start ");
////                        MyApplication.getZhBraceletService().tryConnectDevice();
//                    }
//                });
//    }
//
//    @Override
//    public void stopWork(Intent intent, int flags, int startId) {
//        stopService();
////        MainApplication.getInstance().getAdapetUtils().unRegistryConnectingListener(connectingListener);
////        MainApplication.getInstance().getAdapetUtils().UnregistryScanCallback(iBleScanCallBack);//注销扫描回调
//    }
//
//    /**
//     * 任务是否正在运行?
//     *
//     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
//     */
//    @Override
//    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
//        //若还没有取消订阅, 就说明任务仍在运行.
//        return sDisposable != null && !sDisposable.isDisposed();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent, Void v) {
//        return null;
//    }
//
//    @Override
//    public void onServiceKilled(Intent rootIntent) {
//        LogUtil.i(TAG + " onServiceKilled ");
//    }

//    /**
//     * 蓝牙搜索回调
//     */
//    private BleAdapetUtils.IBleScanCallBack iBleScanCallBack = new BleAdapetUtils.IBleScanCallBack() {
//        @Override
//        public void scanStarted() {
//            smart_device = SPUtil.getData(MainApplication.getInstance(), SpConfig.SMART_DEVICE, "").toString();
//            smart_device_address = SPUtil.getData(MainApplication.getInstance(), SpConfig.SMART_DEVICE_ADDRESS, "").toString();
//        }
//
//        @SuppressLint("MissingPermission")
//        @Override
//        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//            if (!StringUtils.StringIsEmptyOrNull(device.getName()))
//                LogUtils.i(" name " + device.getName() + " smart " + smart_device);
//            if (!StringUtils.StringIsEmptyOrNull(device.getName()) &&
//                    device.getName().equals(smart_device) &&
//                    device.getAddress().equals(smart_device_address)) {
//                MainApplication.getInstance().getAdapetUtils().stopScan();
//                if (MainApplication.getInstance().getAdapetUtils().getConnectionWatch() == null) {//如果
//                    if (MainApplication.getInstance().getBle_app_status() == 0) {
//                        MainApplication.getInstance().getAdapetUtils().connect(device, device.getName());
//                        MainApplication.getInstance().setBle_app_status(1);
//                        MainApplication.getInstance().getAdapetUtils().stopScan();
//                    }
//                    LogUtils.i(" name " + device.getName() + " smart " + smart_device + " status " + MainApplication.getInstance().getBle_app_status());
//                }
//            }
//        }
//
//        @Override
//        public void scanStoped() {
//            smart_device = "";
//            smart_device_address = "";
//            if (MainApplication.getInstance().getBle_app_status() == 1) {
//                MainApplication.getInstance().setBle_app_status(0);
//            }
//        }
//    };
//
//    /**
//     * 手表链接回调
//     */
//    private BleAdapetUtils.IWatchConnectingListener connectingListener = new BleAdapetUtils.IWatchConnectingListener() {
//        @Override
//        public void onConnectingStart(WatchBase watch) {
//        }
//
//        @Override
//        public void onConnected(WatchBase watch) {
//            MainApplication.getInstance().setBle_app_status(1);
//        }
//
//        @Override
//        public void onDisconnect(WatchBase watch) {
//            if (MainApplication.getInstance().getBle_app_status() == 1) {
//                MainApplication.getInstance().setBle_app_status(0);
//            }
//        }
//
//        @Override
//        public void onConnectFailed(WatchBase watch) {
//            if (MainApplication.getInstance().getBle_app_status() == 1) {
//                MainApplication.getInstance().setBle_app_status(0);
//            }
//        }
//
//        @Override
//        public void onReConnect(WatchBase watch) {
//            if (MainApplication.getInstance().getBle_app_status() == 1) {
//                MainApplication.getInstance().setBle_app_status(0);
//            }
//        }
//    };

}
