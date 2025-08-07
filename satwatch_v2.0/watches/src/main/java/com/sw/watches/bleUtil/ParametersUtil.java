package com.sw.watches.bleUtil;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

public class ParametersUtil {

    public static int param1 = 1;

    public static int param2 = 1000;

    public static int inputLength = 1500;

    public static int inputStreamLen = 100;

    public static int outputStreamLen = 0;

    public static int mModuleLevel = 0;

    public static final String sign = "/%partition%/";

    public static void setParameters(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Context context) {
        if (param1 != paramInt1 || param2 != paramInt2 || inputLength != paramInt3 || inputStreamLen != paramInt4) {
            param1 = paramInt1;
            if (paramInt2 < 20)
                paramInt2 = 20;
            param2 = paramInt2;
            if (paramInt3 < 20)
                paramInt3 = 20;
            inputLength = paramInt3;
            if (paramInt4 < 20)
                paramInt4 = 20;
            inputStreamLen = paramInt4;
            new SpBelUtil(context).putParameters(paramInt1 + sign + paramInt2 + sign + paramInt3 + sign + paramInt4);
        }
    }

    public static void putModuleLevel(int paramInt, Context context) {
        if (mModuleLevel != paramInt) {
            mModuleLevel = paramInt;
            new SpBelUtil(context).putModuleLevel(mModuleLevel);
        }
    }

    public static void initParameters(Context context) {
        SpBelUtil spBelUtil = new SpBelUtil(context);
        int modelLevel = spBelUtil.getModuleLevel();
        String str = spBelUtil.getParameters();
        if (!TextUtils.isEmpty(str)) {
            param1 = Integer.parseInt(DeviceModelUtil.parseStr(str, 0, sign));
            param2 = Integer.parseInt(DeviceModelUtil.parseStr(str, 1, sign));
            inputLength = Integer.parseInt(DeviceModelUtil.parseStr(str, 2, sign));
            inputStreamLen = Integer.parseInt(DeviceModelUtil.parseStr(str, 3, sign));
        }
    }

    public static int a() {
        if (++outputStreamLen > 10)
            outputStreamLen = 10;
        return outputStreamLen;
    }

    public static int g() {
        if (--outputStreamLen < 0)
            outputStreamLen = 0;
        return outputStreamLen;
    }

    public static int getInputStreamLen() {
        return inputStreamLen;
    }

    public static int e() {
        return isHuawei() ? (param1 + 2) : param1;
    }

    public static int b() {
        return param2;
    }

    public static int getInputLength() {
        return inputLength;
    }

    public static int getOutputStreamLen() {
        return outputStreamLen;
    }

    public static boolean isHuawei() {
        if ("huawei".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        } else {
            return "honor".equalsIgnoreCase(Build.MANUFACTURER) ? true : "rongyao".equalsIgnoreCase(Build.MANUFACTURER);
        }
    }
}