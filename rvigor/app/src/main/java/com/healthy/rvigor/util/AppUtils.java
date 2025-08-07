package com.healthy.rvigor.util;//跟App相关的辅助类

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.R;
import com.healthy.rvigor.bean.PermissionItem;
import com.healthy.rvigor.mvp.view.activity.main.MainActivity;
import com.healthy.rvigor.service.NotificationsListenerService;
import com.healthy.rvigor.view.ConfirmTipsView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AppUtils {

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {

        try {

            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(

                    context.getPackageName(), 0);

            return packageInfo.versionName;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized int getVersionCode(Context context) {

        try {

            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(

                    context.getPackageName(), 0);

            return packageInfo.versionCode;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return 0;

    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getPackageName(Context context) {

        try {

            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(

                    context.getPackageName(), 0);

            return packageInfo.packageName;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }

    /**
     * 显示通知权限弹窗
     */
    public static void showNotificationTisp(Activity activity) {
        if (!AppUtils.isOpenNotice()) {//是否通知被打开
            ConfirmTipsView tisp = new ConfirmTipsView(activity);
            tisp.showDialog(activity.getResources().getString(R.string.notification_not_open), activity.getResources().getString(R.string.deny)
                    , activity.getResources().getString(R.string.correct), new ConfirmTipsView.OnBtnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean isConfirm) {
                            tisp.dismiss();
                            if (isConfirm) {
                                NotificationUtils.openNotificationAccess(activity);
                            }
                        }
                    });
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

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public static void showInput(Context context, final EditText et) {
        if (et == null) return;
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    public static void hideInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != view) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 格式化电话
     *
     * @param phone
     * @return
     */
    public static String getEncryPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return "";
        if (phone.length() < 11) return "";
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4, phone.length());
    }

    public static String getUserName(String name) {
//        if (!TextUtils.isEmpty(name)) {
//            return name;
//        } else if (AppUserInfo.getInstance().userInfo != null && !TextUtils.isEmpty(AppUserInfo.getInstance().userInfo.phone)) {
//            return "U" + AppUserInfo.getInstance().userInfo.phone.substring(AppUserInfo.getInstance().userInfo.phone.length() - 6, AppUserInfo.getInstance().userInfo.phone.length());
//        } else {
        return "";
//        }
    }

    public static boolean compareVersion(String newVersion, String curVersion) {
        try {
            if (TextUtils.isEmpty(newVersion) || TextUtils.isEmpty(curVersion)) return false;

            if (newVersion.contains("V")) {
                newVersion = newVersion.replace("V", "");
            }
            if (newVersion.contains("v")) {
                newVersion = newVersion.replace("v", "");
            }
            if (newVersion == null || curVersion == null) {
                throw new Exception("compareVersion error:illegal params.");
            }
            String[] versionArray1 = newVersion.split("\\.");//注意此处为正则匹配，不能用"."；
            String[] versionArray2 = curVersion.split("\\.");
            int idx = 0;
            int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
            int diff = 0;
            while (idx < minLength
                    && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                    && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
                ++idx;
            }
            //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
            diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
            if (diff > 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager manager = (ConnectivityManager) MyApplication.Companion.instance().getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    /**
     * 检测 响应某个Intent的Activity 是否存在
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);
        return list.size() > 0;
    }

    /**
     * @param context
     * @param packageName
     * @return
     * @Title isPackageExist
     * @Description .判断package是否存在
     * @date 2013年12月31日 上午9:49:59
     */
    public static boolean isPackageExist(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent().setPackage(packageName);
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos == null || infos.size() < 1) {
            return false;
        } else {
            return true;
        }
    }

    //确认NotificationMonitor是否开启
    public static void ensureCollectorRunning(Context context) {
        try {
            ComponentName collectorComponent = new ComponentName(context, NotificationsListenerService.class);
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            boolean collectorRunning = false;
            List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
            if (runningServices == null) {
                return;
            }
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                if (service.service.equals(collectorComponent)) {
                    if (service.pid == android.os.Process.myPid()) {
                        collectorRunning = true;
                    }
                }
            }
//            LogUtils.i(" NotificationsListenerService " + collectorRunning);
            if (collectorRunning) {
                return;
            }
            toggleNotificationListenerService(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //重新开启NotificationMonitor
    public static void toggleNotificationListenerService(Context context) {
        try {
            ComponentName thisComponent = new ComponentName(context, NotificationsListenerService.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void ensureCavoCollectorRunning(Context context) {
//        try {
//            ComponentName collectorComponent = new ComponentName(context, CavoNotificationsService.class);
//            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            boolean collectorRunning = false;
//            List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
//            if (runningServices == null) {
//                return;
//            }
//            for (ActivityManager.RunningServiceInfo service : runningServices) {
//                if (service.service.equals(collectorComponent)) {
//                    if (service.pid == android.os.Process.myPid()) {
//                        collectorRunning = true;
//                    }
//                }
//            }
//            LogUtils.i(" NotificationsListenerService " + collectorRunning);
//            if (collectorRunning) {
//                return;
//            }
//            toggleCavoNotificationListenerService(context);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //重新开启NotificationMonitor
//    public static void toggleCavoNotificationListenerService(Context context) {
//        try {
//            ComponentName thisComponent = new ComponentName(context, CavoNotificationsService.class);
//            PackageManager pm = context.getPackageManager();
//            pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//            pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        } else {
            sdDir = Environment.getRootDirectory();
        }
        return sdDir.toString();
    }

    /**
     * 打开文件
     *
     * @param activity
     * @param path
     */
    public static void openFile(Activity activity, String path) {
        if (activity == null) return;
        Intent intent = new Intent();
        File file = new File(path);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
        intent.setAction(Intent.ACTION_VIEW);//动作，查看
        intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));//设置类型
        activity.startActivity(intent);
    }

    private static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0)
            return type;
        /* 获取文件的后缀名 */
        String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (fileType == null || "".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (fileType.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    public static void setStartBarColor(Activity activity) {
        setStartBarColor(activity, R.color.white);
    }

    public static void setStartBarColor(Activity activity, int color) {
        ImmersionBar.with(activity)
                .statusBarColor(color)     //状态栏颜色，不写默认透明色
                .statusBarDarkFont(true) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .fitsSystemWindows(true)
                .keyboardEnable(true)
                .init();
    }

    private static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    /**
     * 打开后台运动权限
     *
     * @param activity
     */
    public static void startRunBackActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = activity.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                activity.startActivity(intent);
            }
        }
    }

    public static void goHuaweiSetting() {
        try {
            showActivity("com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                showActivity("com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 跳转到指定应用的首页
     */
    private static void showActivity(Activity activity, String packageName) {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        activity.startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private static void showActivity(String packageName, String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.Companion.instance().getApplicationContext().startActivity(intent);
    }

    public static boolean isEqualMac(String oldMac, String mac) {
        if (TextUtils.isEmpty(oldMac) || TextUtils.isEmpty(mac)) return false;
        try {
            String[] oldMacString = oldMac.split(":");
            String[] macString = mac.split(":");
            for (int i = 0; i < oldMacString.length && i < macString.length; i++) {
                int oldValue = Integer.parseInt(oldMacString[i], 16);
                int value = Integer.parseInt(macString[i], 16);
                if ((oldValue == value) || (i == oldMacString.length - 1 && (oldValue + 1) == value)) {

                } else {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return TextUtils.equals(oldMac, mac);
        }
    }

    //是否是Dfu升级失败应用
    public static boolean isEqualErrorMac(String name, String mac) {

        if (!isDfuDevice(name)) return false;

        String smartDeviceAddress = SPUtil.getData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.DEVICE_ADDRESS, "").toString();
        if (isEqualMac(smartDeviceAddress, mac)) return true;

        List<String> macList = new ArrayList<>();
        String errorMsg = (String) SPUtil.getData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.UPDATE_ERROR_DEVICE_MAC, "");
        if (!TextUtils.isEmpty(errorMsg)) {
            macList = new Gson().fromJson(errorMsg, new TypeToken<List<String>>() {
            }.getType());
        }
        if (macList.size() > 0) {
            for (String s : macList) {
                if (isEqualMac(s, mac)) {
                    return true;
                }
            }
        }
        return false;
    }

    //是否是Dfu设备
    public static boolean isDfuDevice(String name) {
        if (!TextUtils.isEmpty(name) && (name.startsWith("Dfu") || name.startsWith("dfu"))) {
            return true;
        }
        return false;
    }

    //手表更新文件是否存在
    public static boolean isUpdateFileExist() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Environment.getExternalStorageDirectory().getPath());
            if (WatchBeanUtil.getWatchStyle() == WatchBeanUtil.C100_WATCH_TYPE) {
                stringBuilder.append("/Download/test.bin");
            } else {
                stringBuilder.append("/Download/test.zip");
            }
            File file = new File(stringBuilder.toString());
            if (file.exists()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //重要事件星期转换为byte
    public static byte parseRepeat(String repeat) {
        int result = 0;
        if (null != repeat && repeat.length() == 7) {
            for (int i = 0; i < 7; i++) {
                String str = repeat.substring(i, i + 1);
                int value = Integer.parseInt(str);
                if (value == 1) {
                    result += Math.pow(2, 6 - i);
                }
            }
        }
        return (byte) (result & 0xFF);
    }

    //是否打开了通知
    public static boolean isOpenNotice() {
        //新的通知类，需要重新请求一下
        boolean isNewNoticeOpen = (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_OPEN_NOTICE, false);
        //通知是否打开
        boolean isEnable = NotificationUtils.isEnabled(MyApplication.Companion.instance());
        //请求弹窗弹下设置为Ture
        SPUtil.saveData(MyApplication.Companion.instance(), SpConfig.IS_OPEN_NOTICE, true);
        return isNewNoticeOpen && isEnable;
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

    public static String readTextFile(Context context, String assetFile) {
        try {
            InputStream inputStream = context.getResources().getAssets().open(assetFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            try {
                len = inputStream.read(buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (len != -1) {
                outputStream.write(buf, 0, len);
                len = inputStream.read(buf);
            }
            outputStream.close();
            inputStream.close();
            return outputStream.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /*语言类型：
     * 此处支持3种语言类型，更多可以自行添加。
     * */
    public static final String ENGLISH = "en";
    public static final String CHINESE = "zh";

    private static HashMap<String, Locale> languagesList = new HashMap<String, Locale>(2) {{
        put(ENGLISH, Locale.ENGLISH);
        put(CHINESE, Locale.CHINESE);
    }};

    /**
     * 修改语言
     *
     * @param activity 上下文
     * @param language 例如修改为 英文传“en”，参考上文字符串常量
     */
    public static void changeAppLanguage(Activity activity, String language) {
        try {
            Resources resources = activity.getResources();
            Configuration configuration = resources.getConfiguration();
            // app locale 默认简体中文
            Locale locale = getLocaleByLanguage(TextUtils.isEmpty(language) ? "zh" : language);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale);
            } else {
                configuration.locale = locale;
            }
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);

            Log.e("Log", "设置的语言：" + language);
            //finish();
            // 重启app
            Intent intent = new Intent(activity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            //加载动画
            //activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
            //activity.overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeLanguage(Activity activity, String language) {
        try {
            Resources resources = activity.getResources();
            Configuration configuration = resources.getConfiguration();
            // app locale 默认简体中文
            Locale locale = getLocaleByLanguage(TextUtils.isEmpty(language) ? "zh" : language);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale);
            } else {
                configuration.locale = locale;
            }
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);

            Log.e("Log", "设置的语言：" + language);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //finish();
        //加载动画
        //activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        //activity.overridePendingTransition(0, 0);
    }


    /**
     * 获取指定语言的locale信息，如果指定语言不存在
     * 返回本机语言，如果本机语言不是语言集合中的一种，返回英语
     */
    private static Locale getLocaleByLanguage(String language) {
        if (isContainsKeyLanguage(language)) {
            return languagesList.get(language);
        } else {
            Locale locale = Locale.getDefault();
            for (String key : languagesList.keySet()) {
                if (TextUtils.equals(languagesList.get(key).getLanguage(), locale.getLanguage())) {
                    return locale;
                }
            }
        }
        return Locale.ENGLISH;
    }


    /**
     * 如果此映射包含指定键的映射关系，则返回 true
     */
    private static boolean isContainsKeyLanguage(String language) {
        return languagesList.containsKey(language);
    }

    // 获取系统语言
    public static Locale getSystemLocale(Context context) {
        return context.getResources().getConfiguration().locale;
    }

    //语言设置
    public static Context localeAttachBaseContext(Context context) {
//        int type = (int) SPUtil.getData(context, SpConfig.APP_LANGUAGE, LanguageType.LANGUAGE_SAMPLE_CHINESE);
//        String language;
//        if (type == LanguageType.LANGUAGE_SAMPLE_CHINESE) {
//            language = "zh";
//        } else {
//            language = "en";
//        }
//        if (TextUtils.isEmpty(language)) {//默认跟随系统设置
//            return context;
//        }
//        Locale locale = new Locale(language);
        Locale locale = context.getResources().getConfiguration().locale;
        Configuration configuration = context.getResources().getConfiguration();
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            context.getResources().updateConfiguration(configuration,
                    context.getResources().getDisplayMetrics());
        }

        return context;
    }

    public static void startFilePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, 2514);
        }
    }

    public static void shareFile(Context context, String fileName) {
        File file = new File(fileName);
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context, "com.app.healthy.fileprovider", file);
                share.putExtra(Intent.EXTRA_STREAM, contentUri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            share.setType("application/pdf");
//            share.setType("application/vnd.ms-excel");//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        } else {
            LogUtils.i("分享文件不存在");
        }
    }

    //获取区号
    public static String getCountryIso(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getNetworkCountryIso();
        }
        return null;
    }

    //是否有该权限
    public static boolean hasPermissions(Context con, List<PermissionItem> permissions) {
        if ((permissions != null) && (permissions.size() > 0)) {
            boolean R = true;
            for (int i = 0; i < permissions.size(); i++) {
                R = (R && (con.checkSelfPermission(permissions.get(i).getName()) == PackageManager.PERMISSION_GRANTED));
            }
            return R;
        }
        return false;
    }

    public static String urlEncoding(String s, String enc) {
        try {
            return URLEncoder.encode(s, enc);
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public static String getAge(long day) {
        if (day <= 0) return "";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDay = new Date(day);
//        try {
//            birthDay = sdf.parse(day);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return day;
//        }
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return age + "";
    }

    public static int fromStringToInteger(String num, int defaultValue) {
        try {
            if (TextUtils.isEmpty(num)) return 0;
            return Integer.parseInt(num);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static float fromStringToFloat(String num, float defaultValue) {
        try {
            if (TextUtils.isEmpty(num)) return 0;
            return Float.parseFloat(num);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static boolean checkSelfPermissions(@NonNull Context activity, @NonNull String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //蓝牙权限
    public static String[] getBluetoothPerm() {
        List<String> list = new ArrayList<>();
        list.add(Manifest.permission.READ_PHONE_STATE);
        list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        list.add(Manifest.permission.BLUETOOTH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list.add(Manifest.permission.BLUETOOTH_SCAN);
            list.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        return list.toArray(new String[0]);
    }

    /**
     * 跳转到设置页面
     *
     * @param context
     */
    public static void goIntentSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeBitmapColor(ImageView img) {
        Bitmap bitmap = BitmapFactory.decodeResource(MyApplication.Companion.instance().getResources(), R.mipmap.ic_battery_bg);// 创建对象
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();
//        int[] pixels = new int[w * h * 10];
//        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),bitmap.getHeight());// 放入
//        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),bitmap.getHeight());// 取出

        Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int[] pixels = new int[b.getWidth() * b.getHeight()];// 创建像素数组
        b.setPixel(0, 0, 0xFF00FF00);
        b.setPixel(0, 1, 0x8000FF00);
        b.setPixel(1, 0, 0x0000FF00);
        b.getPixels(pixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
        b.setPixels(pixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
        img.setImageBitmap(b);// 显示
    }


    //文字加粗
    public static SpannableString getBlodBigTxt(String msg, String originalString, String blodString, int bigSize) {
        SpannableString spannableString = new SpannableString(msg);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        // 将BOLD样式应用于 %s 占位符所在的部分
        spannableString.setSpan(boldSpan, originalString.indexOf("%s"),
                originalString.indexOf("%s") + blodString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(
                new AbsoluteSizeSpan(bigSize, true),
                originalString.indexOf("%s"),
                originalString.indexOf("%s") + blodString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannableString;
    }

    public static SpannableString highlights(String text, String[] targets, String color, MyClickableSpan.OnMyClickListener listener) {
        SpannableString spannableString = new SpannableString(text);
        for (int i = 0; i < targets.length; i++) {
            String target = targets[i];
            Pattern pattern = Pattern.compile(target);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(color));
                spannableString.setSpan(new MyClickableSpan(target, listener), matcher.start(), matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(span, matcher.start(), matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }
}
