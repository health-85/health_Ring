package com.healthy.rvigor.util;

import android.util.Log;

import com.healthy.rvigor.BuildConfig;


/**
 * @author tcb
 * 日志输出
 */
public class LogUtils {

    /**
     * 是否为debug
     */
    private static volatile boolean isDebug = true;

    private static volatile String TAG = "测试";

    /**
     * 是否
     *
     * @param debug 是否调试
     */
    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * 错误输出
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    /**
     * 错误输出
     *
     * @param msg
     */
    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    /**
     * v输出
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    /**
     * v输出
     *
     * @param msg
     */
    public static void v(String msg) {
        if (isDebug) {
            Log.v(TAG, msg);
        }
    }

    /**
     * i输出
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    /**
     * i输出
     *
     * @param msg
     */
    public static void i(String msg) {
        if (isDebug) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.v(TAG, msg);
        }
    }

}
