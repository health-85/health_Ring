package com.healthbit.framework.util;

import android.content.ComponentName;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author : zengq.
 * @date : 2018/11/28.
 * @description : 检测手机使用的ROM工具类.
 */
public class RomUtil {

    private static final String TAG = "Rom";

    public static final String ROM_MIUI = "MIUI";
    public static final String ROM_EMUI = "EMUI";
    public static final String ROM_FLYME = "FLYME";
    public static final String ROM_OPPO = "OPPO";
    public static final String ROM_SMARTISAN = "SMARTISAN";
    public static final String ROM_SONY = "SONY";
    public static final String ROM_VIVO = "VIVO";
    public static final String ROM_QIKU = "QIKU";
    public static final String ROM_HUAWEI = "HUAWEI";

    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.displayName";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_SONY = "ro.build.version.sony";
    private static final String KEY_VERSION_HUAWEI = "ro.build.hw_emui_api_level";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";
    private static final String KEY_SETTING = "com.android.settings";


    public static final String COMPONENT_FINGERPRINT = "component_fingerprint";
    private static String sName;
    private static String sVersion;

    public static boolean isEmui() {
        return check(ROM_EMUI);
    }

    public static boolean isMiui() {
        return check(ROM_MIUI);
    }

    public static boolean isVivo() {
        return check(ROM_VIVO);
    }

    public static boolean isOppo() {
        return check(ROM_OPPO);
    }

    public static boolean isHuawei() {
        return check(ROM_HUAWEI);
    }

    public static boolean isFlyme() {
        return check(ROM_FLYME);
    }

    public static boolean is360() {
        return check(ROM_QIKU) || check("360");
    }

    public static boolean isSmartisan() {
        return check(ROM_SMARTISAN);
    }

    public static boolean isSony() {
        return check(ROM_SONY);
    }

    public static String getName() {
        if (sName == null) {
            check("");
        }
        return sName;
    }

    public static String getVersion() {
        if (sVersion == null) {
            check("");
        }
        return sVersion;
    }

    private static boolean check(String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }

        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
            sName = ROM_MIUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
            sName = ROM_EMUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
            sName = ROM_OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
            sName = ROM_VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sName = ROM_SMARTISAN;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_HUAWEI))) {
            sName = ROM_HUAWEI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SONY))) {
            sName = ROM_SONY;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase().contains(ROM_FLYME)) {
                sName = ROM_FLYME;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sName.equals(rom);
    }

    private static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read prop " + name, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    public static ComponentName getSettingComponentName(String type) {
        return new ComponentName(getPackageName(type), getComponentName(type));
    }

    private static String getPackageName(String componentName) {
        String pcgName = KEY_SETTING;
        if (componentName.equals(COMPONENT_FINGERPRINT)) {
            if (isOppo()) {
                pcgName = "com.coloros.fingerprint";
            }
            //TODO 待更新其他机型相关包名
        }
        return pcgName;
    }

    private static String getComponentName(String componentName) {
        String clsName = "com.android.settings.Settings";
        if (COMPONENT_FINGERPRINT.equals(componentName)) {
            if (isOppo()) {
                clsName = "com.coloros.fingerprint.FingerLockActivity";
            } else if (isSony()) {
                clsName = "com.android.settings.Settings$FingerprintEnrollSuggestionActivity";
            } else if (isHuawei()) {
                clsName = "com.android.settings.fingerprint.FingerprintSettingsActivity";
            }
        }
        //TODO 设置更多跳转设置
        return clsName;
    }

}
