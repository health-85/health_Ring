package com.healthy.rvigor.receive;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.watch.WatchBase;

/**
 * 蓝牙状态监控蓝牙打开时 从新连接
 */
public class BluetoothStateBroadcastReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (blueState == BluetoothAdapter.STATE_ON) {
                WatchBase watchBase = MyApplication.Companion.instance().getBleUtils().getConnectionWatch();
                if (watchBase == null) {
                    MyApplication.Companion.instance().getWatchSyncUtils().reStartScanDevice();
                }
            }

            if (blueState == BluetoothAdapter.STATE_OFF) {
                MyApplication.Companion.instance().getBleUtils().stopScan();
                WatchBase watchBase = MyApplication.Companion.instance().getBleUtils().getConnectionWatch();
                if (watchBase != null) {
                    watchBase.close();
                }
            }
        }

        if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
            if (state == BluetoothAdapter.STATE_DISCONNECTED) {
                MyApplication.Companion.instance().getWatchSyncUtils().reStartScanDevice();
            } else if (state == BluetoothAdapter.STATE_CONNECTED) {
                // 蓝牙连接成功
                // 在此处添加你的逻辑代码
            }
        }

        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
            int i = 0;
        }

        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
//            if (MainApplication.getInstance() != null) {
//                if (MainApplication.getInstance().getAdapetUtils().IsDeviceScaning()) {
//                    MainApplication.getInstance().getAdapetUtils().stopScan();
//                }
//            }
        }
    }
}
