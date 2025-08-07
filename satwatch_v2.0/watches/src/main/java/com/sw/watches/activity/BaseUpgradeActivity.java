package com.sw.watches.activity;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sw.watches.service.ZhBraceletService;
import com.sw.watches.listener.UpgradeDeviceListener;

public class BaseUpgradeActivity extends AppCompatActivity implements UpgradeDeviceListener {

    public ZhBraceletService mBleService;

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }

    public void startUpgrade(ZhBraceletService zhBraceletService) {
        mBleService = zhBraceletService;
        mBleService.addUpgradeDeviceListener(this);
        mBleService.upgradeDevice();
    }

    public void cancelUpgrade() {
        if (mBleService == null) return;
        this.mBleService.cancelUpgrade();
    }

    public void pauseUpgrade() {
        if (mBleService == null) return;
        mBleService.pauseUpgrade();
    }

    public void resumeUpgrade() {
        if (mBleService == null) return;
        mBleService.resumeUpgrade();
    }

    public void onUpgradeDeviceError(int paramInt1, int paramInt2, String paramString) {
    }

    public void onUpgradeDeviceProgress(int paramInt) {
    }

    public void onUpgradeDeviceCompleted() {
    }

    public void onUpgradeDeviceStarting(int paramInt) {
    }

    @Override
    public void onUpgradeDeviceTip(String message) {

    }

    @Override
    public void onReConnectUpdateDevice(String s, boolean start) {

    }

    public void onDestroy() {
        if (mBleService != null) {
            mBleService.removeUpgradeDeviceListener();
        }
        super.onDestroy();
    }
}
