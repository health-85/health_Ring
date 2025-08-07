package com.sw.watches.notification;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.sw.watches.bleUtil.SpDeviceTools;

/**
 * 提醒工具类
 */
public class NotificationUtils {

    /**
     * 保存的蓝牙MAC是否为空
     * @param context
     * @return
     */
    public static boolean isBindDeviceMac(Context context) {
        String str = new SpDeviceTools(context).getBleMac();
        return (str != null && !str.equals(""));
    }

    /**
     * 获取通知提醒权限是否有效
     * @param context
     * @return
     */
    public static boolean isEnabled(Context context) {
        String str2 = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(str2)) {
            String[] arrayOfString = str2.split(":");
            for (byte b = 0; b < arrayOfString.length; b = (byte) (b + 1)) {
                ComponentName componentName = ComponentName.unflattenFromString(arrayOfString[b]);
                if (componentName != null && TextUtils.equals(context.getPackageName(), componentName.getPackageName()))
                    return true;
            }
        }
        return false;
    }

    /**
     * 跳转到设置里的修改通知提醒权限
     * @param context
     */
    public static void openNotificationAccess(Context context) {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        context.startActivity(intent);
    }

}
