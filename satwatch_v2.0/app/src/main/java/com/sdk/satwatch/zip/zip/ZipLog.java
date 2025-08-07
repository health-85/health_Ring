package com.sdk.satwatch.zip.zip;

import android.util.Log;

import com.sdk.satwatch.BuildConfig;

/**
 * function:
 *
 * <p>
 * Created by Leo on 2018/1/16.
 */
final class ZipLog {
    private static final String TAG = "ZipLog";

    private static boolean DEBUG = BuildConfig.DEBUG;

    static void config(boolean debug) {
        DEBUG = debug;
    }

    static void debug(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }
}
