package com.sdk.satwatch.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sdk.satwatch.BuildConfig;
import com.sdk.satwatch.listener.ScannerListener;
import com.sdk.satwatch.util.CompressUtil;
import com.sdk.satwatch.util.DateTimeUtils;
import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.R;
import com.sdk.satwatch.util.FileUtil;
import com.sdk.satwatch.util.RxUtil;
import com.sdk.satwatch.util.Utils;
import com.sdk.satwatch.adapter.MeasureHeartAdapter;
import com.sdk.satwatch.zip.zip.IZipCallback;
import com.sdk.satwatch.zip.zip.ZipManager;
import com.sw.watches.bean.DeviceModule;
import com.sw.watches.bean.ECGData;
import com.sw.watches.bean.ECGInfo;
import com.sw.watches.bean.HeartInfo;
import com.sw.watches.bean.PPGData;
import com.sw.watches.bean.PPGInfo;
import com.sw.watches.bean.WatchSaveInfo;
import com.sw.watches.bleUtil.ByteToStringUtil;
import com.sw.watches.listener.ConnectorListener;
import com.sw.watches.listener.SimplePerformerListener;
import com.sw.watches.service.ZhBraceletService;
import com.sw.watches.util.LogUtil;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


public class MeasurementActivity extends AppCompatActivity {

    final private String Tag = "MeasurementActivity.class";

    // 蓝牙相关
    private static ZhBraceletService mBleService = MyApplication.getZhBraceletService();

    private TextView my_heart;
    private TextView my_sbp;
    private TextView my_dbp;
    private TextView tv_ecg_msg;
    private TextView tv_ppg_msg;
    private NestedScrollView ecgScroll;
    private NestedScrollView ppgScroll;

    //    private RecyclerView rvEcgMeasure;
//    private RecyclerView rvPpgMeasure;
    private Handler mHandler;

    private volatile int ecgNum;
    private volatile int ppgNum;

    private volatile long mStartTime;
    private volatile long mEndTime;

    private static final int MSG_DATA_HEART = 0x13;
    private static final int MSG_DATA_ECG = 0x14;
    private static final int MSG_DATA_PPG = 0x15;
    private static final int MSG_DATA_ORIGINAL = 0x16;
    private static final int MSG_DATA_ECG_ORIGINAL = 0x17;
    private static final int MSG_DATA_PPG_ORIGINAL = 0x18;

    private volatile boolean isEcgSendAgain = true;
    private volatile boolean isPpgSendAgain = true;
    private volatile boolean isOriginalEcgSendAgain = true;
    private volatile boolean isOriginalPpgSendAgain = true;

    private StringBuffer ppgBuilder = new StringBuffer();
    private StringBuffer ppgOutBuilder = new StringBuffer();
    private StringBuffer ecgBuilder = new StringBuffer();
    private StringBuffer ecgOutBuilder = new StringBuffer();
    private StringBuffer originalBuilder = new StringBuffer();
    private StringBuffer originalECGOutBuilder = new StringBuffer();
    private StringBuffer originalPPGOutBuilder = new StringBuffer();

    private StringBuffer ecgShowOutBuilder = new StringBuffer();

    private StringBuffer ppgShowOutBuilder = new StringBuffer();
    private String ppgTextPath;
    private String ecgTextPath;
    private String originalECGTextPath;
    private String originalPPGTextPath;

    private String ppgFilePath;
    private String ecgFilePath;
    private String originalECGFilePath;
    private String originalPPGFilePath;

    private String localPath = Utils.getSDPath() + "/Download/ECGAndPPG";
//    private BaseQuickAdapter mEcgAdapter;
//    private BaseQuickAdapter mPpgAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        if (mBleService != null) {
            mBleService.addConnectorListener(mConnectorListener);
        }
        initView();
        handler_init();
        initData();
        startIntervalConnect();
    }

    void initView() {
        my_heart = (TextView) findViewById(R.id.my_heart);
        my_sbp = (TextView) findViewById(R.id.my_sbp);
        my_dbp = (TextView) findViewById(R.id.my_dbp);
        tv_ecg_msg = (TextView) findViewById(R.id.tv_ecg_msg);
        tv_ppg_msg = (TextView) findViewById(R.id.tv_ppg_msg);
        ecgScroll = (NestedScrollView) findViewById(R.id.ecg_scroll);
        ppgScroll = (NestedScrollView) findViewById(R.id.ppg_scroll);
//        rvEcgMeasure = findViewById(R.id.rv_ecg);
//        rvPpgMeasure = findViewById(R.id.rv_ppg);

//        mEcgAdapter = new MeasureHeartAdapter(new ArrayList<>());
//        rvEcgMeasure.setLayoutManager(new LinearLayoutManager(this));
//        rvEcgMeasure.setAdapter(mEcgAdapter);
//
//        mPpgAdapter = new MeasureHeartAdapter(new ArrayList<>());
//        rvPpgMeasure.setLayoutManager(new LinearLayoutManager(this));
//        rvPpgMeasure.setAdapter(mPpgAdapter);

        initPath();
    }

    void initData() {
        if (mBleService != null) {
            mBleService.addSimplePerformerListenerLis(mPerformerListener);
        }
    }

    private void initPath() {
        long time = System.currentTimeMillis();
        ppgFilePath = localPath + File.separator + "ECGAndPPG_" + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format_) + File.separator + "PPG";
        ecgFilePath = localPath + File.separator + "ECGAndPPG_" + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format_) + File.separator + "ECG";
        originalECGFilePath = localPath + File.separator + "ECGAndPPG_" + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format_) + File.separator + "ECGOriginal";
        originalPPGFilePath = localPath + File.separator + "ECGAndPPG_" + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format_) + File.separator + "PPGOriginal";
    }

    private Disposable mShowDisposable;

    private void startSetMsg() {
        if (mShowDisposable != null) {
            mShowDisposable.dispose();
        }
        mShowDisposable = Observable.interval(1800, TimeUnit.MILLISECONDS)
                .compose(RxUtil.IoToMainObserve())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (!TextUtils.isEmpty(ecgShowOutBuilder.toString())) {
                            tv_ecg_msg.setText(Html.fromHtml(ecgShowOutBuilder.toString()));
//                            ecgShowOutBuilder.delete(0, ecgShowOutBuilder.toString().length());
//                            ecgScroll.fullScroll(View.FOCUS_DOWN);
                        }
                        if (!TextUtils.isEmpty(ppgShowOutBuilder.toString())) {
                            tv_ppg_msg.setText(Html.fromHtml(ppgShowOutBuilder.toString()));
//                            ppgShowOutBuilder.delete(0, ppgShowOutBuilder.toString().length());
//                            ppgScroll.fullScroll(View.FOCUS_DOWN);
                        }
                    }
                });
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        closeMeasure();
//    }

    protected void onDestroy() {
        super.onDestroy();
        if (mBleService != null && mPerformerListener != null) {
            mBleService.removeSimplePerformerListenerLis(mPerformerListener);
            mBleService.removeConnectorListener(mConnectorListener);
        }
        closeMeasure();
    }

    void handler_init() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case MSG_DATA_HEART:
                        HeartInfo mHeartInfo = (HeartInfo) msg.obj;
                        my_heart.setText(getString(R.string.heart) + " = " + mHeartInfo.getHeartInfoHR());
                        my_sbp.setText(getString(R.string.sbp) + " = " + mHeartInfo.getHeartInfoSBP());
                        my_dbp.setText(getString(R.string.dbp) + " = " + mHeartInfo.getHeartInfoDBP());
//                        builder.append("-----------测量结果--------------\n");
//                        builder.append(getString(R.string.heart) + " = " + mHeartInfo.getHeartInfoHR() + "\n");
//                        builder.append(getString(R.string.sbp) + " = " + mHeartInfo.getHeartInfoSBP() + "\n");
//                        builder.append(getString(R.string.dbp) + " = " + mHeartInfo.getHeartInfoDBP() + "\n");
//                        tv_msg.setText(builder.toString());
                        break;
                    case MSG_DATA_ECG:
                        isEcgSendAgain = true;
                        String ecgText = (String) msg.obj;
//                        ecgBuilder.append(ecgText);
                        tv_ecg_msg.setText(Html.fromHtml(ecgText));
//                        mEcgAdapter.addData(ecgText);
//                        rvEcgMeasure.scrollToPosition(mEcgAdapter.getData().size() - 1);
//                        tv_ecg_msg.setText(Html.fromHtml(ecgBuilder.toString()));
//                        ecgScroll.fullScroll(View.FOCUS_DOWN);
                        break;
                    case MSG_DATA_PPG:
                        isPpgSendAgain = true;
                        String ppgText = (String) msg.obj;
                        ppgBuilder.append(ppgText);
                        tv_ppg_msg.setText(Html.fromHtml(ppgText));
//                        ppgScroll.fullScroll(View.FOCUS_DOWN);
                        break;
                    case MSG_DATA_ECG_ORIGINAL:
                        isOriginalEcgSendAgain = true;
                        String originalEcgText = (String) msg.obj;
                        originalECGOutBuilder.append(originalEcgText);
                        break;
                    case MSG_DATA_PPG_ORIGINAL:
                        isOriginalPpgSendAgain = true;
                        String originalPpgText = (String) msg.obj;
                        originalPPGOutBuilder.append(originalPpgText);
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public void openMesure(View view) {
        deleteFile();
        initPath();
        startMeasure();
        startMeasureTime();
        startSetMsg();
    }

    public void closeMesure(View view) {
        close();
    }

    private void close() {
        closeMeasure();
        saveData();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        if (mShowDisposable != null) {
            mShowDisposable.dispose();
        }
    }

    public void Calibration(View view) {
        if (mBleService != null) {
//            Utils.MyLog(Tag, "校准");
//            builder.append("-----------校准--------------\n");
//            tv_msg.setText(builder.toString());
            Utils.MyLog(Tag, "calibration");
//            mBleService.setUserCalibration(new UserCalibration(60, 150, 70));
            cleanData();
//            tv_ecg_msg.setText("");
//            tv_ppg_msg.setText("");
        }
    }

    private void cleanData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ecgNum = 0;
                ppgNum = 0;
                isEcgSendAgain = true;
                isPpgSendAgain = true;
                ecgBuilder.delete(0, ecgBuilder.toString().length());
                ppgBuilder.delete(0, ppgBuilder.toString().length());
                ecgOutBuilder.delete(0, ecgOutBuilder.toString().length());
                ppgOutBuilder.delete(0, ppgOutBuilder.toString().length());
                originalBuilder.delete(0, originalBuilder.toString().length());
                originalECGOutBuilder.delete(0, originalECGOutBuilder.toString().length());
                originalPPGOutBuilder.delete(0, originalPPGOutBuilder.toString().length());
                tv_ecg_msg.setText("");
                tv_ppg_msg.setText("");
//                if (mEcgAdapter != null) {
//                    mEcgAdapter.setNewData(new ArrayList());
//                }
//                if (mPpgAdapter != null) {
//                    mPpgAdapter.setNewData(new ArrayList());
//                }
            }
        });
    }

    public void MeasureInfo(View view) {
//        if (mBleService != null) {
////            Utils.MyLog(Tag, "测量结果");
////            builder.append("-----------上传测量结果--------------\n");
////            tv_msg.setText(builder.toString());
//            Utils.MyLog(Tag, "Measurement results");
//            mBleService.setMeasureInfo(new MesureInfo(61, 125, 68));
//        }
        closeMeasure();
        saveData();
        showExportDialog();
    }

    void sendHeartDate(HeartInfo mHeartInfo) {
        Message message = new Message();
        message.what = MSG_DATA_HEART;
        message.obj = mHeartInfo;
        mHandler.sendMessage(message);
    }

    private SimplePerformerListener mPerformerListener = new SimplePerformerListener() {
        public void onResponseHeartInfo(HeartInfo mHeartInfo) {
//            Utils.MyLog(Tag, "onResponseMesureInfo");
//            sendHeartDate(mHeartInfo);
        }

        @Override
        public void onResponseECGInfo(ECGInfo ecgInfo) {
            super.onResponseECGInfo(ecgInfo);
            synchronized (this) {
                StringBuffer builder = new StringBuffer();
                String text = "[ECG][编号" + (++ecgNum) + "]";
                builder.append("<font color='#DC143C'>" + text + "</font>");
                if (ecgInfo != null && ecgInfo.getEcgList() != null && ecgInfo.getEcgList().size() > 0) {
                    for (ECGData ecgData : ecgInfo.getEcgList()) {
                        builder.append("[");
                        for (int i = 0; i < ecgData.getDataList().size(); i++) {
                            Integer t = ecgData.getDataList().get(i);
                            if (i == ecgData.getDataList().size() - 1) {
                                builder.append(t);
                                ecgOutBuilder.append(t);
                            } else {
                                builder.append(t + ",");
                                ecgOutBuilder.append(t + ", ");
                            }
                        }
                        builder.append("]");
                        builder.append("<br/>");
//                                        builder.append("<br/>");
                        ecgOutBuilder.append("\n");
                    }
                }
                ecgShowOutBuilder.delete(0, ecgShowOutBuilder.toString().length());
                ecgShowOutBuilder.append(builder.toString());

                if (ecgInfo.getData() != null && ecgInfo.getData().length > 0) {
                    originalECGOutBuilder.append("[");
                    for (int i = 0; i < ecgInfo.getData().length; i++) {
                        if (i == ecgInfo.getData().length - 1) {
                            originalECGOutBuilder.append(ByteToStringUtil.byteToInt(ecgInfo.getData()[i]));
                        } else {
                            originalECGOutBuilder.append(ByteToStringUtil.byteToInt(ecgInfo.getData()[i]) + ",");
                        }
                    }
                    originalECGOutBuilder.append("]");
                    originalECGOutBuilder.append("\n");
                }

//                Observable.just(ecgInfo).flatMap(new Function<ECGInfo, ObservableSource<String>>() {
//                            @Override
//                            public ObservableSource<String> apply(ECGInfo ecgInfo) throws Exception {
//                                StringBuffer builder = new StringBuffer();
//                                String text = "[ECG][编号" + (++ecgNum) + "]";
//                                builder.append("<font color='#DC143C'>" + text + "</font>");
//                                if (ecgInfo != null && ecgInfo.getEcgList() != null && ecgInfo.getEcgList().size() > 0) {
//                                    for (ECGData ecgData : ecgInfo.getEcgList()) {
//                                        builder.append("[");
//                                        for (int i = 0; i < ecgData.getDataList().size(); i++) {
//                                            Integer t = ecgData.getDataList().get(i);
//                                            if (i == ecgData.getDataList().size() - 1) {
//                                                builder.append(t);
//                                                ecgOutBuilder.append(t);
//                                            } else {
//                                                builder.append(t + ",");
//                                                ecgOutBuilder.append(t + ", ");
//                                            }
//                                        }
//                                        builder.append("]");
////                                        builder.append("<br/>");
//                                        ecgOutBuilder.append("\n");
//                                    }
//                                }
//                                if (ecgInfo.getData() != null && ecgInfo.getData().length > 0) {
//                                    originalECGOutBuilder.append("[");
//                                    for (int i = 0; i < ecgInfo.getData().length; i++) {
//                                        if (i == ecgInfo.getData().length - 1) {
//                                            originalECGOutBuilder.append(ByteToStringUtil.byteToInt(ecgInfo.getData()[i]));
//                                        } else {
//                                            originalECGOutBuilder.append(ByteToStringUtil.byteToInt(ecgInfo.getData()[i]) + ",");
//                                        }
//                                    }
//                                    originalECGOutBuilder.append("]");
//                                    originalECGOutBuilder.append("\n");
//                                }
////                if (isEcgSendAgain) {
////                    isEcgSendAgain = false;
////                    Message message = new Message();
////                    message.what = MSG_DATA_ECG;
////                    message.obj = builder.toString();
////                    mHandler.sendMessageDelayed(message, 1000);
////                }
//                                if (isEcgSendAgain) {
//                                    isEcgSendAgain = false;
//                                    return Observable.just(builder.toString()).delay(1, TimeUnit.SECONDS);
//                                } else {
//                                    return Observable.just("");
//                                }
//                            }
//                        }).compose(RxUtil.IoToMainObserve())
//                        .subscribe(new Observer<String>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(String value) {
//                                if (!TextUtils.isEmpty(value)) {
//                                    isEcgSendAgain = true;
//                                    tv_ecg_msg.setText(Html.fromHtml(value));
////                                    if (mEcgAdapter.getData().size() > 100) {
////                                        mEcgAdapter.setNewData(new ArrayList());
////                                    } else {
////                                        mEcgAdapter.addData(value);
////                                    }
////                                    rvEcgMeasure.scrollToPosition(mEcgAdapter.getData().size() - 1);
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
            }
        }

        @Override
        public void onResponsePPGInfo(PPGInfo ppgInfo) {
            super.onResponsePPGInfo(ppgInfo);
            synchronized (this) {
                StringBuffer builder = new StringBuffer();
                String text = "[PPG][编号" + (++ppgNum) + "]";
                builder.append("<font color='#DC143C'>" + text + "</font>");
                if (ppgInfo != null && ppgInfo.getPpgList() != null && ppgInfo.getPpgList().size() > 0) {
                    for (PPGData ppgData : ppgInfo.getPpgList()) {
                        builder.append("[");
                        for (int i = 0; i < ppgData.getDataList().size(); i++) {
                            Integer t = ppgData.getDataList().get(i);
                            if (i == ppgData.getDataList().size() - 1) {
                                builder.append(t);
                                ppgOutBuilder.append(t);
                            } else {
                                builder.append(t + ",");
                                ppgOutBuilder.append(t + ", ");
                            }
                        }
                        builder.append("]");
                        builder.append("<br/>");
                        ppgOutBuilder.append("\n");
                    }
                }
                ppgShowOutBuilder.delete(0, ppgShowOutBuilder.toString().length());
                ppgShowOutBuilder.append(builder.toString());
                if (ppgInfo.getData() != null && ppgInfo.getData().length > 0) {
                    originalPPGOutBuilder.append("[");
                    for (int i = 0; i < ppgInfo.getData().length; i++) {
                        if (i == ppgInfo.getData().length - 1) {
                            originalPPGOutBuilder.append(ByteToStringUtil.byteToInt(ppgInfo.getData()[i]));
                        } else {
                            originalPPGOutBuilder.append(ByteToStringUtil.byteToInt(ppgInfo.getData()[i]) + ",");
                        }
                    }
                    originalPPGOutBuilder.append("]");
                    originalPPGOutBuilder.append("\n");
                }
//                Observable.just(ppgInfo).flatMap(new Function<PPGInfo, ObservableSource<String>>() {
//                            @Override
//                            public ObservableSource<String> apply(PPGInfo ppgInfo) throws Exception {
//                                StringBuffer builder = new StringBuffer();
//                                String text = "[PPG][编号" + (++ppgNum) + "]";
//                                builder.append("<font color='#DC143C'>" + text + "</font>");
//                                if (ppgInfo != null && ppgInfo.getPpgList() != null && ppgInfo.getPpgList().size() > 0) {
//                                    for (PPGData ppgData : ppgInfo.getPpgList()) {
//                                        builder.append("[");
//                                        for (int i = 0; i < ppgData.getDataList().size(); i++) {
//                                            Integer t = ppgData.getDataList().get(i);
//                                            if (i == ppgData.getDataList().size() - 1) {
//                                                builder.append(t);
//                                                ppgOutBuilder.append(t);
//                                            } else {
//                                                builder.append(t + ",");
//                                                ppgOutBuilder.append(t + ", ");
//                                            }
//                                        }
//                                        builder.append("]");
//                                        ppgOutBuilder.append("\n");
//                                    }
//                                }
//                                if (ppgInfo.getData() != null && ppgInfo.getData().length > 0) {
//                                    originalPPGOutBuilder.append("[");
//                                    for (int i = 0; i < ppgInfo.getData().length; i++) {
//                                        if (i == ppgInfo.getData().length - 1) {
//                                            originalPPGOutBuilder.append(ByteToStringUtil.byteToInt(ppgInfo.getData()[i]));
//                                        } else {
//                                            originalPPGOutBuilder.append(ByteToStringUtil.byteToInt(ppgInfo.getData()[i]) + ",");
//                                        }
//                                    }
//                                    originalPPGOutBuilder.append("]");
//                                    originalPPGOutBuilder.append("\n");
//                                }
//                                if (isPpgSendAgain) {
//                                    isPpgSendAgain = false;
//                                    return Observable.just(builder.toString()).delay(1, TimeUnit.SECONDS);
//                                } else {
//                                    return Observable.just("");
//                                }
//                            }
//                        }).compose(RxUtil.IoToMainObserve())
//                        .subscribe(new Observer<String>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(String value) {
//                                if (!TextUtils.isEmpty(value)) {
//                                    isPpgSendAgain = true;
//                                    tv_ppg_msg.setText(Html.fromHtml(value));
////                                    if (mPpgAdapter.getData().size() > 100) {
////                                        mPpgAdapter.setNewData(new ArrayList());
////                                    } else {
////                                        mPpgAdapter.addData(value);
////                                    }
////                                    rvPpgMeasure.scrollToPosition(mPpgAdapter.getData().size() - 1);
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
            }
        }


        //        @Override
//        public void onResponsePCGByteArray(byte[] bytes) {
//            super.onResponsePCGByteArray(bytes);
//            synchronized (this) {
//                StringBuffer builder = new StringBuffer();
//                if (bytes != null && bytes.length > 0) {
//                    builder.append("[");
//                    for (int i = 0; i < bytes.length; i++) {
//                        if (i == bytes.length - 1) {
//                            builder.append(ByteToStringUtil.byteToInt(bytes[i]));
//                        } else {
//                            builder.append(ByteToStringUtil.byteToInt(bytes[i]) + ",");
//                        }
//                    }
//                    builder.append("]");
//                    builder.append("\n");
//                }
//                if (isOriginalPpgSendAgain){
//                    isOriginalPpgSendAgain = false;
//                    Message message = new Message();
//                    message.what = MSG_DATA_PPG_ORIGINAL;
//                    message.obj = builder.toString();
//                    mHandler.sendMessage(message);
//                }
////                LogUtil.i(" onResponsePCGByteArray ", originalPPGOutBuilder.toString());
//            }
//        }
//
//        @Override
//        public void onResponseECGByteArray(byte[] bytes) {
//            super.onResponseECGByteArray(bytes);
//            synchronized (this) {
//                StringBuffer builder = new StringBuffer();
//                if (bytes != null && bytes.length > 0) {
//                    builder.append("[");
//                    for (int i = 0; i < bytes.length; i++) {
//                        if (i == bytes.length - 1) {
//                            builder.append(ByteToStringUtil.byteToInt(bytes[i]));
//                        } else {
//                            builder.append(ByteToStringUtil.byteToInt(bytes[i]) + ",");
//                        }
//                    }
//                    builder.append("]");
//                    builder.append("\n");
//                }
//                if (isOriginalEcgSendAgain){
//                    isOriginalEcgSendAgain = false;
//                    Message message = new Message();
//                    message.what = MSG_DATA_ECG_ORIGINAL;
//                    message.obj = builder.toString();
//                    mHandler.sendMessage(message);
//                }
////                LogUtil.i(" onResponseECGByteArray ", originalECGOutBuilder.toString());
//            }
//        }

        @Override
        public void onResponseByteArray(byte[] bytes) {
            super.onResponseByteArray(bytes);
        }
    };

    private void showExportDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_export, null, false);
        builder.setView(dialogView);
        Dialog dialog = builder.show();

        TextView tvPpgExport = dialogView.findViewById(R.id.tv_ppg_export);
        TextView tvEcgExport = dialogView.findViewById(R.id.tv_ecg_export);
        TextView tvCleanExport = dialogView.findViewById(R.id.tv_clean_export);
        TextView tvEcgOriginalExport = dialogView.findViewById(R.id.tv_ecg_original_export);
        TextView tvPpgOriginalExport = dialogView.findViewById(R.id.tv_ppg_original_export);
        tvPpgExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mEndTime == 0) {
//                    if (mBleService != null) {
//                        mBleService.closeMeasurement();
//                    }
//                    mEndTime = System.currentTimeMillis();
//                }
//                StringBuffer builder1 = new StringBuffer();
//                builder1.append("===== 开始时间：" + DateTimeUtils.s_long_2_str(mStartTime, DateTimeUtils.f_format) + " =====\n");
//                builder1.append("===== 结束时间：" + DateTimeUtils.s_long_2_str(mEndTime, DateTimeUtils.f_format) + " =====\n");
//                builder1.append("===== 总数据量：" + /*mECGSize*/ecgNum + " =====\n");
//                builder1.append("===== 总历时：" + (mEndTime - mStartTime) / (double) (1000) + " =====\n");
//                builder1.append(ecgOutBuilder.toString());
//                Utils.deleteFile(ecgTextPath);
//                boolean isSave = Utils.saveFileString(ecgTextPath, builder1.toString());
//                Toast.makeText(MeasurementActivity.this, isSave ? "保存成功:" + ecgTextPath : "保存失败", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//                shareFile(MeasurementActivity.this, ecgTextPath);
                zipFile(dialog, ppgFilePath);
            }
        });
        tvEcgExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mEndTime == 0) {
//                    if (mBleService != null) {
//                        mBleService.closeMeasurement();
//                    }
//                    mEndTime = System.currentTimeMillis();
//                }
//                StringBuffer builder1 = new StringBuffer();
//                builder1.append("===== 开始时间：" + DateTimeUtils.s_long_2_str(mStartTime, DateTimeUtils.f_format) + " =====\n");
//                builder1.append("===== 结束时间：" + DateTimeUtils.s_long_2_str(mEndTime, DateTimeUtils.f_format) + " =====\n");
//                builder1.append("===== 总数据量：" + /*mECGSize*/ecgNum + " =====\n");
//                builder1.append("===== 总历时：" + (mEndTime - mStartTime) / (double) (1000) + " =====\n");
//                builder1.append(ecgOutBuilder.toString());
//                Utils.deleteFile(ecgTextPath);
//                boolean isSave = Utils.saveFileString(ecgTextPath, builder1.toString());
//                Toast.makeText(MeasurementActivity.this, isSave ? "保存成功:" + ecgTextPath : "保存失败", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//                shareFile(MeasurementActivity.this, ecgTextPath);
                zipFile(dialog, ecgFilePath);
            }
        });
        tvEcgOriginalExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                StringBuffer builder1 = new StringBuffer();
//                builder1.append(originalECGOutBuilder.toString());
//                boolean isDelete = Utils.deleteFile(originalECGTextPath);
//                Log.i(" result ", builder1.toString() + " delete " + isDelete);
//                boolean isSave = Utils.saveFileString(originalECGTextPath, builder1.toString());
//                Toast.makeText(MeasurementActivity.this, isSave ? "保存成功:" + originalECGTextPath : "保存失败", Toast.LENGTH_SHORT).show();
//                if (isSave) shareFile(MeasurementActivity.this, originalECGTextPath);
//                dialog.dismiss();
                zipFile(dialog, originalECGFilePath);
            }
        });
        tvPpgOriginalExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                StringBuffer builder1 = new StringBuffer();
//                builder1.append(originalPPGOutBuilder.toString());
//                boolean isDelete = Utils.deleteFile(originalPPGTextPath);
//                Log.i(" result ", builder1.toString() + " delete " + isDelete);
//                boolean isSave = Utils.saveFileString(originalPPGTextPath, builder1.toString());
//                Toast.makeText(MeasurementActivity.this, isSave ? "保存成功:" + originalPPGTextPath : "保存失败", Toast.LENGTH_SHORT).show();
//                if (isSave) shareFile(MeasurementActivity.this, originalPPGTextPath);
//                dialog.dismiss();
                zipFile(dialog, originalPPGFilePath);
            }
        });
        tvCleanExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanData();
                Toast.makeText(MeasurementActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void zipFile(Dialog dialog, String path) {
        ArrayList<File> fileList = getFiles(path);
        ZipManager.zip(fileList, path + ".zip", new IZipCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int percentDone) {

            }

            @Override
            public void onFinish(boolean success) {
                shareFile(MeasurementActivity.this, path + ".zip");
                dialog.dismiss();
            }
        });
    }

    private void startMeasure() {
        cleanData();
        long time = System.currentTimeMillis();
        ppgTextPath = ppgFilePath + File.separator + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format_) + "_ppg.txt";
        ecgTextPath = ecgFilePath + File.separator + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format_) + "_ecg.txt";
        originalECGTextPath = originalECGFilePath + File.separator + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format_) + "_originalECG.txt";
        originalPPGTextPath = originalPPGFilePath + File.separator + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format_) + "_originalPPG.txt";
        mStartTime = System.currentTimeMillis();
        if (mBleService != null) {
            Utils.MyLog(Tag, "turn on");
            mBleService.openMeasurement();
        }
    }

    private void deleteFile() {
        FileUtil.deleteDir(localPath);
        FileUtil.deleteDir(ecgFilePath);
        FileUtil.deleteDir(ppgFilePath);
        FileUtil.deleteDir(originalECGFilePath);
        FileUtil.deleteDir(originalPPGFilePath);
        FileUtil.deleteFile(ecgFilePath + ".zip");
        FileUtil.deleteFile(ppgFilePath + ".zip");
        FileUtil.deleteFile(originalECGFilePath + ".zip");
        FileUtil.deleteFile(originalPPGFilePath + ".zip");
    }

    private void closeMeasure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBleService != null) {
                    Utils.MyLog(Tag, "shut down");
                    mBleService.closeMeasurement();
                }
                mEndTime = System.currentTimeMillis();
            }
        });
    }

    private boolean savePPGMsg() {
        StringBuffer builder1 = new StringBuffer();
        builder1.append("===== 开始时间：" + DateTimeUtils.s_long_2_str(mStartTime, DateTimeUtils.f_format) + " =====\n");
        builder1.append("===== 结束时间：" + DateTimeUtils.s_long_2_str(mEndTime, DateTimeUtils.f_format) + " =====\n");
        builder1.append("===== 总数据量：" + /*mPPGSize*/ ppgNum + " =====\n");
        builder1.append("===== 总历时：" + (mEndTime - mStartTime) / (double) (1000) + " =====\n");
        builder1.append(ppgOutBuilder.toString());
//        Utils.deleteFile(ppgTextPath);
        boolean isSave = Utils.saveFileString(ppgTextPath, builder1.toString());
        LogUtil.i(Tag, " path == " + ppgTextPath);
        return isSave;
    }

    private boolean savePPGOriginalMsg() {
        StringBuffer builder1 = new StringBuffer();
        builder1.append(originalPPGOutBuilder.toString());
//        boolean isDelete = Utils.deleteFile(originalPPGTextPath);
//        Log.i(" result ", builder1.toString() + " delete " + isDelete);
        boolean isSave = Utils.saveFileString(originalPPGTextPath, builder1.toString());
        LogUtil.i(Tag, " path == " + originalPPGTextPath);
        return isSave;
    }

    private boolean saveECGMsg() {
        StringBuffer builder1 = new StringBuffer();
        builder1.append("===== 开始时间：" + DateTimeUtils.s_long_2_str(mStartTime, DateTimeUtils.f_format) + " =====\n");
        builder1.append("===== 结束时间：" + DateTimeUtils.s_long_2_str(mEndTime, DateTimeUtils.f_format) + " =====\n");
        builder1.append("===== 总数据量：" + /*mECGSize*/ecgNum + " =====\n");
        builder1.append("===== 总历时：" + (mEndTime - mStartTime) / (double) (1000) + " =====\n");
        builder1.append(ecgOutBuilder.toString());
//        Utils.deleteFile(ecgTextPath);
        boolean isSave = Utils.saveFileString(ecgTextPath, builder1.toString());
        LogUtil.i(Tag, " path == " + ecgTextPath);
        return isSave;
    }

    private boolean saveECGOriginalMsg() {
        StringBuffer builder1 = new StringBuffer();
        builder1.append(originalECGOutBuilder.toString());
//        boolean isDelete = Utils.deleteFile(originalECGTextPath);
//        Log.i(" result ", builder1.toString() + " delete " + isDelete);
        boolean isSave = Utils.saveFileString(originalECGTextPath, builder1.toString());
        LogUtil.i(Tag, " path == " + originalECGTextPath);
        return isSave;
    }

    private ArrayList<File> getFiles(String path) {
        if (TextUtils.isEmpty(path)) return null;
        ArrayList<File> list = new ArrayList<>();
        try {
            File file = new File(path);
            if (file != null && file.listFiles() != null && file.listFiles().length > 0) {
                for (File file1 : file.listFiles()) {
                    list.add(file1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveData() {
        saveECGMsg();
        savePPGMsg();
        saveECGOriginalMsg();
        savePPGOriginalMsg();
    }

    private Disposable mDisposable;

    private void startMeasureTime() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        mDisposable = Observable.interval(1, TimeUnit.HOURS)
                .flatMap(new Function<Long, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Long aLong) throws Exception {
                        LogUtil.i(" startMeasureTime ", " aLong == " + aLong);
                        closeMeasure();
                        saveData();
                        return Observable.just("").delay(1, TimeUnit.SECONDS);
                    }
                }).compose(RxUtil.IoToMainObserve())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        startMeasure();
                    }
                });
    }

    public static void shareFile(Context context, String fileName) {
        File file = new File(fileName);
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                share.putExtra(Intent.EXTRA_STREAM, contentUri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            share.setType("application/vnd.ms-excel");//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        } else {
            Toast.makeText(context, "分享文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private ConnectorListener mConnectorListener = new ConnectorListener() {
        @Override
        public void onConnectAndWrite() {
//            startMeasure();
//            startMeasureTime();
            Utils.MyLog(Tag, "已连接");
        }

        @Override
        public void onDisconnect() {
            closeMeasure();
            saveData();
            Utils.MyLog(Tag, "已断开");
        }

        @Override
        public void onConnectFailed() {
            closeMeasure();
            saveData();
            Utils.MyLog(Tag, "连接失败");
        }

        @Override
        public void onReConnect() {
            Utils.MyLog(Tag, "重新连接，升级时会调用");
        }

        @Override
        public void onConnectSuccess() {

        }
    };

    /**
     * 自动重连
     */
    public void startIntervalConnect() {
        Observable.interval(1, TimeUnit.MINUTES)
                .compose(RxUtil.IoToMainObserve())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtil.i(Tag, " startIntervalConnect aLong " + aLong);
                        if (mBleService != null && !mBleService.isConnectState()) {
                            LogUtil.i(Tag, " startIntervalConnect ConnectState " + mBleService.isConnectState());
                            mBleService.tryConnectDevice();
                            startMeasure();
                            startMeasureTime();
                        }
                    }
                });
    }


}
