package com.sw.watches.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.sw.watches.application.ZhbraceletApplication;
import com.sw.watches.bleUtil.SpDeviceTools;
import com.sw.watches.bluetooth.SIATCommand;
import com.sw.watches.service.ZhBraceletService;

public class SMSReceiver extends BroadcastReceiver {

    public static final String TAG = "ImiChatSMSReceiver";

    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private SpDeviceTools spDeviceTools;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            ZhBraceletService zhBraceletService = ZhbraceletApplication.getZhBraceletService();
            try {
                if (zhBraceletService == null || zhBraceletService.getNotDisturb()) return;
                String msg = null;
                SmsMessage[] messages = getMessagesFromIntent(intent);
                if (messages != null && messages.length > 0) {
                    for (SmsMessage message : messages) {
                        if (message != null) {
//                            Log.i(TAG, message.getOriginatingAddress() + " : " +
//                                    message.getDisplayOriginatingAddress() + " : " +
//                                    message.getDisplayMessageBody() + " : " +
//                                    message.getTimestampMillis());
                            msg = message.getDisplayMessageBody();
                        }
                    }
                }
                if (spDeviceTools == null) {
                    spDeviceTools = new SpDeviceTools(context);
                }
                if (spDeviceTools.getRemindMms()) {
                    if (!TextUtils.isEmpty(msg) && !zhBraceletService.getNotDisturb()) {
                        zhBraceletService.notifyData(msg, 4);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];
        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }


}


