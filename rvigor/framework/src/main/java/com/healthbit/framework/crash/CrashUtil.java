package com.healthbit.framework.crash;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;

import java.util.List;

/**
* @Description:
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/6/5
* @UpdateRemark:   无
* @Version:        1.0
*/
public class CrashUtil {

    public static String getCrashFilePath(String packageName) {
        String dataDir = Environment.getExternalStorageDirectory() + "/Android/data/" + packageName + "/CrashLog.txt";
        return dataDir;
    }

    public static boolean launchAppByPackageName(Context context, String packageName) {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);
        PackageManager pManager = context.getApplicationContext().getPackageManager();
        List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent,
                0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String startappName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(startappName, className);
            intent.setComponent(cn);
            context.getApplicationContext().startActivity(intent);
            return true;
        }
        return false;
    }
}
