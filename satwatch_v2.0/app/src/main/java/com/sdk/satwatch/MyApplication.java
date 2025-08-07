package com.sdk.satwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import com.sw.watches.application.ZhbraceletApplication;

import java.util.Locale;

public class MyApplication extends ZhbraceletApplication {

    private static final String TAG = "MyApplication";

    private static Context mContext;

    public static Context getApp(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
//        TraceServiceImpl.sShouldStopService = false;
//        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

//        WriteLog.install(this, "OTA", 2);
////        boolean isDebug = RtkSettings.getInstance().isDebugEnabled();
//
//        // Mandatory, initialize rtk-core library
//        // this: context
//        // isDebug: true, switch on debug log; false, switch off debug log
//        RtkConfigure configure = new RtkConfigure.Builder()
//                .debugEnabled(BuildConfig.DEBUG)
//                .printLog(true)
//                .logTag("OTA")
//                .build();
//        RtkCore.initialize(this, configure);
//
////        int pid = Process.myPid();
////        String processAppName = null;
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
////            processAppName = getProcessName();
////        }
////        // 如果app启用了远程的service，此application:onCreate会被调用2次
////        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
////            if (processAppName != null && processAppName.equals(getProcessName())) {
//        //            GlobalGatt.DUMP_SERVICE = true
//
//        // Mandatory, initialize rtk-dfu library
//        // this: context
//        // isDebug: true, switch on debug log; false, switch off debug log
//        RtkDfu.initialize(this, BuildConfig.DEBUG);
//        // Optional
//        BaseBinInputStream.MPHEADER_PARSE_FORMAT = BaseBinInputStream.MPHEADER_PARSE_HEADER;
//
//        // Optional for demo
////        PluginsManager.configBugly(this, "customerRealtekPhone", "8fe5f9f85c");
////                AppSettingsHelper.Companion.initialize(this);
//        SettingsHelper.Companion.initialize(this);
//
//        // Optional for quality test
//        DfuQualitySDK.INSTANCE.initialize(this);
//        DfuQualitySDK.INSTANCE.setDBG(BuildConfig.DEBUG);

        //        ZLogger.d(Arrays.toString(DependenceManager.getInstance().getLibMap().values().toArray()));

//        ZLogger.d(
//                String.format(
//                        Locale.US,
//                        "{\nAPPLICATION_ID=%s\nVERSION=%s-%d\nDEBUG=%b\nBUILD_TYPE=%s\nFLAVOR=%s\n}",
//                        BuildConfig.APPLICATION_ID,
//                        BuildConfig.VERSION_NAME,
//                        BuildConfig.VERSION_CODE,
//                        BuildConfig.DEBUG,
//                        BuildConfig.BUILD_TYPE,
//                        "customerRealtekPhone"
//                )
//        );
//            }

    }

}
