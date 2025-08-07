package com.sw.watches.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.sw.watches.bean.DeviceModule;
import com.sw.watches.bleUtil.ReflectUtil;
import com.sw.watches.listener.BluetoothListener;
import com.sw.watches.bleUtil.DeviceModelUtil;
import com.sw.watches.listener.IScanCallback;
import com.sw.watches.bleUtil.ParametersUtil;
import com.sw.watches.receiver.PairingRequestReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BleUtil {

    public BluetoothAdapter bluetoothAdapter;

    public BluetoothSocket bluetoothSocket;

    public OutputStream outputStream;

    public InputStream inputStream;

    public Map<String, BluetoothDevice> bluetoothDeviceMap;

    public List<DeviceModule> deviceModuleList;

    public IScanCallback scanCallback;

    public BluetoothListener bluetoothListener;

    public Context context;

    public boolean bool;

    public Handler handler;

    public List<byte[]> byteList;

    public boolean aBoolean;

    public Thread socketWriteThread;

    public Thread inputThread;

    public String p;

    public Timer timer;

    public TimerTask timerTask;

    public int len;

    public final BroadcastReceiver foundReceiver;

    public BroadcastReceiver disConnectReceiver;

    public BroadcastReceiver blueStateChangeReceiver;

    public BleUtil(Context context) {
        bluetoothSocket = null;
        bluetoothDeviceMap = new HashMap<>();
        deviceModuleList = new ArrayList<>();
        bool = true;
        handler = new Handler();
        byteList = new ArrayList<>();
        aBoolean = false;
        p = null;
        len = 0;
        foundReceiver = new FindDeviceReceiver(this);
        disConnectReceiver = new DisconnectedReceiver(this);
        this.context = context;
        getBlueAdapter();
    }

    private void registerBlueStateChange() {
        blueStateChangeReceiver = new BlueStateChangeReceiver(this);
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(blueStateChangeReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(blueStateChangeReceiver, intentFilter);
        }
    }

    private void getBlueAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void cleanData() {
        deviceModuleList.clear();
        bluetoothDeviceMap.clear();
    }

    @SuppressLint("MissingPermission")
    private void addDeviceModule(BluetoothDevice device, boolean bool, int rssi) {
        int length = bluetoothDeviceMap.size();
        bluetoothDeviceMap.put(device.getAddress(), device);
        DeviceModule deviceModule;
        if (bluetoothDeviceMap.size() > length) {
            deviceModule = new DeviceModule(device.getName(), device, bool, context, rssi);
            deviceModuleList.add(deviceModule);
            addDeviceModule(deviceModule);
        } else {
            Iterator<DeviceModule> iterator = this.deviceModuleList.iterator();
            while (iterator.hasNext()) {
                deviceModule = (DeviceModule) iterator.next();
                if (deviceModule.getMac().equals(device.getAddress())) {
                    deviceModule.setRssi(rssi);
                    addDeviceModule((DeviceModule) null);
                }
            }
        }
    }

    private void addDeviceModule(DeviceModule module) {
        if (scanCallback != null) {
            scanCallback.addDeviceModule(module);
        }
    }

    @SuppressLint("MissingPermission")
    private void connectDevice(BluetoothDevice device) {
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            (new BlueConnectUtil(context)).startBlueConnect(new BlueConnectUtil.BlueConnectListener() {
                @Override
                public void connectSuccess() {
                    log("蓝牙socket连接成功..");
                    aBoolean = true;
                    if (bluetoothListener != null) {
                        bluetoothListener.blueConnectSuccess(BleUtil.this.p);
                    }
                    startWriteSocketThread();
                    startInputThread();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.registerReceiver(disConnectReceiver, new IntentFilter("android.bluetooth.device.action.ACL_DISCONNECTED"), Context.RECEIVER_NOT_EXPORTED);
                    } else {
                        context.registerReceiver(disConnectReceiver, new IntentFilter("android.bluetooth.device.action.ACL_DISCONNECTED"));
                    }
                }

                @Override
                public boolean connect() {
                    try {
                        log("准备开始建立socket连接...");
                        bluetoothSocket.connect();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return true;
                }

                @Override
                public void connectFail(Exception e) {
                    destroy();
                    StringWriter stringWriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(stringWriter));
                    log("连接失败: " + stringWriter.toString(), "e");
                    if (bluetoothListener != null) {
                        String var3 = p;
                        BleUtil.this.bluetoothListener.blueConnectFail(var3, e.toString());
                    }
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void startWriteSocketThread() {
        socketWriteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (aBoolean) {
                    if (byteList.size() > 0) {
                        if (outputStream == null) {
                            try {
                                outputStream = bluetoothSocket.getOutputStream();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        byte[] bytes = byteList.get(0);
                        int len;
                        if (bytes != null) {
                            len = bytes.length;
                            if (len > 0) {
                                try {
                                    outputStream.write(bytes);
                                    bluetoothListener.writeLength(bytes.length);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        try {
                            outputStream.flush();
                            byteList.remove(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        len = ParametersUtil.getOutputStreamLen();
                        if (len <= 0) {
                            continue;
                        }
                        try {
                            Thread.sleep((len * 10));
                            continue;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        socketWriteThread.start();
        log("发送线程就绪..");
    }

    private void startInputThread() {

        try {
            inputStream = bluetoothSocket.getInputStream();
            (this.inputThread = new Thread(new Thread(new Runnable() {
                @Override
                public void run() {
                    int length = ParametersUtil.getInputLength();
                    byte[] bytes = new byte[length];
                    byte[] var3 = null;
                    while (true) {
                        do {
                            if (!aBoolean) {
                                return;
                            }
                            if (bytes.length != ParametersUtil.getInputLength()) {
                                bytes = new byte[ParametersUtil.getInputLength()];
                            }
                        } while (inputStream == null);

                        int available = 0;
                        try {
                            available = inputStream.available();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (available == 0) {
                            continue;
                        }
                        while (true) {
                            int readLen = 0;
                            try {
                                readLen = inputStream.read(bytes);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                startReadTimer(readLen);
                                available = BleUtil.this.getArrayLen(var3);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            int var5 = available;
                            if (available > 200) {
                                try {
                                    BleUtil.this.bluetoothListener.available(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (length - var5 < readLen && var3 != null) {
                                try {
                                    BleUtil.this.cleanAndClone((byte[]) var3, (byte[]) null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                var3 = null;
                            }

                            byte[] var10003 = new byte[0];
                            try {
                                var10003 = BleUtil.this.copyByteArray(var3, bytes, readLen);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            var3 = var10003;
                            try {
                                cleanByteArray(bytes);
                                inputStreamSleep(ParametersUtil.getInputStreamLen());
                                available = inputStream.available();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (available == 0) {
                                try {
                                    BleUtil.this.cleanAndClone(var3, bytes);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                var3 = null;
                                try {
                                    BleUtil.this.bluetoothListener.available(false);
                                    BleUtil.this.cancelTimerTask();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }))).start();
            log("接收线程就绪...");
        } catch (IOException ex) {
            Toast.makeText(context, "设置监听模块数据的socket失败，请重新连接", Toast.LENGTH_SHORT).show();
            StringWriter stringWriter = new StringWriter();
            ex.printStackTrace(new PrintWriter(stringWriter));
            log("设置监听失败：" + stringWriter.toString());
            ex.printStackTrace();
            return;
        }
        log("接收线程就绪..");
    }

    private void inputStreamSleep(int len) {
        try {
            for (int b1 = 0; b1 < len; ++b1) {
                Thread.sleep(1L);
                if (inputStream.available() != 0) {
                    return;
                }
            }
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void startReadTimer(int readLen) {
        if (this.timerTask == null) {
            this.timerTask = new ReadTimerTask(this);
            if (this.timer == null) {
                this.timer = new Timer();
            }
            this.timer.schedule(this.timerTask, 200L, 200L);
        }

        this.len += readLen;
    }

    private void cancelTimerTask() {
        if (this.timerTask != null) {
            timerTask.cancel();
        }
        this.timerTask = null;
        this.len = 0;
    }

    private void cleanAndClone(byte[] byte1Arrays, byte[] byte2Arrays) {
        this.showByteArray((byte[]) byte1Arrays.clone());
        if (byte2Arrays != null) {
            this.cleanByteArray(byte2Arrays);
        }
    }

    private void showByteArray(final byte[] bytes) {
        if (this.bluetoothListener != null) {
            ((Activity) this.context).runOnUiThread(new Runnable() {
                public void run() {
                    BleUtil.this.bluetoothListener.showByteArray(bytes, BleUtil.this.p);
                }
            });
        }
    }

    private void destroy() {
        aBoolean = false;
        p = null;
        closeThread();
        try {
            context.unregisterReceiver(disConnectReceiver);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            if (outputStream != null)
                outputStream.close();
            outputStream = null;
            if (inputStream != null)
                inputStream.close();
            inputStream = null;
            if (bluetoothSocket != null)
                bluetoothSocket.close();
            bluetoothSocket = null;
            log("成功断开蓝牙");
            try {
                if (bluetoothSocket != null)
                    bluetoothSocket.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } catch (IOException iOException) {
            StringWriter stringWriter = new StringWriter();
            log("断开蓝牙失败...", bluetoothDeviceMap + "");
            iOException.printStackTrace();
            iOException.printStackTrace(new PrintWriter(stringWriter));
            log("断开蓝牙失败: " + stringWriter.toString(), "e");
            try {
                if (bluetoothSocket != null)
                    bluetoothSocket.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } finally {

        }
    }

    private void closeThread() {

        if (socketWriteThread != null) {
            socketWriteThread.interrupt();
        }
        socketWriteThread = null;

        if (inputThread != null) {
            inputThread.interrupt();
        }
        inputThread = null;
        log("关闭线程..");
    }

    private void registerBlue() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(foundReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"), Context.RECEIVER_NOT_EXPORTED);
            context.registerReceiver(foundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED), Context.RECEIVER_NOT_EXPORTED);
            context.registerReceiver(foundReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_STARTED"), Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(foundReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
            context.registerReceiver(foundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            context.registerReceiver(foundReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_STARTED"));
        }
        log("注册广播接收器..");
    }

    private void unRegisterBlue() {
        context.unregisterReceiver(this.foundReceiver);
        log("注销广播接收器..");
    }

    private byte[] copyByteArray(byte[] byte1, byte[] byte2, int paramInt) {
        int byteLength = 0;
        if (byte1 != null) {
            byteLength = byte1.length;
        }
        byte[] arrayOfByte = new byte[byteLength + paramInt];
        if (byte1 != null) {
            System.arraycopy(byte1, 0, arrayOfByte, 0, byte1.length);
        }
        System.arraycopy(byte2, 0, arrayOfByte, byteLength, paramInt);
        return arrayOfByte;
    }

    private void cleanByteArray(byte[] arrayOfByte) {
        for (int b1 = 0; b1 < arrayOfByte.length; ++b1) {
            arrayOfByte[b1] = 0;
        }
    }

    private int getArrayLen(byte[] arrayOfByte) {
        return arrayOfByte == null ? 0 : arrayOfByte.length;
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = (new AlertDialog.Builder(context, 5))
                .setTitle("提示")
                .setMessage("请前往打开手机的位置权限!")
                .setCancelable(false);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int var2) {
                Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                ((Activity) context).startActivityForResult(intent, 10);
            }
        };
        builder.setPositiveButton("确定", listener).show();
    }

    private void log(String msg) {
        Log.d("AppRunClassicManage", msg);
        if (bluetoothListener != null) {
            bluetoothListener.log(BleUtil.class.getSimpleName(), msg, "d");
        }
    }

    private void log(String msg, String tag) {
        if (tag.equals("e")) {
            Log.e("AppRunClassicManage", msg);
        } else {
            Log.w("AppRunClassicManage", msg);
        }

        if (bluetoothListener != null) {
            bluetoothListener.log(BleUtil.class.getSimpleName(), msg, tag);
        }

    }

    @SuppressLint("MissingPermission")
    public void startDiscovery(IScanCallback scanCallback) {
        if (scanCallback == null) {
            this.scanCallback = scanCallback;
        }

        if (bool) {
            log("操作扫描...");
            registerBlue();
            cleanData();
            bluetoothAdapter.startDiscovery();
            bool = false;
            handler.postDelayed(new stopScanRunnable(this), 10000L);
        }

    }

    @SuppressLint("MissingPermission")
    public void stop() {
        if (!this.bool) {
            this.log("主动停止..");
            this.handler.removeMessages(0);
            this.bluetoothAdapter.cancelDiscovery();
            this.unRegisterBlue();
            this.bool = true;
        }
    }

    public void beginConnectBlue(String var1, BluetoothListener var2) {
        this.bluetoothListener = var2;
        this.p = var1;
        log("开始连接2.0蓝牙，地址是：" + var1, "w");
        this.stopScanBlue(this.bluetoothAdapter.getRemoteDevice(var1));
    }

    public void addByte(byte[] var1) {
        if (this.bluetoothSocket == null) {
            Toast.makeText(this.context, "请连上蓝牙再发送数据", Toast.LENGTH_SHORT).show();
        } else {
            this.byteList.add(var1);
        }
    }

    public void a() {
        this.destroy();
    }

    public String c() {
        return this.p;
    }

    public List<DeviceModule> getDeviceModuleList() {
        return deviceModuleList;
    }

    @SuppressLint("MissingPermission")
    public boolean isBluetoothEnable() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            return true;
        } else {
            Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            ((Activity) context).startActivityForResult(intent, 1);
            registerBlueStateChange();
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    private void stopScanBlue(final BluetoothDevice var1) {
        try {
            if (!this.bool) {
                scanCallback.addDeviceModule(null);
                log("停止扫描蓝牙");
            }
            log("2.0蓝牙的UUID是:00001101-0000-1000-8000-00805F9B34FB", "w");
            if (var1.getBondState() != 12) {
                try {
                    PairingRequestReceiver receiver = new PairingRequestReceiver(context, new PairingRequestReceiver.ConnectFinishListener() {
                        @Override
                        public void connectFinish() {
                            log("配对完成，回调连接..");
                            connectDevice(var1);
                        }
                    });
                    ReflectUtil.createBond(var1.getClass(), var1);
                    IntentFilter intentFilter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
                    intentFilter.setPriority(Integer.MAX_VALUE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
                    } else {
                        context.registerReceiver(receiver, intentFilter);
                    }
                } catch (Exception exception) {
                    log("" + exception.toString(), "e");
                    exception.printStackTrace();
                }
            } else {
                log("出现错误信息,内容: ", "e");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter));
            Toast.makeText(this.context, "连接失败！", Toast.LENGTH_SHORT).show();
            this.log("建立socket失败：" + exception.toString(), "e");
            if (bluetoothListener != null) {
                String str = this.p;
                bluetoothListener.blueConnectFail(str, exception.toString());
            }
        }

    }

    class ReadTimerTask extends TimerTask {

        BleUtil bluetoothUtil;

        public ReadTimerTask(BleUtil bluetoothUtil) {
            this.bluetoothUtil = bluetoothUtil;
        }

        public void run() {
            ((Activity) (bluetoothUtil.context)).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bluetoothUtil.bluetoothListener.readLength(BleUtil.this.len * 5);
                    bluetoothUtil.len = 0;
                }
            });
        }
    }

    class writeStreamRunnable implements Runnable {

        public writeStreamRunnable(BleUtil this$0) {

        }

        public void run() {
            while (BleUtil.this.aBoolean) {
                if (BleUtil.this.byteList.size() > 0) {
                    OutputStream outputStream = BleUtil.this.outputStream;
                    if (outputStream == null) {
                        try {
                            BleUtil.this.outputStream = BleUtil.this.bluetoothSocket.getOutputStream();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    int len;
                    byte[] bytes = (byte[]) BleUtil.this.byteList.get(0);
                    if (bytes != null) {
                        len = bytes.length;
                        if (len > 0) {
                            try {
                                BleUtil.this.outputStream.write(bytes);
                                BleUtil.this.bluetoothListener.writeLength(bytes.length);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        BleUtil.this.outputStream.flush();
                        BleUtil.this.byteList.remove(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    len = ParametersUtil.getOutputStreamLen();
                    if (len <= 0) {
                        continue;
                    }
                    long sleepTime = (long) (len * 10);
                    try {
                        Thread.sleep(sleepTime);
                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class BlueStateChangeReceiver extends BroadcastReceiver {

        BleUtil bluetoothUtil;

        public BlueStateChangeReceiver(BleUtil bluetoothUtil) {
            this.bluetoothUtil = bluetoothUtil;
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")
                    && intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0) == 12) {
                log("注销广播..");
                context.unregisterReceiver(BleUtil.this.blueStateChangeReceiver);
                if (!DeviceModelUtil.isGpsAndNetworkEnable(bluetoothUtil.context)) {
                    BleUtil.this.showLocationDialog();
                }
            }
        }
    }

    class DisconnectedReceiver extends BroadcastReceiver {

        BleUtil bluetoothUtil;

        public DisconnectedReceiver(BleUtil bluetoothUtil) {
            this.bluetoothUtil = bluetoothUtil;
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("android.bluetooth.device.action.ACL_DISCONNECTED") && bluetoothUtil.bluetoothListener != null) {
                log("监听到蓝牙断线", "e");
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                String address = null;
                if (bluetoothDevice != null) {
                    address = bluetoothDevice.getAddress();
                } else {
//                    address = a.a.a.c.a.t(this.a);
//                    address = a.t(this.a);
                }
                bluetoothUtil.bluetoothListener.disconnectedAddress(address);
            }
        }
    }

    class FindDeviceReceiver extends BroadcastReceiver {

        BleUtil bluetoothUtil;

        public FindDeviceReceiver(BleUtil bluetoothUtil) {
            this.bluetoothUtil = bluetoothUtil;
        }

        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.device.action.FOUND".equals(intent.getAction())) {
                short rssi = 10;
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (bluetoothDevice == null)
                    return;
                if (intent.getExtras() != null)
                    rssi = intent.getExtras().getShort("android.bluetooth.device.extra.RSSI");
                if (bluetoothDevice.getBondState() != 12) {
                    bluetoothUtil.addDeviceModule(bluetoothDevice, false, rssi);
                } else {
                    bluetoothUtil.addDeviceModule(bluetoothDevice, true, rssi);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                if (deviceModuleList.size() == 0) {
                    deviceModuleList.add(new DeviceModule("没有找到新设备", null));
                }
                if (scanCallback != null) {
                    scanCallback.addDeviceModule();
                }
                log("搜索完成", "e");
            }
        }
    }

    class stopScanRunnable implements Runnable {

        BleUtil util;

        public stopScanRunnable(BleUtil a) {
            this.util = a;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            boolean bool;
            StringBuilder stringBuilder = (new StringBuilder()).append("时间到，停止扫描,mIScanCallback: ");
            if (BleUtil.this.scanCallback == null) {
                bool = true;
            } else {
                bool = false;
            }
            util.log(stringBuilder.append(bool).toString(), "e");
            util.bluetoothAdapter.cancelDiscovery();
            util.bool = true;
            util.unRegisterBlue();
            if (BleUtil.this.scanCallback != null) {
                BleUtil.this.scanCallback.addDeviceModule();
            }
        }
    }
}