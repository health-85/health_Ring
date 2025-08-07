package com.sdk.satwatch.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sdk.satwatch.listener.ScannerListener;
import com.sw.watches.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.qqtheme.framework.util.LogUtils;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

/**
 * 扫描蓝牙设备
 */
public class ScanDevice {

    private static final String TAG = "ScanDevice";

    private boolean isScaning;
    /**
     * 权限列表
     */
    private List<String> mPerList;

    private Context mContext;
    //是否限制设备名称
    private boolean isLimitDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLeScanner;
//    private SingBroadcastReceiver mReceiver;

    private List<ScannerListener> mListenerList;

    public volatile static ScanDevice instance;

    /**
     * 实例化ScanDevice
     *
     * @param context
     * @return
     */
    public static ScanDevice getInstance(Context context) {
        if (instance == null) {
            synchronized (ScanDevice.class) {
                if (instance == null) {
                    instance = new ScanDevice(context);
                }
            }
        }
        return instance;
    }

    private ScanDevice(Context context) {
        mContext = context;
        isLimitDevice = true;
        mPerList = new ArrayList<>();
        mListenerList = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mPerList.add(Manifest.permission.BLUETOOTH_CONNECT);
            mPerList.add(Manifest.permission.BLUETOOTH_SCAN);
        }
        mPerList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        mPerList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    /**
     * 开始扫描
     */
    @SuppressLint("MissingPermission")
    public void startScan() {
        if (!hasPemissions(mContext) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mContext.requestPermissions(mPerList.toArray(new String[0]), 2222);
            Toast.makeText(mContext, "请打开蓝牙扫描及链接附近设备权限", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isGpsAndNetworkEnable(mContext)) {
            Toast.makeText(mContext, "请打开定位服务", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isSuportBle(mContext)) {
            Toast.makeText(mContext, "手机不支持蓝牙", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isBleEnable()) {
            Toast.makeText(mContext, "蓝牙未打开 请打开手机蓝牙功能", Toast.LENGTH_LONG).show();
            return;
        }
        BluetoothLeScannerCompat.getScanner().stopScan(mNorScanCallback);

        ScanSettings settings = new ScanSettings.Builder()
//                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                .setReportDelay(1000)
//                .setUseHardwareBatchingIfSupported(true)
                .build();
        final List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder()
//                .setServiceUuid(ParcelUuid.fromString("00000001-0000-1000-8000-00805f9b34fb"))
                .build());
        BluetoothLeScannerCompat.getScanner().startScan(filters, settings, mNorScanCallback);

//        BluetoothLeScannerCompat.getScanner().startScan(mNorScanCallback);
//        if (mReceiver == null) {
//            mReceiver = new SingBroadcastReceiver();
//            IntentFilter filter = new IntentFilter();
//            filter.addAction(BluetoothDevice.ACTION_FOUND);
//            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//            mContext.registerReceiver(mReceiver, filter);
//        }
//        if (mBluetoothAdapter == null) {
//            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        }
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
//            isScaning = mBluetoothAdapter.startLeScan(mLeScanCallback);
//        } else {
//            mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
//            mLeScanner.startScan(scanCallback);
//        }
//        mBluetoothAdapter.startDiscovery();
        if (mListenerList != null && mListenerList.size() > 0) {
            for (ScannerListener listener : mListenerList) {
                listener.scanStarted();
            }
        }
    }

    /**
     * 停止扫描
     */
    @SuppressLint("MissingPermission")
    public void stopScan() {
//        if (mBluetoothAdapter != null && isBleEnable()) {
//            isScaning = false;
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            if (mLeScanner != null) {
//                mLeScanner.stopScan(scanCallback);
//            }
//            mBluetoothAdapter.cancelDiscovery();
//        }
        BluetoothLeScannerCompat.getScanner().stopScan(mNorScanCallback);
        if (mListenerList != null && mListenerList.size() > 0) {
            for (ScannerListener listener : mListenerList) {
                listener.scanStoped();
            }
        }
//        if (mReceiver != null) {
//            mContext.unregisterReceiver(mReceiver);
//            mReceiver = null;
//        }
    }

    /**
     * 添加监听
     *
     * @param listener
     */
    public void addScanListener(ScannerListener listener) {
        if (mListenerList != null) {
            mListenerList.add(listener);
        }
    }

    /**
     * 移除扫描监听
     *
     * @param listener
     */
    public void removeScanListener(ScannerListener listener) {
        if (mListenerList != null) {
            mListenerList.remove(listener);
        }
    }

    public void setLimitDevice(boolean limitDevice) {
        isLimitDevice = limitDevice;
    }

    /**
     * 是否拥有权限
     *
     * @param con
     * @return
     */
    private boolean hasPemissions(Context con) {
        if (mPerList != null && mPerList.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean R = true;
                for (int i = 0; i < mPerList.size(); i++) {
                    R = (R && (con.checkSelfPermission(mPerList.get(i)) == PackageManager.PERMISSION_GRANTED));
                    LogUtil.i(TAG, mPerList.get(i) + " " + R);
                }
                return R;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 定位是否可用
     *
     * @param context
     * @return
     */
    public static boolean isGpsAndNetworkEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && (locationManager.isProviderEnabled("gps") || locationManager.isProviderEnabled("network")))
            return true;
        return false;
    }

    /**
     * 是否支持蓝牙
     *
     * @return
     */
    public boolean isSuportBle(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 蓝牙是否已经打开
     *
     * @return
     */
    public boolean isBleEnable() {
        if (mBluetoothAdapter == null) {
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }


    /**
     * 是否是指定型号的设备
     *
     * @param device
     * @return
     */
    @SuppressLint("MissingPermission")
    public boolean isSpecDevice(BluetoothDevice device) {
        String deviceName = device.getName();
        return deviceName.startsWith("BIO-RING-")|deviceName.startsWith("DfuTarg");
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {


        @SuppressLint("MissingPermission")
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String derp = device.getName() + " - " + device.getAddress();
            if (!TextUtils.isEmpty(device.getName()) && device.getType() == BluetoothDevice.DEVICE_TYPE_LE && isSpecDevice(device)) {
//                Log.i(" onLeScan device ", derp + " " + rssi);
                if (mListenerList != null && mListenerList.size() > 0) {
                    Log.i("", mListenerList.size() + "");
                    for (ScannerListener listener : mListenerList) {
                        listener.onFoundScan(device, rssi, scanRecord);
                    }
                }
            }
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (!TextUtils.isEmpty(device.getName()) && device.getType() == BluetoothDevice.DEVICE_TYPE_LE && isSpecDevice(device)) {
                String derp = device.getName() + " - " + device.getAddress();
//                Log.i(" scanCallback device ", derp + " " + result.getRssi());
                if (mListenerList != null && mListenerList.size() > 0) {
                    Log.i("", mListenerList.size() + "");
                    for (ScannerListener listener : mListenerList) {
                        listener.onFoundScan(device, result.getRssi(), null);
                    }
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            if (mListenerList != null && mListenerList.size() > 0) {
                for (ScannerListener listener : mListenerList) {
                    listener.scanStoped();
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            LogUtil.i(" errorCode ", " errorCode " + errorCode);
            if (mListenerList != null && mListenerList.size() > 0) {
                for (ScannerListener listener : mListenerList) {
                    listener.scanStoped();
                }
            }
        }
    };

    public List<ScannerListener> getListenerList() {
        return mListenerList;
    }

    private no.nordicsemi.android.support.v18.scanner.ScanCallback mNorScanCallback = new no.nordicsemi.android.support.v18.scanner.ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, @NonNull no.nordicsemi.android.support.v18.scanner.ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result != null && result.getDevice() != null && !TextUtils.isEmpty(result.getDevice().getName()) && isSpecDevice(result.getDevice())) {
//                Log.i("", " ScanCallback name " + result.getDevice().getName());
                if (mListenerList != null && mListenerList.size() > 0) {
                    for (ScannerListener listener : mListenerList) {
                        if (result.getScanRecord() != null) {
                            listener.onFoundScan(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                        } else {
                            listener.onFoundScan(result.getDevice(), result.getRssi(), null);
                        }
                    }
                }
            }
        }

        @Override
        public void onBatchScanResults(@NonNull List<no.nordicsemi.android.support.v18.scanner.ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            isScaning = false;
        }
    };

//     class SingBroadcastReceiver extends BroadcastReceiver {
//
//        @SuppressLint("MissingPermission")
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Get the BluetoothDevice object from the Intent
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
//                // Add the name and address to an array adapter to show in a Toast
//                String derp = device.getName() + " - " + device.getAddress();
//                if (!TextUtils.isEmpty(device.getName()) && device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
//                    Log.i(" onReceive device ", derp + " " + rssi);
//                    if (mListener != null){
//                        mListener.onFoundScan(device, rssi, null);
//                    }
//                }
//            }
//        }
//    }

}
