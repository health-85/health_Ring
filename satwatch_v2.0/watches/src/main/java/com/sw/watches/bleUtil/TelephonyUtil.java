package com.sw.watches.bleUtil;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;

public class TelephonyUtil {

    /**
     * 设置endCall方法可用
     * @param context
     */
    public static void endCall(Context context) {
        try {
            Object object  = getITelephony(context);
            if (object != null) {
                object.getClass().getMethod("endCall", new Class[0]).setAccessible(true);
                object.getClass().getMethod("endCall", new Class[0]).invoke(object, new Object[0]);
            }
        } catch (SecurityException securityException) {
            securityException.printStackTrace();
        } catch (NoSuchMethodException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        } catch (InvocationTargetException invocationTargetException) {
            invocationTargetException.printStackTrace();
        }
    }

    /**
     * 获取getITelephony服务类
     * @param context
     * @return
     */
    public static Object getITelephony(Context context) {
        Object object = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.getClass().getDeclaredMethod("getITelephony", new Class[0]).setAccessible(true);
            object = telephonyManager.getClass().getDeclaredMethod("getITelephony", new Class[0])
                    .invoke(telephonyManager, new Object[0]);
        } catch (SecurityException securityException) {
            securityException.printStackTrace();
        } catch (NoSuchMethodException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        } catch (InvocationTargetException invocationTargetException) {
            invocationTargetException.printStackTrace();
        }
        return object;
    }

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    //限制连续点击
    public static boolean isEffectiveClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            flag = true;
        }
        return flag;
    }

}