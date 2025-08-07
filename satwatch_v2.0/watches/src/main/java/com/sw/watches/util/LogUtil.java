package com.sw.watches.util;

import android.util.Log;


/**
 * @author tcb
 * 日志输出
 */
public class LogUtil {

    private static final String TAG = "LogUtil";

    /**
     * 是否为debug
     */
    private static volatile boolean isDebug = false;

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

    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

    public static void error(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void error(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void info(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }
}
