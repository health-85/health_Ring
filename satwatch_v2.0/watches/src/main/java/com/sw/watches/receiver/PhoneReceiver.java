package com.sw.watches.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sw.watches.application.ZhbraceletApplication;
import com.sw.watches.bleUtil.SpDeviceTools;
import com.sw.watches.bleUtil.TelephonyUtil;
import com.sw.watches.bluetooth.SIATCommand;
import com.sw.watches.service.ZhBraceletService;

public class PhoneReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneReceiver";

    private SpDeviceTools spDeviceTools;

    @Override
    public void onReceive(Context context, Intent intent) {
        ZhBraceletService zhBraceletService = ZhbraceletApplication.getZhBraceletService();
        try {
            if (zhBraceletService == null || zhBraceletService.getNotDisturb()) return;
//        System.out.println("action" + intent.getAction());
            //如果是去电
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//            Log.d(TAG, "call OUT:" + phoneNumber);
            } else {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                tm.listen(new PhoneStateListener() {
                    @Override
                    public void onCallStateChanged(int state, String phoneNumber) {
                        super.onCallStateChanged(state, phoneNumber);
                        if (spDeviceTools == null) {
                            spDeviceTools = new SpDeviceTools(context);
                        }
//                    Log.d(TAG, "onCallStateChanged " + state);
                        if (spDeviceTools.getRemindCall()) {
//                        if (state != 0) {
                            if (state == TelephonyManager.CALL_STATE_RINGING) {
                                if (TelephonyUtil.isEffectiveClick() && !zhBraceletService.getNotDisturb()) {
                                    zhBraceletService.notifyData(zhBraceletService.getContactName(phoneNumber), 1);
                                }
                            }
//                        } else {
//                            zhBraceletService.sendThread(SIATCommand.getPhoneStateCom());
//                        }
                        }
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE);
                //设置一个监听器
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
