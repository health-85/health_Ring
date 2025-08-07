package com.healthy.rvigor.util

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import com.google.gson.Gson
import com.healthbit.framework.util.ToastUtil
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.bean.PermissionItem
import com.healthy.rvigor.watch.IBleScanCallBack
import com.healthy.rvigor.watch.IUpgradeDeviceListener
import com.healthy.rvigor.watch.IWatchConnectingListener
import com.healthy.rvigor.watch.IWatchFunctionDataCallBack
import com.healthy.rvigor.watch.WatchBase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.util.concurrent.TimeUnit


/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/7 16:20
 * @UpdateRemark:
 */
class BleUtils {

    //扫描时间
    private var mScanTime: Long = 0

    private var mAdapter: BluetoothAdapter? = null

    //权限
    private val pms = mutableListOf<PermissionItem>()

    //扫描定时
    private var mStartAgainDis: Disposable? = null

    //扫描结果
    private var mScanResult: String? = null

    /**
     * 当前升级进度
     */
    private var upgradeDeviceProgressPercent = 0

    /**
     * 当前正在链接的手表
     */
    private var connectionWatch: WatchBase? = null

    /**
     * 扫描回调集合
     */
    private val leScanCallbacks = mutableListOf<IBleScanCallBack>()
    /**
     * 链接回调集合
     */
    private val watchDataCallBacks = mutableListOf<IWatchFunctionDataCallBack>()

    /**
     * 设备链接集合
     */
    private val connectingListeners = mutableListOf<IWatchConnectingListener>()

    /**
     * 设备升级回调
     */
    private val upgradeDeviceListeners = mutableListOf<IUpgradeDeviceListener>()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //android12以上
            pms.clear()
            pms.add(PermissionItem(Manifest.permission.BLUETOOTH_SCAN, "蓝牙扫描"))
            pms.add(
                PermissionItem(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    "蓝牙链接"
                )
            )
        }
    }

    /**
     * 是否支持蓝牙
     *
     * @return
     */
    fun isSupportBle(): Boolean {
        return MyApplication.instance().packageManager
            .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    /**
     * 蓝牙是否已经打开
     *
     * @return
     */
    fun isBleEnable(): Boolean {
        if (mAdapter == null){
            mAdapter = BluetoothAdapter.getDefaultAdapter()
        }
        return if (mAdapter == null) {
            false
        } else mAdapter?.isEnabled ?: false
    }

    /**
     * 打开蓝牙
     */
    @SuppressLint("MissingPermission")
    fun openBLE(): Boolean {
        if (mAdapter == null){
            mAdapter = BluetoothAdapter.getDefaultAdapter()
        }
        return if (mAdapter == null) {
            false
        } else try {
            mAdapter!!.enable()
        } catch (e: Exception) {
            false
        }
    }

//    //是否支持
//    fun isSupportBle(context: Context?): Boolean {
//        if (context == null || !context.packageManager
//                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
//        ) {
//            return false
//        }
//        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        return manager.adapter != null
//    }
//
//    //是否开启
//    fun isBleEnable(context: Context): Boolean {
//        if (!isSupportBle(context)) {
//            return false
//        }
//        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        return manager.adapter.isEnabled
//    }

    /**
     * 是否拥有扫描权限及发现附近设备的权限  主要是针对android12以后
     *
     * @return
     */
    private fun hasBluScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //android12以上
            AppUtils.hasPermissions(MyApplication.instance(), pms)
        } else true
    }

    /**
     * 开始是扫描蓝牙设备  需在UI线程中操作
     */
    @SuppressLint("MissingPermission")
    fun startScan(): Boolean {
        if (!hasBluScanPermission()) {
            return false
        }
        if (!isSupportBle()) {
            ToastUtil.showToast(
                MyApplication.instance(),
                MyApplication.instance().resources.getString(R.string.not_support_bluetooth)
            )
            return false
        }
        if (!isBleEnable()) {
            ToastUtil.showToast(
                MyApplication.instance(),
                MyApplication.instance().resources.getString(R.string.bluetooth_not_turned_on)
            )
            return false
        }
        LogUtils.i(" startScan ")
        mScanTime = System.currentTimeMillis()
        doneLeScanStartCallback()

        BluetoothLeScannerCompat.getScanner().stopScan(mNorScanCallback)
//        startScanTimeCount()

        val settings: ScanSettings = ScanSettings.Builder()
//            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//            .setReportDelay(5000)
//            .setUseHardwareBatchingIfSupported(true)
            .build()
        val filters: MutableList<ScanFilter> = ArrayList()
        filters.add(
            ScanFilter.Builder()
                .build()
        )
        BluetoothLeScannerCompat.getScanner()
            .startScan(filters, settings, mNorScanCallback)
        return true
    }

    //默认搜索时长为1分钟，1分钟结束后仍未搜索出任何设备，跳转搜索失败页
    private fun startScanTimeCount() {
        if (mStartAgainDis != null) {
            mStartAgainDis?.dispose()
            mStartAgainDis = null
        }
        mStartAgainDis = Observable.timer(1, TimeUnit.MINUTES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (!TextUtils.isEmpty(mScanResult)) {
                    doneLeScanStopCallback()
                    BluetoothLeScannerCompat.getScanner().stopScan(mNorScanCallback)
                }
//                if (TextUtils.isEmpty(mScanResult)) {
//                    BluetoothLeScannerCompat.getScanner()
//                        .stopScan(mNorScanCallback)
//                    val settings: ScanSettings = ScanSettings.Builder()
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                        .build()
//                    val filters: MutableList<ScanFilter> =
//                        ArrayList()
//                    filters.add(
//                        ScanFilter.Builder()
//                            .build()
//                    )
//                    BluetoothLeScannerCompat.getScanner()
//                        .startScan(filters, settings, mNorScanCallback)
//                    doneLeScanStartCallback()
//                }
            }
    }

    private val mNorScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
//            LogUtils.i(" onScanResult " + Gson().toJson(result) + " indices == " + leScanCallbacks.indices)
            if (result?.device != null && !TextUtils.isEmpty(result.device.name)) {
                mScanResult = result.device.name
                LogUtils.i(" onScanResult name " + result.device.name)
                if (result.scanRecord != null) {
                    doneLeScanCallback(result.device, result.rssi, result?.scanRecord?.bytes)
                } else {
                    doneLeScanCallback(result.device, result.rssi, null)
                }
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
//            LogUtils.i(" onScanResult " + Gson().toJson(results))
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            LogUtils.i(" onScanFailed errorCode $errorCode")
            doneLeScanStopCallback()
        }
    }

    /**
     * 开始扫描回调
     */
    private fun doneLeScanStartCallback() {
        for (i in leScanCallbacks.indices) {
            leScanCallbacks[i].scanStarted()
        }
    }

    /**
     * 停止扫描回调
     */
    private fun doneLeScanStopCallback() {
        for (i in leScanCallbacks.indices) {
            leScanCallbacks[i].scanStop()
        }
    }

    /**
     * 在UI线程处理回调
     *
     * @param device
     * @param rssi
     * @param scanRecord
     */
    private fun doneLeScanCallback(
        device: BluetoothDevice,
        rssi: Int,
        scanRecord: ByteArray?
    ) {
        for (i in leScanCallbacks.indices) {
            leScanCallbacks[i].onLeScan(device, rssi, scanRecord)
        }
    }

    /**
     * 注册扫描回调  UI线程
     *
     * @param callback
     */
    fun registerScanCallback(callback: IBleScanCallBack?) {
        if (!leScanCallbacks.contains(callback)) {
            leScanCallbacks.add(callback!!)
        }
    }

    /**
     * 注销回调  UI线程
     *
     * @param callback
     */
    fun unregisterScanCallback(callback: IBleScanCallBack?) {
        leScanCallbacks.remove(callback)
    }

    /**
     * 链接蓝牙设备
     *
     * @param device
     * @return
     */
    fun connect(device: BluetoothDevice?, deviceName: String?): Boolean {
        if (device == null) {
            return false
        }
        if (!isBleEnable()) {
            ToastUtil.showToast(
                MyApplication.instance(),
                MyApplication.instance().resources.getString(R.string.bluetooth_not_turned_on)
            )
            return false
        }
        connectionWatch?.close()
        MyApplication.instance().watchSyncUtils?.setOldConnectedDevice(device)
        if (MyApplication.instance().siatDeviceService != null) {
            return MyApplication.instance().siatDeviceService?.Connection(
                device,
                deviceName
            ) ?: false
        } else {
            MyApplication.instance().startSIATService()
        }
        return false
    }

    /**
     * 注册手表功能回调
     *
     * @param callBack
     */
    fun registryWatchFunctionDataCallBack(callBack: IWatchFunctionDataCallBack?) {
        if (callBack != null && !watchDataCallBacks.contains(callBack)) {
            watchDataCallBacks.add(callBack)
        }
    }

    /**
     * 注册手表功能回调
     *
     * @param callBack
     */
    fun unRegistryWatchFunctionDataCallBack(callBack: IWatchFunctionDataCallBack?) {
        watchDataCallBacks.remove(callBack)
    }

    /**
     * 接收到手表功能返回数据
     *
     * @param watch
     * @param functionname 功能名称
     * @param bean
     */
    fun performWatchDataArrived(watch: WatchBase?, functionname: String?, bean: Any?) {
        if (watchDataCallBacks != null && watchDataCallBacks.size > 0) {
            for (i in watchDataCallBacks.indices) {
                watchDataCallBacks[i].WatchDataArrived(watch, functionname, bean)
            }
        }
    }

    /**
     * 注册链接监听
     *
     * @param listener
     */
    fun registryConnectingListener(listener: IWatchConnectingListener) {
        if (!connectingListeners.contains(listener)) {
            connectingListeners.add(listener)
        }
    }

    /**
     * 反注册事件
     *
     * @param listener
     */
    fun unRegistryConnectingListener(listener: IWatchConnectingListener) {
        connectingListeners.remove(listener)
    }


    /**
     * 获取当前链接的设备  须在UI线程调用此方法
     *
     * @return
     */
    fun getConnectionWatch(): WatchBase? {
        return connectionWatch
    }

    fun cleanConnectionWatch() {
        connectionWatch = null
    }

    /**
     * 派遣设备链接事件
     *
     * @param watchBase
     * @param connState //0 链接成功并发送命令  1 链接失败  2 断开链接  3开始连接  4重新链接 5链接成功
     */
    fun perfermConnectingEvent(watchBase: WatchBase?, connState: Int) {
        if (connState == 3) { //断开链接
            for (i in connectingListeners.indices) {
                connectingListeners[i].onConnectingStart(watchBase)
            }
        }
        if (connState == 0) { //链接成功
            if (watchBase != null && !AppUtils.isDfuDevice(watchBase.deviceName)) {
                if (connectionWatch != null) {
                    connectionWatch!!.close()
                }
                connectionWatch = watchBase
            }
            stopScan()
            if (connectingListeners != null && connectingListeners.size > 0) {
                for (i in connectingListeners.indices) {
                    connectingListeners[i].onConnectedAndWrite(watchBase)
                }
            }
        } else {
            connectionWatch = null //当前链接的手表
            LogUtils.i(" perfermConnectingEvent watchBase $connState")
        }
        if (connState == 1) { //链接失败
            for (i in connectingListeners.indices) {
                connectingListeners[i].onConnectFailed(watchBase)
            }
        }
        if (connState == 2) { //断开链接
            for (i in connectingListeners.indices) {
                connectingListeners[i].onDisconnect(watchBase)
            }
        }
        if (connState == 4) { //重新链接
            for (i in connectingListeners.indices) {
                connectingListeners[i].onReConnect(watchBase)
            }
        }
        if (connState == 5) { //链接成功
            if (watchBase != null && AppUtils.isDfuDevice(watchBase.deviceName)) {
                if (connectionWatch != null) {
                    connectionWatch!!.close()
                }
                connectionWatch = watchBase
                stopScan()
            }
            for (i in connectingListeners.indices) {
                connectingListeners[i].onConnectSuccess(watchBase)
            }
        }
    }

    /**
     * 注册设备升级回调
     *
     * @param upgradeDeviceListener
     */
    fun registryUpgradeDeviceListener(upgradeDeviceListener: IUpgradeDeviceListener?) {
        if (upgradeDeviceListener != null && !upgradeDeviceListeners.contains(upgradeDeviceListener)) {
            upgradeDeviceListeners.add(upgradeDeviceListener)
        }
    }

    /**
     * 反注册设备升级回调
     *
     * @param upgradeDeviceListener
     */
    fun unRegistryUpgradeDeviceListener(upgradeDeviceListener: IUpgradeDeviceListener?) {
        upgradeDeviceListeners.remove(upgradeDeviceListener)
    }


    /**
     * 派遣升级开始事件
     */
    fun performUpgradeDeviceStarting(state: Int) {
        upgradeDeviceProgressPercent = 0 //升级进度
        for (i in upgradeDeviceListeners.indices) {
            upgradeDeviceListeners.get(i).onUpgradeDeviceStarting(state)
        }
    }

    fun performUpgradeDeviceTip(tip: String?) {
        upgradeDeviceProgressPercent = 0 //升级进度
        for (i in upgradeDeviceListeners.indices) {
            upgradeDeviceListeners.get(i).onUpgradeDeviceTip(tip)
        }
    }

    /**
     * 派遣事件开始
     *
     * @param percent
     */
    fun performUpgradeDeviceProgress(percent: Int) {
        upgradeDeviceProgressPercent = percent
        for (i in upgradeDeviceListeners.indices) {
            upgradeDeviceListeners.get(i).onUpgradeDeviceProgress(percent)
        }
    }

    /**
     * 搜索设备
     *
     * @param s
     */
    fun performOnReConnectUpdateDevice(s: String?, start: Boolean) {
        for (i in upgradeDeviceListeners.indices) {
            upgradeDeviceListeners.get(i).onReConnectUpdateDevice(s, start)
        }
    }


    /**
     * 升级错误
     *
     * @param param1
     * @param param2
     * @param errorString
     */
    fun performUpgradeDeviceError(param1: Int, param2: Int, errorString: String?) {
        if (upgradeDeviceProgressPercent >= 100) { //如果大于等于100就代表升级成功
            return
        }
        for (i in upgradeDeviceListeners.indices) {
            upgradeDeviceListeners.get(i).onUpgradeDeviceError(param1, param2, errorString)
        }
        MyApplication.instance().restartSIATService()
    }


    /**
     * 升级完成
     */
    fun performUpgradeDeviceCompleted() {
        for (i in upgradeDeviceListeners.indices) {
            upgradeDeviceListeners.get(i).onUpgradeDeviceCompleted()
        }
        MyApplication.instance().restartSIATService()
    }

    fun stopScan() {
        if (!hasBluScanPermission()) {
            return
        }
        try {
            BluetoothLeScannerCompat.getScanner().stopScan(mNorScanCallback)
            doneLeScanStopCallback()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 断开连接、解绑设备
     */
    fun disConnecting() {
        if (connectionWatch != null) {
            connectionWatch?.close()
        } else {
            if (MyApplication.instance().siatDeviceService != null) {
                MyApplication.instance().siatDeviceService?.UnBindDevice()
            }
        }
    }

    //断开连接
    fun disConnect() {
        try {
            if (MyApplication.instance().siatDeviceService != null) {
                MyApplication.instance().siatDeviceService?.disconnect()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}