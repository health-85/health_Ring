package com.sdk.satwatch.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sdk.satwatch.util.PermissionUtil;
import com.sdk.satwatch.R;
import com.sdk.satwatch.util.RxUtil;
import com.sdk.satwatch.util.ScanDevice;
import com.sdk.satwatch.listener.ScannerListener;
import com.sdk.satwatch.adapter.LeDeviceListAdapter;
import com.sdk.satwatch.util.Utils;
import com.sw.watches.bean.DeviceModule;
import com.sw.watches.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.qqtheme.framework.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class ScanDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView scan_start, scan_stop;

    private ListView device_list;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    private ScanDevice scanDevice;

    private ProgressBar bar;

    private int ScanDeviceTime = 30 * 1000;

    private ArrayList<DeviceModule> mDeviceModuleList = new ArrayList<>();

    public final static int MESSAGE_BLE_SCANF = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scand_device);
        setTitle("ScanDeviceActivity");

        initBluetoothPermission();
        initView();
        initSetAdapter();

        mDeviceModuleList.clear();

//        openBleService();

        scanDevice = ScanDevice.getInstance(this);

        scanDevice.removeScanListener(scannerListener);
        scanDevice.addScanListener(scannerListener);

        ScanDevice(true);

        startAddAdapter();
    }

    private void startAddAdapter() {
        Observable.interval(1500, TimeUnit.MILLISECONDS)
                .compose(RxUtil.IoToMainObserve())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mLeDeviceListAdapter.addAllData(mDeviceModuleList);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                });
    }

    ScannerListener scannerListener = new ScannerListener() {
        @Override
        public void reconnectDevice() {
            LogUtil.i("ScannerListener", "reconnectDevice");
            scanDevice.startScan();
        }

        @Override
        public void scanStarted() {
            if (bar != null) bar.setVisibility(View.VISIBLE);
            scan_start.setEnabled(false);
            scan_stop.setEnabled(true);
            LogUtil.i("ScannerListener", "scanStarted");
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onFoundScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            synchronized (this) {
                if (bluetoothDevice != null) {
                    DeviceModule deviceModule = new DeviceModule(bluetoothDevice.getName(), bluetoothDevice, rssi);
                    if (mLeDeviceListAdapter.isRepeat(mDeviceModuleList, deviceModule)) {
                        mDeviceModuleList.add(deviceModule);
                    }
                }
            }
        }

        @Override
        public void scanStoped() {
            LogUtil.i("ScannerListener", "scanStoped");
            scan_start.setEnabled(true);
            scan_stop.setEnabled(false);
        }
    };

    /**
     * 申请蓝牙权限
     *
     * @return
     */
    public boolean initBluetoothPermission() {
        if (PermissionUtil.checkSelfPermissions(ScanDeviceActivity.this, PermissionUtil.getInstance().getBluetoothPerm())) {
            return true;
        } else {
//            if (PermissionUtil.isShowBluetoothPerRationale(ScanDeviceActivity.this)) {
//                PermissionUtil.startPerSettingActivity(ScanDeviceActivity.this);
//            } else {
            PermissionUtil.requestPermissions(ScanDeviceActivity.this, PermissionUtil.PERMISSION_BLUETOOTH_CODE,
                    PermissionUtil.getInstance().getBluetoothPerm());
//            }
        }
        return false;
    }

    void initView() {
        device_list = (ListView) findViewById(R.id.device_list);
        bar = (ProgressBar) findViewById(R.id.bar);
        scan_start = (TextView) findViewById(R.id.scan_start);
        scan_stop = (TextView) findViewById(R.id.scan_stop);
        scan_start.setOnClickListener(this);
        scan_stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_start:
                ScanDevice(true);
                break;
            case R.id.scan_stop:
                ScanDevice(false);
                break;
        }
    }


    /**
     * @param scan true = 开始扫描
     *             false = 停止扫描
     */
    void ScanDevice(boolean scan) {
        if (scan) {
            if (mLeDeviceListAdapter != null) {
                mLeDeviceListAdapter.clear();
            }
            scanDevice.startScan();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bar != null) bar.setVisibility(View.GONE);
                    scanDevice.stopScan();
                    scan_start.setEnabled(true);
                    scan_stop.setEnabled(false);
                }
            }, ScanDeviceTime);
        } else {
            if (bar != null) bar.setVisibility(View.GONE);
            mDeviceModuleList.clear();
            scanDevice.stopScan();
        }
    }

    void initSetAdapter() {
        mLeDeviceListAdapter = new LeDeviceListAdapter(ScanDeviceActivity.this);
        device_list.setAdapter(mLeDeviceListAdapter);
        device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                ScanDevice(false);
                DeviceModule deviceModule = mLeDeviceListAdapter.getDevice(position);
                Toast.makeText(ScanDeviceActivity.this, "DeviceAddress = " + deviceModule.getMac(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ScanDeviceActivity.this, BraceletActivity.class);
                intent.putExtra(BraceletActivity.EXTRA_DEVICE, deviceModule);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScanDevice(false);
        if (scanDevice != null) {
            scanDevice.removeScanListener(scannerListener);
        }
    }
}
