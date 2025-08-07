package com.sdk.satwatch.receive;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class BluetoothPairingRequest extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        if (!action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
//            return;
//        }
        if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
            boolean isEffective = isEffectiveClick();
            Log.i("BluetoothPairingRequest", "  action " + action + " isEffective " + isEffective);
//            if (!isEffective) {
//                abortBroadcast();
//            }
//            abortBroadcast();
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            device.setPairingConfirmation(true);
        }
    }

    private static final int MIN_CLICK_DELAY_TIME = 10000;
    private static long lastClickTime;

    public boolean isEffectiveClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            flag = true;
        }
        return flag;
    }
}
