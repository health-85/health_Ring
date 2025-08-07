package com.sdk.satwatch.receive;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sdk.satwatch.util.ScanDevice;
import com.sdk.satwatch.listener.ScannerListener;

import java.util.List;

/**
 * 蓝牙状态通知监听
 */
public class BluetoothStateReceive extends BroadcastReceiver {

    /**
     * 蓝牙开启、关闭、发现设备
     * @param context
     * @param intent
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(" BluetoothStateReceive ", " action " + intent.getAction());
        List<ScannerListener> list =  ScanDevice.getInstance(context).getListenerList();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (blueState == BluetoothAdapter.STATE_ON) {
                if (list != null && list.size() > 0) {
                    for (ScannerListener listener : list){
                        listener.reconnectDevice();
                    }
                }
            }
            if (blueState == BluetoothAdapter.STATE_OFF) {
                ScanDevice.getInstance(context).stopScan();
                if (list != null && list.size() > 0) {
                    for (ScannerListener listener : list){
                        listener.scanStoped();
                    }
                }
            }
        }
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {

        }
        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
//            ScanDevice.getInstance(context).stopScan();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            String mac = (device.getAddress().replace(":", ""));
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("设备名称:" + device.getName() + "\n");
//            stringBuilder.append("mac地址:" + toLowerCase(mac, 0, mac.length()) + "\n");
//            //用一个新的string集合去对比设备名称和mac地址，不能拼接rssi和uuid后再去对比
//            if (stringList.indexOf(stringBuilder.toString()) == -1) {
//                // 防止重复添加
//                stringList.add(stringBuilder.toString());
//                if (device.getName() != null) {
//                    stringBuilder.append("rssi:" + intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI) + "\n");
//                    stringBuilder.append("Uuid:" + device.getUuids());
//                    blueToothList.add(stringBuilder.toString()); // 获取设备名称和mac地址
//                }
//            }
//            if (list != null && list.size() > 0) {
//                for (ScannerListener listener : list){
//                    listener.onFoundScan(device, 0, null);
//                }
//            }
        }
    }
}
