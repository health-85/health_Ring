package com.sw.watches.bluetooth;


import android.content.Context;
import android.os.Handler;

import com.sw.watches.bean.DeviceModule;
import com.sw.watches.bleUtil.ParametersUtil;
import com.sw.watches.listener.IScanBluetoothListener;
import com.sw.watches.listener.IScanCallback;

import java.util.ArrayList;
import java.util.List;

public class AllBluetoothManage {
    public Context mContext;
    public BleUtil mClassicManage;
    public BleManage mBleManage;
    public List<DeviceModule> mClassicBluetoothArray = new ArrayList<>();
    public List<DeviceModule> mScanAllModuleArray = new ArrayList<>();
    public IScanBluetoothListener mIBluetooth;
    public AllBluetoothManage.BlueState mState;
    public boolean mUpdateTheLimit;
    public Handler mTimeHandler;

    public AllBluetoothManage(Context context, IScanBluetoothListener iBluetooth) {
        this.mContext = context;
        this.mIBluetooth = iBluetooth;
        this.mState = AllBluetoothManage.BlueState.STOP_SCAN_STATE;
        this.mUpdateTheLimit = false;
        this.mTimeHandler = new Handler();
        this.mClassicManage = new BleUtil(context);
        this.mBleManage = new BleManage(context);
        ParametersUtil.initParameters(context);
    }

    private synchronized void callbackActivity(DeviceModule module, boolean bool) {
        if (mIBluetooth != null) {
            if ((bool || mUpdateTheLimit) && module == null) {
                return;
            }
            if (module == null) {
                mUpdateTheLimit = true;
                mTimeHandler.postDelayed(new Runnable() {
                    public void run() {
                        AllBluetoothManage.this.mUpdateTheLimit = false;
                    }
                }, 200L);
            }
            mIBluetooth.updateList(module);
            if (module != null) {
                mScanAllModuleArray.add(module);
            }
        }

    }

    public boolean bleScan() {
        if (this.mState == AllBluetoothManage.BlueState.START_SCAN_STATE) {
            return false;
        } else {
            this.mScanAllModuleArray.clear();
            this.mState = AllBluetoothManage.BlueState.START_SCAN_STATE;
            this.mBleManage.scanBluetooth(new IScanCallback() {
                public void addDeviceModule() {
                    AllBluetoothManage.this.mIBluetooth.updateEnd();
                    AllBluetoothManage.this.mState = AllBluetoothManage.BlueState.STOP_SCAN_STATE;
                }

                public void addDeviceModule(DeviceModule deviceModule) {
                    AllBluetoothManage.this.callbackActivity(deviceModule, true);
                }
            });
            return true;
        }
    }

    public void stopScan() {
        try {
            try {
                mIBluetooth.updateEnd();
                mClassicManage.stop();
                mBleManage.stopScan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Throwable throwable) {
            mState = AllBluetoothManage.BlueState.STOP_SCAN_STATE;
            throw throwable;
        }

        mState = AllBluetoothManage.BlueState.STOP_SCAN_STATE;
    }

    public boolean isStartBluetooth() {
        return this.mClassicManage.isBluetoothEnable();
    }

    private enum BlueState {

        START_SCAN_STATE,
        STOP_SCAN_STATE;

        BlueState() {

        }
    }

}
