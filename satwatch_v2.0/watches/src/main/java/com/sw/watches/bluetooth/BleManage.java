package com.sw.watches.bluetooth;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sw.watches.bean.DeviceModule;
import com.sw.watches.bleUtil.DeviceNameUtil;
import com.sw.watches.bleUtil.DeviceModelUtil;
import com.sw.watches.listener.IScanCallback;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BleManage {

    public static final int SERVICE_CONNECT_FAIL = 2;

    public static final int SERVICE_SEND_DATA_NUMBER = 4;

    public static final int SERVICE_READ_LOG = 5;

    public static final String SERVICE_SEPARATOR = "/**separator**/";

    public static final long SCAN_PERIOD = 20000L;

    public BluetoothAdapter mBluetoothAdapter;

    public ScanCallback mScanCallback;

    public ScanCallback mScanCallbackMessyCode;

    public BluetoothLeScanner mBluetoothLeScanner;

    public Handler mTimeHandler = new Handler();

    public Context mContext;

    public List<DeviceModule> mListDevices;

    public boolean isOffScan = true;

    public boolean isTimeScan = true;

    public IScanCallback mIScanCallback;

    public BluetoothAdapter.LeScanCallback mLeScanCallback = new MyLeScanCallback(this);

    public BleManage(Context context) {
        mContext = context;
        init_ble();
        mListDevices = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 21) {
            mScanCallback = new BleManage.BleScanCallback(this);
        }
    }

    private boolean filtterDeviceName(String paramString) {
        boolean bool = false;
        if (paramString != null && !paramString.equals("") && paramString.length() >= 5 && paramString.substring(paramString.length() - 5, paramString.length() - 4).equals("_"))
            bool = true;
        return bool;
    }

    private void addDeviceModel(BluetoothDevice bluetoothDevice, int rssi, String deviceName, ScanResult scanResult) {
        if (!filtterDeviceName(deviceName))
            return;
        if (mListDevices.size() == 0) {
            mListDevices.add(new DeviceModule(bluetoothDevice, rssi, deviceName, mContext, scanResult));
            mIScanCallback.addDeviceModule(mListDevices.get(0));
            return;
        }
        Iterator<DeviceModule> iterator = mListDevices.iterator();
        while (iterator.hasNext()) {
            DeviceModule deviceModule1;
            if ((deviceModule1 = iterator.next()).getDevice().toString().equals(mContext.toString())) {
                deviceModule1.setRssi(rssi);
                mIScanCallback.addDeviceModule(null);
                return;
            }
        }
        DeviceModule deviceModule = new DeviceModule(bluetoothDevice, rssi, deviceName, mContext, scanResult);
        mListDevices.add(deviceModule);
        mIScanCallback.addDeviceModule(deviceModule);
    }

    private void init_ble() {
        if (!mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Toast.makeText(mContext, "不支持BLE蓝牙,请退出...", Toast.LENGTH_SHORT).show();
            ((Activity) mContext).finish();
            return;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        } else {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    private void log(String paramString) {
        Log.d("AppRun" + BleManage.class.getSimpleName(), paramString);
    }

    @SuppressLint("MissingPermission")
    public void scanBluetooth(IScanCallback scanCallback) {
        mIScanCallback = scanCallback;
        if (mBluetoothAdapter == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            } else {
                this.mBluetoothAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            }
        }
        if (!this.mBluetoothAdapter.isEnabled()) {
            Toast.makeText(mContext, "蓝牙未打开", Toast.LENGTH_LONG).show();
            return;
        }
        if (mBluetoothLeScanner == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
        if (isOffScan) {
            this.isOffScan = false;
            this.isTimeScan = true;
            this.mTimeHandler.postDelayed(new StopScanRunnable(this), 20000L);
            log("开始扫描");
            this.mListDevices.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    log("高功耗扫描模式...");
                    ScanSettings.Builder builder = (new ScanSettings.Builder()).setScanMode(2);
                    mBluetoothLeScanner.startScan(null, builder.build(), mScanCallback);
                } else {
                    mBluetoothLeScanner.startScan(mScanCallback);
                }
            } else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void stopScan() {
        if (!isOffScan) {
            isOffScan = true;
            isTimeScan = false;
            log("手动停止扫描");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothLeScanner.stopScan(mScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            mTimeHandler.removeMessages(0);
            log("搜索到个数" + mListDevices.size());
        }
    }

    class BleScanCallback extends ScanCallback {

        public BleScanCallback(BleManage bleBluetoothManage) {

        }

        @SuppressLint("MissingPermission")
        public void onScanResult(int callbackType, ScanResult scanResult) {
            String deviceName = null;
            BluetoothDevice device = scanResult.getDevice();
            if (device != null && scanResult.getScanRecord() != null && DeviceModelUtil.isDeviceModel(device.getName())) {
                deviceName = device.getName();
                if (TextUtils.isEmpty(deviceName)) {
                    byte[] bytes = DeviceNameUtil.parseDeviceName(DeviceNameUtil.len3, scanResult.getScanRecord().getBytes());
                    try {
                        deviceName = new String(bytes, "GBK");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (deviceName == null && device != null) {
                deviceName = device.getName();
            }
            String finalDeviceName = deviceName;
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addDeviceModel(device, scanResult.getRssi(), finalDeviceName, scanResult);
                }
            });

        }

        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "搜索出错", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class MyLeScanCallback implements BluetoothAdapter.LeScanCallback {

        BleManage manage;

        public MyLeScanCallback(BleManage manage) {
            this.manage = manage;
        }

        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            ((Activity) manage.mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addDeviceModel(bluetoothDevice, rssi, null, null);
                }
            });
        }
    }

    class StopScanRunnable implements Runnable {

        public StopScanRunnable(BleManage manage) {

        }

        @SuppressLint("MissingPermission")
        public void run() {
            if (!isTimeScan) {
                log("时间到，已提前停止扫描");
                return;
            }
            log("自动停止扫描");
            mIScanCallback.addDeviceModule();
            if (Build.VERSION.SDK_INT >= 21) {
                mBluetoothLeScanner.stopScan(mScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            log("搜索到个数" + mListDevices.size());
        }
    }
}
