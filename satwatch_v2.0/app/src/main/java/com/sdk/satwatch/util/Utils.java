package com.sdk.satwatch.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class Utils {

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static void MyLog(String tag, String msg) {
        System.out.println("MyLog:" + tag + "->" + msg);
    }

    public static String GetFormat(int length, float value) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(length);
        df.setGroupingSize(0);
        df.setRoundingMode(RoundingMode.FLOOR);
        return df.format(value);
    }

    //限制连续点击
    public static boolean isEffectiveClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            flag = true;
        }
        return flag;
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param filePath The path of file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null)
            return false;
        if (file.exists())
            return file.isFile();
        if (!createOrExistsDir(file.getParentFile()))
            return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param dirPath The path of directory.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null)
            return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return whether the file exists.
     *
     * @param filePath The path of file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        } else {
            sdDir = Environment.getRootDirectory();
        }
        return sdDir.toString();
    }

    /**
     * Delete the file.
     *
     * @param srcFilePath The path of source file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFile(final String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    /**
     * Delete the file.
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }


    public static boolean saveFileString(String filePath, String content) {
        if (!createOrExistsFile(filePath))
            return false;
        if (TextUtils.isEmpty(content))
            return false;
        FileOutputStream fos;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream(fos);
            bos.write(content.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static long ipToTen(String ip) {
        int decimalValue = 0;
        String[] i = ip.split(":");
        if (i.length > 0){
            decimalValue = Integer.parseInt(i[i.length - 1], 16);
        }
        return decimalValue;
    }

    /**
     * 是否是相同的设备
     * @param oldMac
     * @param mac
     * @return
     */
    public static boolean isEqualMac(String oldMac, String mac){
        if (TextUtils.isEmpty(oldMac) || TextUtils.isEmpty(mac)) return false;
        try {
            String[] oldMacString = oldMac.split(":");
            String[] macString = mac.split(":");
            for (int i = 0; i < oldMacString.length && i < macString.length; i++) {
                int oldValue = Integer.parseInt(oldMacString[i], 16);
                int value = Integer.parseInt(macString[i], 16);
                if ((oldValue == value) || (i == oldMacString.length - 1 && (oldValue  + 1) == value)){

                }else {
                    return false;
                }
                Utils.MyLog(" isEqualMac ", " isEqualMac oldValue " + oldValue + " value " + value + " oldMac " + oldMac + " mac " + mac);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return TextUtils.equals(oldMac, mac);
        }
    }

}
