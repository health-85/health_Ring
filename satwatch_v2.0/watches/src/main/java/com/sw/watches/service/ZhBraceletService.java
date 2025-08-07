package com.sw.watches.service;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.sw.watches.bean.AlarmInfo;
import com.sw.watches.bean.DrinkInfo;
import com.sw.watches.bean.MedicalInfo;
import com.sw.watches.bean.MeetingInfo;
import com.sw.watches.bean.MesureInfo;
import com.sw.watches.bean.SitInfo;
import com.sw.watches.bean.UserCalibration;
import com.sw.watches.bean.UserInfo;
import com.sw.watches.bean.DeviceModule;
import com.sw.watches.listener.ConnectorListener;
import com.sw.watches.listener.SimplePerformerListener;
import com.sw.watches.listener.UpgradeDeviceListener;
import com.sw.watches.bluetooth.ParseWatchesData;
import com.sw.watches.bluetooth.SIATCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint({"DefaultLocale"})
public class ZhBraceletService extends WatchService {

    private static final String TAG = "ZhBraceletService";

    public final LocalBinder localBinder = new LocalBinder(this);

    public ZhBraceletService() {

    }

    public IBinder onBind(Intent intent) {
        return (IBinder) localBinder;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * 绑定设备
     *
     * @param deviceModule
     */
    @SuppressLint("MissingPermission")
    public void BindDevice(DeviceModule deviceModule) {
        super.executor.execute(() -> {
            if (deviceModule != null && !TextUtils.isEmpty(deviceModule.getMac()) && !connectState) {
                disconnect();
                mBluetoothDevice = deviceModule.getDevice();
                if (mBluetoothDevice == null) {
                    log("找不到设备");
                    Looper.prepare();
                    Toast.makeText(ZhBraceletService.this, "找不到设备", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    return;
                }
                if (spDeviceTools != null && mBluetoothDevice != null && !TextUtils.isEmpty(mBluetoothDevice.getName())){
                    spDeviceTools.putBleName(mBluetoothDevice.getName());
                }
                log("开始连接设备");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE,
                            BluetoothDevice.PHY_LE_1M_MASK | BluetoothDevice.PHY_LE_2M_MASK);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
                } else {
                    mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, gattCallback);
                }
            }
        });
    }

    /**
     * 解绑设备
     */
    public void UnBindDevice() {
        spDeviceTools.putBleMac("");
        disconnect();
    }

    /**
     * 重新连接已绑定设备
     */
    public void tryConnectDevice() {
        executor.execute(() -> {
            String bleMac = spDeviceTools.getBleMac();
            if (bleMac != null && !bleMac.equals("")) {
                if (!connectState) {
                    disconnect();
                    connect(bleMac);
                }
                return;
            }
        });
    }

    /**
     * 获取连接状态
     *
     * @return
     */
    public boolean getBleConnectState() {
        return connectState;
    }

    /**
     * 添加连接状态
     *
     * @param connectorListener
     */
    public void addConnectorListener(ConnectorListener connectorListener) {
        if (!connectorListenersList.contains(connectorListener))
            connectorListenersList.add(connectorListener);
    }

    /**
     * 移除连接状态
     *
     * @param connectorListener
     */
    public void removeConnectorListener(ConnectorListener connectorListener) {
        if (connectorListenersList.contains(connectorListener))
            connectorListenersList.remove(connectorListener);
    }

    /**
     * 添加数据监听
     *
     * @param listener
     */
    public void addSimplePerformerListenerLis(SimplePerformerListener listener) {
        if (!simplePerformerListenerList.contains(listener))
            simplePerformerListenerList.add(listener);
    }

    /**
     * 移除数据监听
     *
     * @param listener
     */
    public void removeSimplePerformerListenerLis(SimplePerformerListener listener) {
        if (simplePerformerListenerList.contains(listener))
            simplePerformerListenerList.remove(listener);
    }

    /**
     * 同步时间
     */
    public void syncTime() {
        sendThread(SIATCommand.getSyncTimeCom());
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        sendThread(SIATCommand.getSyncTimeCom(SIATCommand.SIGN_AA_HEAD));
    }

    /**
     * 查找设备
     */
    public void findDevice() {
//        sendThread(SIATCommand.getFindDeviceCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getFindDeviceCom());
    }

    /**
     * 设置采用间隔时长
     */
    public void setCollectTimeGap(int uptime) {
        sendThread(SIATCommand.getCollectGapTime(uptime));
    }


    /**
     * 获取设备信息
     */
    public void getDeviceInfo() {
//        sendThread(SIATCommand.getDeviceInfoCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getDeviceInfoCom());
    }

    /**
     * 测量血压、心率
     */
    public void openMeasurement() {
        isReceiveSaveData = false;
//        sendThread(SIATCommand.getOpenMeasurementCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getOpenMeasurementCom());
    }

    /**
     * 关闭血压、心率
     */
    public void closeMeasurement() {
//        sendThread(SIATCommand.getCloseMeasurementCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getCloseMeasurementCom());
    }

    //发送生命力
    public void sendVitality(List<Integer> list){
//        sendThread(SIATCommand.getLifeCom(SIATCommand.SIGN_HEAD, list));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getLifeCom(list));
    }

    private boolean unbinderUpgradeList() {
        if (uploadDeviceListener == null) {
            Toast.makeText(this, "未添加升级监听", Toast.LENGTH_LONG).show();
            return true;
        } else {
            return false;
        }
    }

    private boolean isDfuServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Iterator<ActivityManager.RunningServiceInfo> iterator = manager.getRunningServices(Integer.MAX_VALUE).iterator();
        do {
            if (!iterator.hasNext()) {
                return false;
            }
        } while (!DfuService.class.getName().equals(((ActivityManager.RunningServiceInfo) iterator.next()).service.getClassName()));
        return true;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return super.getBluetoothAdapter();
    }


    /**
     * 添加更新设备提醒
     *
     * @param upgradeDeviceListener
     */
    public void addUpgradeDeviceListener(UpgradeDeviceListener upgradeDeviceListener) {
        super.addUpgradeDeviceListener(upgradeDeviceListener);
    }

    /**
     * 移除更新设备提醒
     */
    public void removeUpgradeDeviceListener() {
        super.removeUpgradeDeviceListener();
    }

    /**
     * 获取Mac
     *
     * @return
     */
    public String getBleMac() {
        return super.getBleMac();
    }

    /**
     * 更新设备
     */
    public void upgradeDevice() {
        super.upgradeDevice();
    }

    /**
     * 取消更新设备
     */
    public void cancelUpgrade() {
        super.cancelUpgrade();
    }

    /**
     * 停止更新设备
     */
    public void pauseUpgrade() {
        super.pauseUpgrade();
    }

    public void resumeUpgrade() {
        super.resumeUpgrade();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        super.disconnect();
    }

    /**
     * 穿戴模式
     *
     * @return
     */
    public boolean getWearType() {
        return spDeviceTools.getColockType();
    }

    /**
     * 设备单位
     *
     * @return
     */
    public boolean getUnit() {
        return spDeviceTools.getDeviceUnit();
    }

    public boolean getTimeFormat() {
        return spDeviceTools.getColockType();
    }

    /**
     * 语言
     *
     * @return
     */
    public int getLanguagen() {
        return spDeviceTools.getLanguage();
    }

    public boolean getPoHeart() {
        return spDeviceTools.getPointMeasurementHeart();
    }

    public boolean getWoHeart() {
        return spDeviceTools.getWointMeasurementHeart();
    }

    public boolean getTaiWan() {
        return spDeviceTools.getTaiwan();
    }

    public boolean getZhuanWan() {
        return spDeviceTools.getZhuanwan();
    }

    public boolean getSnoreMonitor() {
        return spDeviceTools.getSnoreMonitor();
    }

    public boolean getHighHeartRemind() {
        return spDeviceTools.getHighHeartRemind();
    }

    /**
     * 勿扰模式是否打开
     *
     * @return
     */
    public boolean getNotDisturb() {
        return spDeviceTools.getNotDisturb();
    }

    /**
     * 恢复厂商设备
     */
    public void restore_factory() {
//        sendThread(SIATCommand.getRestoreFactoryCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getRestoreFactoryCom());
    }

    /**
     * 设置语言
     *
     * @param languagen
     */
    public void setLanguagen(int languagen) {
        spDeviceTools.putLanguage(languagen);
//        sendThread(SIATCommand.getLanguagenCom(SIATCommand.SIGN_HEAD, languagen));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getLanguagenCom(languagen));
    }

    public void setWearType(Boolean b) {
        spDeviceTools.putWearWay(b.booleanValue());
    }

    public void setTimeFormat(boolean b) {
        spDeviceTools.putColockType(b);
        if (b) {
//            sendThread(SIATCommand.getTimeFormatCom(SIATCommand.SIGN_HEAD, 1));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getTimeFormatCom(1));
        } else {
//            sendThread(SIATCommand.getTimeFormatCom(SIATCommand.SIGN_HEAD, 0));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getTimeFormatCom(0));
        }
    }

    public void setUnit(boolean unit) {
        spDeviceTools.putDeviceUnit(unit);
        setStepAlgorithmType(unit);
        if (unit) {
//            sendThread(SIATCommand.getUnitCom(SIATCommand.SIGN_HEAD, 1));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getUnitCom(1));
        } else {
//            sendThread(SIATCommand.getUnitCom(SIATCommand.SIGN_HEAD,0));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getUnitCom(0));
        }
    }

    public void setWoHeart(boolean b) {
        spDeviceTools.putWointMeasurementHeart(b);
        if (b) {
//            sendThread(SIATCommand.getWoHeartCom(SIATCommand.SIGN_HEAD,1));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getWoHeartCom(1));
        } else {
//            sendThread(SIATCommand.getWoHeartCom(SIATCommand.SIGN_HEAD,0));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getWoHeartCom(0));
        }
    }

    public void setTaiWan(boolean b) {
        spDeviceTools.putTaiwan(b);
        if (b) {
//            sendThread(SIATCommand.getTaiWanCom(SIATCommand.SIGN_HEAD,1));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getTaiWanCom(1));
        } else {
//            sendThread(SIATCommand.getTaiWanCom(SIATCommand.SIGN_HEAD,0));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getTaiWanCom(0));
        }
    }

    public void setZhuanWan(boolean b) {
        spDeviceTools.putZhuanwan(b);
        if (b) {
//            sendThread(SIATCommand.getZhuanWanCom(SIATCommand.SIGN_HEAD,1));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getZhuanWanCom(1));
        } else {
//            sendThread(SIATCommand.getZhuanWanCom(SIATCommand.SIGN_HEAD,0));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getZhuanWanCom(0));
        }
    }

    public void setSnoreMonitor(boolean b) {
        spDeviceTools.putSnoreMonitor(b);
        if (b) {
//            sendThread(SIATCommand.getSnoreMonitorCom(SIATCommand.SIGN_HEAD,1));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getSnoreMonitorCom(1));
        } else {
//            sendThread(SIATCommand.getSnoreMonitorCom(SIATCommand.SIGN_HEAD,0));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getSnoreMonitorCom(0));
        }
    }
    /**
     * 设置勿扰模式
     *
     * @param b
     */
    public void setNotDisturb(boolean b) {
        spDeviceTools.putNotDisturb(b);
        if (b) {
//            sendThread(SIATCommand.getNotDisturbCom(SIATCommand.SIGN_HEAD,1));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getNotDisturbCom(1));
        } else {
//            sendThread(SIATCommand.getNotDisturbCom(SIATCommand.SIGN_HEAD,0));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getNotDisturbCom(0));
        }
    }

    /**
     * 设置整点心率
     *
     * @param b
     */
    public void setPoHeart(boolean b) {
        spDeviceTools.putPointMeasurementHeart(b);
        if (b) {
//            sendThread(SIATCommand.getPoHeartCom(SIATCommand.SIGN_HEAD,1));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getPoHeartCom(1));
        } else {
//            sendThread(SIATCommand.getPoHeartCom(SIATCommand.SIGN_HEAD,0));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getPoHeartCom(0));
        }
    }

    public void setHighHeartRemind(boolean b) {
        spDeviceTools.putHighHeartRemind(b);
        if (b) {
//            sendThread(SIATCommand.getHighHeartRemindCom(SIATCommand.SIGN_HEAD,1));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getHighHeartRemindCom(1));
        } else {
//            sendThread(SIATCommand.getHighHeartRemindCom(SIATCommand.SIGN_HEAD,0));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getHighHeartRemindCom(0));
        }
    }

    public void setUserInfo(UserInfo userInfo) {
        spDeviceTools.putUserHeight(userInfo.getUserHeight());
        spDeviceTools.putUserWeight(userInfo.getUserWeight());
        spDeviceTools.putUserAge(userInfo.getAge());
        spDeviceTools.putUserSex(userInfo.getSex().booleanValue());
//        sendThread(SIATCommand.getUserInfoCom(SIATCommand.SIGN_HEAD, userInfo.getSex().booleanValue() ? 1 : 0, spDeviceTools.getUserAge(),
//                spDeviceTools.getUserHeight(), spDeviceTools.getUserWeight()));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getUserInfoCom(userInfo.getSex().booleanValue() ? 1 : 0, spDeviceTools.getUserAge(),
                spDeviceTools.getUserHeight(), spDeviceTools.getUserWeight()));
    }

    public void setCamera(boolean b) {
        spDeviceTools.putControlPhoto(b);
//        sendThread(SIATCommand.getCameraCom(SIATCommand.SIGN_HEAD, b ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getCameraCom(b ? 1 : 0));
    }

    public ArrayList<AlarmInfo> getAlarmData() {
        return ParseWatchesData.stringToAlarmList(spDeviceTools.getAlarmData());
    }

    public ArrayList<AlarmInfo> addAlarmData(ArrayList<AlarmInfo> infoArrayList, AlarmInfo info) {
        return ParseWatchesData.addAlarmInfo(infoArrayList, info);
    }

    public ArrayList<AlarmInfo> deleteAlarmData(ArrayList<AlarmInfo> infoArrayList, int position) {
        return ParseWatchesData.removeAlarmInfo(infoArrayList, position);
    }

    public ArrayList<AlarmInfo> updateAlarmData(ArrayList<AlarmInfo> infoArrayList, AlarmInfo info, int position) {
        return ParseWatchesData.setAlarmInfo(infoArrayList, info, position);
    }

    public void saveAlarmData(ArrayList<AlarmInfo> infoArrayList) {
        spDeviceTools.putAlarmData(ParseWatchesData.alarmInfoListToJson(infoArrayList));
//        sendThread(SIATCommand.getSaveAlarmDataCom(SIATCommand.SIGN_HEAD, infoArrayList));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSaveAlarmDataCom(infoArrayList));
    }

    public void saveAlarmData(AlarmInfo alarmInfo) {
        ArrayList<AlarmInfo> infoArrayList = new ArrayList<>();
        infoArrayList.add(alarmInfo);
        spDeviceTools.putAlarmData(ParseWatchesData.alarmInfoListToJson(infoArrayList));
//        sendThread(SIATCommand.getSaveAlarmDataCom(SIATCommand.SIGN_HEAD, infoArrayList));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSaveAlarmDataCom(infoArrayList));
    }

    public void saveRepeatAlarmData(ArrayList<AlarmInfo> infoArrayList) {
        spDeviceTools.putAlarmData(ParseWatchesData.alarmInfoListToJson(infoArrayList));
//        sendThread(SIATCommand.getSaveRepeatAlarmDataCom(SIATCommand.SIGN_HEAD, infoArrayList));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSaveRepeatAlarmDataCom(infoArrayList));
    }

    public void closeDevice() {
//        sendThread(SIATCommand.getCloseDeviceCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getCloseDeviceCom());
    }

    public void deviceFullShow() {
//        sendThread(SIATCommand.getDeviceFullShowCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getDeviceFullShowCom());
    }

    public void setStepAlgorithmType(boolean b) {
        spDeviceTools.putStepAlgorithmType(b ? 1 : 0);
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setBrightScreenTime(int lightTime) {
        spDeviceTools.putLightTime(lightTime);
//        sendThread(SIATCommand.getBrightScreenTimeCom(SIATCommand.SIGN_HEAD, lightTime));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getBrightScreenTimeCom(lightTime));
    }

    public int getLightTime() {
        return spDeviceTools.getLightTime();
    }

    public void setBrightness(int brightness) {
        spDeviceTools.putBrightness(brightness);
//        sendThread(SIATCommand.getBrightnessCom(SIATCommand.SIGN_HEAD, brightness));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getBrightnessCom(brightness));
    }

    public int getBrightness() {
        return spDeviceTools.getBrightness();
    }

    public void setUIType(int ui_type) {
        spDeviceTools.putUiType(ui_type);
//        sendThread(SIATCommand.getUITypeCom(SIATCommand.SIGN_HEAD, ui_type));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getUITypeCom(ui_type));
    }

    public int getUIType() {
        return spDeviceTools.getUiType();
    }

    public void setSkin(int skin) {
        spDeviceTools.putSkin(skin);
//        sendThread(SIATCommand.getSkinCom(SIATCommand.SIGN_HEAD, skin));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSkinCom(skin));
    }

    public int getSkin() {
        return spDeviceTools.getSkin();
    }

    public void setStepNumber(int h_number) {
        spDeviceTools.puthNumber(h_number);
//        sendThread(SIATCommand.getStepNumberCom(SIATCommand.SIGN_HEAD, h_number));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getStepNumberCom(h_number));
    }

    public int getStepNumber() {
        return spDeviceTools.gethNumber();
    }

    public void getImageInfo() {
//        sendThread(SIATCommand.getImageInfoCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getImageInfoCom());
    }

    public void responePhone() {
//        sendThread(SIATCommand.getResponePhoneCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getResponePhoneCom());
    }

    public void getHardwareStatue() {
//        sendThread(SIATCommand.getHardwareStatueCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getHardwareStatueCom());
    }

    public boolean[] getCheckBoolean(byte b) {
        return ParseWatchesData.parseCheckBoolean(b);
    }

    public int getCheckInt(boolean[] booleans) {
        return ParseWatchesData.parseCheckInt(booleans);
    }

    public void setMedicalInfo(MedicalInfo medicalInfo) {
        spRemindTools.putMedicalStartHour(medicalInfo.getMedicalStartHour());
        spRemindTools.putMedicalStartMin(medicalInfo.getMedicalStartMin());
        spRemindTools.putMedicalEndHour(medicalInfo.getMedicalEndHour());
        spRemindTools.putMedicalEndMin(medicalInfo.getMedicalEndMin());
        spRemindTools.putMedicalPeriod(medicalInfo.getMedicalPeriod());
        spRemindTools.putMedicalEnable(medicalInfo.getMedicalEnable());
        boolean bool = spRemindTools.getMedicalEnable();
//        sendThread(SIATCommand.getMedicalInfoCom(SIATCommand.SIGN_HEAD,spRemindTools.getMedicalStartHour(), spRemindTools.getMedicalStartMin(), spRemindTools.getMedicalEndHour()
//                , spRemindTools.getMedicalEndMin(), spRemindTools.getMedicalPeriod(), bool));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getMedicalInfoCom(spRemindTools.getMedicalStartHour(), spRemindTools.getMedicalStartMin(), spRemindTools.getMedicalEndHour()
                , spRemindTools.getMedicalEndMin(), spRemindTools.getMedicalPeriod(), bool));
    }

    public MedicalInfo getMedicalInfo() {
        boolean bool = spRemindTools.getMedicalEnable();
        return new MedicalInfo(spRemindTools.getMedicalStartHour(), spRemindTools.getMedicalStartMin(),
                spRemindTools.getMedicalEndHour(), spRemindTools.getMedicalEndMin(), spRemindTools.getMedicalPeriod(), bool);
    }

    public void setSitInfo(SitInfo sitInfo) {
        spRemindTools.putSitStartHour(sitInfo.getSitStartHour());
        spRemindTools.putSitStartMin(sitInfo.getSitStartMin());
        spRemindTools.putSitEndHour(sitInfo.getSitEndHour());
        spRemindTools.putSitEndMin(sitInfo.getSitEndMin());
        spRemindTools.putSitPeriod(sitInfo.getSitPeriod());
        spRemindTools.putSitEnable(sitInfo.isSitEnable());
        boolean bool = spRemindTools.getSitEnable();
//        sendThread(SIATCommand.getSitInfoCom(SIATCommand.SIGN_HEAD, spRemindTools.getSitStartHour(), spRemindTools.getSitStartMin(), spRemindTools.getSitEndHour(),
//                spRemindTools.getSitEndMin(), spRemindTools.getSitPeriod(), bool));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSitInfoCom(spRemindTools.getSitStartHour(), spRemindTools.getSitStartMin(), spRemindTools.getSitEndHour(),
                spRemindTools.getSitEndMin(), spRemindTools.getSitPeriod(), bool));
    }

    public SitInfo getSitInfo() {
        boolean bool = spRemindTools.getSitEnable();
        return new SitInfo(spRemindTools.getSitStartHour(), spRemindTools.getSitStartMin(), spRemindTools.getSitEndHour(),
                spRemindTools.getSitEndMin(), spRemindTools.getSitPeriod(), bool);
    }

    public void setDrinkInfo(DrinkInfo drinkInfo) {
        spRemindTools.putDrinkStartHour(drinkInfo.getDrinkStartHour());
        spRemindTools.putDrinkStartMin(drinkInfo.getDrinkStartMin());
        spRemindTools.putDrinkEndHour(drinkInfo.getDrinkEndHour());
        spRemindTools.putDrinkEndMin(drinkInfo.getDrinkEndMin());
        spRemindTools.putDrinkPeriod(drinkInfo.getDrinkPeriod());
        spRemindTools.putDrinkEnable(drinkInfo.getDrinkEnable());
        boolean bool = spRemindTools.getDrinkEnable();
//        sendThread(SIATCommand.getDrinkInfoCom(SIATCommand.SIGN_HEAD, spRemindTools.getDrinkStartHour(), spRemindTools.getDrinkStartMin(), spRemindTools.getDrinkEndHour(),
//                spRemindTools.getDrinkEndMin(), spRemindTools.getDrinkPeriod(), bool));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getDrinkInfoCom(spRemindTools.getDrinkStartHour(), spRemindTools.getDrinkStartMin(), spRemindTools.getDrinkEndHour(),
                spRemindTools.getDrinkEndMin(), spRemindTools.getDrinkPeriod(), bool));
    }

    public DrinkInfo getDrinkInfo() {
        boolean bool = spRemindTools.getDrinkEnable();
        return new DrinkInfo(spRemindTools.getDrinkStartHour(), spRemindTools.getDrinkStartMin(), spRemindTools.getDrinkEndHour(),
                spRemindTools.getDrinkEndMin(), spRemindTools.getDrinkPeriod(), bool);
    }

    public void setMeetingInfo(MeetingInfo meetingInfo) {
        spRemindTools.putMeedingYear(meetingInfo.getMeetingYear());
        spRemindTools.putMeedingMonth(meetingInfo.getMeetingMonth());
        spRemindTools.putMeedingDay(meetingInfo.getMeetingDay());
        spRemindTools.putMeedingHour(meetingInfo.getMeetingHour());
        spRemindTools.putMeedingMin(meetingInfo.getMeetingMin());
        spRemindTools.putMeedingEnable(meetingInfo.getMeetingEnable());
        boolean bool = spRemindTools.getMeedingEnable();
//        sendThread(SIATCommand.getMeetingInfoCom(SIATCommand.SIGN_HEAD, spRemindTools.getMeedingYear(), spRemindTools.getMeedingMonth(), spRemindTools.getMeedingDay(),
//                spRemindTools.getMeedingHour(), spRemindTools.getMeedingMin(), bool));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getMeetingInfoCom(spRemindTools.getMeedingYear(), spRemindTools.getMeedingMonth(), spRemindTools.getMeedingDay(),
                spRemindTools.getMeedingHour(), spRemindTools.getMeedingMin(), bool));
    }

    public MeetingInfo getMeetingInfo() {
        boolean bool = spRemindTools.getMeedingEnable();
        return new MeetingInfo(spRemindTools.getMeedingYear(), spRemindTools.getMeedingMonth(), spRemindTools.getMeedingDay(),
                spRemindTools.getMeedingHour(), spRemindTools.getMeedingMin(), bool);
    }

    public void setUserCalibration(UserCalibration userCalibration) {
        spDeviceTools.putUserCalibrationHr(userCalibration.getUserCalibrationHR());
        spDeviceTools.putUserCalibrationSbp(userCalibration.getUserCalibrationSBP());
        spDeviceTools.putUserCalibrationDbp(userCalibration.getUserCalibrationDBP());
//        sendThread(SIATCommand.getUserCalibrationCom(SIATCommand.SIGN_HEAD, spDeviceTools.getUserCalibrationSbp(), spDeviceTools.getUserCalibrationHr()));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getUserCalibrationCom(spDeviceTools.getUserCalibrationSbp(), spDeviceTools.getUserCalibrationHr()));
    }

    public void setMeasureInfo(MesureInfo measureInfo) {
//        sendThread(SIATCommand.getMeasureInfoCom(SIATCommand.SIGN_HEAD, measureInfo.getMesureInfoHR(), measureInfo.getMesureInfoSBP(), measureInfo.getMesureInfoDBP()));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getMeasureInfoCom(measureInfo.getMesureInfoHR(), measureInfo.getMesureInfoSBP(), measureInfo.getMesureInfoDBP()));
    }

    //发送蓝牙图片
    public void sendBleImgCom(byte[] addr, byte[] size){
//        sendThread(SIATCommand.getSendImgCom(SIATCommand.SIGN_HEAD, addr, size));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSendImgCom(addr, size));
    }

    //发送睡眠心率和血氧
    public void sendSleepHeartAndOx(int sleepAverageHeart, int sleepAverageOx){
//        sendThread(SIATCommand.getSendHeartAndOx(SIATCommand.SIGN_HEAD, sleepAverageHeart, sleepAverageOx));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSendHeartAndOx(sleepAverageHeart, sleepAverageOx));
    }

    //发送开关
    public void sendSwitchCom(boolean heartSwitchRemind, boolean sitSwitchRemind, boolean sleepSwitchRemind, boolean oxSwitchRemind, boolean disturbSwitchRemind, boolean sleepOxRemind){
//        sendThread(SIATCommand.sendSwitchCom(SIATCommand.SIGN_HEAD, heartSwitchRemind, sitSwitchRemind, sleepSwitchRemind, oxSwitchRemind, disturbSwitchRemind, sleepOxRemind));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.sendSwitchCom(heartSwitchRemind, sitSwitchRemind, sleepSwitchRemind, oxSwitchRemind, disturbSwitchRemind, sleepOxRemind));
    }

    public void measureHeart(){
        sendTestHeartCom();
    }

    public void measureOx(){
        sendTestOxCom();
    }

    public void measureHeat(){
        sendTestHeatCom();
    }

    public void measureTemp(){
        sendTestTempCom();
    }


    public void measureTireAndPressure(){
        sendTestTirePressureCom();
    }

    public class LocalBinder extends Binder {

        public LocalBinder(ZhBraceletService service) {

        }

        public ZhBraceletService getService() {
            return ZhBraceletService.this;
        }
    }
}