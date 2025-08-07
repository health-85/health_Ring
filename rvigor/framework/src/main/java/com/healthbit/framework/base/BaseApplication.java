package com.healthbit.framework.base;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import com.healthbit.framework.FrameworkConfig;
import com.healthbit.framework.crash.CrashHandler;
import com.healthbit.framework.crash.CrashUtil;
import com.healthbit.framework.util.DeviceUtil;

import java.io.File;

/**
* @Description:    
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/6/5
* @UpdateRemark:   无
* @Version:        1.0
*/
public class BaseApplication extends Application {

    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (FrameworkConfig.getCrashConfig().crashCatch) {
            if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CrashHandler)) {
                String processName = DeviceUtil.getProcessName(Process.myPid());
                if (!TextUtils.isEmpty(processName)) {
                    File file = new File(CrashUtil.getCrashFilePath(processName));
                    if (file.exists() && file.length() > FrameworkConfig.getCrashConfig().crashFileMaxSize) {
                        file.delete();
                    }
                    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(processName));
                }
            }
        }
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
