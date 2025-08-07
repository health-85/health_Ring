package com.healthbit.framework.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.healthy.rvigor.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author : zengq.
 * @date : 2018/11/28.
 * @description : 状态栏utils，用于展示沉浸式状态栏.
 */
public class StatusBarUtil {

    public static int windowStatusHeight = -1;

    public static int getStatusBarHeight(Context context) {
        if (windowStatusHeight == -1) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                windowStatusHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return windowStatusHeight;
    }

    /**
     * 设置状态栏透明，适用于图片
     * @param activity 当前activity
     * @param view 用于放置于状态栏的view
     */
    public static void setStatusBarByTranslucent(Activity activity, View view) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup contentView = (ViewGroup) window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
            contentView.getChildAt(0).setFitsSystemWindows(false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = getStatusBarHeight(activity.getBaseContext());
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置状态栏透明，适用于纯色
     * @param activity 该activity
     * @param color 需要设置的颜色值，如传小于0，则使用默认纯白色
     */
    public static void setStatusBarByPureColor(Activity activity, int color) {
        int sdkInt = Build.VERSION.SDK_INT;
        int currentColor = ContextCompat.getColor(activity.getBaseContext(), R.color.color_ffffff);
        boolean isDarkFont = false;
        if (color > 0) {
            currentColor = ContextCompat.getColor(activity.getBaseContext(), color);
        }
        if (currentColor == ContextCompat.getColor(activity.getBaseContext(), R.color.color_ffffff)) {
            isDarkFont = true;
        }
        Window window = activity.getWindow();
        //6.0以上
        if (sdkInt >= Build.VERSION_CODES.M) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(isDarkFont ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_VISIBLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(currentColor);
        // 5.0 以上
        } else if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (RomUtil.isFlyme()) {
                if (!setFlymeStatusBarLightMode(activity, isDarkFont)) {
                    window.setStatusBarColor(currentColor);
                }
            } else if (RomUtil.isMiui()) {
                if (!setMIUIStatusBarLightMode(activity, isDarkFont)) {
                    window.setStatusBarColor(currentColor);
                }
            } else {
                window.setStatusBarColor(currentColor);
            }
        // 4.4-5.0
        } else if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup contentView = (ViewGroup) window.getDecorView().findViewById(android.R.id.content);
            contentView.getChildAt(0).setFitsSystemWindows(true);
//            contentView.setBackgroundColor(color);
        }
    }

    /**
     * 设置flyme系统状态栏颜色,需要flyme 4.0+
     *
     * @param activity        当前Activity
     * @param isFontColorDark 是否需要黑色字体
     * @return 设置是否成功，如果在设置过程中抛异常即为失败。
     */
    private static boolean setFlymeStatusBarLightMode(Activity activity, boolean isFontColorDark) {
        Window window = activity.getWindow();
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (isFontColorDark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 设置MIUI系统状态栏颜色,需要MIUI 6+
     *
     * @param activity        当前Activity
     * @param isFontColorDark 是否需要黑色字体
     * @return 设置是否成功，如果在设置过程中抛异常即为失败。
     */
    private static boolean setMIUIStatusBarLightMode(Activity activity, boolean isFontColorDark) {
        Window window = activity.getWindow();
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (isFontColorDark) {
                    //状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}
