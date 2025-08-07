package com.sdk.satwatch.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realsil.sdk.core.logger.ZLogger;
import com.realsil.sdk.core.utility.FileUtils;
import com.realsil.sdk.support.file.RxFiles;
import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.R;
import com.sdk.satwatch.bean.BluImgBean;
import com.sdk.satwatch.util.RxUtil;
import com.sdk.satwatch.util.Utils;
import com.sw.watches.bean.DeviceInfo;
import com.sw.watches.bleUtil.ByteToStringUtil;
import com.sw.watches.bluetooth.ParseWatchesData;
import com.sw.watches.bluetooth.SIATCommand;
import com.sw.watches.listener.SimplePerformerListener;
import com.sw.watches.service.ZhBraceletService;
import com.sw.watches.util.LogUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

//上传图片
public class UploadImgActivity extends AppCompatActivity {

    private TextView tvMsg;
    private String mFilePath;
    private ZhBraceletService mBleService;

    private StringBuilder mBuilder = new StringBuilder();
    private List<BluImgBean> mBluImgList = new ArrayList<>();
    public ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private volatile int mTimes = 0;
    private volatile int mIndex = 0;
    private volatile int mOldIndex = 0;
    private volatile int mArrayIndex = 0;
    private volatile String[] mArrayList;

    private int mSend56ComTime = 0;

    private int mSend57ComTime = 0;

    private boolean isSending = false;

    private final static int SEND_57_COM = 101;
    private final static int SEND_56_COM = 102;

    private Disposable mTimeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_img);
        tvMsg = findViewById(R.id.tv_msg);
        mBleService = MyApplication.getZhBraceletService();
        isSending = false;
        String[] cameraPerm = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,};
        ActivityCompat.requestPermissions(this, cameraPerm, 2525);
        mBleService.addSimplePerformerListenerLis(listener);
    }

    private void startTimeCount() {
        if (mTimeDisposable != null) {
            mTimeDisposable.dispose();
        }
        mTimeDisposable = Observable.interval(2, TimeUnit.SECONDS)
                .compose(RxUtil.IoToMainObserve())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        tvMsg.setText(mBuilder.toString());
                    }
                });
    }

    private SimplePerformerListener listener = new SimplePerformerListener() {

        @Override
        public void onResponseReceiveImgInfo(boolean isSendImgAgain, String msg) {
            super.onResponseReceiveImgInfo(isSendImgAgain, msg);
            mHandler.removeMessages(SEND_57_COM);
            mSend57ComTime = 0;
            if (isSendImgAgain) {
                if (mBluImgList != null && mArrayIndex < (mBluImgList.size() - 1)) {
                    mArrayIndex++;
                    startSend56Com();
                } else {
                    isSending = false;
                    mBuilder.append("=========传输完毕=========\n");
                }
            } else {
                //发送图片
                send57Img();
            }
        }

        @Override
        public void onResponseStartSendImgInfo(String msg) {
            super.onResponseStartSendImgInfo(msg);
            LogUtil.i(" onResponseStartSendImgInfo msg " + msg);
            mBuilder.append("=========收到0x56回复=========\n");
            mSend56ComTime = 0;
            mHandler.removeMessages(SEND_56_COM);
            mIndex = 0;
            mOldIndex = 0;
            if (mBluImgList != null && mArrayIndex < mBluImgList.size()) {
                BluImgBean bean = mBluImgList.get(mArrayIndex);
                if (!TextUtils.isEmpty(bean.getImg())) {
                    mArrayList = bean.getImg().split(",");
                    mBuilder.append("=========图" + (mArrayIndex + 1) + "传输开始=========\n");
                }
            }
            send57Img();
        }
    };

    public void upload(View view) {
        if (mBleService == null) return;
        if (mBluImgList == null || mBluImgList.isEmpty()) {
            Toast.makeText(this, "请选择文件", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isSending) {
            Toast.makeText(this, "正在发送图片", Toast.LENGTH_SHORT).show();
            return;
        }

        isSending = true;

        mTimes = 0;
        mIndex = 0;
        mOldIndex = 0;
        mArrayIndex = 0;
        mSend56ComTime = 0;
        mSend57ComTime = 0;
        mBuilder.append("=========开始上传图片发送数据=========\n");
        tvMsg.setText(mBuilder.toString());
        startTimeCount();
        startSend56Com();
    }

    public void clean(View view) {
        mBuilder.delete(0, mBuilder.toString().length());
        tvMsg.setText("");
    }

    private void startSend56Com() {
        synchronized (this){
            if (mBluImgList != null && mArrayIndex < mBluImgList.size()) {
                send56ComMsg();
                BluImgBean bean = mBluImgList.get(mArrayIndex);
                if (!TextUtils.isEmpty(bean.getImg())) {
                    byte[] addr = parseByte(bean.getAddress());
                    byte[] size = parseByte(bean.getSize());
                    mBleService.sendBleImgCom(addr, size);
                    mBuilder.append("=========图" + (mArrayIndex + 1) + "发送56传输命令" + mSend56ComTime + "次=========\n");
//                    LogUtil.i(" startSend56Com " + ByteToStringUtil.ByteToString(SIATCommand.getSendImgCom(SIATCommand.SIGN_HEAD, addr, size)));
                }
            } else if (mArrayIndex >= mBluImgList.size()) {
                isSending = false;
                mBuilder.append("=========传输完毕=========\n");
            }
        }
    }

    private void send57Img() {
        synchronized (this) {
            if (mArrayList != null && mIndex < mArrayList.length) {
                try {
                    send57ComMsg();

                    mOldIndex = mIndex;
                    int len = 100;
                    if (mIndex + 100 > mArrayList.length - 1) {
                        len = mArrayList.length - mIndex;
                    }
                    byte[] bytes = new byte[len];
                    for (int k = 0; k < len; k++) {
                        String s = mArrayList[mIndex].replace("0X", "");
                        int decimalValue = Integer.parseInt(s, 16);
                        bytes[k] = ByteToStringUtil.intToByte(decimalValue);
                        mIndex++;
                    }
                    mTimes++;
                    byte[] temp = bytes;
                    mBuilder.append("图" + (mArrayIndex + 1) + "：发送" + mIndex + "个数据,共发送了" + mTimes + "次\n");
                    mBleService.sendThread(temp);
//                    LogUtil.i(" send57Img " + ByteToStringUtil.ByteToString(bytes));
                } catch (Exception e) {
                    e.printStackTrace();
                    mBuilder.append("传输图片异常：" + e + "\n");
                }
            } else {
                mBuilder.append("=========图" + (mArrayIndex + 1) + "传输完毕=========\n");
            }
        }
    }

    private void send56ComMsg() {
        mSend56ComTime++;
        Message message = new Message();
        message.what = SEND_56_COM;
        message.arg1 = mSend56ComTime;
        message.arg2 = mArrayIndex;
        mHandler.sendMessageDelayed(message, 5000);
    }

    private void send57ComMsg() {
        mSend57ComTime++;
        Message message = new Message();
        message.what = SEND_57_COM;
        message.arg1 = mSend57ComTime;
        message.arg2 = mArrayIndex;
        mHandler.sendMessageDelayed(message, 1000);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case SEND_57_COM:
                    if (msg.arg1 > 2) {
                        isSending = false;
                        mSend57ComTime = 0;
                        mBuilder.append("=========图" + (mArrayIndex + 1) + "发送图片数据失败=========\n");
                    } else {
                        mIndex = mOldIndex;
                        mBuilder.append("=========重新发送数据=========\n");
                        send57Img();
                    }
                    break;
                case SEND_56_COM:
                    if (msg.arg1 > 2) {
                        isSending = false;
                        mSend56ComTime = 0;
                        mBuilder.append("=========图" + (mArrayIndex + 1) + "发送56命令失败=========\n");
                    } else {
                        startSend56Com();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private byte[] parseByte(String data) {
        if (TextUtils.isEmpty(data)) return null;
        byte[] bytes = new byte[4];
        String s = data.replace("0X", "").replace("0x", "");
        int index = 0;
        for (int i = 0; i + 2 <= s.length(); i += 2) {
            String temp = s.substring(i, i + 2);
            int decimalValue = Integer.parseInt(temp, 16);
            if (index < 4) {
                bytes[index] = ByteToStringUtil.intToByte(decimalValue);
                index++;
            }
        }
        return bytes;
    }

    public void sleFile(View view) {
        if (!hasExternalStoragePermission()) {
            openFileAccessManager();
            return;
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new RxFiles(this).request(Intent.ACTION_GET_CONTENT, "*/*")
                    .subscribe(s -> {
                                ZLogger.v(String.format("accept: %s, suffix:%s", s, FileUtils.getSuffix(s)));
                                mFilePath = s;
                                mBuilder.append("文件地址：" + mFilePath + "\n");
                                tvMsg.setText(mBuilder.toString());
                                getFileMsg();
                            },
                            t -> {
                                ZLogger.e("onError: " + t.toString());
                                mBuilder.append("选择文件异常：" + t.toString() + "\n");
                                tvMsg.setText(mBuilder.toString());
                            },
                            () -> ZLogger.v("OnComplete"));
        }
    }

    private String getFileMsg() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mFilePath.contains("/external_path")) {
                    mFilePath = mFilePath.replace("/external_path", Utils.getSDPath());
                }
                Log.i("", " getFileMsg " + mFilePath);
                FileInputStream fis = null;
                try {
                    int length = 0;
                    File file = new File(mFilePath);
                    byte[] buffer = new byte[1024]; // 创建缓冲区
                    fis = new FileInputStream(file);
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        if (!((length = fis.read(buffer)) != -1)) break;
                        sb.append(new String(buffer, 0, length)); // 将读取的内容追加到StringBuilder中
                    }
                    String fileContent = sb.toString(); // 获取文件内容
                    if (!TextUtils.isEmpty(fileContent)) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(fileContent);
                            String data = jsonObject.optString("data");
                            if (!TextUtils.isEmpty(data)) {
                                mBluImgList = new Gson().fromJson(data, new TypeToken<List<BluImgBean>>() {
                                }.getType());
                                LogUtil.i(" data ", data);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBuilder.append("文件异常：" + e + "\n");
                                    tvMsg.setText(mBuilder.toString());
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBuilder.append("文件异常：" + e + "\n");
                            tvMsg.setText(mBuilder.toString());
                        }
                    });
                }
            }
        });
        return "";
    }

    public static boolean hasExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return true;
    }

    private void openFileAccessManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        if (mBleService != null) {
            mBleService.removeSimplePerformerListenerLis(listener);
        }
        super.onDestroy();
    }

}
