package com.sw.watches.receiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.sw.watches.bleUtil.ReflectUtil;

import java.util.Timer;
import java.util.TimerTask;

public class PairingRequestReceiver extends BroadcastReceiver {

    public static final String PIN = "1234";
    public static final String TAG = PairingRequestReceiver.class.getSimpleName();

    public int time = 0;
    public Context context;
    public ConnectFinishListener listener;

    public PairingRequestReceiver(Context context, ConnectFinishListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                BluetoothDevice device = intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Log.d("AppRun" + TAG, "接收到广播");
                abortBroadcast();
                if (device == null) {
                    Log.e("AppRun" + TAG, "bluetoothDevice is null !!");
                    return;
                }

                if (Build.VERSION.SDK_INT >= 19) {
                    try {
                        device.setPin(PIN.getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        ReflectUtil.setPin(device.getClass(), device, PIN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                MyTimerTask timerTask = new MyTimerTask(this, device, new Timer(), new FinishTask(this));
                (new Timer()).schedule(timerTask, 300L, 200L);
            }

        }
    }

    class FinishTask extends TimerTask {

        PairingRequestReceiver BlueReceiver;

        public FinishTask(PairingRequestReceiver BlueReceiver) {
            this.BlueReceiver = BlueReceiver;
        }

        public void run() {
            Log.w("AppRun", "Bluetooth bond state is none,connect bluetooth");
            if (listener != null)
                try {
                    listener.connectFinish();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
        }
    }

    class MyTimerTask extends TimerTask {

        Timer timer;
        TimerTask timerTask;
        BluetoothDevice device;
        PairingRequestReceiver receiver;

        public MyTimerTask(PairingRequestReceiver BlueReceiver, BluetoothDevice device, Timer timer, TimerTask timerTask) {
            this.receiver = BlueReceiver;
            this.device = device;
            this.timer = timer;
            this.timerTask = timerTask;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            receiver.time++;
            Log.d("AppRun" + receiver.listener, "loop,device bond state is " + device.getBondState());
            if (receiver.time == 8 || device.getBondState() == 10) {
                try {
                    cancel();
                    context.unregisterReceiver(receiver);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                Log.w("AppRun" + receiver.listener, "Close broadcast,delayed 500 ms,connect bluetooth");
                timer.schedule(timerTask, 500L);
                return;
            }
            if (this.device.getBondState() == 12 && receiver.listener != null)
                try {
                    Log.d("AppRun" + receiver.listener, "Close broadcast, bluetooth bond state is bonded (success)");
                    receiver.listener.connectFinish();
                    receiver.context.unregisterReceiver(receiver);
                    cancel();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
        }
    }

    public interface ConnectFinishListener {
        void connectFinish();
    }
}