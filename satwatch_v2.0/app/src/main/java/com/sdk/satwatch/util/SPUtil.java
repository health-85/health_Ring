package com.sdk.satwatch.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class SPUtil {
    private static final String FILE_NAME = "yjt_sp_data";
    public static final String HOME_INDEX_IMG_URL = "HOME_INDEX_IMG_URL";

    public static void saveData(Context context , String key, Object object){
        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }
        editor.commit();
    }

    public static void saveData(Context context ,String saveFileName, String key, Object object){
        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(saveFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }
        editor.commit();
    }

    public static Object getData(Context context , String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if("String".equals(type)){
            return sp.getString(key, (String)defaultObject);
        }
        else if("Integer".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }
        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }
        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }
        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }
        return null;
    }

    public static Object getData(Context context , String saveFileName ,String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(saveFileName, Context.MODE_PRIVATE);
        if("String".equals(type)){
            return sp.getString(key, (String)defaultObject);
        }
        else if("Integer".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }
        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }
        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }
        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }
        return null;
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

    public static void clear(Context context, String saveFileName) {
        SharedPreferences sp = context.getSharedPreferences(saveFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

    public static void clearValue(Context context,String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void clearValue(Context context,String saveFileName,String key) {
        SharedPreferences sp = context.getSharedPreferences(saveFileName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static boolean saveArray(Context mContext , List<String> list, String listName) {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, mContext.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1= sp.edit();
        mEdit1.putInt(listName,list.size());

        for(int i=0;i<list.size();i++) {
            mEdit1.remove(listName + i);
            mEdit1.putString(listName + i, list.get(i));
        }
        return mEdit1.commit();
    }
    public static List<String> loadArray(Context mContext,List<String> list,String listName) {
        SharedPreferences mSharedPreference1 = mContext.getSharedPreferences(FILE_NAME, mContext.MODE_PRIVATE);
        list.clear();
        int size = mSharedPreference1.getInt(listName, 0);
        for(int i=0;i<size;i++) {
            list.add(mSharedPreference1.getString(listName + i, null));
        }
        return list;
    }

    public static void deleteArray(Context context,String listName){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1= sp.edit();

        int size = sp.getInt(listName, 0);
        for(int i=0;i<size;i++) {
            mEdit1.remove(listName + i);
        }
        mEdit1.putInt(listName,0);
        mEdit1.commit();
    }
}
