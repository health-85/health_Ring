package com.healthy.rvigor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.healthy.rvigor.bean.AppUserInfo
import com.healthy.rvigor.dao.util.AppDaoManager
import com.healthy.rvigor.dao.util.WatchSyncUtils
import com.healthy.rvigor.net.ApiService
import com.healthy.rvigor.net.Client
import com.healthy.rvigor.net.UIThreadCallBackDoneUtils
import com.healthy.rvigor.service.NotificationsListenerService
import com.healthy.rvigor.service.SIATDeviceService
import com.healthy.rvigor.util.BleUtils
import com.healthy.rvigor.util.CustomerTimerRepeatExecutor
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.watch.AppSoftUpdateUtils
import com.healthy.rvigor.watch.UpdateDeviceUtils
import com.sw.watches.notification.NotificationSetting
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class MyApplication : Application() {

    val bleUtils by lazy { BleUtils() }

    var siatDeviceService: SIATDeviceService? = null

    var appDaoManager: AppDaoManager? = null

    val watchSyncUtils by lazy { WatchSyncUtils() }

    /**
     * 用于特殊任务的执行器
     */
    private var singleTasks: Executor? = null

    var notificationSetting: NotificationSetting? = null

    private var appSoftUpdateUtils: AppSoftUpdateUtils? = null

    private var updateDeviceUtils: UpdateDeviceUtils? = null

    var uiHandler: UIThreadCallBackDoneUtils? = null

    var timerRepeatExecutor: CustomerTimerRepeatExecutor? = null

    val appUserInfo by lazy { AppUserInfo() }


    private var mScanDis: Disposable? = null

    companion object {
        var mAppCount = 0
        private var instance: MyApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        uiHandler = UIThreadCallBackDoneUtils()
        timerRepeatExecutor = CustomerTimerRepeatExecutor()
        appDaoManager = AppDaoManager(this)
        singleTasks = Executors.newSingleThreadExecutor()
        notificationSetting = NotificationSetting(this)
//        updateDeviceUtils = UpdateDeviceUtils(this) //设备升级
//        appSoftUpdateUtils = AppSoftUpdateUtils(this);//App升级模块
        createNotificationChannel()
        startSIATService()
        startScanTime()
        registerLifecycle()
        setNotification()
        //Bugly
        CrashReport.initCrashReport(applicationContext, "a67c6c92ef", true)
//        disableAPIDialog()
    }

    private fun registerLifecycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                LogUtils.i(" registerActivityLifecycleCallbacks onActivityCreated ")
            }

            override fun onActivityStarted(activity: Activity) {
                if (mAppCount == 0) {
                    startScanTime()
                }
                mAppCount++
                LogUtils.i(" registerActivityLifecycleCallbacks onActivityStarted ")
            }

            override fun onActivityResumed(activity: Activity) {
                LogUtils.i(" registerActivityLifecycleCallbacks onActivityResumed AppCount $mAppCount")
            }

            override fun onActivityPaused(activity: Activity) {
                LogUtils.i(" registerActivityLifecycleCallbacks onActivityPaused ")
            }

            override fun onActivityStopped(activity: Activity) {
                mAppCount--
                LogUtils.i(" registerActivityLifecycleCallbacks onActivityStopped AppCount $mAppCount")
                if (mAppCount == 0 && instance.bleUtils.getConnectionWatch() != null) {
                    mScanDis?.dispose()
                    instance.bleUtils.disConnecting()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                LogUtils.i(" registerActivityLifecycleCallbacks onActivitySaveInstanceState ")
            }

            override fun onActivityDestroyed(activity: Activity) {
                LogUtils.i(" registerActivityLifecycleCallbacks onActivityDestroyed ")
            }
        })
    }

    /**
     * 反射 禁止弹窗
     */
    @SuppressLint("SoonBlockedPrivateApi")
    private fun disableAPIDialog() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }
        try {
            val clazz = Class.forName("android.app.ActivityThread")
            val currentActivityThread = clazz.getDeclaredMethod("currentActivityThread")
            currentActivityThread.isAccessible = true
            val activityThread = currentActivityThread.invoke(null)
            val mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown")
            mHiddenApiWarningShown.isAccessible = true
            mHiddenApiWarningShown.setBoolean(activityThread, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getService(): ApiService {
        return Client.sIntance.getService(ApiService::class.java, Constants.baseURL()) as ApiService
    }

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mNotificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                // 通知渠道的id
                val id = "1"
                // 用户可以看到的通知渠道的名字.
                val name: CharSequence = "notification channel"
                // 用户可以看到的通知渠道的描述
                val description = "notification description"
//                val importance = NotificationChannel.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
                // 配置通知渠道的属性
                mChannel.description = description
                // 设置通知出现时的闪灯（如果 android 设备支持的话）
                mChannel.enableLights(true)
                mChannel.lightColor = Color.RED
                // 设置通知出现时的震动（如果 android 设备支持的话）
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                //最后在notificationmanager中创建该通知渠道
                mNotificationManager.createNotificationChannel(mChannel)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 开启SIAT手表服务
     * 31以上由于电话权限问题，打开App会崩溃
     */
    fun startSIATService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        try {
            val intent = Intent()
            intent.setClass(this, SIATDeviceService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 重启服务
     */
    fun restartSIATService() {
        singleTasks?.execute(restartSIATServiceRun)
    }

    private val restartSIATServiceRun = Runnable {
        uiHandler?.PostAndWait(stopSIATServiceRunUI)
        try {
            Thread.sleep(5000)
        } catch (exception: java.lang.Exception) {
        }
        uiHandler?.PostAndWait(reconnectDeviceUIRun)
    }

    private val stopSIATServiceRunUI = Runnable { stopSIATService() }

    private val reconnectDeviceUIRun = Runnable {
        LogUtils.i("reStartScanDevice")
        watchSyncUtils?.reStartScanDevice()
    }

    private fun stopSIATService() {
        if (siatDeviceService != null) {
            siatDeviceService!!.stopSelf()
        }
    }

    private fun startScanTime() {
        if (mScanDis != null) {
            mScanDis?.dispose()
            mScanDis = null
        }
        mScanDis = Observable.interval(30, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (MyApplication.instance().bleUtils.getConnectionWatch() != null) {
//                    MyApplication.instance().bleUtils.stopScan()
                } else {
//                    MyApplication.instance().watchSyncUtils.reConnectDevice()
                    MyApplication.instance().watchSyncUtils.reStartScanDevice()
                }
            }
    }

    private fun setNotification() {
        try {
            val packageManager = packageManager
            //            packageManager.setComponentEnabledSetting(new ComponentName((Context) this, NotificationsListenerService.class),
//                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
//            packageManager.setComponentEnabledSetting(new ComponentName((Context) this, NotificationsListenerService.class),
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(
                ComponentName(
                    this as Context,
                    NotificationsListenerService::class.java
                ),
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP
            )
            packageManager.setComponentEnabledSetting(
                ComponentName(
                    this as Context,
                    NotificationsListenerService::class.java
                ),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
            )
            LogUtils.i(" setNotification ")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LogUtils.i(" setNotification $e")
        }
    }

}