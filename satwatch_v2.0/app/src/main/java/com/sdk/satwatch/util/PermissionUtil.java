package com.sdk.satwatch.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sdk.satwatch.R;
import com.sw.watches.util.LogUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/6 0006.
 */

public class PermissionUtil {

    private static final String TAG = "PermissionUtil";

    //总的权限
    public static final int PERMISSION_REQUEST_CODE = 0x01;
    //蓝牙权限
    public static final int PERMISSION_BLUETOOTH_CODE = 0x02;
    //相机权限
    public static final int PERMISSION_CAMERA_CODE = 0x03;
    //手机状态权限
    public static final int PERMISSION_PHONE_STATUS_CODE = 0x04;

    public String[] getBluetoothPerm() {
        List<String> list = new ArrayList<>();
        list.add(Manifest.permission.READ_PHONE_STATE);
        list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            list.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        list.add(Manifest.permission.BLUETOOTH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list.add(Manifest.permission.BLUETOOTH_SCAN);
            list.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        return list.toArray(new String[0]);
    }

    public String[] getCameraPerm() {
        String[] cameraPerm = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,};
        return cameraPerm;
    }

    private static PermissionUtil instance = new PermissionUtil();

    public static PermissionUtil getInstance() {
        return instance;
    }

    public static boolean checkSelfPermissions(@NonNull Activity activity, @NonNull String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isShowBluetoothPerRationale(Activity activity) {
        if (Build.VERSION.SDK_INT >= 31) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH_SCAN)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH_CONNECT)
            ) {
                return true;
            } else {
                return false;
            }
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)
            ) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isShowCameraPerRationale(Activity activity) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
                && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取被拒绝的权限
     */
    public static List<String> getDeniedPermissions(@NonNull Activity activity, @NonNull String... permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (!deniedPermissions.isEmpty()) {
            return deniedPermissions;
        }
        return null;
    }


    /**
     * 是否拒绝了再次申请权限的请求（选择了不再询问 || 部分机型默认为不在询问）
     */
    public static boolean deniedRequestPermissionsAgain(@NonNull Activity activity, @NonNull String... permissions) {
        for (String permission : permissions) {
            /**
             * 注：调用该方法的前提是应用已申请过该权限，如应用未申请就调用此方法，返回false
             * 1.已请求过该权限且用户拒绝了请求,返回true
             * 2.用于拒绝了请求，且选择了不再询问,返回false
             * 3.设备规范禁止应用具有该权限，返回false
             */
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public static void startApplicationDetailsSettings(@NonNull Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 申请权限<br/>
     *
     * @return 是否已经获取权限
     */
    public static boolean requestPermissions(Activity activity, int requestCode, String... permissions) {
        if (!checkSelfPermissions(activity, permissions)) {// 权限不全
            List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
            if (deniedPermissions != null) {// 申请权限
                ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            }
            return false;
        }
        return true;
    }


    /**
     * 申请定位权限
     *
     * @param activity
     * @param requestCode
     * @return
     */
    public static boolean requestLocationPermissions(Activity activity, int requestCode) {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!checkSelfPermissions(activity, permissions)) {// 权限不全
            List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
            if (deniedPermissions != null) {// 申请权限
                ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            }
            return false;
        }
        return true;
    }

    /**
     * 申请权限返回
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults,
                                                  @NonNull OnRequestPermissionsResultCallbacks callBack) {
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        if (!granted.isEmpty()) {
            callBack.onPermissionsGranted(requestCode, granted, denied.isEmpty());
        }
        if (!denied.isEmpty()) {
            callBack.onPermissionsDenied(requestCode, denied, granted.isEmpty());
        }
    }

    public static List<String> getPermissionExplainList(Context ctx, String... permissions) {
        List<String> explainList = new LinkedList<>();
        // 避免存储权限说明添加两次，
        boolean storage = false;
        // 避免位置权限说明添加两次，
        boolean location = false;
        //避免蓝牙权限说明添加两次
        boolean bluetooth = false;
        for (String permission : permissions) {
            switch (permission) {
                case Manifest.permission.BLUETOOTH:
                case Manifest.permission.BLUETOOTH_SCAN:
                case Manifest.permission.BLUETOOTH_CONNECT:
                    if (!bluetooth) {
                        explainList.add(ctx.getString(R.string.tip_permission_bluetooth));
                    }
                    bluetooth = true;
                    break;
                case Manifest.permission.CAMERA:
                    explainList.add(ctx.getString(R.string.tip_permission_camera));
                    break;
                case Manifest.permission.READ_PHONE_STATE:
                    explainList.add(ctx.getString(R.string.tip_permission_phone));
                    break;
                case Manifest.permission.ACCESS_FINE_LOCATION:
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                    if (!location) {
                        explainList.add(ctx.getString(R.string.tip_permission_location));
                    }
                    location = true;
                    break;
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    if (!storage) {
                        explainList.add(ctx.getString(R.string.tip_permission_storage));
                    }
                    storage = true;
                    break;
                default:
                    LogUtil.e(TAG, "权限说明没定义, " + permission);
            }
        }
        return explainList;
    }

    public static String getPermissionExplainText(Context ctx, String... permissions) {
        List<String> explainList = getPermissionExplainList(ctx, permissions);
        return ctx.getString(R.string.tip_permission_header) + '\n' +
                TextUtils.join("\n", explainList);
    }

    /**
     * 申请权限返回
     */
    public interface OnRequestPermissionsResultCallbacks {
        /**
         * @param isAllGranted 是否全部同意
         */
        void onPermissionsGranted(int requestCode, List<String> perms, boolean isAllGranted);

        /**
         * @param isAllDenied 是否全部拒绝
         */
        void onPermissionsDenied(int requestCode, List<String> perms, boolean isAllDenied);
    }

    public static boolean isBrandHuawei() {
        return "huawei".equalsIgnoreCase(Build.BRAND)
                || "huawei".equalsIgnoreCase(Build.MANUFACTURER);
    }

    /**
     * 跳转到设置页面
     *
     * @param context
     */
    public static void startPerSettingActivity(Context context) {
        goIntentSetting(context);
    }

    private static void goIntentSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
