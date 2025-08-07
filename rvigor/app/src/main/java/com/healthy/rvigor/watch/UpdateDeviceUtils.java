package com.healthy.rvigor.watch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.healthbit.framework.base.BaseActivity;
import com.healthbit.framework.util.ToastUtil;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.R;
import com.healthy.rvigor.net.http.HttpRequestBase;
import com.healthy.rvigor.net.http.IActivityManagerCallBase;
import com.healthy.rvigor.util.AppUtils;
import com.healthy.rvigor.util.BleUtils;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.LogUtils;
import com.healthy.rvigor.util.RxUtil;
import com.healthy.rvigor.util.SPUtil;
import com.healthy.rvigor.util.SpConfig;
import com.healthy.rvigor.util.WatchBeanUtil;
import com.healthy.rvigor.view.ConfirmTipsView;
import com.zhangteng.utils.JsonUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * 设备升级单元
 */
public class UpdateDeviceUtils {

//    private static final String TAG = "UpdateDeviceUtils";
//
//    /**
//     *
//     */
//    private MyApplication application = null;
//
//    private FileDownLoadUtils fileDownLoadUtils = null;
//
//    private String downLoadUrl; //下载地址
//
//    /**
//     * 设备升级回调
//     */
//    private IUpgradeDeviceListener upgradeDeviceListener = new IUpgradeDeviceListener() {
//        @Override
//        public void onUpgradeDeviceCompleted() {
//            performState("升级完成 等待设备开机");
//            performComplate();
//            //更新设备失败的次数
//            SPUtil.saveData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.UPDATE_ERROR_DEVICE_TIME, 0);
//            Log.e("UpgradeDeviceCompleted", "");
//            isdeviceUpgrade = false;
//        }
//
//        @Override
//        public void onUpgradeDeviceError(int paramInt1, int paramInt2, String paramString) {
//            performError("升级错误:" + paramString + "\n");
//            isdeviceUpgrade = false;
//            try {
//                List<String> macList = new ArrayList<>();
//                String mac = (String) SPUtil.getData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.SMART_DEVICE_ADDRESS, "");
//                String errorMsg = (String) SPUtil.getData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.UPDATE_ERROR_DEVICE_MAC, "");
//                LogUtils.i(" onUpgradeDeviceError errorMsg " + errorMsg);
//                if (!TextUtils.isEmpty(errorMsg)) {
//                    macList = new Gson().fromJson(errorMsg, new TypeToken<List<String>>() {
//                    }.getType());
//                    if (macList != null && !macList.contains(mac)) {
//                        macList.add(mac);
//                    }
//                } else {
//                    macList.add(mac);
//                }
//                SPUtil.saveData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.UPDATE_ERROR_DEVICE_MAC, new Gson().toJson(macList));
//                LogUtils.i(" onUpgradeDeviceError mac " + mac);
//                //保存更新设备失败的次数
//                int time = (int) SPUtil.getData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.UPDATE_ERROR_DEVICE_TIME, 0);
//                time++;
//                SPUtil.saveData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.UPDATE_ERROR_DEVICE_TIME, time);
//                LogUtils.e("onUpgradeDeviceError", "paramInt1=" + paramInt1 + ", paramInt2=" + paramInt2 + ",paramString=" + paramString);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onUpgradeDeviceProgress(int paramInt) {
//            performState("升级进度" + paramInt + "%");
//            performUpgradeDeviceProgress(paramInt);
//            Log.e("onUpgradeDeviceProgress", "" + paramInt);
//        }
//
//        @Override
//        public void onUpgradeDeviceStarting(int paramInt) {
//            if (paramInt == 1) {
//                performState("正在加载dfu...");
////                 updateCommandValue("deviceupdate", "正在加载dfu...", true);
//            }
//
//            if (paramInt == 2) {
//                performState("正在启动设备bootloader...");
//                // updateCommandValue("deviceupdate", "正在启动bootloader...", true);
//            }
//            if (paramInt == 0) {
//                performState("dfu链接手表...");
//                //updateCommandValue("deviceupdate", "dfu链接手表...", true);
//            }
//            Log.e("onUpgradeDeviceStarting", "paramInt=" + paramInt);
//        }
//
//        @Override
//        public void onUpgradeDeviceTip(String tip) {
//            performState(tip);
//        }
//
//        @Override
//        public void onReConnectUpdateDevice(String s, boolean start) {
//            LogUtils.i(" onReConnectUpdateDevice " + s);
//            performState(s);
//        }
//    };
//
//    /**
//     * 下载进度
//     */
//    private FileDownLoadUtils.IDownLoadListener downLoadListener = new FileDownLoadUtils.IDownLoadListener() {
//
//        @Override
//        public void onStart() {
//            performState("正在下载升级文件");
//            performDownloadStart();
//        }
//
//        @Override
//        public void onProgress(int percent) {
//            performState("升级文件下载进度" + percent + "%");
//            performDownLoadProgress(percent);
//        }
//
//        @Override
//        public void onComplate() {
//            performState("升级文件下载完成 开始升级");
//            performLoaderComplate();
//            WatchBase watchBase = MyApplication.Companion.instance()
//                    .getBleUtils().getConnectionWatch();
//            if (watchBase != null) {
//                if (watchBase instanceof SIATWatch) {
//                    isdeviceUpgrade = watchBase.UpgradeDevice();
//                } else {
//                    performState("链接的手表暂不支持");
//                    performError("链接的手表暂不支持");
//                    isdeviceUpgrade = false;
//                }
//            } else {
//                performState(MyApplication.Companion.instance().getResources().getString(R.string.main_unconnected_watch));
//                performError(MyApplication.Companion.instance().getResources().getString(R.string.main_unconnected_watch));
//                isdeviceUpgrade = false;
//            }
//        }
//
//        @Override
//        public void onError(Exception ex) {
//            isdeviceUpgrade = false;
//            performState("升级文件下载失败");
//            performError("升级文件下载失败");
//        }
//    };
//
//
//    private IWatchConnectingListener connectingListener = new IWatchConnectingListener() {
//        @Override
//        public void onConnectingStart(WatchBase watch) {
//
//        }
//
//        @Override
//        public void onConnectedAndWrite(WatchBase watch) {
//            hasNewVersion = false;//链接设备时候没有新版本
//            state = "";
//        }
//
//        @Override
//        public void onDisconnect(WatchBase watch) {
//
//        }
//
//        @Override
//        public void onConnectFailed(WatchBase watch) {
//
//        }
//
//        @Override
//        public void onReConnect(WatchBase watch) {
//
//        }
//
//        @Override
//        public void onConnectSuccess(WatchBase watch) {
//
//        }
//    };
//
//    /**
//     * 是否设备正在升级
//     *
//     * @return
//     */
//    public boolean isIsdeviceUpgrade() {
//        validateUIThread();
//        boolean ret = false;
//        ret = (ret || MyApplication.Companion.instance()
//                .getDeviceUpdateHttpComponet().containsRequestByTag("checkDeviceNewVersion"));
//        ret = (ret || fileDownLoadUtils.hasDownloadDeviceUpdateZipFile());
//        ret = (ret || isdeviceUpgrade);
//        return ret;
//    }
//
//    /**
//     * 设备是否在升级
//     */
//    private boolean isdeviceUpgrade = false;
//
//    public UpdateDeviceUtils(MyApplication application) {
//        this.application = application;
//        fileDownLoadUtils = new FileDownLoadUtils(application);
//        application.getAdapetUtils().RegistryUpgradeDeviceListener(upgradeDeviceListener);
//        application.getAdapetUtils().registryConnectingListener(connectingListener);
//        fileDownLoadUtils.addListener(downLoadListener);
//    }
//
//    /**
//     * 升级回调
//     */
//    public static interface ICustomerUpdateCallBack {
//
//        public void onVersion(boolean hasNewVersion, String versionName, boolean download);
//
//        public void onUpdateState(String state);
//
//        /**
//         * 开始下载
//         */
//        public void onDownLoadStart();
//
//        public void onDownLoadProgress(int percent);
//
//        public void onDownLoaderComplate();
//
//        public void onUpgradeDeviceProgress(int percent);
//
//        public void onError(String error);
//
//        public void onComplate();
//    }
//
//    /**
//     * 事件集合
//     */
//    private final List<ICustomerUpdateCallBack> callBacks = new ArrayList<>();
//
//    /**
//     * 添加毁掉
//     *
//     * @param callBack
//     */
//    public void AddCallBack(ICustomerUpdateCallBack callBack) {
//        validateUIThread();
//        if (!callBacks.contains(callBack)) {
//            callBacks.add(callBack);
//        }
//    }
//
//
//    /**
//     * 移除毁掉
//     *
//     * @param callBack
//     */
//    public void removeCallBack(ICustomerUpdateCallBack callBack) {
//        validateUIThread();
//        callBacks.remove(callBack);
//    }
//
//    /**
//     * 推送新版本
//     *
//     * @param hasNewVersion
//     */
//    private void performVersion(boolean hasNewVersion, String versionName, boolean download) {
//        validateUIThread();
//        for (int i = 0; i < callBacks.size(); i++) {
//            callBacks.get(i).onVersion(hasNewVersion, versionName, download);
//        }
//    }
//
//    /**
//     * 下载开始
//     */
//    private void performDownloadStart() {
//        validateUIThread();
//        for (int i = 0; i < callBacks.size(); i++) {
//            callBacks.get(i).onDownLoadStart();
//        }
//    }
//
//    /**
//     * 下载进度
//     *
//     * @param percent
//     */
//    private void performDownLoadProgress(int percent) {
//        validateUIThread();
//        for (int i = 0; i < callBacks.size(); i++) {
//            callBacks.get(i).onDownLoadProgress(percent);
//        }
//    }
//
//    /**
//     * 下载完成
//     */
//    private void performLoaderComplate() {
//        validateUIThread();
//        for (int i = 0; i < callBacks.size(); i++) {
//            callBacks.get(i).onDownLoaderComplate();
//        }
//    }
//
//    /**
//     * 设备升级进度
//     *
//     * @param percent
//     */
//    private void performUpgradeDeviceProgress(int percent) {
//        validateUIThread();
//        for (int i = 0; i < callBacks.size(); i++) {
//            callBacks.get(i).onUpgradeDeviceProgress(percent);
//        }
//    }
//
//    /**
//     * 获取状态信息
//     *
//     * @return
//     */
//    public String getState() {
//        validateUIThread();
//        return state;
//    }
//
//    private String state = "";
//
//
//    public void performState(String state) {
//        validateUIThread();
//        for (int i = 0; i < callBacks.size(); i++) {
//            callBacks.get(i).onUpdateState(state);
//        }
//        this.state = state;
//    }
//
//    /**
//     * 错误信息
//     *
//     * @param error
//     */
//    private void performError(String error) {
//        validateUIThread();
//        for (int i = 0; i < callBacks.size(); i++) {
//            callBacks.get(i).onError(error);
//        }
//    }
//
//    /**
//     * 派遣完成信息
//     */
//    private void performComplate() {
//        validateUIThread();
//        for (int i = 0; i < callBacks.size(); i++) {
//            callBacks.get(i).onComplate();
//        }
//    }
//
//    /**
//     * 是否有新
//     *
//     * @return
//     */
//    public boolean isHasNewVersion() {
//        validateUIThread();
//        return hasNewVersion;
//    }
//
//    private void validateUIThread() {
////        if (!application.IsUIThread()) {
////            throw new RuntimeException("必须在UI中操作");
////        }
//    }
//
//    /**
//     * 是否有新版本
//     */
//    private boolean hasNewVersion = false;
//
//    /**
//     * 模式下载
//     */
//    private long modeltype = 2;
//
//    /**
//     * 检查新版本
//     */
//    public void checkedDeviceNewVersion(WatchBase watchBase, String uuid, String pid, boolean showerror) {
//        validateUIThread();
//        if (watchBase instanceof SIATWatch) {
//            if (!isIsdeviceUpgrade()) {//如果设备没有在升级
//                if (NetTool.isNewNet) {
//                    String token = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.TOKEN, "");
//                    ApiHelper.get(NetTool.base_url + "system-service/front/version/selectData")
//                            .putHeader("Authorization", token)
//                            .putParam("modelType", WatchBeanUtil.getWatchModelType() + "")
////                            .putParam("currversion", watchBase.versoinNumber + "")
//                            .setUUID(uuid)
//                            .setTag("checkDeviceNewVersion")
//                            .setProgressUUID(pid)
//                            .requestCallBack(new checkedDeviceNewVersionCallBack(null
//                                    , showerror))
//                            .execute(MyApplication.Companion.instance().getDeviceUpdateHttpComponet());
//                } else {
//                    ApiHelper.get(NetTool.base_url + "front/rversion/selectData")
//                            .putParam("modelType", "2")
////                            .putParam("currversion", watchBase.versoinNumber + "")
//                            .setUUID(uuid)
//                            .setTag("checkDeviceNewVersion")
//                            .setProgressUUID(pid)
//                            .requestCallBack(new checkedDeviceNewVersionCallBack(null
//                                    , showerror))
//                            .execute(MyApplication.Companion.instance().getDeviceUpdateHttpComponet());
//                }
//            }
//        }
//    }
//
//    public void checkedDeviceNewVersion(WatchBase watchBase, String uuid, String pid, boolean showerror, boolean isUpdateDevice) {
//        validateUIThread();
//        if (watchBase instanceof SIATWatch) {
//            if (!isIsdeviceUpgrade()) {//如果设备没有在升级
//                String token = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.TOKEN, "");
//                ApiHelper.get(NetTool.base_url + "system-service/front/version/selectData")
//                        .putHeader("Authorization", token)
//                        .putParam("modelType", WatchBeanUtil.getWatchModelType() + "")
//                        .putParam("currversion", watchBase.versoinNumber + "")
//                        .setUUID(uuid)
//                        .setTag("checkDeviceNewVersion")
//                        .setProgressUUID(pid)
//                        .requestCallBack(new checkedDeviceNewVersionCallBack(null
//                                , showerror, isUpdateDevice))
//                        .execute(MyApplication.Companion.instance().getDeviceUpdateHttpComponet());
//            }
//        }
//    }
//
//
//    public static int FILEACCESS_REQUEST_CODE = 6789;
//
//    /**
//     * 是否拥有扩展存储器权限
//     *
//     * @return
//     */
//    public static boolean hasExternalStrorage_Permission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        }
//        return true;
//    }
//
//    /**
//     * 返回值的处理
//     *
//     * @param activity
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    public void onActivityResult(BaseActivity activity, int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == FILEACCESS_REQUEST_CODE) {
//            if (hasExternalStrorage_Permission()) {//如果拥有权限
//                if (hasNewVersion) {//并且有新版本
//                    if (DownloadFile(downLoadUrl, modeltype)) {
//                        IActivityManagerCallBase ui = getTopBaseActivity(application);
//                        if (ui != null) {
//                            if (ui instanceof BaseActivity) {
//                                ((BaseActivity) ui).showUpgradeDailog();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private BaseActivity getTopBaseActivity(CommonApplication commonApplication) {
//        ArrayList<IActivityManagerCallBase> uis = commonApplication.getActivityManager();
//        if (uis != null) {
//            if (uis.size() > 0) {
//                for (int i = uis.size() - 1; i >= 0; i--) {
//                    IActivityManagerCallBase managerCallBase = uis.get(i);
//                    if (managerCallBase instanceof BaseActivity) {
//                        return (BaseActivity) managerCallBase;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    public void checkWatchVersion(Activity activity, boolean showToast, boolean mainUpdate) {
//        if (activity == null) return;
//        if (WatchBeanUtil.isV101Watch("") || WatchBeanUtil.isUT001Watch("") || WatchBeanUtil.isTK12Watch(""))
//            return;
//        if (WatchBeanUtil.isEnglishApp()) return;
//        WatchBase watchBase = MyApplication.Companion.instance().getAdapetUtils().getConnectionWatch();
//        if (watchBase != null) {
//            if (!MyApplication.Companion.instance().getUpdateDeviceUtils().isIsdeviceUpgrade()) {
//                if (watchBase instanceof SIATWatch) {
//                    if (watchBase.versoinNumber > 0 || AppUtils.isDfuDevice(watchBase.getDeviceName())) {
////                        NetTool.getApi().selectData2(WatchBeanUtil.getWatchModelType())
//                        NetTool.getApi().getNewVersion(watchBase.versoinNumber)
//                                .compose(RxUtil.IoToMainObserve())
//                                .subscribe(new BeanObserver<WatchDevice>(activity) {
//                                    @Override
//                                    public void onSuccess(BaseResponse<WatchDevice> bean) {
//                                        if (bean != null && bean.getData() != null) {
//                                            WatchDevice device = bean.getData();
//                                            boolean isNewVersion = (watchBase.versoinNumber < device.getVersion()) ||
//                                                    AppUtils.isDfuDevice(watchBase.getDeviceName())/* || BuildConfig.DEBUG*/;
//                                            if (isNewVersion) {
////                                                checkUiUpdate(activity, watchBase, device, showToast, mainUpdate);
//                                                checkWatchVersion(activity, watchBase, device, showToast, mainUpdate);
//                                            } else {
//                                                if (showToast) {
//                                                    ToastUtil.showToast(MyApplication.Companion.instance(), activity.getResources().getString(R.string.watch_latest_version), Toast.LENGTH_SHORT);
//                                                }
//                                            }
//                                        } else {
//                                            if (showToast) {
//                                                ToastUtil.showToast(MyApplication.Companion.instance(), activity.getResources().getString(R.string.watch_latest_version), Toast.LENGTH_SHORT);
//                                            }
////                                            checkUiUpdate(activity, watchBase, null, showToast, mainUpdate);
//                                        }
//                                    }
//                                });
//                    } else {
//                        MyApplication.Companion.instance().showToast(activity.getResources().getString(R.string.watch_not_obtained));
//                    }
//                } else {
//                    MyApplication.Companion.instance().showToast(activity.getResources().getString(R.string.watch_not_support_upgrades));
//                }
//            } else {
//                MyApplication.Companion.instance().showToast(activity.getResources().getString(R.string.watch_upgrading_in_progress));
//            }
//        } else {
//            MyApplication.Companion.instance().showToast(activity.getResources().getString(R.string.watch_not_linked));
//        }
//    }
//
//    public void checkWatchVersion(Activity activity) {
//        if (activity == null) return;
//        if (WatchBeanUtil.isV101Watch("") || WatchBeanUtil.isUT001Watch("") || WatchBeanUtil.isTK12Watch(""))
//            return;
//        if (WatchBeanUtil.isEnglishApp()) return;
//        WatchBase watchBase = MyApplication.Companion.instance().getAdapetUtils().getConnectionWatch();
//        if (watchBase != null && watchBase.versoinNumber > 0) {
//            final WatchDevice[] normalWatchDevice = new WatchDevice[1];
////            NetTool.getApi().selectData2(WatchBeanUtil.getWatchModelType())
//            NetTool.getApi().getNewVersion(watchBase.versoinNumber)
////                    .flatMap(new Function<BaseResponse<WatchDevice>, ObservableSource<BaseResponse<WatchDevice>>>() {
////                        @Override
////                        public ObservableSource<BaseResponse<WatchDevice>> apply(BaseResponse<WatchDevice> watchDeviceBaseResponse) throws Exception {
////                            if (watchDeviceBaseResponse != null && watchDeviceBaseResponse.getData() != null) {
////                                normalWatchDevice[0] = watchDeviceBaseResponse.getData();
////                            }
////                            return NetTool.getApi().selectDataForUI(WatchBeanUtil.getWatchModelType(), watchBase.versoinNumber);
////                        }
////                    })
//                    .compose(RxUtil.IoToMainObserve())
//                    .subscribe(new BeanObserver<WatchDevice>(activity) {
//                        @Override
//                        public void onSuccess(BaseResponse<WatchDevice> bean) {
//                            WatchDevice device = null;
//                            if (bean != null && bean.getData() != null && (watchBase.versoinNumber < bean.getData().getVersion())) {
//                                device = bean.getData();
//                            } else {
//                                device = normalWatchDevice[0];
//                            }
//                            if (device != null) {
//                                int version = device.getVersion();
//                                int currversion = watchBase.versoinNumber;
//                                boolean isNewVersion = (currversion < version);
//                                if (isNewVersion) {
//                                    SPUtil.saveData(activity, SpConfig.IS_WATCH_UPDATE, true);
//                                    SPUtil.saveData(activity, SpConfig.WATCH_UPDATE_MSG, device.getContent());
//                                } else {
//                                    SPUtil.saveData(activity, SpConfig.IS_WATCH_UPDATE, false);
//                                    SPUtil.saveData(activity, SpConfig.WATCH_UPDATE_MSG, "");
//                                }
//                                EventBus.getDefault().post(new UpdateEvent());
//                            } else {
//                                SPUtil.saveData(activity, SpConfig.IS_WATCH_UPDATE, false);
//                            }
//                        }
//                    });
//        }
//    }
//
//    /**
//     * 更新UI图
//     *
//     * @param activity
//     * @param watchBase
//     * @param newDevice
//     * @param showToast
//     * @param mainUpdate
//     */
//    public void checkUiUpdate(Activity activity, WatchBase watchBase, WatchDevice newDevice, boolean showToast, boolean mainUpdate) {
//        NetTool.getApi().selectDataForUI(WatchBeanUtil.getWatchModelType(), watchBase.versoinNumber)
//                .compose(RxUtil.IoToMainObserve())
//                .subscribe(new Consumer<BaseResponse<WatchDevice>>() {
//                    @Override
//                    public void accept(BaseResponse<WatchDevice> watchDeviceBaseResponse) throws Exception {
//                        WatchDevice uiWatchDevice = null;
//                        if (watchDeviceBaseResponse != null && watchDeviceBaseResponse.getData() != null) {
//                            uiWatchDevice = watchDeviceBaseResponse.getData();
//                        }
//                        if (uiWatchDevice != null) {
//                            int curVersion = watchBase.versoinNumber;
//                            if (newDevice != null && newDevice.getVersion() == uiWatchDevice.getVersion()) {
//                                long newCreateTime = 0;
//                                long uiCreateTime = 0;
//                                if (!TextUtils.isEmpty(newDevice.getCreated()) && !TextUtils.isEmpty(uiWatchDevice.getCreated())) {
//                                    newCreateTime = DateTimeUtils.convertStrToDateForThisProject(newDevice.getCreated()).getTime();
//                                    uiCreateTime = DateTimeUtils.convertStrToDateForThisProject(uiWatchDevice.getCreated()).getTime();
//                                }
//                                if (newCreateTime > uiCreateTime) {
//                                    checkWatchVersion(activity, watchBase, newDevice, showToast, mainUpdate);
//                                } else {
//                                    checkWatchVersion(activity, watchBase, uiWatchDevice, showToast, mainUpdate);
//                                }
//                            } else {
//                                if (curVersion < uiWatchDevice.getVersion()) {
//                                    checkWatchVersion(activity, watchBase, uiWatchDevice, showToast, mainUpdate);
//                                } else {
//                                    checkWatchVersion(activity, watchBase, newDevice, showToast, mainUpdate);
//                                }
//                            }
//                        } else {
//                            checkWatchVersion(activity, watchBase, newDevice, showToast, mainUpdate);
//                        }
//                    }
//                });
//    }
//
//    public void checkWatchVersion(Activity activity, WatchBase watchBase, WatchDevice device, boolean showToast, boolean mainUpdate) {
//        if (device == null) {
//            if (showToast) {
//                ToastUtil.showToast(MyApplication.Companion.instance(), activity.getResources().getString(R.string.watch_latest_version), Toast.LENGTH_SHORT);
//            }
//            return;
//        }
//        int version = device.getVersion();
//        int currversion = watchBase.versoinNumber;
//        long modelType = device.getModelType();
//
//        String textUrl = device.getTextUrl();
//        String uploadFile = device.getUploadFile();
//
//        UpdateDeviceUtils updateDeviceUtils = MyApplication.Companion.instance().getUpdateDeviceUtils();
//        updateDeviceUtils.downLoadUrl = uploadFile;
//
//        LogUtils.i(TAG, " currversion == " + currversion + " " + new Gson().toJson(device));
//        boolean isNewVersion = /*AppUtils.compareVersion(device.getModelName(), watchBase.deviceVersion) ||*/ (currversion < version) ||
//                AppUtils.isDfuDevice(watchBase.getDeviceName()) /*|| BuildConfig.DEBUG*/;
//        if (isNewVersion) {
//            updateDeviceUtils.hasNewVersion = true;
//            updateDeviceUtils.modeltype = modelType;
//            SPUtil.saveData(activity, SpConfig.IS_WATCH_UPDATE, true);
//            SPUtil.saveData(activity, SpConfig.WATCH_UPDATE_MSG, device.getContent());
//        } else {
//            updateDeviceUtils.hasNewVersion = false;
//            SPUtil.saveData(activity, SpConfig.IS_WATCH_UPDATE, false);
//        }
//
//        boolean isForce = (device.getUploadType() == 1 || device.getUploadType() == 5 || device.getIsUi()) && mainUpdate;
//
//        if (mainUpdate && isNewVersion) {
//            isNewVersion = isCanUpdateDevice(device);
//        }
//        if (AppUtils.isDfuDevice(watchBase.getDeviceName()) /*|| BuildConfig.DEBUG*/) {
//            isForce = true;
//            isNewVersion = true;
//        }
//        if (isNewVersion) {
//            boolean finalIsForce = isForce;
//            DialogUtil.getInstance().showWatchUpdateDialog(activity, device.getVersionName() + "", device.getContent(), !isForce, new DialogUtil.OnDialogPositiveClickListener() {
//                @Override
//                public void onPositiveClickListener(View v, Object msg) {
//                    if (hasExternalStrorage_Permission()) {//拥有扩展权限
//                        if (updateDeviceUtils.DownloadFile(downLoadUrl, modelType)) {
//                            if (activity instanceof store.zootopia.app.version.base.BaseActivity) {
//                                ((store.zootopia.app.version.base.BaseActivity) activity).showUpgradeDailog();
//                            }
//                        }
//                    } else {
//                        openFileAccessManager(activity);
//                        application.showToast("没有读写扩展空间所有文件权限！请赋予权限");
//                    }
//                }
//            }, new DialogUtil.OnDialogDismissClickListener() {
//                @Override
//                public void onDismissClickListener(View v, boolean isSure) {
//                    if (finalIsForce) {
//                        if (BuildConfig.DEBUG) {
//                            Toast.makeText(activity, "退出App，测试用", Toast.LENGTH_SHORT).show();
//                        } else {
//                            System.exit(0);
//                        }
////                        LogUtils.i(TAG, " exit app ");
//                    }
//                }
//            });
//        } else {
//            if (showToast) {
//                Toast.makeText(activity, activity.getResources().getString(R.string.watch_latest_version), Toast.LENGTH_SHORT).show();
//            }
//        }
//        EventBus.getDefault().post(new UpdateEvent());
//        updateDeviceUtils.performVersion(updateDeviceUtils.hasNewVersion, device.getModelName(), showToast);
//    }
//
//    /**
//     * 1、强制更新 2、强制更新提示 3、弱提示更新 4、不提示更新 5、UI更新
//     * 是否能更新设备
//     *
//     * @param device
//     * @return
//     */
//    public boolean isCanUpdateDevice(WatchDevice device) {
//        boolean canUpdate = false;
//        if (device.getUploadType() == 1 || device.getUploadType() == 5) {
//            canUpdate = true;
//        } else if (device.getUploadType() == 2) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            int curDay = calendar.get(Calendar.DAY_OF_YEAR);
//            int saveDay = (int) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.UPDATE_WATCH_DAY, 0);
//            String oldVersion = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.UPDATE_WATCH_VERSION, "");
//            if (curDay != saveDay || !TextUtils.equals(device.getVersionName(), oldVersion)) {
//                SPUtil.saveData(MyApplication.Companion.instance(), SpConfig.UPDATE_WATCH_DAY, calendar.get(Calendar.DAY_OF_YEAR));
//                SPUtil.saveData(MyApplication.Companion.instance(), SpConfig.UPDATE_WATCH_VERSION, device.getVersionName());
//                canUpdate = true;
//            }
//        } else if (device.getUploadType() == 3) {
//            boolean isShow = (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_WATCH_SHOW_UPDATE, false);
//            String oldVersion = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.UPDATE_WATCH_VERSION, "");
//            if (!TextUtils.equals(device.getVersionName(), oldVersion) || !isShow) {
//                SPUtil.saveData(MyApplication.Companion.instance(), SpConfig.IS_WATCH_SHOW_UPDATE, true);
//                SPUtil.saveData(MyApplication.Companion.instance(), SpConfig.UPDATE_WATCH_VERSION, device.getVersionName());
//                canUpdate = true;
//            }
//        }
//        return canUpdate;
//    }
//
//    /**
//     * 开始下载文件
//     *
//     * @param modelType
//     */
//    private boolean DownloadFile(String url, long modelType) {
//        if (NetTool.isNewNet) {
//            return fileDownLoadUtils.DownloadDeviceUpdateZipFile(url);
//        } else {
//            return fileDownLoadUtils.DownloadDeviceUpdateZipFile(NetTool.base_url + "front/rversion/downloadFile?modelType=" + modelType);
//        }
//    }
//
//    //下载图片文件
//    private boolean downLoadImgTxt(String url) {
//        return fileDownLoadUtils.downloadImgTextFile(url);
//    }
//
//    private static class checkedDeviceNewVersionCallBack extends ProjectJsonRequestCallBack {
//
//        public boolean isUpdateDevice = false;
//
//        public checkedDeviceNewVersionCallBack(UIDataBinderBase<JSONObject> dataBinder, boolean showErrorToast) {
//            super(dataBinder, showErrorToast);
//        }
//
//        public checkedDeviceNewVersionCallBack(UIDataBinderBase<JSONObject> dataBinder, boolean showErrorToast, boolean updateDevice) {
//            super(dataBinder, showErrorToast);
//            this.isUpdateDevice = updateDevice;
//        }
//
//        public checkedDeviceNewVersionCallBack(UIDataBinderBase<JSONObject> dataBinder, String onSuccessInUIInvokeMethodName, String onErrorInUIInvokeMethodName, String onAfterInUIInvokeMethodName, boolean showErrorToast) {
//            super(dataBinder, onSuccessInUIInvokeMethodName, onErrorInUIInvokeMethodName, onAfterInUIInvokeMethodName, showErrorToast);
//        }
//
//        @Override
//        public void onSuccessInUI(CommonApplication con, HttpRequestBase req, JSONObject object) {
//            super.onSuccessInUI(con, req, object);
//            JsonResult jsonResult = new JsonResult(object);
//            if (jsonResult.Succeed()) {
//                JsonUtils data = jsonResult.getData();
//                if (data != null) {
//                    LogUtils.i(TAG, new Gson().toJson(data));
//                    int version = data.getInt("version", -1);
//                    int currversion = NumberUtils.convertStringToInt(req.getParam("currversion"), -1);
//                    int modelType = data.getInt("modelType", 2);
//                    String uploadFile = data.getString("uploadFile");
//                    UpdateDeviceUtils updateDeviceUtils = MyApplication.Companion.instance().getUpdateDeviceUtils();
//                    updateDeviceUtils.downLoadUrl = uploadFile;
////                    LogUtils.i(TAG, new Gson().toJson(data));
//                    if (version > currversion || isUpdateDevice) {
//                        isUpdateDevice = false;
//                        updateDeviceUtils.hasNewVersion = true;
//                        updateDeviceUtils.modeltype = modelType;
//                        BaseActivity baseActivity = getTopBaseActivity(con);
//                        if (baseActivity != null) {
//                            baseActivity.tisp.showDialog("手表有新版本,是否更新？"
//                                    , MyApplication.Companion.instance().getResources().getString(R.string.deny), MyApplication.Companion.instance().getResources().getString(R.string.correct), new ConfirmBtnClick(MyApplication.Companion.instance().getUpdateDeviceUtils(), con, baseActivity.getUUID(), modelType, uploadFile));
//                        }
//                    } else {
//                        updateDeviceUtils.hasNewVersion = false;
//                        if (showErrorToast) {
//                            showToast(con, MyApplication.Companion.instance().getResources().getString(R.string.watch_latest_version));
//                        }
//                    }
//                    updateDeviceUtils.performVersion(updateDeviceUtils.hasNewVersion, "", false);
//                } else {
//                    if (showErrorToast) {
//                        showToast(con, "无法获取版本信息");
//                    }
//                }
//            }
//        }
//
//
//        private static class ConfirmBtnClick implements ConfirmTipsView.OnBtnClickListener {
//            private UpdateDeviceUtils deviceUtils = null;
//            private CommonApplication application = null;
//            private String uuid = "";
//            private int modelType = 1;
//            private String downLoadUrl;
//
//            public ConfirmBtnClick(UpdateDeviceUtils deviceUtils, CommonApplication application, String uuid, int modelType) {
//                this.deviceUtils = deviceUtils;
//                this.application = application;
//                this.uuid = uuid;
//                this.modelType = modelType;
//            }
//
//            public ConfirmBtnClick(UpdateDeviceUtils deviceUtils, CommonApplication application, String uuid, int modelType, String downLoadUrl) {
//                this.deviceUtils = deviceUtils;
//                this.application = application;
//                this.uuid = uuid;
//                this.modelType = modelType;
//                this.downLoadUrl = downLoadUrl;
//            }
//
//            @Override
//            public void onClick(boolean isConfirm) {
//                IActivityManagerCallBase ui = application.getActivityManagerCallBaseByUUID(uuid);
//                if (ui != null) {
//                    if (ui instanceof BaseActivity) {
//                        ((BaseActivity) ui).tisp.dismiss();
//                    }
//                }
//                if (isConfirm) {
//                    if (hasExternalStrorage_Permission()) {//拥有扩展权限
//                        if (deviceUtils.DownloadFile(downLoadUrl, modelType)) {
//                            if (ui != null) {
//                                if (ui instanceof BaseActivity) {
//                                    ((BaseActivity) ui).showUpgradeDailog();
//                                }
//                            }
//                        }
//                    } else {
//                        if (ui != null) {
//                            if (ui instanceof BaseActivity) {
//                                openFileAccessManager((BaseActivity) ui);
//                            }
//                        }
//                        application.showToast("没有读写扩展空间所有文件权限！请赋予权限");
//                    }
//                }
//                application = null;
//                deviceUtils = null;
//            }
//
//
//            /**
//             * 打开文件访问赋予权限
//             */
//            private void openFileAccessManager(BaseActivity activity) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
//                    activity.startActivityForResult(intent, FILEACCESS_REQUEST_CODE);
//                }
//            }
//        }
//
//
//        private BaseActivity getTopBaseActivity(CommonApplication commonApplication) {
//            ArrayList<IActivityManagerCallBase> uis = commonApplication.getActivityManager();
//            if (uis != null) {
//                if (uis.size() > 0) {
//                    for (int i = uis.size() - 1; i >= 0; i--) {
//                        IActivityManagerCallBase managerCallBase = uis.get(i);
//                        if (managerCallBase instanceof BaseActivity) {
//                            return (BaseActivity) managerCallBase;
//                        }
//                    }
//                }
//            }
//            return null;
//        }
//    }
//
//    /**
//     * 打开文件访问赋予权限
//     */
//    private void openFileAccessManager(Context activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//            intent.setData(Uri.parse("package:" + activity.getPackageName()));
//            if (activity instanceof Activity) {
//                ((Activity) activity).startActivityForResult(intent, FILEACCESS_REQUEST_CODE);
//            } else {
//                activity.startActivity(intent);
//            }
//        }
//    }

}
