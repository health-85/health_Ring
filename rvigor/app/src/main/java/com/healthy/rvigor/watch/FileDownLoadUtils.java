package com.healthy.rvigor.watch;

import android.os.Environment;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.net.http.HttpRequestBase;
import com.healthy.rvigor.net.http.HttpRequestCallBackBase;
import com.healthy.rvigor.util.LogUtils;
import com.healthy.rvigor.util.WatchBeanUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 文件下载
 */
public class FileDownLoadUtils {

//    private static final String TAG = "FileDownLoadUtils";
//
//    public MyApplication application = null;
//
//    public FileDownLoadUtils( MyApplication application) {
//        this.application = application;
//    }
//
//
//    public static interface IDownLoadListener {
//
//        public void onStart();
//
//        public void onProgress(int percent);
//
//        public void onComplate();
//
//        public void onError(Exception ex);
//
//    }
//
//
//    private final List<IDownLoadListener> listeners = new ArrayList<>();
//
//    /**
//     * 添加监听
//     *
//     * @param loadListener
//     */
//    public void addListener(IDownLoadListener loadListener) {
//        validateUIThread();
//        if (!listeners.contains(loadListener)) {
//            listeners.add(loadListener);
//        }
//    }
//
//    /**
//     * 移除监听
//     *
//     * @param loadListener
//     */
//    public void removeListener(IDownLoadListener loadListener) {
//        validateUIThread();
//        listeners.remove(loadListener);
//    }
//
//
//    private void performStart() {
//        validateUIThread();
//        for (int i = 0; i < listeners.size(); i++) {
//            listeners.get(i).onStart();
//        }
//    }
//
//    private void performComplate() {
//        validateUIThread();
//        for (int i = 0; i < listeners.size(); i++) {
//            listeners.get(i).onComplate();
//        }
//    }
//
//    private void performError(Exception ex) {
//        validateUIThread();
//        for (int i = 0; i < listeners.size(); i++) {
//            listeners.get(i).onError(ex);
//        }
//    }
//
//    private void performProgress(int percent) {
//        validateUIThread();
//        for (int i = 0; i < listeners.size(); i++) {
//            listeners.get(i).onProgress(percent);
//        }
//    }
//
//    /**
//     * 是否正在下载升级文件
//     *
//     * @return
//     */
//    public boolean hasDownloadDeviceUpdateZipFile() {
//        validateUIThread();
//        return application.getDeviceUpdateHttpComponet().containsRequestByTag("downloadDeviceUpdateZipFile");
//    }
//
//    private void validateUIThread() {
////        if (!application.IsUIThread()) {
////            throw new RuntimeException("必须在UI线程中操作");
////        }
//    }
//
//    public boolean DownloadDeviceUpdateZipFile(String url) {
//        validateUIThread();
//        if (!application.getDeviceUpdateHttpComponet().containsRequestByTag("downloadDeviceUpdateZipFile")) {
//            performStart();
//            ApiHelper
//                    .get(url)
//                    .setTag("downloadDeviceUpdateZipFile")
//                    .requestCallBack(new downloadDeviceUpdateZipFileCallBack(null, this))
//                    .execute( MyApplication.getInstance().getDeviceUpdateHttpComponet());
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * 下载
//     */
//    private static class downloadDeviceUpdateZipFileCallBack extends HttpRequestCallBackBase {
//
//        private FileDownLoadUtils fileDownLoadUtils = null;
//
//        public downloadDeviceUpdateZipFileCallBack(UIDataBinderBase dataBinder, FileDownLoadUtils fileDownLoadUtils) {
//            super(dataBinder);
//            this.fileDownLoadUtils = fileDownLoadUtils;
//        }
//
//        public downloadDeviceUpdateZipFileCallBack(UIDataBinderBase dataBinder, String onSuccessInUIInvokeMethodName, String onErrorInUIInvokeMethodName, String onAfterInUIInvokeMethodName, FileDownLoadUtils fileDownLoadUtils) {
//            super(dataBinder, onSuccessInUIInvokeMethodName, onErrorInUIInvokeMethodName, onAfterInUIInvokeMethodName);
//            this.fileDownLoadUtils = fileDownLoadUtils;
//        }
//
//        @Override
//        public Object convertSuccess(CommonApplication con, HttpRequestBase req, long contentlength, InputStream input) throws Exception {
//            copyUpgradeFileToDownload(con, input, contentlength, fileDownLoadUtils);
//            return null;
//        }
//
//
//        @Override
//        public void onSuccessInUI(CommonApplication con, HttpRequestBase req, Object object) {
//            super.onSuccessInUI(con, req, object);
//            fileDownLoadUtils.performComplate();
//        }
//
//
//        @Override
//        public void onErrorInUI(CommonApplication con, HttpRequestBase req, Exception ex) {
//            super.onErrorInUI(con, req, ex);
//            fileDownLoadUtils.performError(ex);
//            showToast(con, ex.getMessage() + "");
//            ex.printStackTrace();
//        }
//
//        @Override
//        public void onAfter(CommonApplication con, HttpRequestBase req) {
//            super.onAfter(con, req);
//            fileDownLoadUtils = null;
//        }
//
//
//        private void copyUpgradeFileToDownload(CommonApplication con
//                , InputStream inputStream, long contentlength, FileDownLoadUtils afileDownLoadUtils) throws Exception {
//            StringBuilder temppath = new StringBuilder();
//            temppath.append(Environment.getExternalStorageDirectory().getPath());
//            temppath.append("/Download/test.temp");
//
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(Environment.getExternalStorageDirectory().getPath());
//            String fileName;
//            if (WatchBeanUtil.getWatchStyle() == WatchBeanUtil.C100_WATCH_TYPE){
//                fileName = "/Download/test.bin";
//            }else {
//                fileName = "/Download/test.zip";
//            }
//            stringBuilder.append(fileName);
//            FileOutputStream fos = null;
//            try {
//                File tempfile = new File(temppath.toString());
//                if (tempfile.exists()) {
//                    if (tempfile.canWrite()) {
//                        tempfile.delete();
//                    }
//                }else {
//                    FileUtil.createOrExistsFile(tempfile);
//                }
//                fos = new FileOutputStream(tempfile);
//                byte[] buff = new byte[1024 * 8];
//                int rd = 0;
//                long readlen = 0;
//                int percent = caculatePercent(readlen, contentlength);
//                long oldtime = System.currentTimeMillis();
//                performProgress(con, afileDownLoadUtils, percent);
//                while (true) {
//                    rd = inputStream.read(buff);
//                    if (rd > 0) {
//                        fos.write(buff, 0, rd);
//                        readlen += rd;
//                        if ((System.currentTimeMillis() - oldtime) > 2000) {
//                            oldtime = System.currentTimeMillis();
//                            percent = caculatePercent(readlen, contentlength);
//                            performProgress(con, afileDownLoadUtils, percent);
//                        }
//                    }
//                    if (rd == -1) {
//                        break;
//                    }
//                }
//                inputStream.close();
//                fos.close();
//                if (readlen != contentlength) {
//                    throw new Exception("下载失败");
//                } else {
//                    performProgress(con, afileDownLoadUtils, 100);
//                }
//                File upfile = new File(stringBuilder.toString());
//                boolean deltrue = true;
//                if (upfile.exists()) {
//                    if (!upfile.delete()) {
//                        deltrue = false;
//                    }
//                }
//                if (deltrue) {
//                    if (!tempfile.renameTo(upfile)) {
//                        new Exception("文件操作失败");
//                    }
//                } else {
//                    new Exception("文件操作失败");
//                }
//            } catch (Exception e) {
//                try {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                } catch (IOException ioException) {
//                }
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException ioException) {
//                    }
//                }
//                throw e;
//            }
//        }
//
//
//        /**
//         * 派遣进度
//         *
//         * @param con
//         * @param afileDownLoadUtils
//         * @param percent
//         */
//        private void performProgress(CommonApplication con, FileDownLoadUtils afileDownLoadUtils, int percent) {
//            con.getUIHandler().PostAndWait(new ProgressUIRun(percent, afileDownLoadUtils));
//        }
//
//        /**
//         * 计算百分比
//         *
//         * @param readlen
//         * @param totallen
//         * @return
//         */
//        private int caculatePercent(long readlen, long totallen) {
//            if (totallen > 0) {
//                return (int) (((double) readlen * 100) / (double) totallen);
//            }
//            return 0;
//        }
//
//        /**
//         * 下载进度
//         */
//        private static class ProgressUIRun implements Runnable {
//
//            private int percent = 0;
//
//            private FileDownLoadUtils fileDownLoadUtils = null;
//
//            public ProgressUIRun(int percent, FileDownLoadUtils fileDownLoadUtils) {
//                this.percent = percent;
//                this.fileDownLoadUtils = fileDownLoadUtils;
//            }
//
//            @Override
//            public void run() {
//                fileDownLoadUtils.performProgress(percent);
//                fileDownLoadUtils = null;
//            }
//        }
//    }
//
//    /**
//     * 下载图片文本
//     */
//    public boolean downloadImgTextFile(String url) {
//        ApiHelper.get(url)
//                .setTag("DownLoadImgTextFileCallBack")
//                .requestCallBack(new DownLoadImgTextFileCallBack(null, this))
//                .execute( MyApplication.getInstance().getDeviceUpdateHttpComponet());
//        return true;
//    }
//
//    /**
//     * 下载图片文本
//     */
//    private static class DownLoadImgTextFileCallBack extends HttpRequestCallBackBase {
//
//        private FileDownLoadUtils fileDownLoadUtils = null;
//
//        public DownLoadImgTextFileCallBack(UIDataBinderBase dataBinder, FileDownLoadUtils fileDownLoadUtils) {
//            super(dataBinder);
//            this.fileDownLoadUtils = fileDownLoadUtils;
//        }
//
//        @Override
//        public Object convertSuccess(CommonApplication con, HttpRequestBase req, long contentlength, InputStream input) throws Exception {
//            LogUtils.i(" DownLoadImgTextFileCallBack convertSuccess ");
//            copyUpgradeFileToDownload(con, input, contentlength, fileDownLoadUtils);
//            return null;
//        }
//
//        @Override
//        public void onSuccessInUI(CommonApplication con, HttpRequestBase req, Object object) {
//            super.onSuccessInUI(con, req, object);
//            LogUtils.i(" DownLoadImgTextFileCallBack onSuccessInUI ");
//        }
//
//
//        @Override
//        public void onErrorInUI(CommonApplication con, HttpRequestBase req, Exception ex) {
//            super.onErrorInUI(con, req, ex);
//            LogUtils.i(" DownLoadImgTextFileCallBack onErrorInUI ");
//        }
//
//        @Override
//        public void onAfter(CommonApplication con, HttpRequestBase req) {
//            super.onAfter(con, req);
//        }
//
//        private void copyUpgradeFileToDownload(CommonApplication con
//                , InputStream inputStream, long contentlength, FileDownLoadUtils afileDownLoadUtils) throws Exception {
//            StringBuilder temppath = new StringBuilder();
//            temppath.append(Environment.getExternalStorageDirectory().getPath());
//            temppath.append("/Download/test.text");
//
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(Environment.getExternalStorageDirectory().getPath());
//            String fileName = "/Download/textImg.text";
//            stringBuilder.append(fileName);
//            FileOutputStream fos = null;
//            try {
//                File tempfile = new File(temppath.toString());
//                if (tempfile.exists()) {
//                    if (tempfile.canWrite()) {
//                        tempfile.delete();
//                    }
//                } else {
//                    FileUtil.createOrExistsFile(tempfile);
//                }
//                fos = new FileOutputStream(tempfile);
//                byte[] buff = new byte[1024 * 8];
//                int rd = 0;
//                long readlen = 0;
//                int percent = caculatePercent(readlen, contentlength);
//                long oldtime = System.currentTimeMillis();
//                performProgress(con, afileDownLoadUtils, percent);
//                while (true) {
//                    rd = inputStream.read(buff);
//                    if (rd > 0) {
//                        fos.write(buff, 0, rd);
//                        readlen += rd;
//                        if ((System.currentTimeMillis() - oldtime) > 2000) {
//                            oldtime = System.currentTimeMillis();
//                            percent = caculatePercent(readlen, contentlength);
//                            performProgress(con, afileDownLoadUtils, percent);
//                        }
//                    }
//                    if (rd == -1) {
//                        break;
//                    }
//                }
//                inputStream.close();
//                fos.close();
//                if (readlen != contentlength) {
//                    throw new Exception("下载失败");
//                } else {
//                    performProgress(con, afileDownLoadUtils, 100);
//                }
//                File upfile = new File(stringBuilder.toString());
//                boolean deltrue = true;
//                if (upfile.exists()) {
//                    if (!upfile.delete()) {
//                        deltrue = false;
//                    }
//                }
//                if (deltrue) {
//                    if (!tempfile.renameTo(upfile)) {
//                        new Exception("文件操作失败");
//                    }
//                } else {
//                    new Exception("文件操作失败");
//                }
//            } catch (Exception e) {
//                try {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                } catch (IOException ioException) {
//                }
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException ioException) {
//                    }
//                }
//                throw e;
//            }
//        }
//
//        /**
//         * 派遣进度
//         *
//         * @param con
//         * @param afileDownLoadUtils
//         * @param percent
//         */
//        private void performProgress(CommonApplication con, FileDownLoadUtils afileDownLoadUtils, int percent) {
//            con.getUIHandler().PostAndWait(new downloadDeviceUpdateZipFileCallBack.ProgressUIRun(percent, afileDownLoadUtils));
//        }
//
//        /**
//         * 计算百分比
//         *
//         * @param readlen
//         * @param totallen
//         * @return
//         */
//        private int caculatePercent(long readlen, long totallen) {
//            if (totallen > 0) {
//                return (int) (((double) readlen * 100) / (double) totallen);
//            }
//            return 0;
//        }
//
//        /**
//         * 下载进度
//         */
//        private static class ProgressUIRun implements Runnable {
//
//            private int percent = 0;
//
//            private FileDownLoadUtils fileDownLoadUtils = null;
//
//            public ProgressUIRun(int percent, FileDownLoadUtils fileDownLoadUtils) {
//                this.percent = percent;
//                this.fileDownLoadUtils = fileDownLoadUtils;
//            }
//
//            @Override
//            public void run() {
//                fileDownLoadUtils.performProgress(percent);
//                fileDownLoadUtils = null;
//            }
//        }
//    }
}
