package com.sw.watches.bleUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


public class SpBelUtil {

    public SharedPreferences sharedPreferences;

    public final String COLLECT = "collect";

    public final String PARAMETERS = "parameters";

    public final String MODULE_LEVEL = "ModuleLevel";

    @SuppressLint({"WrongConstant"})
    public SpBelUtil(Context context) {
        sharedPreferences = context.getSharedPreferences("data", 33554432);
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value);
        sharedPreferences.edit().apply();
    }

    public String getString(String str) {
        return sharedPreferences.getString(str, null);
    }

    public void putCollect(String key, String collect) {
        sharedPreferences.edit().putString(COLLECT + key, collect);
        sharedPreferences.edit().apply();
    }

    public String getCollect(String collect) {
        return sharedPreferences.getString(COLLECT + collect, null);
    }

    public void putParameters(String parameters) {
        sharedPreferences.edit().putString(PARAMETERS, parameters);
        sharedPreferences.edit().apply();
    }

    public void putModuleLevel(int level) {
        sharedPreferences.edit().putInt(MODULE_LEVEL, level);
        sharedPreferences.edit().apply();
    }

    public int getModuleLevel() {
        return sharedPreferences.getInt(MODULE_LEVEL, 0);
    }

    public String getParameters() {
        return sharedPreferences.getString(PARAMETERS, null);
    }
}