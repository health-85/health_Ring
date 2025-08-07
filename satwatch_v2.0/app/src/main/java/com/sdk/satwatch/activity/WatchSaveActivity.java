package com.sdk.satwatch.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import com.sdk.satwatch.BuildConfig;
import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.R;
import com.sdk.satwatch.util.DateTimeUtils;
import com.sdk.satwatch.util.FileUtil;
import com.sdk.satwatch.util.RxUtil;
import com.sdk.satwatch.util.Utils;
import com.sw.watches.bean.WatchSaveInfo;
import com.sw.watches.listener.SimplePerformerListener;
import com.sw.watches.service.ZhBraceletService;
import com.sw.watches.util.LogUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class WatchSaveActivity extends AppCompatActivity {

    private boolean isSendSave = false;

    private ZhBraceletService mBleService = MyApplication.getZhBraceletService();

    private TextView tvMsg;

    private StringBuffer buffer = new StringBuffer();
    private StringBuffer originalBuffer = new StringBuffer();

    private String mSavePath;

    private String mOriginalSavePath;

    private NestedScrollView mScrollView;

    private String mLocalPath = Utils.getSDPath() + "/Download/WatchSave";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_save);
        tvMsg = findViewById(R.id.tv_msg);
        mScrollView = findViewById(R.id.scrollView);
        mBleService.addSimplePerformerListenerLis(mPerformerListener);
        mSavePath = mLocalPath + File.separator + "Save_" + DateTimeUtils.s_long_2_str(System.currentTimeMillis(), DateTimeUtils.f_format_) + ".txt";
        mOriginalSavePath = mLocalPath + File.separator + "OriginalSave_" + DateTimeUtils.s_long_2_str(System.currentTimeMillis(), DateTimeUtils.f_format_) + ".txt";
    }

    //开始发送存储指令
    public void testSave(View view) {
        isSendSave = true;
        FileUtil.deleteDir(mLocalPath);
//        buffer.delete(0, buffer.toString().length());
//        originalBuffer.delete(0, originalBuffer.toString().length());
        mSavePath = mLocalPath + File.separator + "Save_" + DateTimeUtils.s_long_2_str(System.currentTimeMillis(), DateTimeUtils.f_format_) + ".txt";
        mOriginalSavePath = mLocalPath + File.separator + "OriginalSave_" + DateTimeUtils.s_long_2_str(System.currentTimeMillis(), DateTimeUtils.f_format_) + ".txt";

//        tvMsg.setText("");
        mBleService.sendWatchSaveCom();
        startSetMsg();
    }

    //清除存储
    public void cleanLog(View view) {
        buffer.delete(0, buffer.toString().length());
        originalBuffer.delete(0, originalBuffer.toString().length());
        tvMsg.setText("");
    }

    //分享内容
    public void share(View view) {
        boolean isSave = Utils.saveFileString(mSavePath, buffer.toString());
        shareFile(WatchSaveActivity.this, mSavePath);
    }

    //分享原始数据
    public void shareOriginal(View view) {
        boolean isSave = Utils.saveFileString(mOriginalSavePath, originalBuffer.toString());
        shareFile(WatchSaveActivity.this, mOriginalSavePath);
    }

    public void stopTest(View view) {
        mBleService.closeMeasurement();
    }

    public void shareFile(Context context, String fileName) {
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

    private SimplePerformerListener mPerformerListener = new SimplePerformerListener() {
        @Override
        public void onResponseWatchSaveInfo(WatchSaveInfo info, String log) {
            super.onResponseWatchSaveInfo(info, log);
            if (info != null) {
                buffer.append("---------手表存储数据---------------\n");
                buffer.append(info.getByteData() + "\n");
                buffer.append("A:" + info.getA() + "  ");
                buffer.append("B:" + info.getB() + "  ");
                buffer.append("C:" + info.getC() + "  ");
                buffer.append("D:" + info.getD() + "  ");
                buffer.append("Time：" + info.getTime() + "\n");
                originalBuffer.append(info.getByteData() + "\n");
            } else {
                buffer.append("---------接收结束---------------\n");
            }
        }

    };

    private Disposable mDisposable;

    private void startSetMsg() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        mDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .compose(RxUtil.IoToMainObserve())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (!TextUtils.isEmpty(buffer.toString())) {
                            tvMsg.setText(buffer.toString());
                        }
                    }
                });
    }

    private static final int MSG_SEND_SAVE = 0x15;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_SEND_SAVE:
                    isSendSave = true;
                    tvMsg.setText(buffer.toString());
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        mBleService.setReceiveSaveData(false);
        mBleService.removeSimplePerformerListenerLis(mPerformerListener);
    }
}
