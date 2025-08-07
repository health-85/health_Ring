package com.healthbit.framework.util;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Description:
 * @Author: zxy(1051244836 @ qq.com)
 * @CreateDate: 2019/5/28
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class FileUtil {

    private static final String[] imageExts = new String[]{"jpg", "png", "jpeg", "gif", "bmp"};

    /**
     * 获取文件扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(".");
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    /**
     * 根据扩展名判断是否是图片
     *
     * @param extName
     * @return
     */
    public static boolean isImage(@NonNull String extName) {
        for (String imageExt : imageExts) {
            if (extName.equalsIgnoreCase(imageExt)) {
                return true;
            }
        }
        return false;
    }

    public static String getFolderName(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }


    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    public static void writeFile(String filePath, String content, boolean append) throws IOException {
        if (TextUtils.isEmpty(content)) {
            return;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
        } catch (IOException e) {
            throw e;
        } finally {
            fileWriter.close();
        }
    }

    public static void writeFile(File file, InputStream stream, boolean append) throws IOException {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                throw e;
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }
}
