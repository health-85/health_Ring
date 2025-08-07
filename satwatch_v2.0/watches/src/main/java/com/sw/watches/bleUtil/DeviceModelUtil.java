package com.sw.watches.bleUtil;

import android.content.Context;
import android.location.LocationManager;

public class DeviceModelUtil {

    public static boolean isDeviceModel(String str) {
        if (str == null)
            return false;
        int i = str.length();
        for (byte b = 1; b < i; b++) {
            if (str.substring(b - 1, b).equals("�")) {
                return true;
            }
        }
        return false;
    }

    public static String parseStr(String str, int position, String sign) {
        if (position == 0) {
            return str.substring(0, str.indexOf(sign));
        } else {
            String temp;
            if (position == 1) {
                temp = str.substring(str.indexOf(sign) + sign.length());
                return temp.substring(0, temp.indexOf(sign));
            } else if (position == 2) {
                int len = parseStr(str, 1, sign).length();
                temp = str.substring(parseStr(str, 0, sign).length() + len + sign.length() * 2);
                return temp.substring(0, temp.indexOf(sign));
            } else {
                return str.substring(parseStr(str, 0, sign).length() + parseStr(str, 1, sign).length() + parseStr(str, 2, sign).length() + sign.length() * 3);
            }
        }
    }

    /**
     * 定位是否可用
     *
     * @param context
     * @return
     */
    public static boolean isGpsAndNetworkEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && (locationManager.isProviderEnabled("gps") || locationManager.isProviderEnabled("network")))
            return true;
        return false;
    }
}
