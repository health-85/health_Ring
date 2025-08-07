package com.sdk.satwatch.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.sdk.satwatch.bean.BoBean;
import com.sdk.satwatch.BuildConfig;
import com.sdk.satwatch.listener.ScannerListener;
import com.sdk.satwatch.receive.BluetoothPairingRequest;
import com.sdk.satwatch.util.DateTimeUtils;
import com.sdk.satwatch.bean.HrBean;
import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.R;
import com.sdk.satwatch.util.RxUtil;
import com.sdk.satwatch.util.SPUtil;
import com.sdk.satwatch.util.ScanDevice;
import com.sdk.satwatch.util.SpConfig;
import com.sdk.satwatch.util.Utils;
import com.sw.watches.application.ZhbraceletApplication;
import com.sw.watches.bean.AbnormalHeartInfo;
import com.sw.watches.bean.AbnormalHeartListInfo;
import com.sw.watches.bean.BreatheInfo;
import com.sw.watches.bean.EmotionInfo;
import com.sw.watches.bean.HeartListInfo;
import com.sw.watches.bean.DeviceInfo;
import com.sw.watches.bean.DeviceModule;
import com.sw.watches.bean.ECGInfo;
import com.sw.watches.bean.EnviTempInfo;
import com.sw.watches.bean.HeartInfo;
import com.sw.watches.bean.HeatInfo;
import com.sw.watches.bean.HrvInfo;
import com.sw.watches.bean.MotionInfo;
import com.sw.watches.bean.PPGData;
import com.sw.watches.bean.PPGInfo;
import com.sw.watches.bean.PoHeartInfo;
import com.sw.watches.bean.PressureInfo;
import com.sw.watches.bean.SiestaInfo;
import com.sw.watches.bean.SleepInfo;
import com.sw.watches.bean.SleepLogInfo;
import com.sw.watches.bean.SleepOxInfo;
import com.sw.watches.bean.SnoreInfo;
import com.sw.watches.bean.SpoData;
import com.sw.watches.bean.SpoInfo;
import com.sw.watches.bean.StrengthInfo;
import com.sw.watches.bean.SwitchInfo;
import com.sw.watches.bean.SymptomInfo;
import com.sw.watches.bean.SymptomInfo2;
import com.sw.watches.bean.SymptomListInfo;
import com.sw.watches.bean.TireInfo;
import com.sw.watches.bean.UvInfo;
import com.sw.watches.bean.WoHeartInfo;
import com.sw.watches.bleUtil.ByteToStringUtil;
import com.sw.watches.bluetooth.ParseWatchesData;
import com.sw.watches.bluetooth.SIATCommand;
import com.sw.watches.listener.ConnectorListener;
import com.sw.watches.listener.SimplePerformerListener;
import com.sw.watches.notification.NotificationUtils;
import com.sw.watches.service.ZhBraceletService;
import com.sw.watches.util.LogUtil;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class BraceletActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, ScannerListener {

    final private String Tag = "BraceletActivity.class";

    private static final int MSG_CONNECT = 0x11;
    private static final int MSG_DISCONNECT = 0x12;
    private static final int MSG_CONNECTFAIL = 0x13;
    private static final int MSG_CONNECTING = 0x14;
    private static final int MSG_SEND_SAVE = 0x15;

    public static final String EXTRA_DEVICE = "ble_device";

    private DeviceModule mBleDeviceWrapper;

    private ZhBraceletService mBleService = MyApplication.getZhBraceletService();

    private ToggleButton togg_wear, togg_unit, togg_time, togg_languagen;
    private CheckBox check_po_heart, check_wo_heart, togg_taiwan, togg_zhuanwan, togg_disturb, togg_heart, togg_snore;
    private TextView tvMsg;
    private TextView et_test,gap_time;

    private ScrollView scrollView;
    private Handler mHandler;

    private BluetoothPairingRequest mPairReceiver;

    private boolean isSendSave = false;

    private final Map<Long, List<Integer>> ecgdatas = new HashMap<>();

    private final Map<Long, List<Integer>> ppgdatas = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bracelet);
        initView();
        handler_init();
        intData();
        sendMsg(MSG_CONNECTING);
        MyApplication.getInstance().startZhBraceletService(this);
        ScanDevice.getInstance(this).stopScan();
        //启动保活服务
//        TraceServiceImpl.sShouldStopService = false;
//        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);
        ScanDevice.getInstance(this).addScanListener(this);
//        mBleService.initC100Sdk(MyApplication.getApp());
        openBleService();
        AutoJudgmentConnectDevice(null);

        mPairReceiver = new BluetoothPairingRequest();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.setPriority(10000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mPairReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }else {
            registerReceiver(mPairReceiver, filter);
        }

    }

    void initView() {
        togg_wear = (ToggleButton) findViewById(R.id.togg_wear);
        togg_unit = (ToggleButton) findViewById(R.id.togg_unit);
        togg_time = (ToggleButton) findViewById(R.id.togg_time);
        togg_languagen = (ToggleButton) findViewById(R.id.togg_languagen);
        check_po_heart = (CheckBox) findViewById(R.id.check_po_heart);
        check_wo_heart = (CheckBox) findViewById(R.id.check_wo_heart);
        togg_taiwan = (CheckBox) findViewById(R.id.togg_taiwan);
        togg_zhuanwan = (CheckBox) findViewById(R.id.togg_zhuanwan);
        togg_disturb = (CheckBox) findViewById(R.id.togg_disturb);
        togg_heart = (CheckBox) findViewById(R.id.togg_heart);
        togg_snore = (CheckBox) findViewById(R.id.togg_snore);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        et_test = (TextView) findViewById(R.id.et_test);
        gap_time=findViewById(R.id.et_edit_gap_time);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
    }

    void intData() {
        mBleDeviceWrapper = getIntent().getParcelableExtra(EXTRA_DEVICE);
        if (mBleService != null) {
            togg_wear.setChecked(mBleService.getWearType());
            togg_unit.setChecked(mBleService.getUnit());
            togg_time.setChecked(mBleService.getTimeFormat());
            togg_heart.setChecked(mBleService.getHighHeartRemind());
            togg_snore.setChecked(mBleService.getSnoreMonitor());
            togg_languagen.setChecked(mBleService.getLanguagen() > 0);
            check_po_heart.setChecked(mBleService.getPoHeart());
            check_wo_heart.setChecked(mBleService.getWoHeart());
            togg_taiwan.setChecked(mBleService.getTaiWan());
            togg_zhuanwan.setChecked(mBleService.getZhuanWan());
            togg_disturb.setChecked(mBleService.getNotDisturb());
            mBleService.addConnectorListener(mConnectorListener);
            mBleService.addSimplePerformerListenerLis(mPerformerListener);
        }
        togg_time.setOnCheckedChangeListener(this);
        togg_languagen.setOnCheckedChangeListener(this);
        togg_wear.setOnCheckedChangeListener(this);
        togg_unit.setOnCheckedChangeListener(this);
        check_po_heart.setOnCheckedChangeListener(this);
        check_wo_heart.setOnCheckedChangeListener(this);
        togg_taiwan.setOnCheckedChangeListener(this);
        togg_zhuanwan.setOnCheckedChangeListener(this);
        togg_disturb.setOnCheckedChangeListener(this);
        togg_heart.setOnCheckedChangeListener(this);
        togg_snore.setOnCheckedChangeListener(this);
    }

    public void openBleService() {
        if (mBleService != null) return;
        Intent intent = new Intent(this, ZhBraceletService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ZhbraceletApplication.getInstance().setZhBraceletService(null);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ZhbraceletApplication.getInstance().setZhBraceletService(((ZhBraceletService.LocalBinder) service)
                    .getService());
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPairReceiver);
        mBleService.removeConnectorListener(mConnectorListener);
        mBleService.removeSimplePerformerListenerLis(mPerformerListener);
    }

    public void BindBle(View view) {
        if (mBleService != null) {
            Utils.MyLog(Tag, "Binding device");
            mBleService.BindDevice(mBleDeviceWrapper);
        }
    }

    public void UnBindBle(View view) {
        if (mBleService != null) {
//            Utils.MyLog(Tag, "设备连接状态 = " + mBleService.getBleConnectState());
//            Utils.MyLog(Tag, "解除绑定");
            Utils.MyLog(Tag, "Device connection status= " + mBleService.getBleConnectState());
            Utils.MyLog(Tag, "Unbinding");

            mBleService.UnBindDevice();


            Intent intent = new Intent(BraceletActivity.this, ScanDeviceActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void AutoJudgmentConnectDevice(View view) {
//        if (mBleService != null) {
//            Boolean isBand = NotificationUtils.isBindDeviceMac(this);
//            Utils.MyLog(Tag, "Determine if you have bound = " + isBand);
//            if (isBand) {
//                Utils.MyLog(Tag, "Connect a bound device");
//                mBleService.tryConnectDevice();
//            } else {
        if (mBleDeviceWrapper != null) {
            Utils.MyLog(Tag, "Binding device");
            LogUtil.i(Tag, "Binding device");
            mBleService.BindDevice(mBleDeviceWrapper);
        }
//            }
//        }
    }

    public void TryConnectBle(View view) {
        if (mBleService != null) {
            sendMsg(MSG_CONNECTING);
            mBleService.tryConnectDevice();
        }
    }

    public void syncTime(View view) {
        if (mBleService != null) {
            mBleService.syncTime();
        }
    }

    public void findDevice(View view) {
        if (!Utils.isEffectiveClick()) return;
        if (mBleService != null) {
            mBleService.findDevice();
        }
    }

    public void setDeviceGapTime(View view) {
        String text=gap_time.getText().toString();
        int number = Integer.parseInt(text);

        if (mBleService != null && TextUtils.isDigitsOnly(text)) {
            mBleService.setCollectTimeGap(number);
        }
    }

    public void getDeviceInfo(View view) {
        if (mBleService != null) {
            mBleService.getDeviceInfo();
        }
    }

    public void setUserInfo(View view) {
//        if (mBleService != null) {
//            mBleService.setUserInfo(new UserInfo(50, 25, 50, false));
//        }
        startActivity(new Intent(BraceletActivity.this, SetUserInfoActivity.class));
    }

    public void sendEnry(View view) {
        if (mBleService != null) {
            String data = "zzX3i+4x7r68mi1E42rRLg==";
            builder.append("发送加密数据 " + data + "\n");
            byte[] bytes;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bytes = Base64.getDecoder().decode(data);
            } else {
                bytes = android.util.Base64.decode(data, android.util.Base64.DEFAULT);
            }
            builder.append("发送的加密数组:" + ParseWatchesData.byteFormatDate(bytes) + "\n");
            tvMsg.setText(builder.toString());
            mBleService.sendEncryptCom(bytes);
//            mBleService.sendThread(getEncryptCom(bytes));

//            testBreathe();

//            testbraceletBreathe();

//            testsyminfoBreathe();

//            testSnore();

//            testSymptom();

            testSpo();

//            testTempInfo();

        }
    }

//    public static byte[] getEncryptCom(byte[] data) {
////        data[28]={0xAB,0,0,0x14,0,0,0,0,1,0,0x84,0,code[0],code[1],code[2],…,code[15]}
//        byte[] comByte = new byte[29];
//        comByte[0] = SIATCommand.SIGN_AA_HEAD;
//        comByte[3] = 20;
//        comByte[8] = 1;
//        comByte[10] = -124;
//        comByte[12] = 15;
//        for (int i = 0; i < 16; i++) {
//            comByte[13 + i] = data[i];
//        }
//        return comByte;
//    }

    public void sendTest(View view) {

//        sendOxHeart();

        sendTest();
    }

    public void sendParseTest(View view) {
        parseTest();
    }

    public void updateDevice(View view) {
        if (mBleService == null) {
            return;
        }
        Intent intent = new Intent(BraceletActivity.this, UpgradeActivity.class);
//        Intent intent = new Intent(BraceletActivity.this, UpgradeDfuActivity.class);
        startActivity(intent);
    }

    public void uploadImg(View view) {
        if (mBleService == null) {
            return;
        }
        Intent intent = new Intent(BraceletActivity.this, UploadImgActivity.class);
//        Intent intent = new Intent(BraceletActivity.this, UpgradeDfuActivity.class);
        startActivity(intent);
    }

    public void MeasurementAcitivity(View view) {
        if (mBleService == null) {
            return;
        }
        Intent intent = new Intent(BraceletActivity.this, MeasurementActivity.class);
        startActivity(intent);
    }

    public void NotifaceActivity(View view) {
        if (mBleService == null) {
            return;
        }
        Intent intent = new Intent(BraceletActivity.this, NotifaceActivity.class);
        startActivity(intent);
    }

    public void ReminActivity(View view) {
        if (mBleService == null) {
            return;
        }
        Intent intent = new Intent(BraceletActivity.this, ReminActivity.class);
        startActivity(intent);
    }

    public void CameraControlActivity(View view) {
//        Intent intent = new Intent(BraceletActivity.this, CameraControlActivity.class);
//        startActivity(intent);
    }

    public void AlarmActivity(View view) {
        if (mBleService == null) {
            return;
        }
        Intent intent = new Intent(BraceletActivity.this, AlarmActivity.class);
        startActivity(intent);
    }

    void sendMsg(int MSG) {
        Message message = new Message();
        message.what = MSG;
        mHandler.sendMessage(message);
    }

    public void sendSave(View view) {
        if (mBleService == null) {
            return;
        }
        startActivity(new Intent(this, WatchSaveActivity.class));
    }

    public void cleanLog(View view) {
        builder.delete(0, builder.toString().length());
        tvMsg.setText("");
    }

    //测量心率
    public void testHeart(View view) {
        if (mBleService == null) {
            return;
        }
        mBleService.measureHeart();
    }

    //测量血氧
    public void testOx(View view) {
        if (mBleService == null) {
            return;
        }
        mBleService.measureOx();
    }

    public void testHeat(View view) {
        if (mBleService == null) {
            return;
        }
        mBleService.measureHeat();
//        test2();
    }

    public void testTireAndPressure(View view) {
        if (mBleService == null) {
            return;
        }
        mBleService.measureTireAndPressure();
    }

    private ConnectorListener mConnectorListener = new ConnectorListener() {
        @Override
        public void onConnectAndWrite() {
            Utils.MyLog(Tag, "已连接");
            if (!TextUtils.isEmpty(mBleDeviceWrapper.getName()) && !"DfuTarg".contains(mBleDeviceWrapper.getName())) {
                Utils.MyLog(Tag, " 已连接 已保存的名称 " + mBleDeviceWrapper.getName() + " Mac " + mBleDeviceWrapper.getMac());
                SPUtil.saveData(BraceletActivity.this, SpConfig.BLUETOOTH_NAME, mBleDeviceWrapper.getName());
                SPUtil.saveData(BraceletActivity.this, SpConfig.BLUETOOTH_MAC, mBleDeviceWrapper.getMac());
            }
            if (mBleService != null) {
                mBleService.syncTime();
            }
        }

        @Override
        public void onDisconnect() {
            Utils.MyLog(Tag, "已断开");
        }

        @Override
        public void onConnectFailed() {
            Utils.MyLog(Tag, "连接失败");
        }

        @Override
        public void onReConnect() {
            Utils.MyLog(Tag, "重新连接，升级时会调用");
        }

        @Override
        public void onConnectSuccess() {
            Utils.MyLog(Tag, "连接成功");
            String oldMac = (String) SPUtil.getData(BraceletActivity.this, SpConfig.BLUETOOTH_MAC, "");
            boolean isEqual = Utils.isEqualMac(oldMac, mBleDeviceWrapper.getMac());
            Utils.MyLog(Tag, " isEqual " + isEqual);
            if (!TextUtils.isEmpty(mBleDeviceWrapper.getName()) && !"DfuTarg".contains(mBleDeviceWrapper.getName())) {
                Utils.MyLog(Tag, " 已连接 已保存的名称 " + mBleDeviceWrapper.getName() + " Mac " + mBleDeviceWrapper.getMac());
                SPUtil.saveData(BraceletActivity.this, SpConfig.BLUETOOTH_NAME, mBleDeviceWrapper.getName());
                SPUtil.saveData(BraceletActivity.this, SpConfig.BLUETOOTH_MAC, mBleDeviceWrapper.getMac());
            }
        }
    };

    StringBuilder builder = new StringBuilder();

    private SimplePerformerListener mPerformerListener = new SimplePerformerListener() {

        //电量、类型、版本、版本名
        public void onResponseDeviceInfo(DeviceInfo mDeviceInfo) {
            Utils.MyLog(Tag, "onResponseDeviceInfo");
            Utils.MyLog(Tag, "电量 = " + mDeviceInfo.getDeviceBattery());
            Utils.MyLog(Tag, "类型 = " + mDeviceInfo.getDeviceType());
            Utils.MyLog(Tag, "新旧版本规则 = " + mDeviceInfo.getVersionRule());
            Utils.MyLog(Tag, "版本名 = " + mDeviceInfo.getDeviceVersionName());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------手表数据--------------\n");
                    builder.append("电量 = " + mDeviceInfo.getDeviceBattery() + "\n");
                    builder.append("类型 = " + mDeviceInfo.getDeviceType() + "\n");
                    builder.append("版本 = " + mDeviceInfo.getDeviceVersionNumber() + "\n");
                    builder.append("版本名 = " + mDeviceInfo.getDeviceVersionName() + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        //运动数据
        public void onResponseMotionInfo(MotionInfo mMotionInfo) {
            Utils.MyLog(Tag, "onResponseDeviceInfo");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------运动数据--------------\n");
                    builder.append("日期 = " + mMotionInfo.getMotionDate() + "\n");
                    builder.append("卡路里 = " + mMotionInfo.getCalorie() + " " + "\n");
                    builder.append("距离 = " + mMotionInfo.getDistance() + " " + "\n");
                    builder.append("步数 = " + mMotionInfo.getTotalStep() + "\n");
                    builder.append("数据 = " + mMotionInfo.getStepData().toString() + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        //睡眠数据
        public void onResponseSleepInfo(SleepInfo mSleepInfo) {
            Utils.MyLog(Tag, "onResponseSleepInfo");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------睡眠数据--------------\n");
                    builder.append("日期 = " + mSleepInfo.getSleepDate() + "\n");
                    builder.append("睡眠总时间 = " + mSleepInfo.getSleepTotalTime() + "\n");
                    builder.append("入睡时间 = " + mSleepInfo.getSleepFallTime() + "\n");
                    builder.append("深睡时间 = " + mSleepInfo.getSleepDeepTime() + "\n");
                    builder.append("浅睡时间 = " + mSleepInfo.getSleepLightTime() + "\n");
                    builder.append("熬夜时间 = " + mSleepInfo.getSleepStayupTime() + "\n");
                    builder.append("REM时间 = " + mSleepInfo.getSleepRemTime() + "\n");
                    builder.append("醒来次数 = " + mSleepInfo.getSleepWakingNumber() + "\n");
                    builder.append("数据 = " + mSleepInfo.getSleepData().toString() + "\n");
                    builder.append("总时间 = " + mSleepInfo.getTotalTime() + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        //心率数据
        public void onResponsePoHeartInfo(PoHeartInfo poHeartInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------心脏数据--------------\n");
                    builder.append("日期 = " + poHeartInfo.getPoHeartDate() + "\n");
//                    builder.append("心率返回分钟数据 = " + poHeartInfo.getPoHeartData().toString() + "\n");
                    if ((poHeartInfo.getPoHeartData() != null) && (poHeartInfo.getPoHeartData().size() > 0)) {
                        int hr = getLastData(poHeartInfo.getPoHeartData());
//                        mBleService.hearInfoDetail(hr);
                        HrBean hrBean = new HrBean();
                        hrBean.heartRate = hr;
                        hrBean.heartDate = poHeartInfo.getPoHeartDate();
                        builder.append("心脏 = " + hr + "次/分钟" + "\n");
                        builder.append("时间间隔 = " + poHeartInfo.getHrTimeGap() + "分钟" + "\n");
                        builder.append("数据长度 = " + poHeartInfo.getPoHeartData().size()+ "\n");
                    }
                    tvMsg.setText(builder.toString());
                }
            });
        }

        //血压数据
        public void onResponseHeartInfo(HeartInfo heartInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //heartInfo.getHeartInfoDBP()为低压(舒张压)   heartInfo.HeartInfoSBP为高压（收缩压）
                    builder.append("-----------血压--------------\n");
                    builder.append("心率 = " + heartInfo.getHeartInfoHR() + "\n");
                    builder.append("低压(舒张压) = " + heartInfo.getHeartInfoDBP() + "\n");
                    builder.append("高压（收缩压） = " + heartInfo.getHeartInfoSBP() + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        //PPG数据
        public void onResponsePPGInfo(PPGInfo bean) {
            Utils.MyLog(Tag, "PPGInfo");
//            if (bean instanceof PPGInfo) {
//                ppgdatas.clear();
//                PPGInfo ppgInfo = (PPGInfo) bean;
//                List<PPGData> ppgDatas = ppgInfo.getPpgList();
//                if ((ppgDatas != null) && (ppgDatas.size() > 0)) {
//                    for (int i = 0; i < ppgDatas.size(); i++) {
//                        PPGData ppgData = ppgDatas.get(i);
//                        long ppgStartTime = DateTimeUtils.getDateTimeDatePart(new Date()).getTime();
//                        if ((ppgData.ppgDateTime != null)
//                                && (ppgData.ppgDateTime.dataTime != null)) {
//                            Date datatime = DateTimeUtils.getDateTimeDatePart(new Date(ppgData.ppgDateTime.dataTime));
//                            if (datatime != null) {
//                                ppgStartTime = datatime.getTime();
//                            }
//                        }
//                        if (ppgData.dataList != null) {
//                            List<Integer> currdatas = ppgdatas.get(ppgStartTime);
//                            if (currdatas != null) {
//                                currdatas.addAll(ppgData.dataList);
//                            } else {
//                                currdatas = new ArrayList<>();
//                                currdatas.addAll(ppgData.dataList);
//                                ppgdatas.put(ppgStartTime, currdatas);
//                            }
//                        }
//                    }
//                }
//            }
        }

        //ECG数据
        public void onResponseECGInfo(ECGInfo var1) {
            Utils.MyLog(Tag, "ECGInfo");

        }

        //血氧数据
        public void onResponseSpoInfo(SpoInfo spoInfo) {
            if (spoInfo == null || spoInfo.getSpoList() == null || spoInfo.getSpoList().size() <= 0)
                return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------血氧数据--------------\n");
                    for (int i = 0; i < spoInfo.getSpoList().size(); i++) {
                        SpoData data = spoInfo.getSpoList().get(i);
                        builder.append("日期 = " + data.spoTime);
                        builder.append("血氧 = " + data.spoValue + " ");
                        builder.append("心率 = " + data.heartValue + "\n");
                    }
                    tvMsg.setText(builder.toString());
                }
            });
        }

        //午睡数据
        @Override
        public void onResponseSiestaInfo(SiestaInfo siestaInfo) {
            if (siestaInfo == null) return;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, siestaInfo.getStartYear());
            calendar.set(Calendar.MONTH, siestaInfo.getStartMonth() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, siestaInfo.getStartDay());

            calendar.set(Calendar.HOUR_OF_DAY, siestaInfo.getStartHour());
            calendar.set(Calendar.MINUTE, siestaInfo.getStartMin());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTime = calendar.getTimeInMillis();

            calendar.set(Calendar.HOUR_OF_DAY, siestaInfo.getEndHour());
            calendar.set(Calendar.MINUTE, siestaInfo.getEndMin());
            long endTime = calendar.getTimeInMillis();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------午睡数据--------------\n");
                    if (siestaInfo.getSleepTime() <= 0) {
                        builder.append("午睡时间为" + 0 + "\n");
                    } else {
                        builder.append("开始时间 = " + DateTimeUtils.s_long_2_str(startTime, DateTimeUtils.f_format) + "\n");
                        builder.append("结束时间 = " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format) + "\n");

                        builder.append("午睡时间 开始时间减去结束时间 = " + DateTimeUtils.toDayHoursMinutesString(endTime - startTime) +
                                " 接口返回时间 = " + siestaInfo.getSleepTime() + "\n");
                        tvMsg.setText(builder.toString());
                    }
                }
            });
        }

        //疲劳数据
        @Override
        public void onResponseTireInfoInfo(TireInfo tireInfo) {
            super.onResponseTireInfoInfo(tireInfo);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("----------疲劳数据--------------\n");
                    if (tireInfo == null) return;

                    builder.append("时间间隔/数据长度="+tireInfo.getTireTimeGap()+"+"+tireInfo.getList().size());
                    builder.append("日期: " + tireInfo.getDate() + "\n");
                    builder.append("疲劳数据: ");
                    if (tireInfo.getList() != null && !tireInfo.getList().isEmpty()) {
                        for (Integer integer : tireInfo.getList()) {
                            builder.append("" + integer + ",");
                        }
                    }
                    builder.append("\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        //血压数据
        @Override
        public void onResponseHeartListInfo(HeartListInfo info) {
            super.onResponseHeartListInfo(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------血压--------------\n");
                    if (info == null || info.getList() == null || info.getList().isEmpty()) return;
                    for (HeartInfo info1 : info.getList()) {
                        builder.append(" 时间 " + info1.getTime() + "\n");
                        builder.append(" 心率 " + info1.getHeartInfoHR() + "\n");
                        builder.append(" 高压 " + info1.getHeartInfoSBP() + "\n");
                        builder.append(" 低压 " + info1.getHeartInfoDBP() + "\n");
                    }
                    tvMsg.setText(builder.toString());
                }
            });
        }

        //同步数据完成
        public void onResponseComplete() {
            Utils.MyLog(Tag, "onResponseComplete");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("----------接收数据完成--------------\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseEncryp(byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("----------加密返回数据--------------\n");
                    builder.append("加密返回数据:" + ParseWatchesData.byteFormatDate(bytes) + "\n");
                    if (bytes.length > 13) {
                        builder.append("加密结果：" + (bytes[13] == 1 ? "成功\n" : "失败\n"));
                    }
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseByteArray(byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("----------接收蓝牙整合返回数据--------------\n");
                    builder.append("" + ByteToStringUtil.byteToString(bytes) + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponsePressureAndEmotionInfo(PressureInfo pressureInfo, EmotionInfo emotionInfo) {
            super.onResponsePressureAndEmotionInfo(pressureInfo, emotionInfo);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pressureInfo != null) {
                        builder.append("时间间隔/数据长度:"+pressureInfo.getPressureTimeGap()+"+"+pressureInfo.getPressureList().size());
                        builder.append("----------压力数据--------------\n");
                        builder.append("日期: " + pressureInfo.getPressureDate() + "\n");
                        builder.append("压力数据: ");
                        if (pressureInfo.getPressureList() != null && pressureInfo.getPressureList().size() > 0) {
                            for (Integer integer : pressureInfo.getPressureList()) {
                                builder.append("" + integer + ",");
                            }
                        }
                        builder.append("\n");
                    }
                    if (emotionInfo != null) {
                        builder.append("时间间隔/数据长度:"+emotionInfo.getEmotionTimeGap()+"+"+emotionInfo.getEmotionList().size());
                        builder.append("----------情绪数据--------------\n");
                        builder.append("日期: " + emotionInfo.getEmotionDate() + "\n");
                        builder.append("情绪数据: ");
                        if (emotionInfo.getEmotionList() != null && emotionInfo.getEmotionList().size() > 0) {
                            for (Integer integer : emotionInfo.getEmotionList()) {
                                builder.append("" + integer + ",");
                            }
                        }
                        builder.append("\n");
                    }
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseTempInfoInfo(HeatInfo heatInfo, EnviTempInfo enviTempInfo, UvInfo uvInfo) {
            super.onResponseTempInfoInfo(heatInfo, enviTempInfo, uvInfo);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("----------体温、环境、紫外温度数据--------------\n");
                    if (heatInfo != null) {
                        builder.append("日期: " + heatInfo.heatDate + "\n");
                        builder.append("体温数据: ");
                        if (heatInfo.list != null && heatInfo.list.size() > 0) {
                            for (float d : heatInfo.list) {
                                builder.append("" + d + ",");
                            }
                        }
                        builder.append("\n");
                        tvMsg.setText(builder.toString());
                    }
                    if (enviTempInfo != null) {
                        builder.append("日期: " + enviTempInfo.date + "\n");
                        builder.append("环境温度数据: ");
                        if (enviTempInfo.list != null && enviTempInfo.list.size() > 0) {
                            for (int d : enviTempInfo.list) {
                                builder.append("" + d + ",");
                            }
                        }
                        builder.append("\n");
                        tvMsg.setText(builder.toString());
                    }
                    if (uvInfo != null) {
                        builder.append("日期: " + uvInfo.date + "\n");
                        builder.append("紫外: ");
                        if (uvInfo.list != null && uvInfo.list.size() > 0) {
                            for (Integer d : uvInfo.list) {
                                builder.append("" + d + ",");
                            }
                        }
                        builder.append("\n");
                        tvMsg.setText(builder.toString());
                    }
                }
            });
        }

        @Override
        public void onResponseTest(String s) {
            super.onResponseTest(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append(s + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseSleepLogInfo(SleepLogInfo info) {
            super.onResponseSleepLogInfo(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------睡眠日志--------------\n");
                    if (info == null || info.getList() == null || info.getList().isEmpty()) return;
                    builder.append("日期" + info.getDate() + "\n");
                    for (Integer i : info.getList()) {
                        builder.append(i + ",");
                    }
                    builder.append("\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseBreatheInfo(BreatheInfo info) {
            super.onResponseBreatheInfo(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------呼吸数据--------------\n");
                    if (info == null || info.getList() == null || info.getList().isEmpty()) return;
                    builder.append("日期" + info.getDate() + "\n");
                    //15f分钟一个呼吸数据
                    builder.append("呼吸频率数据：\n");
                    for (Integer i : info.getList()) {
                        builder.append(i + ",");
                    }
                    builder.append("\n");
                    builder.append("采样时间间隔:" + info.getBreathTimeGap() + "\n");
                    builder.append("数据长度:" + info.getList().size() + "\n");
                    builder.append("低通气指数:" + info.getHypopnea() + "\n");
                    builder.append("累计阻塞时长:" + info.getBlockLen() + "\n");
                    builder.append("呼吸紊乱指数:" + info.getChaosIndex() + "\n");
                    builder.append("呼吸暂停次数:" + info.getPauseCount() + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseSymptomListInfo(SymptomListInfo info) {
            super.onResponseSymptomListInfo(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (info == null) return;
                    builder.append("-----------症状数据--------------\n");
                    builder.append("症状值个数：" + info.symptomCount + "\n");
                    for (SymptomInfo info : info.getList()) {
                        builder.append("症状编号：" + info.getNumber() + " 时间: " + info.getSymptomTime() + "\n");
                    }
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseSnoreInfo(SnoreInfo info) {
            super.onResponseSnoreInfo(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (info == null) return;
                    builder.append("-----------打鼾数据--------------\n");
                    builder.append("日期：" + info.getDate() + "\n");
                    builder.append("打鼾时长：" + info.getSnoreLen() + "\n");
                    builder.append("打鼾最大分贝：" + info.getMaxDbF() + "\n");
                    builder.append("打鼾平均分贝：" + info.getAverageDb() + "\n");
                    builder.append("打鼾最小分贝：" + info.getMinDb() + "\n");
                    builder.append("鼾声指数：" + info.getSnoreIndex() + "\n");
                    builder.append("打鼾频次：" + info.getSnoreFrequency() + "\n");
                    builder.append("正常鼾声：" + info.getSnoreNormal() + "\n");
                    builder.append("轻度鼾声：" + info.getSnoreMild() + "\n");
                    builder.append("中度鼾声：" + info.getSnoreMiddle() + "\n");
                    builder.append("重度鼾声：" + info.getSnoreSerious() + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseStrengthInfo(StrengthInfo info) {
            super.onResponseStrengthInfo(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (info == null) return;
                    builder.append("-----------运动强度--------------\n");
                    builder.append("日期：" + DateTimeUtils.s_long_2_str(info.getDate(), DateTimeUtils.day_format) + "\n");
                    builder.append("低强度运动：" + info.getLowTime() + "分钟\n");
                    builder.append("中强度运动：" + info.getMiddleTime() + "分钟\n");
                    builder.append("高强度运动：" + info.getHighTime() + "分钟\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseAbnormalHeartListInfo(AbnormalHeartListInfo infos) {
            super.onResponseAbnormalHeartListInfo(infos);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (infos == null || infos.getList() == null || infos.getList().isEmpty())
                        return;
                    builder.append("-----------异常心率--------------\n");
                    for (AbnormalHeartInfo info : infos.getList()) {
                        builder.append("时间：" + info.getTime() + "\n");
                        builder.append("心率：" + info.getHeart() + "\n");
                    }
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseRunStep(int step) {
            super.onResponseRunStep(step);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("-----------运动步数--------------\n");
                    builder.append("运动步数：" + step + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseDeviceMac(String s) {
            super.onResponseDeviceMac(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append(" MAC地址：" + s + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseTestOx(int ox) {
            super.onResponseTestOx(ox);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append(" 测量血氧：" + ox + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseMeasureTemp(float heat, int temp) {
            super.onResponseMeasureTemp(heat, temp);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("体温：" + heat + " ");
                    builder.append("环境温度：" + temp + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseMeasureTireAndPressure(int tire, int pressure) {
            super.onResponseMeasureTireAndPressure(tire, pressure);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.append("疲劳：" + tire + " ");
                    builder.append("压力：" + pressure + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseSwitchInfo(SwitchInfo info) {
            super.onResponseSwitchInfo(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (info == null) return;
                    builder.append("心率过高提醒:" + info.isHeartRemind() + "\n");
                    builder.append("久坐提醒:" + info.isSitRemind() + "\n");
                    builder.append("睡眠提醒:" + info.isSleepRemind() + "\n");
                    builder.append("血氧过低提醒:" + info.isLowOxRemind() + "\n");
                    builder.append("勿扰模式:" + info.isDisturbRemind() + "\n");
                    builder.append("设备语言:" + info.isLanguageRemind() + "\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseSleepOxInfo(SleepOxInfo sleepOxInfo) {
            super.onResponseSleepOxInfo(sleepOxInfo);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sleepOxInfo == null || sleepOxInfo.getTime() <= 0) return;
                    if (sleepOxInfo.getTime() > 0) {
                        builder.append("睡眠血氧开始记录时间：" + DateTimeUtils.s_long_2_str(sleepOxInfo.getTime(), DateTimeUtils.f_format) + "\n");
                    }
                    builder.append("数据：");
                    if (sleepOxInfo.getList() != null && sleepOxInfo.getList().size() > 0) {
                        for (int temp : sleepOxInfo.getList()) {
                            builder.append(temp + ",");
                        }
                    }
                    builder.append("\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseHrvInfo(HrvInfo hrvInfo) {
            super.onResponseHrvInfo(hrvInfo);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (hrvInfo == null) return;
                    builder.append("Hrv日期：" + hrvInfo.getHrvDate() + "\n");
                    builder.append("Hrv数据：");
                    if (hrvInfo.getHrvList() != null && hrvInfo.getHrvList().size() > 0) {
                        for (int hrv : hrvInfo.getHrvList()) {
                            builder.append(hrv + ",");
                        }
                    }
                    builder.append("\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }

        @Override
        public void onResponseSymptomListInfo2(SymptomListInfo info) {
            super.onResponseSymptomListInfo2(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (info == null || info.getSymptomInfo2() == null) return;
                    for (SymptomInfo2 symptomInfo2 : info.getSymptomInfo2()) {
                        builder.append("933症状时间：" + symptomInfo2.getSymptomTime() + "\n");
                        if (symptomInfo2.getSymptomArray() != null && symptomInfo2.getSymptomArray().length > 0) {
                            builder.append("症状：");
                            for (int i : symptomInfo2.getSymptomArray()) {
                                builder.append(i + ",");
                            }
                        }
                    }
                    builder.append("\n");
                    tvMsg.setText(builder.toString());
                }
            });
        }
    };

    public void RestoreFactoryDialog(View view) {
        new android.app.AlertDialog.Builder(BraceletActivity.this)
                .setTitle(getString(R.string.dailog1_title))
                .setMessage(getString(R.string.dailog1_message))
                .setPositiveButton(getString(R.string.dailog1_positive), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mBleService != null) {
//                            Utils.MyLog(Tag, "恢复出厂设置");
                            Utils.MyLog(Tag, "reset");
                            mBleService.restore_factory();
                            mBleService.setLanguagen(1);
                            togg_languagen.setChecked(true);
                        }
                    }

                }).setNegativeButton(getString(R.string.dailog1_negative), new DialogInterface.OnClickListener() {//添加返回按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件

                    }
                }).show();
    }

    void handler_init() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case MSG_CONNECT:
//                        setTitle(getString(R.string.state_connect));
                        break;
                    case MSG_DISCONNECT:
//                        setTitle(getString(R.string.state_disconnect));
                        break;
                    case MSG_CONNECTFAIL:
//                        setTitle(getString(R.string.state_connect_fail));
                        break;
                    case MSG_CONNECTING:
//                        setTitle(getString(R.string.state_connecting));
                        break;
                    case MSG_SEND_SAVE:
                        isSendSave = true;
                        tvMsg.setText(msg.obj.toString());
//                        setTitle(getString(R.string.state_connecting));
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        switch (compoundButton.getId()) {
            case R.id.togg_wear:
//                Utils.MyLog(Tag, "佩戴方式 = " + isChecked);
                Utils.MyLog(Tag, "wearing method = " + isChecked);
                if (mBleService != null && mBleService.connectState) {
                    mBleService.setWearType(isChecked);
                }
                break;

            case R.id.togg_unit:
//                Utils.MyLog(Tag, "单位 = " + isChecked);
                Utils.MyLog(Tag, "unit = " + isChecked);

                if (mBleService != null && mBleService.connectState) {
                    mBleService.setUnit(isChecked);
                } else {
                    togg_unit.setChecked(!togg_unit.isChecked());
                    Toast.makeText(BraceletActivity.this, "未连接蓝牙", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.togg_time:
//                Utils.MyLog(Tag, "时间 = " + isChecked);
                Utils.MyLog(Tag, "time = " + isChecked);
                if (mBleService != null && mBleService.connectState) {
                    mBleService.setTimeFormat(isChecked);
                } else {
                    togg_time.setChecked(!togg_time.isChecked());
                    Toast.makeText(BraceletActivity.this, "未连接蓝牙", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.togg_languagen:
//                Utils.MyLog(Tag, "语言 = " + isChecked);
                Utils.MyLog(Tag, "Language = " + isChecked);
                if (mBleService != null && mBleService.connectState) {
                    mBleService.setLanguagen(isChecked ? 1 : 0);
                } else {
                    togg_languagen.setChecked(!togg_languagen.isChecked());
                    Toast.makeText(BraceletActivity.this, "未连接蓝牙", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.check_po_heart:
//                Utils.MyLog(Tag, "整点心率 = " + isChecked);
                Utils.MyLog(Tag, "Whole dessert rate = " + isChecked);

                if (mBleService != null) {
                    mBleService.setPoHeart(isChecked);
                }
                break;

            case R.id.check_wo_heart:
//                Utils.MyLog(Tag, "连续心率 = " + isChecked);
                Utils.MyLog(Tag, "Continuous heart rate = " + isChecked);

                if (mBleService != null) {
                    mBleService.setWoHeart(isChecked);
                }
                break;


            case R.id.togg_taiwan:
//                Utils.MyLog(Tag, "抬腕亮屏 = " + isChecked);
                Utils.MyLog(Tag, "Lift the wrist bright screen = " + isChecked);

                if (mBleService != null && mBleService.connectState) {
                    mBleService.setTaiWan(isChecked);
                } else {
                    togg_taiwan.setChecked(!togg_taiwan.isChecked());
                    Toast.makeText(BraceletActivity.this, "未连接蓝牙", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.togg_zhuanwan:

//                Utils.MyLog(Tag, "转腕切屏 = " + isChecked);
                Utils.MyLog(Tag, "Wrist cut screen = " + isChecked);

                if (mBleService != null) {
                    mBleService.setZhuanWan(isChecked);
                }
                break;

            case R.id.togg_disturb:

//                Utils.MyLog(Tag, "勿扰模式 = " + isChecked);
                Utils.MyLog(Tag, "Do not disturb mode = " + isChecked);

                if (mBleService != null && mBleService.connectState) {
                    mBleService.setNotDisturb(isChecked);
                } else {
                    togg_disturb.setChecked(!togg_disturb.isChecked());
                    Toast.makeText(BraceletActivity.this, "未连接蓝牙", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.togg_heart:
                if (mBleService != null) {
                    mBleService.setHighHeartRemind(isChecked);
                }
                break;
            case R.id.togg_snore:
                if (mBleService != null) {
                    mBleService.setSnoreMonitor(isChecked);
                }
                break;
        }

    }

    private BoBean getBoBeanFrom(SpoInfo spoInfo) {
        BoBean boBean = new BoBean();
        boBean.boRate = "0";
        boBean.boDate = DateTimeUtils.toDateString(new Date(), "yyyy-MM-dd HH:mm:ss");
        SpoData spoDatag = null;
        if ((spoInfo.spoList != null) && (spoInfo.spoList.size() > 0)) {
            for (int i = 0; i < spoInfo.spoList.size(); i++) {
                SpoData spoData = spoInfo.spoList.get(i);
                if (spoData.spoValue > 0) {
                    if (spoDatag == null) {
                        spoDatag = spoData;
                    } else {
                        long oldtime = DateTimeUtils.getNonNullDate(DateTimeUtils.convertStrToDateForThisProject(spoDatag.spoTime)).getTime();
                        long newtime = DateTimeUtils.getNonNullDate(DateTimeUtils.convertStrToDateForThisProject(spoData.spoTime)).getTime();
                        if (newtime > oldtime) {
                            spoDatag = spoData;
                        }
                    }
                }
            }
        }
        if (spoDatag != null) {
            boBean.boRate = spoDatag.spoValue + "";
            boBean.boDate = spoDatag.spoTime;
        }
        return boBean;
    }

    private int getLastData(List<Integer> datas) {
        if ((datas != null) && (datas.size() > 0)) {
            for (int i = datas.size() - 1; i >= 0; i--) {
                int curr = datas.get(i);
                if (curr > 0) {
                    return curr;
                }
            }
        }
        return 0;
    }

//    private void test() {
//        if (!BuildConfig.DEBUG) return;
//        byte[] bytes = new byte[1457];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 5;
//        bytes[3] = -87;
//        bytes[8] = 3;
//        bytes[10] = 5;
//        bytes[13] = 45;
//        bytes[14] = 47;
//        bytes[16] = 48;
//        for (int i = 17; i < bytes.length; i++) {
//            bytes[i] = (byte) (i % 120);
//        }
//        if (mBleService != null) {
//            int i = 0;
//            for (i = 0; i < bytes.length; ) {
//                byte[] b;
//                if (i + 40 > bytes.length) {
//                    b = new byte[bytes.length - i];
//                } else {
//                    b = new byte[40];
//                }
//                for (int j = 0; j < 40 && i < bytes.length; j++) {
//                    b[j] = bytes[i];
//                    i++;
//                }
//                mBleService.test(b);
//            }
//        }
//    }
//
//    private void test1() {
//        if (!BuildConfig.DEBUG) return;
//        byte[] bytes = new byte[1457];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 5;
//        bytes[3] = -87;
//        bytes[8] = 3;
//        bytes[10] = 5;
//        bytes[13] = 45;
//        bytes[14] = 47;
//        bytes[16] = 48;
//        for (int i = 17; i < bytes.length; i++) {
//            bytes[i] = 1;
//        }
//        if (mBleService != null) {
//            int i = 0;
//            for (i = 0; i < bytes.length; ) {
//                byte[] b;
//                if (i + 30 > bytes.length) {
//                    b = new byte[bytes.length - i];
//                } else {
//                    b = new byte[30];
//                }
//                for (int j = 0; j < 30 && i < bytes.length; j++) {
//                    b[j] = bytes[i];
//                    i++;
//                }
//                mBleService.test(b);
//            }
//        }
//    }
//
//    private void testSieaInfo() {
//        byte[] bytes = new byte[21];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0;
//        bytes[3] = 0x0d;
//        bytes[8] = 3;
//        bytes[10] = 0x44;
//        bytes[11] = 0;
//        bytes[12] = 0x14;
//        bytes[13] = 0x16;
//        bytes[14] = 0x09;
//        bytes[15] = 0x10;
//        bytes[16] = 0x0d;
//        bytes[17] = 0x01;
//        bytes[18] = 0x0d;
//        bytes[19] = 0x1e;
//        bytes[20] = 0x3c;
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }

//    /**
//     * 发送压力数据
//     */
//    public void testPressure() {
////        if (!BuildConfig.DEBUG) return;
//        byte[] bytes = new byte[42];
//        bytes[0] = (byte) 0xab;
//        bytes[2] = 0;
//        bytes[3] = 0x21;
//        bytes[5] = 0x3c;
//        bytes[8] = 3;
//        bytes[10] = 0x10;
//        bytes[11] = 0;
//        bytes[12] = 0;
////        bytes[13] = 0x2d;
////        bytes[14] = (byte) 0x9c;
//        bytes[15] = 0;
//        bytes[16] = 0;
//        bytes[18] = 0x0b;
////        Random random = new Random();
////        for (int i = 17; i < 41; i++) {
////            int v = random.nextInt(100);
////            bytes[i] = ByteToStringUtil.intToBytes(v)[3];
////        }
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }
//
//    /**
//     * 发送疲劳数据
//     */
//    public void testTire() {
////        if (!BuildConfig.DEBUG) return;
//        byte[] bytes = new byte[41];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0;
//        bytes[3] = 0x21;
//        bytes[4] = 0x45;
//        bytes[5] = 0x3c;
//        bytes[8] = 3;
//        bytes[10] = 0x11;
//        bytes[11] = 0;
//        bytes[12] = 0;
//        bytes[13] = 0x2e;
//        bytes[14] = 0x24;
//        bytes[15] = 0;
//        bytes[16] = 0;
////        bytes[18] = 0x16;
////        bytes[26] = 0x2d;
//        Random random = new Random();
//        for (int i = 17; i < 41; i++) {
//            int v = random.nextInt(100);
//            bytes[i] = ByteToStringUtil.intToBytes(v)[3];
//        }
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }
//
//    private void testTempInfo() {
//        byte[] bytes = new byte[137];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0;
//        bytes[3] = (byte) 0x81;
//        bytes[8] = 3;
//        bytes[10] = 0x15;
//        bytes[11] = 0;
//        bytes[12] = 0x14;
//        bytes[13] = 0x2f;
//        bytes[14] = 0x2a;
//        bytes[15] = 0x10;
//        bytes[16] = 0x0d;
//        Random random = new Random();
//        for (int i = 17; i < 137; i += 5) {
//            int v = random.nextInt(255);
//            bytes[i] = (byte) ByteToStringUtil.intToBytes(v)[3];
//            int vf = random.nextInt(9);
//            bytes[i + 1] = (byte) ByteToStringUtil.intToBytes(vf)[3];
////            Log.i("", " EnviHeat " + bytes[i] + " " +  bytes[i + 1] + " " + v + " " + vf);
//
//            int h = random.nextInt(8) + 34;
//            bytes[i + 2] = (byte) ByteToStringUtil.intToBytes(h)[3];
//            int hf = random.nextInt(9);
//            bytes[i + 3] = (byte) ByteToStringUtil.intToBytes(hf)[3];
////            Log.i("", " Heat " + bytes[i + 2] + " " +  bytes[i + 3] + " " + h + " " + hf);
//
//            bytes[i + 4] = (byte) ByteToStringUtil.intToBytes(v)[3];
//        }
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }
//
//    public void testDevice() {
//        byte[] bytes = new byte[20];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0;
//        bytes[3] = 0x0C;
//        bytes[7] = 0x01;
//        bytes[8] = 0x03;
//        bytes[10] = 0x08;
//        bytes[11] = 0;
//        bytes[12] = 0x07;
//        bytes[13] = 0x64;
//        bytes[14] = 0x02;
//        bytes[15] = 0x40;
//        bytes[16] = 0x10;
//        bytes[17] = 0x23;
//        bytes[18] = 0x21;
//        bytes[19] = 0x08;
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }
//
//    public void testStep() {
//        byte[] bytes = new byte[209];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0;
//        bytes[3] = (byte) 0xcb;
//        bytes[4] = (byte) 0x99;
//        bytes[8] = 0x03;
//        bytes[10] = 0x02;
//        bytes[11] = 0;
//        bytes[12] = 0x15;
//        bytes[13] = 0x2e;
//        bytes[14] = 0x4e;
//        bytes[15] = 0x09;
//        bytes[16] = 0x5a;
//        bytes[17] = (byte) 0xe1;
//        bytes[18] = 0x5c;
//        bytes[19] = 0x03;
//        bytes[20] = 0x63;
//
//        bytes[21] = 0x45;
//        bytes[22] = 0x63;
//        bytes[23] = 0x45;
//        bytes[24] = 0x69;
//        bytes[25] = 0x61;
//        bytes[26] = 0x69;
//
//        bytes[27] = (byte) 0x82;
//        bytes[28] = 0x6a;
//        bytes[29] = (byte) 0xc3;
//        bytes[30] = 0x71;
//        bytes[31] = (byte) 0xc2;
//        bytes[32] = 0x71;
//        bytes[33] = (byte) 0xe5;
//
////        bytes[209] = 12;
////        bytes[210] = (byte) 0xfd;
//
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }
//
//    public void testSleep() {
////        byte[] bytes = new byte[48];
////        bytes[0] = SIATCommand.SIGN_HEAD;
////        bytes[2] = 0;
////        bytes[3] = 0x28;
////        bytes[4] = (byte) 0x41;
////        bytes[8] = 0x03;
////        bytes[10] = 0x03;
////        bytes[11] = 0;
////        bytes[12] = 0x23;
////        bytes[13] = 0x2e;
////        bytes[14] = 0x61;
////        bytes[15] = 0x10;
////        bytes[16] = 0x15;
////        bytes[17] = (byte) 0x61;
////        bytes[18] = 0x16;
////        bytes[19] = (byte) 0xc2;
////        bytes[20] = 0x1b;
////
////        bytes[21] = (byte) 0x86;
////        bytes[22] = (byte) 0x1d;
////        bytes[23] = (byte) 0x42;
////        bytes[24] = (byte) 0x22;
////        bytes[25] = (byte) 0xc3;
////        bytes[26] = 0x28;
////        bytes[27] = (byte) 0x86;
////        bytes[28] = 0x28;
////        bytes[29] = (byte) 0xa2;
////        bytes[30] = 0x2b;
////        bytes[31] = (byte) 0xc6;
////        bytes[32] = 0x2d;
////        bytes[33] = (byte) 0x22;
////        bytes[34] = (byte) 0x30;
////        bytes[35] = (byte) 0x83;
////        bytes[36] = (byte) 0x34;
////        bytes[37] = (byte) 0x23;
////        bytes[38] = (byte) 0x34;
////        bytes[39] = (byte) 0x46;
////        bytes[40] = (byte) 0x36;
////        bytes[41] = (byte) 0x2;
////        bytes[42] = (byte) 0x40;
////        bytes[43] = (byte) 0x6;
////        bytes[44] = (byte) 0x42;
////        bytes[45] = (byte) 0x22;
////        bytes[46] = (byte) 0x42;
////        bytes[47] = (byte) 0x45;
//
//        String s = "171,0,0,58,128,0,0,0,3,0,3,0,53,46,139,25,189,129,189,194,190,198,190,226,3,35,7,66,8,6,9,35,11,98,14,195,17,194,23,6,23,67,26,162,27,166,28,2,32,70,33,98,37,195,43,6,43,226,48,38,49,2,64,70,65,69";
//        String[] arr = s.split(",");
//        byte[] bytes = new byte[arr.length];
//        for (int i = 0; i < arr.length; i++) {
//            int integer = Integer.valueOf(arr[i]);
//            bytes[i] = ByteToStringUtil.intToByte(integer);
//        }
//        StringBuilder builder1 = new StringBuilder();
//        for (int j = 0; j < bytes.length; j++) {
//            builder1.append(bytes[j]);
//        }
////        LogUtil.i(" json ", builder1.toString());
//
////        byte[] bytes = new byte[38];
////        bytes[0] = SIATCommand.SIGN_HEAD;
////        bytes[2] = 0;
////        bytes[3] = 0x1e;
////        bytes[4] = (byte) 0xa8;
////        bytes[8] = 0x03;
////        bytes[10] = 0x03;
////        bytes[11] = 0;
////        bytes[12] = 0x19;
////        bytes[13] = 0x27;
////        bytes[14] = (byte) 0x9f;
////        bytes[15] = 0xb;
////        bytes[16] = 0x7;
////        bytes[17] = (byte) 0x21;
////        bytes[18] = 0x8;
////        bytes[19] = (byte) 0xc2;
////        bytes[20] = 0xb;
////
////        bytes[21] = (byte) 0x83;
////        bytes[22] = (byte) 0xe;
////        bytes[23] = (byte) 0x22;
////        bytes[24] = (byte) 0x15;
////        bytes[25] = (byte) 0x63;
////        bytes[26] = 0x19;
////        bytes[27] = (byte) 0x62;
////        bytes[28] = 0x1f;
////        bytes[29] = (byte) 0x43;
////        bytes[30] = 0x21;
////        bytes[31] = (byte) 0xa2;
////        bytes[32] = 0x29;
////        bytes[33] = (byte) 0x83;
////        bytes[34] = (byte) 0x2c;
////        bytes[35] = (byte) 0xc2;
////        bytes[36] = (byte) 0x30;
////        bytes[37] = (byte) 0x5;
//
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }
//
//    private void sendBreathe() {
//        byte[] bytes = new byte[113];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0x01;
//        bytes[3] = (byte) 0x69;
//        bytes[4] = (byte) 0x00;
//        bytes[5] = (byte) 0x0F;
//        bytes[8] = 0x03;
//        bytes[10] = 0x16;
//        bytes[11] = 0;
//        bytes[12] = 0;
//        bytes[13] = 0x2e;
//        bytes[14] = 0x61;
//        bytes[15] = 0;
//        bytes[16] = 0;
//        Random random = new Random();
//        for (int i = 17; i < 113; i++) {
//            int v = random.nextInt(100);
//            bytes[i] = ByteToStringUtil.intToBytes(v)[3];
//        }
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }
//
//    private void testSleepLog() {
//        byte[] bytes = new byte[317];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0x01;
//        bytes[3] = (byte) 0x35;
//        bytes[4] = (byte) 0x00;
//        bytes[5] = (byte) 0x00;
//        bytes[8] = 0x03;
//        bytes[10] = 0x55;
//        bytes[11] = 0;
//        bytes[12] = 0x23;
//        bytes[13] = 0x2e;
//        bytes[14] = 0x61;
//        bytes[15] = 0x10;
//        bytes[16] = 0x15;
//        Random random = new Random();
//        for (int i = 17; i < 317; i++) {
//            int v = random.nextInt(100);
//            bytes[i] = ByteToStringUtil.intToBytes(v)[3];
//        }
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }

    private void sendTest() {

        if (et_test == null) return;

        String msg = et_test.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        String[] msgArray = null;
        if (!TextUtils.isEmpty(msg)) {
            msgArray = msg.split(",");
        }
        List<Integer> list = new ArrayList<>();
        if (msgArray.length > 0) {
            for (int i = 0; i < msgArray.length; i++) {
                try {
                    int temp = Integer.valueOf(msgArray[i]);
                    if (temp < 0) {
                        temp += 256;
                    }
                    list.add(temp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            byte[] bytes = new byte[list.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = ByteToStringUtil.intToByte(list.get(i));
            }
            if (mBleService != null) {
                mBleService.sendThread(bytes);
            }

            builder.append("-----------发送的测试数据--------------\n");
            builder.append(ByteToStringUtil.byteToString(bytes));
            builder.append("\n");
            tvMsg.setText(builder.toString());
        }
    }

    private void parseTest() {
        if (et_test == null) return;

        String msg = et_test.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        String[] msgArray = null;
        if (!TextUtils.isEmpty(msg)) {
            msgArray = msg.split(",");
        }
        List<Integer> list = new ArrayList<>();
        if (msgArray.length > 0) {
            for (int i = 0; i < msgArray.length; i++) {
                try {
                    int temp = Integer.valueOf(msgArray[i]);
                    if (temp < 0) {
                        temp += 256;
                    }
                    list.add(temp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            byte[] bytes = new byte[list.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = ByteToStringUtil.intToByte(list.get(i));
            }
            if (mBleService != null) {
                mBleService.test(bytes);
            }

            builder.append("-----------要解析的测试数据--------------\n");
            builder.append(ByteToStringUtil.byteToString(bytes));
            builder.append("\n");
            tvMsg.setText(builder.toString());
        }
    }

    private void sendLife() {
//        List<Integer> list = new ArrayList<>();
//        Random random = new Random();
//        for (int i = 0; i < 7; i++) {
//            int v = random.nextInt(200);
//            if (v > 100) {
//                v -= 200;
//            }
//            list.add(v);
//        }
//        byte[] bytes = SIATCommand.getLifeCom(list);
//        if (mBleService != null) {
//            mBleService.sendThread(bytes);
//        }
//
//        builder.append("-----------7天的生命力数据--------------\n");
//        for (Integer i : list) {
//            builder.append(i + ",");
//        }
//        builder.append("\n");
//        builder.append("-----------发送的生命力数据--------------\n");
//        builder.append(ByteToStringUtil.byteToString(bytes));
//        builder.append("\n");
//        tvMsg.setText(builder.toString());
    }

    private void sendOxHeart() {
//        byte[] bytes = SIATCommand.getOxHeartCom(SIATCommand.SIGN_AA_HEAD, 96, 60, 100, 80, 100, 50, 90, 60);
//        builder.append("\n");
//        builder.append("-----------发送的心率和血氧--------------\n");
//        builder.append(ByteToStringUtil.byteToString(bytes));
//        builder.append("\n");
//        tvMsg.setText(builder.toString());
//        if (mBleService != null) {
////            mBleService.sendThread(bytes);
//            mBleService.sendOxHeartCommand(96, 60, 100, 80, 100, 50, 90, 60);
//        }
    }

    private void testBreathe() {
        String msg = "171,0,0,109,127,15,0,0,3,0,22,0,0,46,166,0,0,25,0,0,0,29,17,0,0,16,23,20,0,14,27,27,20,14,20,30,25,27,18,17,17,15,20,18,21,15,25,24,14,25,21,29,15,30,21,18,24,20,30,26,24,30,22,21,28,28,13,27,29,29,13,17,21,24,26,26,25,25,24,26,19,22,0,30,26,25,30,19,24,23,30,27,30,25,18,26,30,24,26,18,30,19,21,29,20,23,24,23,16,21,30,28,18,60,90,30,10";
        List<Integer> list = new ArrayList<>();
        String[] msgArray = msg.split(",");
        for (int i = 0; i < msgArray.length; i++) {
            try {
                int temp = Integer.valueOf(msgArray[i]);
                if (temp < 0) {
                    temp += 256;
                }
                list.add(temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = ByteToStringUtil.intToByte(list.get(i));
        }
        if (mBleService != null) {
            mBleService.test(bytes);
        }
    }

//    private void testbraceletBreathe() {
//        byte[] bytes = new byte[28];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0x01;
//        bytes[3] = (byte) 0x14;
//        bytes[4] = (byte) 0x00;
//        bytes[5] = (byte) 0x00;
//        bytes[8] = 0x03;
//        bytes[10] = 0x17;
//        bytes[11] = 0;
//        bytes[12] = 0x14;
//        bytes[13] = 0x2e;
//        bytes[14] = (byte) 0x93;
//        Random random = new Random();
//        for (int i = 17; i < 28; i++) {
//            int v = random.nextInt(100);
//            bytes[i] = ByteToStringUtil.intToBytes(v)[3];
//        }
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }
//
//    private void testsyminfoBreathe() {
//        byte[] bytes = new byte[22];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0x01;
//        bytes[3] = (byte) 0x0E;
//        bytes[4] = (byte) 0x00;
//        bytes[5] = (byte) 0x00;
//        bytes[8] = 0x03;
//        bytes[10] = 0x18;
//        bytes[11] = 0;
//        bytes[12] = 0x14;
//        bytes[13] = 0x2e;
//        bytes[14] = (byte) 0x93;
//        Random random = new Random();
//        for (int i = 17; i < 22; i++) {
//            int v = random.nextInt(100);
//            bytes[i] = ByteToStringUtil.intToBytes(v)[3];
//        }
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }

    private void testSnore() {
        String msg = "171,0,0,22,0,0,0,0,3,0,24,0,0,46,110,0,29,180,55,5,4,0,9,51,0,22,0,5,0,1";
        List<Integer> list = new ArrayList<>();
        String[] msgArray = msg.split(",");
        for (int i = 0; i < msgArray.length; i++) {
            try {
                int temp = Integer.valueOf(msgArray[i]);
                if (temp < 0) {
                    temp += 256;
                }
                list.add(temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = ByteToStringUtil.intToByte(list.get(i));
        }
        if (mBleService != null) {
            mBleService.test(bytes);
        }
    }

//    public void testPressure() {
////        if (!BuildConfig.DEBUG) return;
//        byte[] bytes = new byte[66];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0;
//        bytes[3] = 0x42;
//        bytes[8] = 3;
//        bytes[10] = 0x10;
//        bytes[11] = 0;
//        bytes[12] = 0;
//        bytes[13] = 0x2e;
//        bytes[14] = (byte) 0xd2;
//        bytes[15] = 0;
//        bytes[16] = 0;
//        Random random = new Random();
//        for (int i = 17; i < 66; i++) {
//            int v = random.nextInt(100);
//            bytes[i] = ByteToStringUtil.intToBytes(v)[3];
//        }
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }


    private void testSymptom() {
        String msg = "171,0,0,20,136,0,0,0,3,0,23,0,9,0,0,1,93,40,229,179,100,0,83,40,229,179,100,0";
        List<Integer> list = new ArrayList<>();
        String[] msgArray = msg.split(",");
        for (int i = 0; i < msgArray.length; i++) {
            try {
                int temp = Integer.valueOf(msgArray[i]);
                if (temp < 0) {
                    temp += 256;
                }
                list.add(temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = ByteToStringUtil.intToByte(list.get(i));
        }

        if (mBleService != null) {
            mBleService.test(bytes);
        }
    }

    private void testSpo() {
        String msg = "171,0,0,14,121,0,0,0,3,0,20,0,9,0,0,1,94,106,23,50,100,79";
        List<Integer> list = new ArrayList<>();
        String[] msgArray = msg.split(",");
        for (int i = 0; i < msgArray.length; i++) {
            try {
                int temp = Integer.valueOf(msgArray[i]);
                if (temp < 0) {
                    temp += 256;
                }
                list.add(temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = ByteToStringUtil.intToByte(list.get(i));
        }

        if (mBleService != null) {
            mBleService.test(bytes);
        }
    }

//    private void testHeart() {
//        byte[] bytes = new byte[28];
//        bytes[0] = SIATCommand.SIGN_HEAD;
//        bytes[2] = 0;
//        bytes[3] = 0x14;
//        bytes[8] = 3;
//        bytes[10] = 0x20;
//        bytes[11] = 0;
//        bytes[12] = 0;
//        bytes[13] = 0x2e;
//        bytes[14] = (byte) 0xd2;
//        bytes[15] = 0;
//        bytes[16] = 94;
//        bytes[17] = 8;
//        bytes[18] = (byte) 245;
//        bytes[19] = 22;
//        bytes[20] = 0;
//        bytes[21] = (byte) 0x5a;
//        bytes[22] = 94;
//        bytes[23] = 6;
//        bytes[24] = (byte) 245;
//        bytes[25] = 25;
//        bytes[26] = 0;
//        bytes[27] = (byte) 0x5c;
//        if (mBleService != null) {
//            mBleService.test(bytes);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        AutoJudgmentConnectDevice(null);
    }

    @Override
    public void reconnectDevice() {
        ScanDevice.getInstance(this).startScan();
    }

    @Override
    public void scanStarted() {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onFoundScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mBleService != null && device != null && TextUtils.equals(mBleService.getBleMac(), device.getAddress())) {
            mBleDeviceWrapper = new DeviceModule(device.getName(), device);
            mBleService.BindDevice(mBleDeviceWrapper);
            ScanDevice.getInstance(this).stopScan();
        }
    }

    @Override
    public void scanStoped() {

    }

    private void test2() {
//        AB 00 00 6D 54 0F 00 00 03 00 16 00 00 30 54 00 00 0A OF 0C
//        0A 0A 0C 00 OC OA OE OE OE OE OE OE OE OE OE OE OE OE OE OE
//        0E 0E OE OE OE OE OE OE OE OE OE 17 12 0D OF10 0F 0C 10 0A
//        0E 0A 0D 0C OA 0D OC OD OD OC OF OC OA 0A0F 10 00 00 00 00
//        00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
//        00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
        byte[] data = new byte[20];
        data[0] = (byte) 171;
        data[3] = (byte) 0x6D;
        data[4] = (byte) 0x54;
        data[5] = (byte) 0x0F;
        data[8] = (byte) 0x03;
        data[10] = (byte) 0x16;
        data[13] = (byte) 0x30;
        data[14] = (byte) 0x54;
        data[17] = (byte) 0x0A;
        data[18] = (byte) 16;
        data[19] = (byte) 0x0C;

        byte[] data2 = new byte[20];
        data2[0] = (byte) 0x0A;
        data2[1] = (byte) 0x0A;
        data2[2] = (byte) 0x0C;
        data2[3] = (byte) 0x0A;
        data2[4] = (byte) 0x0E;
        data2[5] = (byte) 0x0E;
        data2[6] = (byte) 0x0E;
        data2[7] = (byte) 0x0E;
        data2[8] = (byte) 0x0E;
        data2[9] = (byte) 0x0E;
        data2[10] = (byte) 0x0A;
        data2[11] = (byte) 0x0A;
        data2[12] = (byte) 0x0C;
        data2[13] = (byte) 0x0A;
        data2[14] = (byte) 0x0E;
        data2[15] = (byte) 0x0E;
        data2[16] = (byte) 0x0E;
        data2[17] = (byte) 0x0E;
        data2[18] = (byte) 0x0E;
        data2[19] = (byte) 0x0E;

        byte[] data3 = new byte[20];
        data3[0] = (byte) 0x0A;
        data3[1] = (byte) 0x0A;
        data3[2] = (byte) 0x0C;
        data3[3] = (byte) 0x0A;
        data3[4] = (byte) 0x0E;
        data3[5] = (byte) 0x0E;
        data3[6] = (byte) 0x0E;
        data3[7] = (byte) 0x0E;
        data3[8] = (byte) 0x0E;
        data3[9] = (byte) 0x0E;
        data3[10] = (byte) 0x0A;
        data3[11] = (byte) 0x0A;
        data3[12] = (byte) 0x0C;
        data3[13] = (byte) 0x0A;
        data3[14] = (byte) 0x0E;
        data3[15] = (byte) 0x0E;
        data3[16] = (byte) 0x0E;
        data3[17] = (byte) 0x0E;
        data3[18] = (byte) 0x0E;
        data3[19] = (byte) 0x0E;

        Observable.interval(1, TimeUnit.SECONDS)
                .take(3)
                .compose(RxUtil.IoToMainObserve())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (aLong == 0) {
                            mBleService.test2(data);
                        } else if (aLong == 1) {
                            mBleService.test2(data2);
                        } else {
                            mBleService.test2(data3);
                        }
                    }
                });
//        mBleService.sendThread(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivityResult", requestCode + " requestCode " + requestCode + " data " + data);
    }
}