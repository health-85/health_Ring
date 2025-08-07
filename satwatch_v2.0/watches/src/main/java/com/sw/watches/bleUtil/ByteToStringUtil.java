package com.sw.watches.bleUtil;

import android.util.Log;

import com.sw.watches.util.LogUtil;

/**
 * Byte转String工具库
 */
public class ByteToStringUtil {

    public static final String[] stringArr = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public ByteToStringUtil() {

    }

    public static String byteToString(int b) {
        if (b < 0) {
            b += 256;
        }
        return stringArr[b /= 16] + stringArr[b % 16];
    }

    public static String byteArrayToString(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            buffer.append(byteToString(bytes[i]));
        }
        return buffer.toString();
    }

    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < bytes.length; ++i) {
//                Log.i(" toHexString ", " bytes " + (bytes[i] & 255) + " Hex " + Integer.toHexString(bytes[i] & 255));
                if ((bytes[i] & 255) < 16){
                    stringBuilder.append("0");
                    stringBuilder.append(Integer.toHexString(bytes[i] & 255));
                }else {
                    stringBuilder.append(Integer.toHexString(bytes[i] & 255));
                }
//                if (i != bytes.length - 1) {
//                    stringBuilder.append(",");
//                }
            }
            return stringBuilder.toString();
        }
    }

    public static String ByteToString(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data) {
            stringBuilder.append(String.format("%02X ", byteChar));
        }
        return stringBuilder.toString();
    }

    public static String byteToString(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(data.length);
//        stringBuilder.append("[");
        for (int i = 0; i < data.length; i++) {
            byte[] b = new byte[1];
            b[0] = data[i];
            if (i == data.length - 1) {
                stringBuilder.append(byteToInt(b) + " ");
            } else {
                stringBuilder.append(byteToInt(b) + " ");
            }
        }
//        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * 将byte数组转换为int数据
     *
     * @param b 字节数组
     * @return 生成的int数据
     */
    public static int byteToInt(byte[] b) {
        int data = 0;
        for (int i = 0; i < b.length; i++) {
            data += (b[i] & 0x0ff) << (i * 8);
        }
        return data;
    }

    public static int byteToInt(byte b) {
        int data = 0;
        byte[] bt = new byte[1];
        bt[0] = b;
        for (int i = 0; i < bt.length; i++) {
            data += (bt[i] & 0x0ff) << (i * 8);
        }
        return data;
    }

    public static int byteTo7Int(byte b) {
        int data = b & 0x7f;
        int sign = (b & 0x80) >> 7;
//        LogUtil.i(" byteTo7Int " , byteToInt(b) + " data " + data + " sign " + sign);
        return sign == 1 ? -data : data;
    }

    public static byte[] intToBytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public static byte intToByte(int i) {
        return (byte)(i & 0xFF);
    }
}