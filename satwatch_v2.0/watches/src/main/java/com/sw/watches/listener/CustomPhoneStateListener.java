package com.sw.watches.listener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;

/**
 * 手机状态监听
 */
public class CustomPhoneStateListener extends PhoneStateListener {

    public Context mContext;

    public CustomPhoneStateListener(Context context) {
        this.mContext = context;
    }

    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
    }

    public void onCallStateChanged(int state, String number) {

    }
}

