package com.healthbit.framework.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @author : zengq.
 * @date : 2018/11/28.
 * @description : toast工具类.
 */
public class ToastUtil {

    private static Toast mToast;

    public static void showToast(Context context, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }

    public static void showToast(Context context, int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    public static void showToastLong(Context context, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_LONG);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }
    public static void showToastLong(Context context, int content) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_LONG);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }

}
