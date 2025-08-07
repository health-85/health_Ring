package com.sw.watches.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.sw.watches.BuildConfig;
import com.sw.watches.notification.NotificationSetting;
import com.sw.watches.service.NotificationsListenerService;
import com.sw.watches.service.ZhBraceletService;
import com.sw.watches.util.LogUtil;

public class ZhbraceletApplication extends Application {

    private static ZhbraceletApplication instance;

    public static ZhBraceletService mZhBraceletService;

    private NotificationSetting notificationSetting = null;

    public static ZhBraceletService getZhBraceletService() {
        return mZhBraceletService;
    }

    public static void setZhBraceletService(ZhBraceletService service) {
        mZhBraceletService = service;
    }

    private void setNotification() {
//        PackageManager packageManager = getPackageManager();
//        packageManager.setComponentEnabledSetting(new ComponentName((Context) this, NotificationsListenerService.class),
//                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
//        packageManager.setComponentEnabledSetting(new ComponentName((Context) this, NotificationsListenerService.class),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        setNotification();
        notificationSetting = new NotificationSetting(this);
        startZhBraceletService(this);
        LogUtil.setDebug(BuildConfig.DEBUG);
    }

    public static ZhbraceletApplication getInstance() {
        return instance;
    }

    public NotificationSetting getNotificationSetting() {
        return notificationSetting;
    }

    public void startZhBraceletService(Context context){
        Intent intent = new Intent(context, ZhBraceletService.class);
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            setZhBraceletService(null);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            setZhBraceletService(((ZhBraceletService.LocalBinder) service).getService());
        }
    };

}