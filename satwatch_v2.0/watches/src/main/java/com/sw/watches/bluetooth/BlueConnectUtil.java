package com.sw.watches.bluetooth;

import android.app.Activity;
import android.content.Context;

public class BlueConnectUtil {

    public Context context;
    public BlueConnectListener listener;

    public BlueConnectUtil(Context context) {
        this.context = context;
    }

    private void startConnectBluetooth() {
        Runnable runnable = new Runnable() {
            public void run() {
                if (listener != null) {
                    Activity activity = (Activity) context;
                    boolean bool;
                    try {
                        bool = listener.connect();
                    } catch (Exception exception) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.connectFail(exception);
                            }
                        });
                        exception.printStackTrace();
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (bool) {
                                listener.connectSuccess();
                            }
                        }
                    });
                }
            }
        };
        (new Thread(runnable)).start();
    }

    public void startBlueConnect(BlueConnectListener listener) {
        this.listener = listener;
        startConnectBluetooth();
    }

    public interface BlueConnectListener {

        void connectSuccess();

        boolean connect();

        void connectFail(Exception e);

    }

}