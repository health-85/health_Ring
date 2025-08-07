package com.healthy.rvigor.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.bean.AppUserInfo;
import com.healthy.rvigor.bean.UserInfo;
import com.healthy.rvigor.watch.WatchBase;

public class WatchBeanUtil {

    public static int F38_WATCH_TYPE = 1;
    public static int A919_WATCH_TYPE = 2;
    public static int Q16_WATCH_TYPE = 3;
    public static int A81_WATCH_TYPE = 4;
    public static int A910_WATCH_TYPE = 5; //隐藏打鼾、血压
    public static int A999_WATCH_TYPE = 6; //所有展示
    public static int A920_WATCH_TYPE = 7; //隐藏呼吸、血压、
    public static int C100_WATCH_TYPE = 8;
    public static int A86_WATCH_TYPE = 9;
    public static int A8_WATCH_TYPE = 10;
    public static int A7_WATCH_TYPE = 11;
    public static int V101_WATCH_TYPE = 12;
    public static int UT_WATCH_TYPE = 13;
    public static int TK12_WATCH_TYPE = 14;
    public static int A933_WATCH_TYPE = 15;
    public static int E100 = 16;

    public static int getWatchStyle() {
//        if (MyApplication.Companion.instance().getAdapetUtils() == null) return A920_WATCH_TYPE;
//        WatchBase watchBase = MainApplication.getInstance().getAdapetUtils().getConnectionWatch();
        String name = null;
//        if (watchBase != null && !TextUtils.isEmpty(watchBase.getDeviceName())) {
//            name = watchBase.getDeviceName();
//        }
        String deviceName = (String) SPUtil.getData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.DEVICE_NAME, "");
        if (!TextUtils.isEmpty(deviceName) && TextUtils.isEmpty(name)) {
            name = deviceName;
        }
        if (TextUtils.isEmpty(name)) return A920_WATCH_TYPE;
        if (name.contains("F38_")) {
            return F38_WATCH_TYPE;
        } else if (name.contains("A919_")) {
            return A919_WATCH_TYPE;
        } else if (name.contains("Q16_")) {
            return Q16_WATCH_TYPE;
        } else if (name.contains("A81")) {
            return A81_WATCH_TYPE;
        } else if (name.contains("A910_")) {
            return A910_WATCH_TYPE;
        } else if (name.contains("A920_")) {
            return A920_WATCH_TYPE;
        } else if (name.contains("A999_")) {
            return A999_WATCH_TYPE;
        } else if (name.contains("S100_") || name.contains("C100_")) {
            return C100_WATCH_TYPE;
        } else if (name.contains("A86")) {
            return A86_WATCH_TYPE;
        } else if (name.contains("A8")) {
            return A8_WATCH_TYPE;
        } else if (name.contains("A7")) {
            return A7_WATCH_TYPE;
        } else if (name.contains("V101")) {
            return V101_WATCH_TYPE;
        } else if (name.contains("UT001")) {
            return UT_WATCH_TYPE;
        } else if (name.contains("TK12")) {
            return TK12_WATCH_TYPE;
        } else if (name.contains("E100")) {
            return E100;
        } else if (name.contains("A933")) {
            return A933_WATCH_TYPE;
        } else {
            return A920_WATCH_TYPE;
        }
    }

    @SuppressLint("MissingPermission")
    public static boolean isSpecDevice(BluetoothDevice device) {
        if (device == null) return false;
        String deviceName = device.getName();
        if (TextUtils.isEmpty(deviceName)) {
            return false;
        }
//            if (deviceName.contains("BIOSENSE-RING")) {
//                return true;
//            }
//            if (deviceName.contains("F38_")) {
//                return true;
//            }
//            if (deviceName.contains("A939") || deviceName.contains("A933") || deviceName.contains("A910_") || deviceName.contains("A919_")
//                    || deviceName.contains("A920_")) {
//                return true;
//            }
//            if (deviceName.contains("Q16_") || deviceName.contains("C100_") || deviceName.contains("A81_") || deviceName.contains("A86_")
//                    || deviceName.contains("A8_") || deviceName.contains("A7_") || deviceName.startsWith("C") || deviceName.contains("E100")
//                    || deviceName.contains("S100")) {
//                return true;
//            }
//            if (deviceName.contains("V101") || deviceName.contains("TK12") || deviceName.contains("UT")) {
//                return true;
//            }
        return true;
    }

    //解绑设备
    public static void unBindDevice() {
        MyApplication.Companion.instance().getBleUtils().stopScan();
        MyApplication.Companion.instance().getBleUtils().disConnect();
        WatchBase watchBase = MyApplication.Companion.instance().getBleUtils().getConnectionWatch();
        if (watchBase != null) {
            watchBase.close();
        }
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().UnBindDevice();
        }
        SPUtil.clearValue(MyApplication.Companion.instance(), SpConfig.DEVICE_NAME);
        SPUtil.clearValue(MyApplication.Companion.instance(), SpConfig.DEVICE_ADDRESS);
        SPUtil.clearValue(MyApplication.Companion.instance(), SpConfig.DFU_SMART_DEVICE);
        SPUtil.clearValue(MyApplication.Companion.instance(), SpConfig.DEVICE_VERSION_NAME);
        SPUtil.clearValue(MyApplication.Companion.instance(), SpConfig.DEVICE_VERSION_CODE);
    }

    //卡路里计算公式 = 步数 × 步长 × 体重（公斤）×0.036
    public static float byteToCalorie(int step) {
        UserInfo info = MyApplication.Companion.instance().getAppUserInfo().getUserInfo();
        float user_height = info.height;
        if (user_height <= 0) user_height = 170;
        float user_weight = info.weigh;
        if (user_weight <= 0) user_weight = 65;
        float stepLen = user_height * 0.41F / 100f;
        float calorie = step * stepLen * user_weight * 0.036f;
//        return calorie;
        return (float) ((int) user_weight * 1.036F * (int) user_height * (float) step * 0.41F * 0.00001F);
    }

    //步长（米）≈ 身高（厘米）× 0.41 / 100
    //距离=步数× 步长（米）
    public static float byteToKm(int step) {
        UserInfo info = MyApplication.Companion.instance().getAppUserInfo().getUserInfo();
        float user_height = info.height;
        if (user_height <= 0) user_height = 170;
        float stepLen = user_height * 0.41F / 100f;
        float distance = step * stepLen;
        return distance / 1000f;
    }

    //获取步数
    public static int getStepTime(int step) {
        return step / 100;
    }

    public static boolean isEnglishApp() {
        return false;
    }


}
