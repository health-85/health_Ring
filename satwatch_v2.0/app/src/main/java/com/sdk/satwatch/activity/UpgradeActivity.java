package com.sdk.satwatch.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;

import com.realsil.sdk.core.logger.ZLogger;
import com.realsil.sdk.core.utility.FileUtils;
import com.realsil.sdk.support.file.RxFiles;
import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.R;
import com.sdk.satwatch.listener.ScannerListener;
import com.sdk.satwatch.util.RxUtil;
import com.sdk.satwatch.util.SPUtil;
import com.sdk.satwatch.util.ScanDevice;
import com.sdk.satwatch.util.SpConfig;
import com.sdk.satwatch.util.Utils;
import com.sw.watches.bean.DeviceModule;
import com.sw.watches.listener.ConnectorListener;
import com.sw.watches.listener.UpgradeDeviceListener;
import com.sw.watches.service.ZhBraceletService;
import com.sw.watches.util.LogUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class UpgradeActivity extends AppCompatActivity implements ScannerListener {

    private static final String TAG = "UpgradeActivity";

    private TextView tvMsg;
    private StringBuilder builder = new StringBuilder();
    private ZhBraceletService mBleService = MyApplication.getZhBraceletService();

    private boolean isStartUpgrade = false;
    private String mFilePath;

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_upgrade);
        tvMsg = findViewById(R.id.tv_msg);
        mBleService.initC100Sdk(MyApplication.getApp());
        ScanDevice.getInstance(this).addScanListener(this);
        mBleService.addConnectorListener(mConnectorListener);
        mBleService.addUpgradeDeviceListener(new UpgradeDeviceListener() {
            @Override
            public void onUpgradeDeviceError(int i1, int i2, String str) {
                LogUtil.i(" upgradeDevice onUpgradeDeviceError " + str);
                builder.append("升级错误: " + str + "\n");
                tvMsg.setText(builder.toString());
                ScanDevice.getInstance(UpgradeActivity.this).startScan();
            }

            @Override
            public void onUpgradeDeviceProgress(int progress) {
                LogUtil.i(" upgradeDevice progress " + progress);
                if(0==progress)
                    builder.append("升级进度：" + progress + "\n");
                else
                {
                    int startIndex = builder.indexOf("升级进度：");
                    builder.delete(startIndex, builder.length());
                    builder.append("升级进度：" + progress + "\n");
                }

                tvMsg.setText(builder.toString());
            }

            @Override
            public void onUpgradeDeviceCompleted() {
                LogUtil.i(" upgradeDevice Completed ");
                isStartUpgrade = false;
                builder.append("升级完成" + "\n");
                tvMsg.setText(builder.toString());
                ScanDevice.getInstance(UpgradeActivity.this).startScan();
            }

            @Override
            public void onUpgradeDeviceStarting(int start) {

            }

            @Override
            public void onUpgradeDeviceTip(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        builder.append(message + "\n");
                        tvMsg.setText(builder.toString());
                    }
                });
            }

            @Override
            public void onReConnectUpdateDevice(String s, boolean start) {

            }
        });
    }

    private ConnectorListener mConnectorListener = new ConnectorListener() {
        @Override
        public void onConnectAndWrite() {
            Utils.MyLog("Tag", "连接");
        }

        @Override
        public void onDisconnect() {
            Utils.MyLog("Tag", "已断开");
        }

        @Override
        public void onConnectFailed() {
            Utils.MyLog("Tag", "连接失败");
        }

        @Override
        public void onReConnect() {
            Utils.MyLog("Tag", "重新连接，升级时会调用");
            Observable.just("")
                    .delay(3, TimeUnit.SECONDS)
                    .compose(RxUtil.IoToMainObserve())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            ScanDevice.getInstance(UpgradeActivity.this).startScan();
                        }
                    });
        }

        @Override
        public void onConnectSuccess() {
            Utils.MyLog("Tag", "连接成功");
            Observable.just("")
                    .delay(3, TimeUnit.SECONDS)
                    .compose(RxUtil.IoToMainObserve())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (isStartUpgrade) {
                                update();
                            }
                        }
                    });
        }
    };

    @Override
    public void onDestroy() {
        if (mBleService != null) {
            mBleService.removeUpgradeDeviceListener();
            mBleService.removeConnectorListener(mConnectorListener);
        }
        ScanDevice.getInstance(this).removeScanListener(this);
        super.onDestroy();
    }

    public void cleanLog(View view) {
        builder.delete(0, builder.toString().length());
        tvMsg.setText("");
    }

    public void sleFile(View view) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new RxFiles(this).request(Intent.ACTION_GET_CONTENT, "*/*")
                    .subscribe(s -> {
                                ZLogger.v(String.format("accept: %s, suffix:%s", s, FileUtils.getSuffix(s)));
                                mFilePath = s;
                                builder.append("文件地址：" + mFilePath + "\n");
                                System.out.println("文件地址:" +  mFilePath);
                                tvMsg.setText(builder.toString());
                            },
                            t -> {
                                ZLogger.e("onError: " + t.toString());
                                builder.append("选择文件异常：" + t.toString() + "\n");
                                tvMsg.setText(builder.toString());
                            },
                            () -> ZLogger.v("OnComplete"));
        }
    }

    public void upgrade(View view) {
        if (!hasExternalStoragePermission()) {
            openFileAccessManager();
            return;
        }

        isStartUpgrade = true;

        update();
    }

    private void update() {
//        if (mBleService == null) return;
//        if (TextUtils.isEmpty(mFilePath)) {
//            Toast.makeText(this, "请选择文件", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (mBleService.getBleName().contains("C100_")) {
//            mBleService.updateC100Device(mFilePath);
//        } else if (mBleService.getBleName().contains("F38_")) {
//            mBleService.upgradeDevice();
//        } else {
            mBleService.updateDeviceByFile("/storage/emulated/0/NEW_WC_52833_711_V12.zip");
//        }
    }

    public static boolean hasExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return true;
    }

    private void openFileAccessManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    @Override
    public void reconnectDevice() {

    }

    @Override
    public void scanStarted() {

    }

    @Override
    public void onFoundScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String oldMac = (String) SPUtil.getData(UpgradeActivity.this, SpConfig.BLUETOOTH_MAC, "");
        if (mBleService != null && device != null && Utils.isEqualMac(oldMac, device.getAddress())) {
            @SuppressLint("MissingPermission") DeviceModule mBleDeviceWrapper = new DeviceModule(device.getName(), device);
            mBleService.BindDevice(mBleDeviceWrapper);
            ScanDevice.getInstance(this).stopScan();
        }
    }

    @Override
    public void scanStoped() {

    }


}
