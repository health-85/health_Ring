package com.sw.watches.bleUtil;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static boolean createBond(Class clazz, BluetoothDevice device) {
        try {
            return ((Boolean)clazz.getMethod("createBond", new Class[0]).invoke(device, new Object[0])).booleanValue();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean removeBond(Class<?> clazz, BluetoothDevice device) {
        try {
            return ((Boolean)clazz.getMethod("removeBond", new Class[0])
                    .invoke(device, new Object[0])).booleanValue();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setPin(Class<? extends BluetoothDevice> clazz, BluetoothDevice device, String str) {
        try {
            boolean bool1 = false;
            (new Object[1])[0] = str.getBytes();
            Boolean bool = (Boolean)clazz.getDeclaredMethod("setPin", new Class[] { byte[].class }).invoke(device, new Object[1]);
            Log.e("returnValue", "" + bool);
        } catch (SecurityException securityException) {
            securityException.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }

    public static boolean cancelPairingUserInput(Class<?> clazz, BluetoothDevice device) {
        try {
            return ((Boolean)clazz.getMethod("cancelPairingUserInput", new Class[0])
                    .invoke(device, new Object[0])).booleanValue();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean cancelBondProcess(Class<?> clazz, BluetoothDevice device) {
        try {
            return ((Boolean)clazz.getMethod("cancelBondProcess", new Class[0])
                    .invoke(device, new Object[0])).booleanValue();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setPairingConfirmation(Class<?> clazz, BluetoothDevice device, boolean bool) {
        Class[] arrayOfClass  = new Class[1];
        arrayOfClass[0] = boolean.class;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Boolean.valueOf(bool);
        try {
            clazz.getDeclaredMethod("setPairingConfirmation", arrayOfClass).invoke(device, arrayOfObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void listMethodAndField(Class clazz) {
        try {
            Method[] methods = clazz.getMethods();
            for (int b2 = 0; b2 < methods.length; b2++) {
                StringBuilder stringBuilder = new StringBuilder();
                Log.e("method name", stringBuilder.append(methods[b2].getName()).append(";and the i is:").append(b2).toString());
            }
            Field[] fields = clazz.getFields();
            for (int b1 = 0; b1 < fields.length; b1++)
                Log.e("Field name", fields[b1].getName());
        } catch (SecurityException securityException) {
            securityException.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}