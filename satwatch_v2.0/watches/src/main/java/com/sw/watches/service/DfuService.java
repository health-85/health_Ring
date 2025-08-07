package com.sw.watches.service;

import android.app.Activity;

import com.sw.watches.activity.NotificationActivity;

import no.nordicsemi.android.dfu.DfuBaseService;

public class DfuService extends DfuBaseService {

    public Class<? extends Activity> getNotificationTarget() {
        return NotificationActivity.class;
    }

    public boolean isDebug() {
        return true;
    }
}