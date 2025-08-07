package com.healthy.rvigor.mvp.view.activity.scan

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.adapter.ScanAdapter
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.databinding.ActivityScanBinding
import com.healthy.rvigor.event.WatchBindEvent
import com.healthy.rvigor.mvp.contract.IScanContract
import com.healthy.rvigor.mvp.presenter.ScanPresenter
import com.healthy.rvigor.mvp.view.activity.main.MainActivity
import com.healthy.rvigor.util.AppUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.util.WatchBeanUtil
import com.healthy.rvigor.view.ConfirmTipsView
import com.healthy.rvigor.watch.IBleScanCallBack
import com.healthy.rvigor.watch.IWatchConnectingListener
import com.healthy.rvigor.watch.WatchBase
import com.permissionx.guolindev.PermissionX
//import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/7 11:10
 * @UpdateRemark:   扫描设备
 */
class ScanActivity : BaseMVPActivity<ActivityScanBinding, ScanPresenter>(), IScanContract.View,
    View.OnClickListener {

    //1 添加设备 2 扫描设备 3 扫描失败
    private var scanState: Int = 0

    private var mAdapter: ScanAdapter? = null

    private var mDisposable: Disposable? = null

    //再次绑定
    private var mAgainBindDis: Disposable? = null

//    //跳转首页
//    private var isStartMain = false

    private var isAgainBind = false
    private var mSelScanDevice: BluetoothDevice? = null

    private val mSearchDeviceMap = mutableMapOf<String, BluetoothDevice>()

    override fun getLayoutResID(): Int {
        return R.layout.activity_scan
    }

    override fun createPresenter(): ScanPresenter {
        return ScanPresenter()
    }

    @SuppressLint("MissingPermission")
    override fun initView() {
        super.initView()

        MyApplication.instance().bleUtils.registerScanCallback(mBleScanCallBack)
        MyApplication.instance().bleUtils.registryConnectingListener(mConnectingListener)
        if (MyApplication.instance().siatDeviceService == null) {
            MyApplication.instance().startSIATService()
        }

        mAdapter = ScanAdapter()
        binding?.rvList?.layoutManager = LinearLayoutManager(this@ScanActivity)
        binding?.rvList?.adapter = mAdapter
        mAdapter?.addOnItemChildClickListener(
            R.id.fl_bind
        ) { adapter, view, position ->
            isAgainBind = false
            mSelScanDevice = adapter.getItem(position)
            MyApplication.instance().bleUtils.stopScan()
            MyApplication.instance().bleUtils.connect(
                mSelScanDevice,
                mSelScanDevice?.name
            )
        }

        binding?.imgBack?.setOnClickListener(this@ScanActivity)
        binding?.tvAddDevice?.setOnClickListener(this@ScanActivity)
        binding?.tvResearch?.setOnClickListener(this@ScanActivity)
    }

    override fun initData(intent: Intent?) {
        super.initData(intent)
//        if (intent != null) {
//            isStartMain = intent.getBooleanExtra(Constants.EXTRA_BOOLEAN, false)
//        }
        scanState = 1
        setScanState()
    }

    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }

            R.id.tv_add_device -> {
                startAddDevice()
            }

            R.id.tv_research -> {
                scanState = 2
                setScanState()
                MyApplication.instance().bleUtils?.startScan()
                timeCount()
            }
        }
    }

    //添加设备
    @SuppressLint("CheckResult")
    private fun startAddDevice() {

        PermissionX.init(this).permissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        ).request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                scanState = 2
                setScanState()
                MyApplication.instance().bleUtils?.startScan()
                timeCount()
            } else {
                ConfirmTipsView(activity).showDialog(
                    resources.getString(R.string.authorize_bluetooth),
                    resources.getString(R.string.authorize_bluetooth_tip),
                    resources.getString(R.string.cancel),
                    resources.getString(R.string.open)
                ) { _, isConfirm ->
                    if (isConfirm) {
                        AppUtils.goIntentSetting(this@ScanActivity)
                    }
                }
            }
        }

//        var perObservable: Observable<Boolean>?
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            perObservable = RxPermissions(this@ScanActivity).request(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.BLUETOOTH_SCAN,
//                Manifest.permission.BLUETOOTH_CONNECT
//            )
//        } else {
//            perObservable = RxPermissions(this@ScanActivity).request(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.READ_PHONE_STATE
//            )
//        }
//        perObservable
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                if (it) {
//                    scanState = 2
//                    setScanState()
//                    MyApplication.instance().bleUtils?.startScan()
//                    timeCount()
//                } else {
//                    ConfirmTipsView(activity).showDialog(
//                        resources.getString(R.string.authorize_bluetooth),
//                        resources.getString(R.string.authorize_bluetooth_tip),
//                        resources.getString(R.string.cancel),
//                        resources.getString(R.string.open)
//                    ) { _, isConfirm ->
//                        if (isConfirm) {
//                            AppUtils.goIntentSetting(this@ScanActivity)
//                        }
//                    }
//                }
//            }
    }

    //设置扫描View
    private fun setScanState() {
        binding?.tvScaningTitle?.text = resources.getString(R.string.scanning_device)
        binding?.clAddDevice?.visibility = if (scanState == 1) View.VISIBLE else View.GONE
        binding?.clScaning?.visibility = if (scanState == 2) View.VISIBLE else View.GONE
        binding?.clScanFail?.visibility = if (scanState == 3) View.VISIBLE else View.GONE
    }

    //一分钟倒计时
    private fun timeCount() {
        if (mDisposable != null) {
            mDisposable?.dispose()
            mDisposable = null
        }
        mDisposable = Observable.timer(1, TimeUnit.MINUTES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                MyApplication.instance().bleUtils?.stopScan()
            }
    }

    private val mBleScanCallBack: IBleScanCallBack = object : IBleScanCallBack {

        override fun scanStarted() {
            scanState = 2
            setScanState()
            binding?.scanProgress?.indeterminateDrawable = ContextCompat.getDrawable(
                this@ScanActivity,
                R.drawable.bg_scan_watch_progress
            )
            binding?.scanProgress?.progressDrawable = ContextCompat.getDrawable(
                this@ScanActivity,
                R.drawable.bg_scan_watch_progress
            )
            mAdapter?.submitList(mutableListOf())
        }

        @SuppressLint("MissingPermission")
        override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
            if (device != null && !TextUtils.isEmpty(device?.name) && !mSearchDeviceMap.containsKey(
                    device.address
                ) && WatchBeanUtil.isSpecDevice(device)
            ) {
                mSearchDeviceMap[device.address] = device
                mAdapter?.add(device)
            }
        }

        override fun scanStop() {
            binding?.scanProgress?.indeterminateDrawable =
                ContextCompat.getDrawable(this@ScanActivity, R.drawable.bg_scan_watch_stop)
            binding?.scanProgress?.progressDrawable =
                ContextCompat.getDrawable(this@ScanActivity, R.drawable.bg_scan_watch_stop)
            if (mAdapter?.itemCount == 0) {
                scanState = 3
                setScanState()
            }
        }

    }

    private val mConnectingListener: IWatchConnectingListener = object : IWatchConnectingListener {

        override fun onConnectingStart(watch: WatchBase?) {
            LogUtils.i("onConnectingStart")
            mAdapter?.bindName = watch?.deviceName
            mAdapter?.notifyDataSetChanged()
            binding?.tvScaningTitle?.text = resources.getString(R.string.binding)
        }

        override fun onConnectedAndWrite(watch: WatchBase?) {
            LogUtils.i("onConnectedAndWrite")
//            if (!TextUtils.isEmpty(watch?.deviceName)) {
//                if (!AppUtils.isDfuDevice(watch?.deviceName)) {
//                    SPUtil.saveData(
//                        this@ScanActivity,
//                        SpConfig.SMART_DEVICE,
//                        watch?.deviceName
//                    )
//                    SPUtil.saveData(
//                        this@ScanActivity,
//                        SpConfig.SMART_DEVICE_ADDRESS,
//                        watch?.deviceMacAddress
//                    )
//                } else {
//                    SPUtil.saveData(
//                        this@ScanActivity,
//                        SpConfig.DFU_SMART_DEVICE,
//                        watch?.deviceName
//                    )
//                }
//            }
//            showToast(resources.getString(R.string.connection_successfully))
//            EventBus.getDefault().post(WatchBindEvent(true))
//            onBackPressed()
        }

        override fun onDisconnect(watch: WatchBase?) {
            LogUtils.i("onDisconnect")
            mAdapter?.bindName = ""
            mAdapter?.notifyDataSetChanged()
        }

        @SuppressLint("MissingPermission")
        override fun onConnectFailed(watch: WatchBase?) {
            LogUtils.i("onConnectFailed")
            if (!isAgainBind) {
                isAgainBind = true
                againBind()
            } else {
                mAdapter?.bindName = ""
                mAdapter?.notifyDataSetChanged()
                binding?.tvScaningTitle?.text = resources.getString(R.string.connection_bind_fail)
                showToast(resources.getString(R.string.connection_bind_fail))
            }

        }

        override fun onReConnect(watch: WatchBase?) {
            LogUtils.i("onReConnect")
        }

        override fun onConnectSuccess(watch: WatchBase?) {
            LogUtils.i("onConnectSuccess")
            if (TextUtils.isEmpty(watch?.deviceName)) return
            if (!AppUtils.isDfuDevice(watch?.deviceName)) {
                SPUtil.saveData(
                    this@ScanActivity,
                    SpConfig.DEVICE_NAME,
                    watch?.deviceName
                )
                SPUtil.saveData(
                    this@ScanActivity,
                    SpConfig.DEVICE_ADDRESS,
                    watch?.deviceMacAddress
                )
            } else {
                SPUtil.saveData(
                    this@ScanActivity,
                    SpConfig.DFU_SMART_DEVICE,
                    watch?.deviceName
                )
            }
            showToast(resources.getString(R.string.connection_successfully))
            EventBus.getDefault().post(WatchBindEvent(Constants.WATCH_CONNECTED))
            startActivity(Intent(this@ScanActivity, MainActivity::class.java))
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    private fun againBind() {
        MyApplication.instance().bleUtils?.disConnecting()
        if (mAgainBindDis != null) {
            mAgainBindDis?.dispose()
            mAgainBindDis = null
        }
        mAgainBindDis = Observable.timer(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                MyApplication.instance().bleUtils?.connect(
                    mSelScanDevice,
                    mSelScanDevice?.name
                )
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        MyApplication.instance().bleUtils?.stopScan()
        MyApplication.instance().bleUtils?.unRegistryConnectingListener(mConnectingListener)
        MyApplication.instance().bleUtils?.unregisterScanCallback(mBleScanCallBack)
    }
}