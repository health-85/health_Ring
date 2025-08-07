package com.healthbit.framework.crash;

import android.content.Intent;
import android.os.Process;
import androidx.annotation.NonNull;

import com.healthbit.framework.FrameworkConfig;
import com.healthbit.framework.base.BaseApplication;
import com.healthbit.framework.util.DateUtil;
import com.healthbit.framework.util.DeviceUtil;
import com.healthbit.framework.util.FileUtil;

import java.io.IOException;

/**
 * @Description: 异常捕捉
 * @Author: zxy(1051244836 @ qq.com)
 * @CreateDate: 2019/6/4
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    String packageName = "packageName";

    public CrashHandler(@NonNull String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        saveCrashLog(e);
        if (FrameworkConfig.getCrashConfig().showCrashUI) {
            jumpToCrashReportActivity(e);
        }
        Process.killProcess(Process.myPid());
    }

    private void saveCrashLog(Throwable e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("-------------------------------------------------------------->>>>\n")
                .append(DateUtil.getYMDHMSDate(System.currentTimeMillis())).append("\n")
                .append("=======系统信息=======\n")
                .append(DeviceUtil.getDeviceBrand()).append("(").append(DeviceUtil.getDeviceModel()).append(")\n")
                .append(DeviceUtil.getOsDescStr()).append("\n");
        String[] deviceAbis = DeviceUtil.getDeviceAbis();
        if (deviceAbis != null) {
            for (int i = 0; i < deviceAbis.length; i++) {
                stringBuilder.append("abi").append(i).append(": ").append(DeviceUtil.getDeviceAbi()).append("\n");
            }
        }
        stringBuilder
                .append("=======Crash=======\n")
                .append(e.toString()).append("\n")
                .append("<<<<--------------------------------------------------------------\n\n\n");
        String dataDir = CrashUtil.getCrashFilePath(packageName);
        try {
            FileUtil.writeFile(dataDir, stringBuilder.toString(), true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void jumpToCrashReportActivity(Throwable e) {
        Intent intent = new Intent(BaseApplication.getContext(), CrashReportActivity.class);
        intent.putExtra("exception", e);
        intent.putExtra("packageName", packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        BaseApplication.getContext().startActivity(intent);
    }
}
