package com.sw.watches.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.realsil.sdk.core.RtkConfigure;
import com.realsil.sdk.core.RtkCore;
import com.realsil.sdk.core.bluetooth.BluetoothProfileManager;
import com.realsil.sdk.core.bluetooth.GlobalGatt;
import com.realsil.sdk.core.logger.WriteLog;
import com.realsil.sdk.core.utility.DataConverter;
import com.realsil.sdk.dfu.DfuConstants;
import com.realsil.sdk.dfu.RtkDfu;
import com.realsil.sdk.dfu.image.BaseBinInputStream;
import com.realsil.sdk.dfu.image.BinFactory;
import com.realsil.sdk.dfu.image.BinIndicator;
import com.realsil.sdk.dfu.image.LoadParams;
import com.realsil.sdk.dfu.model.BinInfo;
import com.realsil.sdk.dfu.model.DfuConfig;
import com.realsil.sdk.dfu.model.DfuProgressInfo;
import com.realsil.sdk.dfu.model.OtaDeviceInfo;
import com.realsil.sdk.dfu.model.Throughput;
import com.realsil.sdk.dfu.quality.DfuQualitySDK;
import com.realsil.sdk.dfu.support.DfuHelperImpl;
import com.realsil.sdk.dfu.support.settings.SettingsHelper;
import com.realsil.sdk.dfu.utils.DfuAdapter;
import com.realsil.sdk.dfu.utils.GattDfuAdapter;
import com.sw.watches.BuildConfig;
import com.sw.watches.R;
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
import com.sw.watches.bean.SleepData;
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
import com.sw.watches.bleUtil.ByteToStringUtil;
import com.sw.watches.bluetooth.ParseWatchesData;
import com.sw.watches.bluetooth.SIATCommand;
import com.sw.watches.bleUtil.SpDeviceTools;
import com.sw.watches.bleUtil.SpRemindTools;
import com.sw.watches.bleUtil.TelephonyUtil;
import com.sw.watches.listener.ConnectorListener;
import com.sw.watches.listener.ResponsePerformerListener;
import com.sw.watches.listener.SimplePerformerListener;
import com.sw.watches.listener.UpgradeDeviceListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class WatchService extends Service {

    //旧手表型号
    public static final UUID GATT_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final UUID WRITE_GATT_CHARACTERISTIC = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");

    public static final UUID FIRST_GATT_SERVICE_UUID = UUID.fromString("00003e01-0000-1000-8000-00805f9b34fb");
    public static final UUID SECOND_GATT_SERVICE_UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    public static final UUID THREE_GATT_SERVICE_UUID = UUID.fromString("00002c01-0000-1000-8000-00805f9b34fb");

    public static final UUID FIRST_GATT_CHARACTERISTIC = UUID.fromString("00003e03-0000-1000-8000-00805f9b34fb");
    public static final UUID SECOND_GATT_CHARACTERISTIC = UUID.fromString("00002c03-0000-1000-8000-00805f9b34fb");
    public static final UUID FOUR_GATT_CHARACTERISTIC = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");

    //新手表型号
    final static UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");

    final static UUID GENERIC_ATTRIBUTE_SERVICE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    final static UUID SERVICE_CHANGED_CHARACTERISTIC = UUID.fromString("00002A05-0000-1000-8000-00805f9b34fb");

    final static UUID UUID_SERVICE_DEVICE = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    final static UUID UUID_MSG_CHARACTERISTIC = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");

    final static UUID RTL_UUID_SERVICE_DEVICE = UUID.fromString("0000cc00-0000-1000-8000-00805f9b34fb");
    final static UUID RTL_UUID_SERVICE_DEVICE_1 = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    final static UUID RTL_UUID_SERVICE_DEVICE_2 = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    final static UUID RTL_UUID_MSG_CHARACTERISTIC = UUID.fromString("0000cc02-0000-1000-8000-00805f9b34fb");
    final static UUID RTL_UUID_NOTIFY_CHARACTERISTIC = UUID.fromString("0000cc03-0000-1000-8000-00805f9b34fb");
    final static UUID RTL_UUID_NOTIFY_CHARACTERISTIC_2 = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    final static UUID RTL_UUID_NOTIFY_CHARACTERISTIC_3 = UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb");

//    final static UUID RTL_UUID_NOTIFY_CHARACTERISTIC_4 = UUID.fromString("0000cc04-0000-1000-8000-00805f9b34fb");
//    final static UUID RTL_UUID_NOTIFY_CHARACTERISTIC_1 = UUID.fromString("0000cc01-0000-1000-8000-00805f9b34fb");

    public static final int NOT_ZIP = 3;
    public static final int NOT_BIND_DEVICE = 2;
    public static final int DISCONNECTING = 1;
    public static final int ABORTED = 0;

    public Handler mHandler;

    public static final String[] contact_info = new String[]{"display_name", "data1", "photo_id", "contact_id"};

    public int taiWan = 0;
    public int zhuanWan = 0;
    public int notDisturb = 0;
    public int colockType = 0;
    public int deviceUnit = 0;
    public int measurementHeart = 0;

    public BluetoothGattService mBluetoothGattServiceFirst = null;
    public BluetoothGattService mBluetoothGattServiceSecond = null;
    public BluetoothGattService mBluetoothGattServiceThree = null;
    public BluetoothGattService mBluetoothGattServiceFour = null;
    public BluetoothGattService mBluetoothGattServiceFive = null;
    public BluetoothGattService mBluetoothGattServiceSix = null;
    public BluetoothGattService mBluetoothGattServiceSeven = null;

    public BluetoothGattCharacteristic mBluetoothGattCharacteristicFirst;
    public BluetoothGattCharacteristic mBluetoothGattCharacteristicSecond;
    public BluetoothGattCharacteristic mBluetoothGattCharacteristicThree;
    public BluetoothGattCharacteristic mBluetoothGattCharacteristicFour;
    public BluetoothGattCharacteristic mBluetoothGattCharacteristicFifth;

    public BluetoothGattCharacteristic mBluetoothGattCharacteristicSix;

    public SpDeviceTools spDeviceTools;
    public SpRemindTools spRemindTools;

    public static BluetoothGatt mBluetoothGatt;
    public BluetoothDevice mBluetoothDevice;
    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;

    public ArrayList<ConnectorListener> connectorListenersList;
    public ArrayList<SimplePerformerListener> simplePerformerListenerList;

    public ExecutorService executor = Executors.newCachedThreadPool();
    public ExecutorService ECGExecutor = Executors.newSingleThreadExecutor();
    public ExecutorService PPGExecutor = Executors.newSingleThreadExecutor();

    public byte[] destData = new byte[400];

    public int start = 0;
    public int length = 0;
    public int count = 0;
    public int receiverAllData = 0;

    public String deviceMac;

    public Timer timer;
    public TimerTask timerTask;

    public PPGDateTime ppgDateTime;
    public ECGDateTime ecgDateTime;

    public boolean connectState = false;
    public boolean prepareUpgrade = false;
    public boolean isReceiverComplete = false;
    public boolean isNeedReconnect = false; //重新扫描

    private static final int F38_WATCH_STYLE = 1;
    private static final int A919_WATCH_STYLE = 2;
    private static final int S100_WATCH_STYLE = 3;

    private static final int Q86_WATCH_STYLE = 4;

    private int watchStyle; // 0 F38 1 A919 2 S100

    //是否接收存储数据
    protected boolean isReceiveSaveData = false;

    public Handler mTimeHandler = new Handler();

    @SuppressLint("MissingPermission")
    public BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (connectorListenersList == null || connectorListenersList.isEmpty()) return;
            Iterator<ConnectorListener> connectIterator;
            if (newState == 133) {
                connectIterator = connectorListenersList.iterator();
                log("出现133问题，需要扫描重连", "e");
                isNeedReconnect = true;
                while (connectIterator.hasNext()) {
                    ConnectorListener listener = connectIterator.next();
//                    listener.onConnectFailed();
                    if (isNeedReconnect) {
                        listener.onReConnect();
                    }
                }
                connectState = false;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isNeedReconnect = false;
                log("连接成功，开始获取服务UUID");
                if (detectionGatt()) {
                    return;
                }
                if (gatt != null && gatt.getDevice() != null && !TextUtils.isEmpty(gatt.getDevice().getAddress())) {
                    spDeviceTools.putBleMac(gatt.getDevice().getAddress());
                }
                mTimeHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (!detectionGatt()) {
                            mBluetoothGatt.discoverServices();
                        }
                    }
                }, 1000L);
                mTimeHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (!detectionGatt()) {
                            log("获取服务UUID超时，断开重连", "e");
                            Iterator<ConnectorListener> connectorListenerIterator = connectorListenersList.iterator();
                            while (connectorListenerIterator.hasNext()) {
                                ((ConnectorListener) connectorListenerIterator.next()).onDisconnect();
                            }
                        }
                    }
                }, 5000L);
                Iterator<ConnectorListener> iterator = connectorListenersList.iterator();
                while (iterator.hasNext()) {
                    ConnectorListener connectorListener = (ConnectorListener) iterator.next();
                    connectState = true;
                    connectorListener.onConnectSuccess();
//                    if (!BuildConfig.DEBUG) {
//                    bindDeviceInit("", gatt.getDevice().getAddress());
//                    }
                }
                if (getWatchStyle() != F38_WATCH_STYLE) {
                    mBluetoothGatt.requestMtu(512);
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                log(" 蓝牙断开 " + " " + isNeedReconnect, "e");
                connectIterator = connectorListenersList.iterator();
                while (connectIterator.hasNext()) {
                    ConnectorListener listener = connectIterator.next();
                    listener.onDisconnect();
                    if (isNeedReconnect) {
                        listener.onReConnect();
                    }
                }
                connectState = false;
                isNeedReconnect = false;
            }
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (BluetoothGatt.GATT_SUCCESS == status) {
                if (mBluetoothGatt != null) {
                    Log.e("MTU change success = ", mtu + "");
                    // 搜索GATT服务。Mtu设置成功之后再去搜索服务
                    mBluetoothGatt.discoverServices();
                }
            } else {
                Log.e("MTU change fail!", "");
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            log(" 连接状态 " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("设置监听成功,可以发送数据了...");
                log("服务中连接成功，给与的返回名称是->" + gatt.getDevice().getName());
                log("服务中连接成功，给与的返回地址是->" + gatt.getDevice().getAddress());
                Iterator<ConnectorListener> iterator = connectorListenersList.iterator();
                while (iterator.hasNext()) {
                    ConnectorListener connectorListener = (ConnectorListener) iterator.next();
                    connectState = true;
                    connectorListener.onConnectAndWrite();
//                    if (!BuildConfig.DEBUG) {
                    bindDeviceInit("", gatt.getDevice().getAddress());
//                    }
                }
            } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                log("没权限...");
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                log("写入失败...");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (!detectionGatt()) {
                mTimeHandler.removeMessages(0);
                List<BluetoothGattService> bluetoothGattServices = mBluetoothGatt.getServices();
                log("扫描到服务的个数:" + bluetoothGattServices.size());
                boolean bool1 = false;
                boolean bool2 = false;
                boolean bool3 = false;
                boolean bool4 = false;
                boolean bool5 = false;
                boolean bool6 = false;
                boolean bool7 = false;

//                for (BluetoothGattService service : bluetoothGattServices) {
//                    log(" 服务 uuid == " + service.getUuid());
//                }

                Iterator<BluetoothGattService> iterator = bluetoothGattServices.iterator();

                while (iterator.hasNext()) {
                    BluetoothGattService gattService = (BluetoothGattService) iterator.next();
                    StringBuilder builder = new StringBuilder();
                    boolean bool;
                    /*if (gattService.getUuid().equals(FIRST_GATT_SERVICE_UUID)) {
                        bool1 = true;
                        mBluetoothGattServiceFirst = gattService;
                        builder.append("bluetoothGattDescriptor=");
                        if (mBluetoothGattServiceFirst == null) {
                            bool = true;
                        } else {
                            bool = false;
                        }
                        log(builder.append(bool).append(",service UUID b=").append(FIRST_GATT_SERVICE_UUID).toString());
                    } else */if (gattService.getUuid().equals(SECOND_GATT_SERVICE_UUID)) {
                        bool2 = true;
                        mBluetoothGattServiceSecond = gattService;
                        builder.append("bluetoothGattDescriptor=");
                        if (mBluetoothGattServiceSecond == null) {
                            bool = true;
                        } else {
                            bool = false;
                        }
                        log(builder.append(bool).append(",service UUID d=").append(SECOND_GATT_SERVICE_UUID).toString());
                    } /*else if (gattService.getUuid().equals(THREE_GATT_SERVICE_UUID)) {
                        bool3 = true;
                        mBluetoothGattServiceThree = gattService;
                        builder.append("bluetoothGattDescriptor=");
                        if (mBluetoothGattServiceThree == null) {
                            bool = true;
                        } else {
                            bool = false;
                        }
                        log(builder.append(bool).append(",service UUID g=").append(THREE_GATT_SERVICE_UUID).toString());
                    } else if (gattService.getUuid().equals(GENERIC_ATTRIBUTE_SERVICE)) {
                        bool4 = true;
                        mBluetoothGattServiceFour = gattService;
                        builder.append("bluetoothGattDescriptor=");
                        if (mBluetoothGattServiceFour == null) {
                            bool = true;
                        } else {
                            bool = false;
                        }
                        log(builder.append(bool).append(",service UUID g=").append(GENERIC_ATTRIBUTE_SERVICE).toString());
                    }*//* else if (getWatchStyle() == S100_WATCH_STYLE) {
                        if (gattService.getUuid().equals(RTL_UUID_SERVICE_DEVICE)) {
                            bool5 = true;
                            mBluetoothGattServiceFive = gattService;
                            builder.append("bluetoothGattDescriptor=");
                            if (mBluetoothGattServiceFive == null) {
                                bool = true;
                            } else {
                                bool = false;
                            }
                            log(builder.append(bool).append(",UUID g=").append(RTL_UUID_SERVICE_DEVICE).toString());
                        } else if (gattService.getUuid().equals(RTL_UUID_SERVICE_DEVICE_1)) {
                            bool6 = true;
                            mBluetoothGattServiceSix = gattService;
                            builder.append("bluetoothGattDescriptor=");
                            if (mBluetoothGattServiceSix == null) {
                                bool = true;
                            } else {
                                bool = false;
                            }
                            log(builder.append(bool).append(",UUID g=").append(RTL_UUID_SERVICE_DEVICE_1).toString());
                        } else if (gattService.getUuid().equals(RTL_UUID_SERVICE_DEVICE_2)) {
                            bool7 = true;
                            mBluetoothGattServiceSeven = gattService;
                            builder.append("bluetoothGattDescriptor=");
                            if (mBluetoothGattServiceSeven == null) {
                                bool = true;
                            } else {
                                bool = false;
                            }
                            log(builder.append(bool).append(",UUID g=").append(RTL_UUID_SERVICE_DEVICE_2).toString());
                        }
                    }*/
                }

//                log(bool1 + " " + bool2 + " " + bool3 + " " + bool4 + " " + bool5 + " " + bool6 + " " + bool7);
//
//                if (bool1) {
//                    log(" characteristic uuid " + FIRST_GATT_CHARACTERISTIC);
//                    mBluetoothGattCharacteristicFirst = mBluetoothGattServiceFirst.getCharacteristic(FIRST_GATT_CHARACTERISTIC);
//                    setCharacteristicNotification("ECG", mBluetoothGatt, mBluetoothGattCharacteristicFirst);
//                }

                if (bool2) {
                    mTimeHandler.postDelayed(new Runnable() {
                        public void run() {
                            log(" characteristic uuid " + FOUR_GATT_CHARACTERISTIC);
                            mBluetoothGattCharacteristicFour = mBluetoothGattServiceSecond.getCharacteristic(FOUR_GATT_CHARACTERISTIC);
                            setCharacteristicNotification("SYS", mBluetoothGatt, mBluetoothGattCharacteristicFour);
                        }
                    }, 800L);
                }

//                if (bool3) {
//                    mTimeHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            log(" characteristic uuid " + SECOND_GATT_CHARACTERISTIC);
//                            mBluetoothGattCharacteristicSecond = mBluetoothGattServiceThree.getCharacteristic(SECOND_GATT_CHARACTERISTIC);
//                            setCharacteristicNotification("PPG", mBluetoothGatt, mBluetoothGattCharacteristicSecond);
//                        }
//                    }, 1200L);
//                }
//
//                if (bool4) {
//                    mTimeHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            log(" characteristic uuid " + SERVICE_CHANGED_CHARACTERISTIC);
//                            mBluetoothGattCharacteristicFifth = mBluetoothGattServiceFour.getCharacteristic(SERVICE_CHANGED_CHARACTERISTIC);
//                            setCharacteristicNotification("SYS", mBluetoothGatt, mBluetoothGattCharacteristicFifth);
//                        }
//                    }, 400L);
//                }
//                if (bool5) {
//                    mTimeHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            BluetoothGattCharacteristic characteristic = mBluetoothGattServiceFive.getCharacteristic(RTL_UUID_NOTIFY_CHARACTERISTIC);
//                            setCharacteristicNotification("SYS", mBluetoothGatt, characteristic);
//                        }
//                    }, 700L);
//                }
//                if (bool6) {
//                    mTimeHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            BluetoothGattCharacteristic characteristic = mBluetoothGattServiceSix.getCharacteristic(RTL_UUID_NOTIFY_CHARACTERISTIC_2);
//                            boolean ecg = setCharacteristicNotification("PPG", mBluetoothGatt, characteristic);
//                            log("PPG notify " + ecg);
//                        }
//                    }, 900L);
//                }
//                if (bool7) {
//                    mTimeHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            BluetoothGattCharacteristic characteristic = mBluetoothGattServiceSeven.getCharacteristic(RTL_UUID_NOTIFY_CHARACTERISTIC_3);
//                            boolean ppg = setCharacteristicNotification("ECG", mBluetoothGatt, characteristic);
//                            log("ECG notifi " + ppg);
//                        }
//                    }, 1100L);
//                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
//            log(ByteToStringUtil.ByteToString(characteristic.getValue()) + " ecg uuid " + characteristic.getUuid().equals(FIRST_GATT_CHARACTERISTIC));
            synchronized (this) {
                byte[] characteristicValue = characteristic.getValue();
                if (characteristicValue != null && characteristicValue.length != 0) {
                    String character = ByteToStringUtil.toHexString(characteristicValue);
                    UUID uuid = characteristic.getUuid();
//                if (BuildConfig.DEBUG){
//                    log("-------接收的原始数据-------");
//                    log("原始数据:" + ByteToStringUtil.ByteToString(characteristicValue));
//                }
//                    log(" ----------------------------------- ");
//                    log(" onCharacteristicChanged Uuid == " + characteristic.getUuid() + " Data == " + character + " IsECG " + characteristic.getUuid().equals(FIRST_GATT_CHARACTERISTIC))

                    if (uuid.equals(SECOND_GATT_CHARACTERISTIC)) {
                        PPGExecutor.execute(() -> {
//                            log("onCharacteristicChanged PpgData " + character);
                            PPGInfo ppgInfo = SIATCommand.parseByteToPPGInfo(ppgDateTime, characteristicValue);
                            ppgInfo.setData(characteristicValue);
                            ResponsePerformerListener.onResponsePPGInfo(simplePerformerListenerList, ppgInfo);
                        });
                    } else if (uuid.equals(FIRST_GATT_CHARACTERISTIC)) {
                        ECGExecutor.execute(() -> {
//                                log(ByteToStringUtil.ByteToString(characteristicValue) + " EcgData ");
                            if (isReceiveSaveData) {
                                watchSaveInfoDetail(characteristicValue);
                            } else {
                                ECGInfo ecgInfo = SIATCommand.parseByteToEcgInfo(ecgDateTime, characteristicValue);
                                ecgInfo.setData(characteristicValue);
                                ResponsePerformerListener.onResponseECGInfo(simplePerformerListenerList, ecgInfo);
                            }
                        });
                    } else {
                        if (character.startsWith(SIATCommand.SIGN_HEAD_TAG) && receiverAllData > 0) {
                            receiverAllData = 0;
                            isReceiverComplete = true;
                            if (isReceiverComplete) {
                                byte[] result = destData;
                                executor.execute(() -> {
                                    byte[] bytes = result;
                                    parseRespone(bytes);
                                });
                            }
                            compositeData(characteristicValue);
                        } else {
                            compositeData(characteristicValue);
                            if (isReceiverComplete) {
                                byte[] result = destData;
                                executor.execute(() -> {
                                    byte[] bytes = result;
                                    parseRespone(bytes);
                                });
                            }
                        }
                    }
                }
            }
        }
    };

    public final DfuProgressListener dfuProgressListener = new DfuProgressListenerAdapter() {
        public void onDeviceConnecting(@NonNull String var1) {
            if (!unbinderUpgradeList()) {
                uploadDeviceListener.onUpgradeDeviceStarting(0);
            }
        }

        public void onDfuProcessStarting(@NonNull String var1) {
            if (!unbinderUpgradeList()) {
                uploadDeviceListener.onUpgradeDeviceStarting(1);
            }
        }

        public void onEnablingDfuMode(@NonNull String var1) {
            if (!unbinderUpgradeList()) {
                uploadDeviceListener.onUpgradeDeviceStarting(2);
            }
        }

        public void onFirmwareValidating(@NonNull String var1) {
            if (!unbinderUpgradeList()) {
                uploadDeviceListener.onUpgradeDeviceStarting(3);
            }
        }

        public void onDeviceDisconnecting(@NonNull String var1) {
//            if (!unbinderUpgradeList()) {
//                uploadDeviceListener.onUpgradeDeviceError(0, 1, "Disconnecting…");
//            }
        }

        public void onDfuCompleted(@NonNull String var1) {
            if (!unbinderUpgradeList()) {
                uploadDeviceListener.onUpgradeDeviceCompleted();
            }
        }

        public void onDfuAborted(@NonNull String var1) {
            if (!unbinderUpgradeList()) {
                uploadDeviceListener.onUpgradeDeviceError(0, 0, "Uploading of the application has been canceled");
            }
        }

        public void onProgressChanged(@NonNull String var1, int var2, float var3, float var4, int var5, int var6) {
            uploadDeviceListener.onUpgradeDeviceProgress(var2);
        }

        public void onError(@NonNull String var1, int var2, int var3, String var4) {
            if (!unbinderUpgradeList()) {
                uploadDeviceListener.onUpgradeDeviceError(var2, var3, var4);
            }
        }
    };
    public UpgradeDeviceListener uploadDeviceListener;

    public WatchService() {

    }

    private void bindDeviceInit(String s, String address) {
        spDeviceTools.putBleMac(address);
        if (spDeviceTools.getTaiwan()) {
            taiWan = 1;
        } else {
            taiWan = 0;
        }

        if (spDeviceTools.getZhuanwan()) {
            zhuanWan = 1;
        } else {
            zhuanWan = 0;
        }

        if (spDeviceTools.getPointMeasurementHeart()) {
            measurementHeart = 1;
        } else {
            measurementHeart = 0;
        }

        if (spDeviceTools.getNotDisturb()) {
            notDisturb = 1;
        } else {
            notDisturb = 0;
        }

        if (spDeviceTools.getColockType()) {
            colockType = 1;
        } else {
            colockType = 0;
        }

        if (spDeviceTools.getDeviceUnit()) {
            deviceUnit = 1;
        } else {
            deviceUnit = 0;
        }

//        mTimeHandler.postDelayed(new Runnable() {
//            public void run() {
//                sendThread(SIATCommand.getSyncTimeCom(SIATCommand.SIGN_HEAD));
//                try {
//                    Thread.sleep(SIATCommand.SLEEP_TIME);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                sendThread(SIATCommand.getSyncTimeCom(SIATCommand.SIGN_AA_HEAD));
//            }
//        }, 300L);
        mTimeHandler.postDelayed(new Runnable() {
            public void run() {
//                sendThread(SIATCommand.getRemindCom(SIATCommand.SIGN_HEAD, colockType, deviceUnit, taiWan, zhuanWan, measurementHeart, notDisturb));
//                try {
//                    Thread.sleep(SIATCommand.SLEEP_TIME);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                sendThread(SIATCommand.getRemindCom(colockType, deviceUnit, taiWan, zhuanWan, measurementHeart, notDisturb));
            }
        }, 2000L);
        mTimeHandler.postDelayed(new Runnable() {
            public void run() {
                setUserCalibrationSbpInit();
            }
        }, 2000L);
        mTimeHandler.postDelayed(new Runnable() {
            public void run() {
                setUserInfoInit();
            }
        }, 2300L);
//        mTimeHandler.postDelayed(new Runnable() {
//            public void run() {
//                sendThread(SIATCommand.getDeviceInfoCom());
//            }
//        }, 2000L);
    }

    private void setUserCalibrationSbpInit() {
//        sendThread(SIATCommand.getUserCalibrationCom(SIATCommand.SIGN_HEAD, spDeviceTools.getUserCalibrationSbp(), spDeviceTools.getUserCalibrationHr()));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getUserCalibrationCom(spDeviceTools.getUserCalibrationSbp(), spDeviceTools.getUserCalibrationHr()));
    }

    private void setUserInfoInit() {
//        sendThread(SIATCommand.getUserInfoCom(SIATCommand.SIGN_HEAD, (byte) (spDeviceTools.getUserSex() ? 1 : 0), spDeviceTools.getUserAge()
//                , spDeviceTools.getUserHeight(), spDeviceTools.getUserWeight()));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getUserInfoCom((byte) (spDeviceTools.getUserSex() ? 1 : 0), spDeviceTools.getUserAge()
                , spDeviceTools.getUserHeight(), spDeviceTools.getUserWeight()));
    }

    public static int toUnsignedInt(byte b) {
        return b & 0xFF;
    }

    private void compositeData(byte[] byteData) {
        try {
            int receiverData = receiverAllData;
            if (receiverData != 0) {
                if (receiverData != 1) {
                    return;
                }
                try {
                    if (start + byteData.length > destData.length) {
                        System.arraycopy(byteData, 0, destData, start, destData.length - start);
                    } else {
                        System.arraycopy(byteData, 0, destData, start, byteData.length);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                start += byteData.length;
                length -= byteData.length;

                if (length <= 0) {
                    try {
                        receiverAllData = 0;
                        isReceiverComplete = true;
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        isReceiverComplete = false;
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (byteData[0] == SIATCommand.SIGN_HEAD) {
                    start = 0;
                    if ((byteData[10] == 5 || byteData[10] == 85 || byteData[10] == 37) && byteData[2] > 0) {
                        length = ((byteData[2] & 0xFF) << 8) + (byteData[3] & 0xFF) + 8;
                    } else {
                        length = toUnsignedInt(byteData[3]) + 8;
                    }
                    destData = new byte[length];
                    System.arraycopy(byteData, 0, destData, start, byteData.length);
                    start = byteData.length;
                    if ((length -= byteData.length) > 0) {
                        receiverAllData = 1;
                        isReceiverComplete = false;
                        return;
                    }
                } else {
                    return;
                }
            }

            resetInit();
        } catch (Exception e) {
            e.printStackTrace();
            resetInit();
            log("解析数据失败");
        }
    }

    private void resetInit() {
        start = 0;
        receiverAllData = 0;
        isReceiverComplete = true;
    }

    @SuppressLint("MissingPermission")
    private boolean setCharacteristicNotification(String s, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        boolean bool = false;
        if (gatt != null && characteristic != null) {
            gatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(GATT_DESCRIPTOR_UUID);
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bool = gatt.writeDescriptor(descriptor);
            }
        }
        return bool;
    }

    private boolean detectionGatt() {
        if (mBluetoothGatt == null) {
            log("出现未知错误，服务关闭，GATT is null", "e");
            stopSelf();
            return true;
        } else {
            return false;
        }
    }

    public String getContactName(String phone) {
        try {
            Cursor cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    contact_info, null, null, null);
            if (cursor != null) {
                while (true) {
                    if (!cursor.moveToNext()) {
                        cursor.close();
                        break;
                    }
                    String str = cursor.getString(1);
                    if (!TextUtils.isEmpty(str) && phone.equals(str.replace(" ", "").replace("+86", ""))) {
                        cursor.close();
                        return cursor.getString(0).trim().replace(" ", "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phone;
    }

    private void registerPhoneStateListener() {
        WatchPhoneStateListener listener = new WatchPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void parseRespone(byte[] respone) {
        if (respone[0] == SIATCommand.SIGN_HEAD) {
            if (respone[8] == 3 || respone[8] == 1) {
//                if (BuildConfig.DEBUG) {
                ResponsePerformerListener.onResponseByteArray(simplePerformerListenerList, respone);
//                }
                byte signByte = respone[10];
                if (signByte == 2) {
                    //运动数据
                    motionInfoDeail(respone);
                } else if (signByte == 3) {
                    //睡眠数据
                    sleepInfoDeail(respone);
                } else if (signByte == 4) {
                    //信息完成标志
                    ResponsePerformerListener.onResponseComplete(simplePerformerListenerList);
//                    sendThread(SIATCommand.h(SIATCommand.SIGN_HEAD));
//                    try {
//                        Thread.sleep(SIATCommand.SLEEP_TIME);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    sendThread(SIATCommand.h());
                } else if (signByte == 5) {
                    //心率数据
                    poHeartInfoDeail(respone);
                } else if (signByte == 6) {
                    if (getCamera()) {
                        ResponsePerformerListener.onResponsePhoto(simplePerformerListenerList);
                    }
                } else if (signByte == 7) {
                    ResponsePerformerListener.onResponseFindPhone(simplePerformerListenerList);
                } else if (signByte == 8) {
                    //设备信息
                    deviceInfo(respone);
                } else if (signByte == 9) {
                    //MAC信息
                    StringBuilder builder = new StringBuilder();
                    for (int i = 12; i < respone.length; ++i) {
                        String s;
                        if (ParseWatchesData.byteToString(respone[i]).length() == 1) {
                            s = "0" + ParseWatchesData.byteToString(respone[i]);
                        } else {
                            s = ParseWatchesData.byteToString(respone[i]);
                        }
                        builder.append(s);
                        if (i != respone.length - 1) {
                            builder.append(":");
                        }
                    }
                    deviceMac = builder.toString();
                    ResponsePerformerListener.onResponseDeviceMac(simplePerformerListenerList, deviceMac);
                } else if (signByte == 11) {
                    heartInfoDeail(respone);
                } else if (signByte == 12) {
                    parseHRV(respone);
                } else if (signByte == 13) {
//                    if (isValidValue(respone)) {
                    woHeartInfoDeail(respone);
//                    }
                } else if (signByte == 16) {
                    //压力数据
                    pressureAndEmotionInfoDetail(respone);
                } else if (signByte == 17) {
                    //疲劳数据
                    tiredInfoDetail(respone);
                } else if (signByte == 18) {
                    bloodInfoDetail(respone);
                } else if (signByte == 20) {
                    //血氧数据
//                    if (isValidValue(respone)) {
                    spoInfoDeail(respone);
//                    }
                } else if (signByte == 21) {
                    //体温和环境温度数据
//                    if (isValidValue(respone)) {
                    tempInfoDetail(respone);
//                    }
                } else if (signByte == 22) {
                    //呼吸数据
                    breatheInfoDetail(respone);
                } else if (signByte == 23) {
                    //症状数据
                    symptomInfoDetail(respone);
                } else if (signByte == 24) {
                    //打鼾数据
                    snoreInfoDetail(respone);
                } else if (signByte == 25) {
                    //运动强度
                    motionStrengthInfoDetail(respone);
                } else if (signByte == 32) {
                    //异常心率
                    abnormalHeartInfoDetail(respone);
                } else if (signByte == 33) {
                    //开关
                    byteToSwitchInfo(respone);
                } else if (signByte == 34) {
                    //PPG数据
                    ppgDateTimeDeail(respone);
                } else if (signByte == 37) {
                    //睡眠血氧
                    sleepOxInfoDetail(respone);
                } else if (signByte == 38) {
                    //HRV
                    byteToHrvInfo(respone);
                }  else if (signByte == 39) {
                    //A933症状
                    byteToSymptomInfo2(respone);
                }else if (signByte == 49) {
                    TelephonyUtil.endCall(this);
                } else if (signByte == 51) {
                    //ECG数据
                    ecgDateTimeDeail(respone);
                } else if (signByte == 68) {
                    //午睡数据
                    siestaInfoDeail(respone);
                } else if (signByte == 83) {
                    if (respone.length >= 13 && respone[12] == 1) {
                        isReceiveSaveData = false;
                        ResponsePerformerListener.onResponseWatchSaveInfo(simplePerformerListenerList, null, "接收结束");
                    }
                } else if (signByte == 84) {
                    //运动步数
                    byteToRunStep(respone);
                } else if (signByte == 85) {
                    //睡眠日志数据
                    sleepLogInfoDetail(respone);
                } else if (signByte == 86) {
                    //蓝牙开始传图
                    ResponsePerformerListener.onResponseStartSendImgInfo(simplePerformerListenerList, "开始传输图片");
                } else if (signByte == 87) {
                    //手表接收到图片
                    if (respone.length > 8) {
                        ResponsePerformerListener.onResponseReceiveImgInfo(simplePerformerListenerList, respone[13] == 2, "接收到图片");
                    }
                } else if (signByte == 90) {
                    if (respone.length > 13) {
                        byte sign = respone[13];
                        ResponsePerformerListener.onResponseTestOx(simplePerformerListenerList, sign);
                    }
                } else if (signByte == 91) {
                    if (respone.length > 13) {
                        int temp = ByteToStringUtil.byteTo7Int(respone[13]);
                        int byte0 = ByteToStringUtil.byteToInt(respone[15]);
                        int byte1 = ByteToStringUtil.byteToInt(respone[16]);
                        float heat = byte0 + 10 * byte1 / 256 * 0.1f;
                        ResponsePerformerListener.onResponseMeasureTemp(simplePerformerListenerList, heat, temp);
                    }
                } else if (signByte == 92) {
                    if (respone.length > 14) {
                        ResponsePerformerListener.onResponseMeasureTireAndPressure(simplePerformerListenerList, respone[13], respone[14]);
                    }
                } else if (signByte == -124) {
                    //加密数据结果
                    encrypInfoDeail(respone);
                    if (respone.length > 12) {
//                        log("加密结果:" + (respone[13] == 1 ? "成功" : "失败"));
                    }
                } else if (signByte == -125 || signByte == -126) {
//                    //升级
                    if (respone[13] == 1) {
                        if (prepareUpgrade) {
                            return;
                        }
                        prepareUpgrade = true;
                        connectState = false;
                        if (uploadDeviceListener != null) {
                            uploadDeviceListener.onReConnectUpdateDevice("重新连接设备...", true);
                        }
                        isNeedReconnect = true;
                        disconnectDeivceReconnect();
                    } else if (respone[13] == 3 && timer != null) {
                        endTimer();
                        upgradeDevice(Environment.getExternalStorageDirectory().getPath() + "/Download/test.zip");
                    }
                }
            }
        }
    }

    private void disconnectDeivceReconnect() {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        if (!connectState) {
//            connect(getBleMac());
            disconnectDeivceReconnect();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                log("startTimer count=" + count);
                if (count < 30) {
                    completePreparation();
                } else {
                    preparationFailCancelUpgrade();
                    endTimer();
                    if (uploadDeviceListener != null) {
                        uploadDeviceListener.onUpgradeDeviceError(0, 5, "准备更新失败");
                    }
                }
                count++;
            }
        };
        timer.schedule(timerTask, 1000L, 1000L);
    }

    private void endTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        log("endTimer count=" + count);
        count = 0;
    }

    private boolean getCamera() {
        return spDeviceTools.getControlPhoto();
    }

    private String getDeviceVersionName(int var1, int var2) {
        String var3 = String.valueOf(var1);
        String var4 = String.valueOf(var2 / 10 + 1);
        return var3 + "." + var4 + "." + var2 % 10;
    }

    private String getDeviceVersionName(byte[] bytes) {
        if (bytes.length < 18) return "";
        String fixVersion = String.valueOf(bytes[15]);
        String secondVersion = String.valueOf(bytes[16]);
        String mainVersion = String.valueOf(bytes[17]);
        String funVersion = String.valueOf(bytes[18]);
        if (bytes[18] > 0) {
            return funVersion + "." + mainVersion + "." + secondVersion + "." + fixVersion;
        } else {
            return mainVersion + "." + secondVersion + "." + fixVersion;
        }
    }

    private boolean hasSleepData(SleepInfo sleepInfo) {
        if (sleepInfo == null) return false;
        boolean bool = true;
        List<SleepData> list = sleepInfo.getSleepData();
        if (list != null && list.size() >= 1 && list.size() <= 10) {
            for (byte i = 0; i < list.size() - 1; ++i) {
                if (((SleepData) list.get(i)).getSleep_type().equals("3")) {
                    String startTime = ((SleepData) list.get(i)).getStartTime();
                    if (Integer.valueOf(timeDis(startTime, ((SleepData) list.get(i + 1)).getStartTime())) >= 240) {
                        bool = false;
                    }
                }
            }
        }

        return bool;
    }

    private int timeMin(String time) {
        if (TextUtils.isEmpty(time)) return 0;
        String[] timeArray = time.split(":");
        if (timeArray == null || timeArray.length < 2) return 0;
        int hour = Integer.valueOf(timeArray[0]);
        int temp = hour >= 20 && hour <= 24 ? hour : hour + 24;
        int min = Integer.valueOf(timeArray[1]);
        return temp * 60 + min;
    }

    private String timeDis(String startTime, String time) {
        int temp = timeMin(startTime);
        return String.valueOf(timeMin(time) - temp);
    }

    private void motionInfoDeail(byte[] bytes) {
        MotionInfo motionInfo = SIATCommand.byteToMotionInfo(bytes, this);
        if (motionInfo == null) return;
        ResponsePerformerListener.onResponseMotionInfo(simplePerformerListenerList, motionInfo);
    }

    private void sleepInfoDeail(byte[] bytes) {
        SleepInfo sleepInfo = SIATCommand.byteToSleepInfo(bytes);
        if (sleepInfo == null) return;
        if (hasSleepData(sleepInfo)) {
            ResponsePerformerListener.onResponseSleepInfo(simplePerformerListenerList, sleepInfo);
        }
    }

    private void poHeartInfoDeail(byte[] bytes) {
        PoHeartInfo poHeartInfo = SIATCommand.byteToHeartInfo(bytes);
        if (poHeartInfo == null) return;
        ResponsePerformerListener.onResponsePoHeartInfo(simplePerformerListenerList, poHeartInfo);
    }

    private byte getCrc(byte b1, byte i) {
        byte b = -105;
        b1 ^= i;
        for (i = 0; i < 8; ++i) {
            if ((b1 & 128) != 0) {
                b1 = (byte) ((byte) (b1 << 1) ^ b);
            } else {
                b1 = (byte) (b1 << 1);
            }
        }
        return b1;
    }

    public void deviceInfo(byte[] respone) {
        String deviceVersionName = getDeviceVersionName(respone);
        ResponsePerformerListener.onResponseDeviceInfo(simplePerformerListenerList,
                new DeviceInfo(respone[13], respone[14] & 0xFF, respone[15], respone[11], deviceVersionName));
    }

    private void spoInfoDeail(byte[] bytes) {
        SpoInfo spoInfo = SIATCommand.byteToSpoInfo(bytes);
        if (spoInfo == null) return;
        ResponsePerformerListener.onResponseSpoInfo(simplePerformerListenerList, spoInfo);
    }

    private void ppgDateTimeDeail(byte[] bytes) {
        PPGDateTime ppgDateTime = SIATCommand.byteToPpgDate(bytes);
//        log("onCharacteristicChanged parse ppgDateTime=" + ppgDateTime);
        ResponsePerformerListener.onResponsePPGDateTime(simplePerformerListenerList, ppgDateTime);
    }

    private void ecgDateTimeDeail(byte[] bytes) {
        ECGDateTime ecgDateTime = SIATCommand.byteToEcgDate(bytes);
//        log("onCharacteristicChanged parse ecgDateTime=" + ecgDateTime);
        ResponsePerformerListener.onResponseECGGDateTime(simplePerformerListenerList, ecgDateTime);
    }

    private void woHeartInfoDeail(byte[] bytes) {
        WoHeartInfo woHeartInfo = SIATCommand.byteToWoHeartInfo(bytes);
        if (woHeartInfo == null) return;
        ResponsePerformerListener.onResponseWoHeartInfo(simplePerformerListenerList, woHeartInfo);
    }

    private void heartInfoDeail(byte[] bytes) {
        HeartInfo heartInfo = SIATCommand.byteToHeartInfo(bytes, spDeviceTools.getUserCalibrationHr(),
                spDeviceTools.getUserCalibrationSbp(), spDeviceTools.getUserCalibrationDbp());
        if (heartInfo == null) return;
        ResponsePerformerListener.onResponseHeartInfo(simplePerformerListenerList, heartInfo);
    }

    private void parseHRV(byte[] bytes) {
        int hrv = SIATCommand.byteToHRV(bytes);
        if (hrv == 0) return;
        ResponsePerformerListener.onResponseHRV(simplePerformerListenerList, hrv);
    }

    private void sleepOxInfoDetail(byte[] bytes) {
        SleepOxInfo info = SIATCommand.byteToSleepOxInfo(bytes);
        ResponsePerformerListener.onResponseSleepOxInfo(simplePerformerListenerList, info);
    }

    private void hearInfoDetail(int hr) {
        HeartInfo heartInfo = SIATCommand.byteToHeartInfo(hr, spDeviceTools.getUserCalibrationHr(),
                spDeviceTools.getUserCalibrationSbp(), spDeviceTools.getUserCalibrationDbp());
        if (heartInfo == null) return;
        ResponsePerformerListener.onResponseHeartInfo(simplePerformerListenerList, heartInfo);
    }

    /**
     * 解析午睡数据
     *
     * @param bytes
     */
    private void siestaInfoDeail(byte[] bytes) {
        SiestaInfo siestaInfo = SIATCommand.byteToSiestaInfo(bytes);
        if (siestaInfo == null) return;
        ResponsePerformerListener.onResponseSiestaInfo(simplePerformerListenerList, siestaInfo);
    }

    /**
     * 解析午睡数据
     *
     * @param bytes
     */
    private void encrypInfoDeail(byte[] bytes) {
        ResponsePerformerListener.onResponseEncryp(simplePerformerListenerList, bytes);
    }

    /**
     * 疲劳数据
     *
     * @param bytes
     */
    private void pressureAndEmotionInfoDetail(byte[] bytes) {
        PressureInfo pressureInfo = SIATCommand.byteToPressureInfo(bytes);
        EmotionInfo emotionInfo = SIATCommand.byteToEmotionInfo(bytes);
        if (pressureInfo == null) return;
        ResponsePerformerListener.onResponsePressureAndEmotionInfo(simplePerformerListenerList, pressureInfo, emotionInfo);

    }

    /**
     * 压力数据
     *
     * @param bytes
     */
    private void tiredInfoDetail(byte[] bytes) {
        TireInfo tireInfo = SIATCommand.byteToTireInfo(bytes);
        if (tireInfo == null) return;
        ResponsePerformerListener.onResponseTireInfo(simplePerformerListenerList, tireInfo);
    }

    /**
     * 体温和环境温度数据
     *
     * @param bytes
     */
    public void tempInfoDetail(byte[] bytes) {
        HeatInfo heatInfo = SIATCommand.byteToHeatInfo(bytes);
        EnviTempInfo enviInfo = SIATCommand.byteToEnviHeatInfo(bytes);
        UvInfo uvInfo = SIATCommand.byteToUVInfo(bytes);
        ResponsePerformerListener.onResponseTempInfo(simplePerformerListenerList, heatInfo, enviInfo, uvInfo);
    }

    /**
     * 解析血压
     *
     * @param bytes
     */
    public void bloodInfoDetail(byte[] bytes) {
        HeartListInfo heartListInfo = SIATCommand.byteToHeartListInfo(bytes);
        ResponsePerformerListener.onResponseHeartListInfo(simplePerformerListenerList, heartListInfo);
    }

    public void sleepLogInfoDetail(byte[] bytes) {
        SleepLogInfo sleepLogInfo = SIATCommand.byteToSleepLogInfo(bytes);
        ResponsePerformerListener.onResponseSleepLogInfo(simplePerformerListenerList, sleepLogInfo);
    }

    public void breatheInfoDetail(byte[] bytes) {
        synchronized (this) {
            byte[] tempBytes = bytes;
            BreatheInfo breatheInfo = SIATCommand.byteToBreatheInfo(tempBytes);
            ResponsePerformerListener.onResponseBreatheInfo(simplePerformerListenerList, breatheInfo);
        }
    }

    public void symptomInfoDetail(byte[] bytes) {
        SymptomListInfo symptomListInfo = SIATCommand.byteToSymptomInfo(bytes);
        ResponsePerformerListener.onResponseSymptomListInfo(simplePerformerListenerList, symptomListInfo);
    }

    public void snoreInfoDetail(byte[] bytes) {
        SnoreInfo snoreInfo = SIATCommand.byteToSnoreInfo(bytes);
        ResponsePerformerListener.onResponseSnoreInfo(simplePerformerListenerList, snoreInfo);
    }

    public void motionStrengthInfoDetail(byte[] bytes) {
        StrengthInfo strengthInfo = SIATCommand.byteToStrengthInfo(bytes);
        ResponsePerformerListener.onResponseStrengthInfo(simplePerformerListenerList, strengthInfo);
    }

    public void abnormalHeartInfoDetail(byte[] bytes) {
        AbnormalHeartListInfo listInfo = SIATCommand.byteToAbnormalHeartListInfo(bytes);
        if (listInfo == null || listInfo.getList() == null || listInfo.getList().isEmpty()) return;
        ResponsePerformerListener.onResponseAbnormalHeartListInfo(simplePerformerListenerList, listInfo);
    }

    public synchronized void watchSaveInfoDetail(byte[] bytes) {
        WatchSaveInfo saveInfo = SIATCommand.byteToWatchSaveInfo(bytes);
        if (saveInfo == null) {
            saveInfo = new WatchSaveInfo(" 解析数据失败：" + ByteToStringUtil.ByteToString(bytes) + " ", 0, 0, 0, 0);
        }
        if (saveInfo.getA() != -1) {
            saveInfo.setByteData(ByteToStringUtil.ByteToString(bytes));
            ResponsePerformerListener.onResponseWatchSaveInfo(simplePerformerListenerList, saveInfo, "");
        }
    }

    //运动步数
    public void byteToRunStep(byte[] bytes) {
        int step = SIATCommand.byteToRunStepInfo(bytes);
        ResponsePerformerListener.onResponseStepInfo(simplePerformerListenerList, step);
    }

    public void byteToSwitchInfo(byte[] bytes) {
        SwitchInfo switchInfo = SIATCommand.byteToSwitchInfo(bytes);
        ResponsePerformerListener.onResponseSwitchInfo(simplePerformerListenerList, switchInfo);
    }

    private void byteToHrvInfo(byte[] bytes) {
        HrvInfo info = SIATCommand.byteToHrvInfo(bytes);
        ResponsePerformerListener.onResponseHrvInfo(simplePerformerListenerList, info);
    }

    private void byteToSymptomInfo2(byte[] bytes) {
        SymptomListInfo info = SIATCommand.byteToSymptomInfo2(bytes);
        ResponsePerformerListener.onResponseSymptomListInfo2(simplePerformerListenerList, info);
    }

    private boolean isValidValue(byte[] bytes) {
        byte b1 = bytes[4];
        int i = 13;
        byte b = 0;
        int len = (bytes[3] & 255) - 5 + i;
        while (i < len) {
            b = getCrc(bytes[i], b);
            ++i;
        }
        log("paramArrayOfbyte validCrc=" + b);
        return b1 == b;
    }

    private void completePreparation() {
//        sendThread(SIATCommand.getCompletePreparationCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getCompletePreparationCom());
    }

    private void preparationFailCancelUpgrade() {
        count = 0;
        prepareUpgrade = false;
//        sendThread(SIATCommand.getPreparationFailCancelUpgradeCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getPreparationFailCancelUpgradeCom());
    }

    @SuppressLint("MissingPermission")
    public void upgradeDevice(String str) {
        if (!MimeTypeMap.getFileExtensionFromUrl(str.substring(str.lastIndexOf("/"))).matches("(?i)ZIP")) {
            uploadDeviceListener.onUpgradeDeviceError(0, 3, "不是zip文件");
        }
        if (!isDfuServiceRunning()) {
            String bleMac = getBleMac();
            if (TextUtils.isEmpty(bleMac)) {
                if (!unbinderUpgradeList()) {
                    uploadDeviceListener.onUpgradeDeviceError(0, 2, "未绑定设备");
                }
            } else {
                BluetoothDevice device = getBluetoothAdapter().getRemoteDevice(bleMac);
                DfuServiceInitiator dfuServiceInitiator = (new DfuServiceInitiator(device.getAddress()))
                        .setDeviceName(device.getName())
                        .setKeepBond(false)
                        .setForceDfu(false)
                        .setPacketsReceiptNotificationsEnabled(false)
                        .setPacketsReceiptNotificationsValue(12)
//                        .setPrepareDataObjectDelay(400L)
                        .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
                if (Build.VERSION.SDK_INT >= 26) {
                    DfuServiceInitiator.createDfuNotificationChannel(this);
                }
                dfuServiceInitiator.setZip(str);
//                dfuServiceInitiator.setZip((Uri) null, str);
                dfuServiceInitiator.start(this, DfuService.class);
            }
        } else {
            if (uploadDeviceListener != null) {
                uploadDeviceListener.onUpgradeDeviceError(0, 4, "Dfu未运行");
            }
        }
    }

    public void test2(byte[] characteristicValue) {
        synchronized (this) {
            if (characteristicValue != null && characteristicValue.length != 0) {
                String character = ByteToStringUtil.toHexString(characteristicValue);
//                UUID uuid = characteristic.getUuid();
//                if (BuildConfig.DEBUG){
//                    log("-------接收的原始数据-------");
                log("原始数据:" + ByteToStringUtil.ByteToString(characteristicValue));
//                }
//                    log(" ----------------------------------- ");
//                    log(" onCharacteristicChanged Uuid == " + characteristic.getUuid() + " Data == " + character + " IsECG " + characteristic.getUuid().equals(FIRST_GATT_CHARACTERISTIC))
//                Log.i(" character ", character);
                if (character.startsWith(SIATCommand.SIGN_HEAD_TAG) && receiverAllData > 0) {
                    receiverAllData = 0;
                    isReceiverComplete = true;
                    if (isReceiverComplete) {
                        byte[] result = destData;
                        executor.execute(() -> {
                            byte[] bytes = result;
                            parseRespone(bytes);
                        });
                    }
                    compositeData(characteristicValue);
                } else {
                    compositeData(characteristicValue);
                    if (isReceiverComplete) {
                        byte[] result = destData;
                        executor.execute(() -> {
                            byte[] bytes = result;
                            parseRespone(bytes);
                        });
                    }
                }
            }
        }
    }

    private boolean unbinderUpgradeList() {
        if (uploadDeviceListener == null) {
            Toast.makeText(this, "未添加升级监听", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    private boolean isDfuServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Iterator<ActivityManager.RunningServiceInfo> iterator = activityManager.getRunningServices(Integer.MAX_VALUE).iterator();
        do {
            if (!iterator.hasNext()) {
                return false;
            }
        } while (!DfuService.class.getName().equals(((ActivityManager.RunningServiceInfo) iterator.next()).service.getClassName()));
        return true;
    }

    protected void log(String msg) {
//        if (!BuildConfig.DEBUG) return;
        Log.d("AppRunService", msg);
        ResponsePerformerListener.onResponseTest(simplePerformerListenerList, msg);
    }

    protected void log(String msg, String tag) {
//        if (!BuildConfig.DEBUG) return;
        if (tag.equals("i")) {
            Log.e("AppRunService", msg);
        } else {
            if (tag.equals("e")) {
                Log.e("AppRunService", msg);
            } else {
                Log.w("AppRunService", msg);
            }
        }
        ResponsePerformerListener.onResponseTest(simplePerformerListenerList, msg);
    }

    public void setConnectState(boolean connectState) {
        this.connectState = connectState;
    }

    public boolean isConnectState() {
        return connectState;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }
        return mBluetoothManager == null ? null : mBluetoothManager.getAdapter();
    }

    public void sendThread(byte[] bytes) {
        try {
            executor.execute(() -> {
                write(bytes);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public boolean write(byte[] wBytes) {
        if (mBluetoothGatt == null) return false;
        try {
            BluetoothGattService gattService = null;
            BluetoothGattCharacteristic characteristic = null;
//            if (getWatchStyle() == S100_WATCH_STYLE) {
//                if (gattService == null) {
//                    gattService = mBluetoothGatt.getService(RTL_UUID_SERVICE_DEVICE);
//                }
//                if (gattService == null) return false;
//                if (characteristic == null) {
//                    characteristic = gattService.getCharacteristic(RTL_UUID_MSG_CHARACTERISTIC);
//                }
//            } else {
            if (gattService == null) {
                gattService = mBluetoothGatt.getService(SECOND_GATT_SERVICE_UUID);
            }
            if (gattService == null) {
                gattService = mBluetoothGatt.getService(UUID_SERVICE_DEVICE);
            }
            if (gattService == null) return false;
            if (characteristic == null) {
                characteristic = gattService.getCharacteristic(WRITE_GATT_CHARACTERISTIC);
            }
            if (characteristic == null) {
                characteristic = gattService.getCharacteristic(UUID_MSG_CHARACTERISTIC);
            }
//            }
            if (characteristic == null) return false;

            int start = 0;
            int len = wBytes.length;

            boolean bool = false;

            if (len > 0) {
                if (getWatchStyle() == F38_WATCH_STYLE) {
                    while (len > 0) {
                        int i;
                        byte[] bytes;
                        if (len < 20) {
                            bytes = new byte[len];
                            for (i = 0; i < len; ++i) {
                                bytes[i] = wBytes[i + start];
                            }
                            characteristic.setValue(bytes);
                            if (mBluetoothGatt != null) {
                                bool = mBluetoothGatt.writeCharacteristic(characteristic);
                            }
                        } else {
                            bytes = new byte[20];
                            for (i = 0; i < 20; ++i) {
                                bytes[i] = wBytes[i + start];
                            }
                            characteristic.setValue(bytes);
                            if (mBluetoothGatt != null) {
                                bool = mBluetoothGatt.writeCharacteristic(characteristic);
                            }
                        }
//                        Log.i("", " write " + ByteToStringUtil.ByteToString(bytes) + " F38 WatchStyle " + getWatchStyle());
                        start = (byte) (start + 20);
                        len -= 20;
                        try {
                            Thread.sleep(80L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
//                    Log.i("", " write " + ByteToStringUtil.ByteToString(wBytes) + " A919 WatchStyle " + getWatchStyle());
                    characteristic.setValue(wBytes);
                    if (mBluetoothGatt != null) {
                        bool = mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                }
            }
            return bool;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onCreate() {
        super.onCreate();
        connectorListenersList = new ArrayList<>();
        simplePerformerListenerList = new ArrayList<>();
        spDeviceTools = new SpDeviceTools(this);
        spRemindTools = new SpRemindTools(this);
        getBluetoothAdapter();
//        registerPhoneStateListener();
        log("开启服务..");
    }

    public void onDestroy() {
        super.onDestroy();
        log("服务关闭..");
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void notifyData(String msg, int id) {
//        sendThread(SIATCommand.getNotifyCom(SIATCommand.SIGN_HEAD, msg, this, id));
//        Log.i(" notifyData ", " notifyData " + ByteToStringUtil.ByteToString(SIATCommand.getNotifyCom(SIATCommand.SIGN_HEAD, msg, this, id)));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getNotifyCom(msg, this, id));
    }

    @SuppressLint("MissingPermission")
    public void disconnect() {
        try {
            log(" 断开连接 disconnect ");
            setConnectState(false);
            if (mBluetoothGatt != null) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public boolean connect(String s) {
        try {
            log("开始连接");
            if (mBluetoothAdapter == null) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (mBluetoothAdapter != null && s != null) {
                if (mBluetoothGatt != null) {
                    return mBluetoothGatt.connect();
                } else {
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(s);
                    if (device == null) {
                        log("开始连接，设备为空");
                        return false;
                    } else {
                        if (spDeviceTools != null && device != null && !TextUtils.isEmpty(device.getName())) {
                            spDeviceTools.putBleName(device.getName());
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            mBluetoothGatt = device.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE,
                                    BluetoothDevice.PHY_LE_1M_MASK | BluetoothDevice.PHY_LE_2M_MASK);
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mBluetoothGatt = device.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
                        } else {
                            mBluetoothGatt = device.connectGatt(this, false, gattCallback);
                        }
                        return true;
                    }
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void upgradeDevice() {
        if (!prepareUpgrade) {
//            sendThread(SIATCommand.getUpdateDeviceCom(SIATCommand.SIGN_HEAD));
//            try {
//                Thread.sleep(SIATCommand.SLEEP_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sendThread(SIATCommand.getUpdateDeviceCom());
        }
    }


    public void sendStartRunCommand() {
//        sendThread(SIATCommand.getStartRunCommand(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getStartRunCommand());
    }

    public void sendEndRunCommand() {
//        sendThread(SIATCommand.getEndRunCommand(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getEndRunCommand());
    }

    //睡眠高心率,睡眠低心率，睡眠高血氧，睡眠低血氧，全天最高心率，全天最低心率，全天最高血氧，全天最低血氧
    public void sendOxHeartCommand(int sleepMaxHeart, int sleepMinHeart, int sleepMaxOx, int sleepMinOx, int maxHeart, int minHeart, int maxOx, int minOx) {
//        sendThread(SIATCommand.getOxHeartCom(SIATCommand.SIGN_HEAD, sleepMaxHeart, sleepMinHeart, sleepMaxOx, sleepMinOx, maxHeart, minHeart, maxOx, minOx));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getOxHeartCom(sleepMaxHeart, sleepMinHeart, sleepMaxOx, sleepMinOx, maxHeart, minHeart, maxOx, minOx));
    }

    public void sendWatchSaveCom() {
        isReceiveSaveData = true;
//        sendThread(SIATCommand.getWatchSaveCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getWatchSaveCom());
    }

    public void sendChinaLocalCom() {
//        sendThread(SIATCommand.getChinaLocalCommand(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getChinaLocalCommand());
    }

    public void sendEncryptCom(byte[] data) {
//        sendThread(SIATCommand.getEncryptCom(SIATCommand.SIGN_HEAD, data));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getEncryptCom(data));
    }

    //发送测量心率
    protected void sendTestHeartCom() {
//        sendThread(SIATCommand.getTestHeartCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getTestHeartCom());
    }

    //发送测量血氧
    protected void sendTestOxCom() {
//        sendThread(SIATCommand.getTestOxCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getTestOxCom());
    }

    //开始体温、环境温度测量
    protected void sendTestHeatCom() {
//        sendThread(SIATCommand.getTestHeatCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getTestHeatCom());
    }

    protected void sendTestTempCom() {
//        sendThread(SIATCommand.getTestTempCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getTestHeatCom());
    }

    //开始疲劳、压力测量
    protected void sendTestTirePressureCom() {
//        sendThread(SIATCommand.getTestTirePressureCom(SIATCommand.SIGN_HEAD));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getTestTirePressureCom());
    }


    //发送电话命令
    public void sendPhoneCom(boolean isOpen){
//        sendThread(SIATCommand.getPhoneCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        Log.i(" sendPhoneCom ", ByteToStringUtil.byteToString(SIATCommand.getPhoneCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0)));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getPhoneCom(isOpen ? 1 : 0));
    }

    //发送信息命令
    public void sendSmsCom(boolean isOpen){
//        sendThread(SIATCommand.getSmsCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        Log.i(" sendSmsCom ", ByteToStringUtil.byteToString(SIATCommand.getSmsCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0)));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSmsCom(isOpen ? 1 : 0));
    }

    public void sendQQCom(boolean isOpen){
//        sendThread(SIATCommand.getQQCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getQQCom(isOpen ? 1 : 0));
    }

    public void sendWeiXinCom(boolean isOpen){
//        sendThread(SIATCommand.getWeiXinCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getWeiXinCom(isOpen ? 1 : 0));
    }

    public void sendSkypeCom(boolean isOpen){
//        sendThread(SIATCommand.getSkypeCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSkypeCom(isOpen ? 1 : 0));
    }

    public void sendWhatsappCom(boolean isOpen){
//        sendThread(SIATCommand.getWhatsappCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getWhatsappCom(isOpen ? 1 : 0));
    }

    public void sendFacebookCom(boolean isOpen){
//        sendThread(SIATCommand.getFacebookCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getFacebookCom(isOpen ? 1 : 0));
    }

    public void sendLinkedlnCom(boolean isOpen){
//        sendThread(SIATCommand.getLinkedlnCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getLinkedlnCom(isOpen ? 1 : 0));
    }

    public void sendTwitterCom(boolean isOpen){
//        sendThread(SIATCommand.getTwitterCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getTwitterCom(isOpen ? 1 : 0));
    }

    public void sendViberCom(boolean isOpen){
//        sendThread(SIATCommand.getViberCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getViberCom(isOpen ? 1 : 0));
    }

    public void sendLineCom(boolean isOpen){
//        sendThread(SIATCommand.getLineCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getLineCom(isOpen ? 1 : 0));
    }

    public void sendMailCom(boolean isOpen){
//        sendThread(SIATCommand.getMailCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getMailCom(isOpen ? 1 : 0));
    }

    public void sendOutlookCom(boolean isOpen){
//        sendThread(SIATCommand.getOutlookCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getOutlookCom(isOpen ? 1 : 0));
    }

    public void sendInstagramCom(boolean isOpen){
//        sendThread(SIATCommand.getInstagramCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getInstagramCom(isOpen ? 1 : 0));
    }

    public void sendSnapchatCom(boolean isOpen){
//        sendThread(SIATCommand.getSnapchatCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getSnapchatCom(isOpen ? 1 : 0));
    }

    public void sendGmailCom(boolean isOpen){
//        sendThread(SIATCommand.getGmailCom(SIATCommand.SIGN_HEAD, isOpen ? 1 : 0));
//        try {
//            Thread.sleep(SIATCommand.SLEEP_TIME);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sendThread(SIATCommand.getGmailCom(isOpen ? 1 : 0));
    }

    public void cancelUpgrade() {
        preparationFailCancelUpgrade();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent("no.nordicsemi.android.dfu.broadcast.BROADCAST_ACTION");
        intent.putExtra("no.nordicsemi.android.dfu.extra.EXTRA_ACTION", 2);
        manager.sendBroadcast(intent);
    }

    public void pauseUpgrade() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent("no.nordicsemi.android.dfu.broadcast.BROADCAST_ACTION");
        intent.putExtra("no.nordicsemi.android.dfu.extra.EXTRA_ACTION", 0);
        manager.sendBroadcast(intent);
    }

    public void resumeUpgrade() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent("no.nordicsemi.android.dfu.broadcast.BROADCAST_ACTION");
        intent.putExtra("no.nordicsemi.android.dfu.extra.EXTRA_ACTION", 1);
        manager.sendBroadcast(intent);
    }

    public String getBleMac() {
        return spDeviceTools.getBleMac();
    }

    public String getBleName() {
        return spDeviceTools.getBleName();
    }

    public void addUpgradeDeviceListener(UpgradeDeviceListener listener) {
        uploadDeviceListener = listener;
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    public void removeUpgradeDeviceListener() {
        uploadDeviceListener = null;
        DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);
    }

    private class WatchPhoneStateListener extends PhoneStateListener {

        public WatchPhoneStateListener() {

        }

        public void onServiceStateChanged(ServiceState state) {
            super.onServiceStateChanged(state);
        }

        public void onCallStateChanged(int state, String phoneNumbe) {
            if (spDeviceTools.getRemindCall()) {
                if (state != 0) {
                    if (state == 1) {
                        notifyData(getContactName(phoneNumbe), 1);
                    }
                } else {
//                    sendThread(SIATCommand.getPhoneStateCom(SIATCommand.SIGN_HEAD));
//                    try {
//                        Thread.sleep(SIATCommand.SLEEP_TIME);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    sendThread(SIATCommand.getPhoneStateCom());
                }
            }
        }

    }

    public synchronized void test(byte[] data) {
//        log("发送的数据是 " + ByteToStringUtil.byteToString(data) + "\n");
        compositeData(data);
        log("isReceiverComplete " + isReceiverComplete + " length == " + length + "\n");
        if (isReceiverComplete) {
            byte[] resultData = destData;
            executor.execute(() -> {
                byte[] bytes = resultData;
                log("接收到的数据是 " + ByteToStringUtil.byteToString(data) + "\n");
//                parseRespone(bytes);
            });
        }
    }

    /**
     * 获取手表类型
     *
     * @return
     */
    public int getWatchStyle() {
        if (spDeviceTools == null) {
            return A919_WATCH_STYLE;
        }
        String name = spDeviceTools.getBleName();
        if (TextUtils.isEmpty(name)) {
            return A919_WATCH_STYLE;
        }
        if (name.contains("F38")) {
            return F38_WATCH_STYLE;
        } else if (name.contains("A919") || name.contains("A920") || name.contains("A910")) {
            return A919_WATCH_STYLE;
        } else if (name.contains("S100") || name.contains("C100")) {
            return S100_WATCH_STYLE;
        } else if (name.contains("A81") || name.contains("A86") || name.contains("Q16")) {
            return Q86_WATCH_STYLE;
        } else {
            return A919_WATCH_STYLE;
        }
    }

    public void setReceiveSaveData(boolean isReceiverSave) {
        isReceiveSaveData = isReceiverSave;
    }

    //初始化C100 SDK
    public void initC100Sdk(Context context) {
        WriteLog.install(context, "OTA", 2);
        RtkConfigure configure = new RtkConfigure.Builder()
                .debugEnabled(BuildConfig.DEBUG)
                .printLog(BuildConfig.DEBUG)
                .logTag("OTA")
                .build();
        RtkCore.initialize(context, configure);
        RtkDfu.initialize(context, BuildConfig.DEBUG);
        // Optional
        BaseBinInputStream.MPHEADER_PARSE_FORMAT = BaseBinInputStream.MPHEADER_PARSE_HEADER;
        SettingsHelper.Companion.initialize(context);
        // Optional for quality test
        DfuQualitySDK.INSTANCE.initialize(context);
        DfuQualitySDK.INSTANCE.setDBG(BuildConfig.DEBUG);

        BluetoothProfileManager.initial(this);

        GattDfuAdapter dfuAdapter = GattDfuAdapter.getInstance(getApplicationContext());
        dfuAdapter.initialize(new DfuAdapter.DfuHelperCallback() {
            @Override
            public void onStateChanged(int state) {
                super.onStateChanged(state);
//                log(" onStateChanged state " + state);
//                if (uploadDeviceListener != null) {
//                    uploadDeviceListener.onUpgradeDeviceTip(" onStateChanged state " + state);
//                }
            }

            @Override
            public void onTargetInfoChanged(OtaDeviceInfo otaDeviceInfo) {
                super.onTargetInfoChanged(otaDeviceInfo);
//                log(" onTargetInfoChanged onTargetInfoChanged ");
            }

            @Override
            public void onError(int type, int code) {
                super.onError(type, code);
                if (uploadDeviceListener != null) {
                    uploadDeviceListener.onUpgradeDeviceError(type, code, "升级失败");
                }
//                log(" onError type " + type + " code " + code);
            }

            @Override
            public void onProcessStateChanged(int state, Throughput throughput) {
                super.onProcessStateChanged(state, throughput);
                String message = getString(DfuHelperImpl.getProgressStateResId(state));
                if (uploadDeviceListener != null) {
                    uploadDeviceListener.onUpgradeDeviceTip(message);
                }
//                log(" onProcessStateChanged state " + state + " message " + message);
            }

            @Override
            public void onProgressChanged(DfuProgressInfo dfuProgressInfo) {
                super.onProgressChanged(dfuProgressInfo);
                if (dfuProgressInfo != null && uploadDeviceListener != null) {
                    if (dfuProgressInfo.getProgress() >= 100) {
                        uploadDeviceListener.onUpgradeDeviceCompleted();
                    } else {
                        uploadDeviceListener.onUpgradeDeviceProgress(dfuProgressInfo.getProgress());
                    }
                }
            }
        });
    }

    public void updateC100Device(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.rtk_file_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        LoadParams.Builder builder = new LoadParams.Builder()
                .with(this)
                .setFilePath(filePath)// Mandatory
                .setFileSuffix("bin")
                .setIcCheckEnabled(false)
                .setSectionSizeCheckEnabled(false)
                .setVersionCheckEnabled(false);
        try {
            BinInfo binInfo = BinFactory.loadImageBinInfo(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

        DfuConfig dfuConfig = new DfuConfig();
        dfuConfig.setFilePath(filePath);
        dfuConfig.setFileIndicator(BinIndicator.INDICATOR_FULL);
        // Mandatory
        dfuConfig.setLocalName(getBleName());
        dfuConfig.setAddress(getBleMac());
        dfuConfig.setProtocolType(0);
        // Optional, used for BLE(GATT)
        String otaServiceUuid = SettingsHelper.Companion.getInstance().getOtaServiceUUID();
        if (!TextUtils.isEmpty(otaServiceUuid)) {
            dfuConfig.setOtaServiceUuid(otaServiceUuid);
        }
        // Optional
        String aesKey = SettingsHelper.Companion.getInstance().getDfuAesKey();
        if (!TextUtils.isEmpty(aesKey)) {
            dfuConfig.setSecretKey(DataConverter.hex2Bytes(aesKey));
        }
        dfuConfig.setBreakpointResumeEnabled(SettingsHelper.Companion.getInstance().isDfuBreakpointResumeEnabled());
        dfuConfig.setAutomaticActiveEnabled(SettingsHelper.Companion.getInstance().isDfuAutomaticActiveEnabled());
        dfuConfig.setBatteryCheckEnabled(SettingsHelper.Companion.getInstance().isDfuBatteryCheckEnabled());
        dfuConfig.setLowBatteryThreshold(SettingsHelper.Companion.getInstance().getDfuLowBatteryThreshold());
        dfuConfig.setBatteryLevelFormat(SettingsHelper.Companion.getInstance().getDfuBatteryLevelFormat());
        dfuConfig.setVersionCheckEnabled(SettingsHelper.Companion.getInstance().isDfuVersionCheckEnabled());
        dfuConfig.setIcCheckEnabled(SettingsHelper.Companion.getInstance().isDfuChipTypeCheckEnabled());
        dfuConfig.setSectionSizeCheckEnabled(SettingsHelper.Companion.getInstance().isDfuImageSectionSizeCheckEnabled());
        dfuConfig.setThroughputEnabled(SettingsHelper.Companion.getInstance().isDfuThroughputEnabled());
        dfuConfig.setMtuUpdateEnabled(SettingsHelper.Companion.getInstance().isDfuMtuUpdateEnabled());
        dfuConfig.setWaitActiveCmdAckEnabled(SettingsHelper.Companion.getInstance().isDfuActiveAndResetAckEnabled());
        // only used for bee1
        dfuConfig.setConParamUpdateLatencyEnabled(SettingsHelper.Companion.getInstance().isDfuConnectionParameterLatencyEnabled());
        dfuConfig.setLatencyTimeout(SettingsHelper.Companion.getInstance().getDfuConnectionParameterLatencyTimeout());
        //optional for RWS
        dfuConfig.setHandoverTimeout(SettingsHelper.Companion.getInstance().getDfuHandoverTimeout());
        dfuConfig.setFileSuffix(SettingsHelper.Companion.getInstance().getFileSuffix());
        if (SettingsHelper.Companion.getInstance().isDfuErrorActionDisconnectEnabled()) {
            dfuConfig.addErrorAction(DfuConfig.ERROR_ACTION_DISCONNECT);
        } else {
            dfuConfig.removeErrorAction(DfuConfig.ERROR_ACTION_DISCONNECT);
        }
        //true: enable refresh service cache, used for GATT
        if (SettingsHelper.Companion.getInstance().isDfuErrorActionRefreshDeviceEnabled()) {
            dfuConfig.addErrorAction(DfuConfig.ERROR_ACTION_REFRESH_DEVICE);
        } else {
            dfuConfig.removeErrorAction(DfuConfig.ERROR_ACTION_REFRESH_DEVICE);
        }
        if (SettingsHelper.Companion.getInstance().isDfuErrorActionCloseGattEnabled()) {
            dfuConfig.addErrorAction(DfuConfig.EA_CLOSE_GATT);
            GlobalGatt.CLOSE_GATT_ENABLED = true;
        } else {
            dfuConfig.removeErrorAction(DfuConfig.EA_CLOSE_GATT);
            GlobalGatt.CLOSE_GATT_ENABLED = false;
        }
        if (SettingsHelper.Companion.getInstance().isDfuCompleteActionRemoveBondEnabled()) {
            dfuConfig.addCompleteAction(DfuConfig.COMPLETE_ACTION_REMOVE_BOND);
        } else {
            dfuConfig.removeCompleteAction(DfuConfig.COMPLETE_ACTION_REMOVE_BOND);
        }
        // optional, for log to debug
        dfuConfig.setLogLevel(BuildConfig.DEBUG ? 1 : 0);
        //option for normal mode
        if (dfuConfig.getOtaWorkMode() == DfuConstants.OTA_MODE_NORMAL_FUNCTION) {
            dfuConfig.setWaitDisconnectWhenEnterOtaMode(SettingsHelper.Companion.getInstance().isDfuWaitDisconnectWhenEnterOtaModeEnabled());
        }
        int bufferCheckLevel = SettingsHelper.Companion.getInstance().getDfuBufferCheckLevel();
        dfuConfig.setBufferCheckLevel(bufferCheckLevel);
        dfuConfig.setSpeedControlEnabled(false);
        dfuConfig.setControlSpeed(0);

        GattDfuAdapter dfuAdapter = GattDfuAdapter.getInstance(getApplicationContext());
        boolean ret = dfuAdapter.startOtaProcedure(dfuConfig);
        if (!ret) {
            Toast.makeText(this, R.string.rtk_toast_operation_failed, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateDeviceByFile(String fileName) {
        upgradeDevice(fileName);
    }

    /**
     * 字符串转byte数组
     *
     * @param var0
     * @return
     */
    public static byte[] parseHexStr2Byte(String var0) {
        if (var0.length() < 1) {
            return null;
        } else {
            byte[] var1 = new byte[var0.length() / 2];
            for (int var2 = 0; var2 < var0.length() / 2; ++var2) {
                int var3;
                int var4;
                int var10002 = Integer.parseInt(var0.substring(var2 * 2, var4 = (var3 = var2 * 2) + 1), 16);
                var3 = Integer.parseInt(var0.substring(var4, var3 + 2), 16);
                var1[var2] = (byte) (var10002 * 16 + var3);
            }
            return var1;
        }
    }

    private int getHeartLastData(List<Integer> datas) {
        if ((datas != null) && (datas.size() > 0)) {
            for (int i = datas.size() - 1; i >= 0; i--) {
                int curr = datas.get(i);
                if (curr > 0) {
                    return curr;
                }
            }
        }
        return 0;
    }
}
