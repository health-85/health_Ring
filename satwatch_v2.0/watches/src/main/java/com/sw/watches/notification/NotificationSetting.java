package com.sw.watches.notification;

import android.content.Context;

import com.sw.watches.bleUtil.SpDeviceTools;

public class NotificationSetting {

    public SpDeviceTools mBleDeviceTools;

    public NotificationSetting(Context context) {
        mBleDeviceTools = new SpDeviceTools(context);
    }

    public boolean get_call() {
        return mBleDeviceTools.getRemindCall();
    }

    public void set_call(boolean paramBoolean) {
        mBleDeviceTools.putRemindCall(paramBoolean);
    }

    public boolean get_qq() {
        return mBleDeviceTools.getRemindQQ();
    }

    public void set_qq(boolean paramBoolean) {
        mBleDeviceTools.putRemindQQ(paramBoolean);
    }

    public boolean get_wx() {
        return mBleDeviceTools.getRemindWx();
    }

    public void set_wx(boolean paramBoolean) {
        mBleDeviceTools.puyRemindWx(paramBoolean);
    }

    public boolean get_sms() {
        return mBleDeviceTools.getRemindMms();
    }

    public void set_sms(boolean paramBoolean) {
        mBleDeviceTools.putRemindMms(paramBoolean);
    }

    public boolean get_skype() {
        return mBleDeviceTools.getRemindSkype();
    }

    public void set_skype(boolean paramBoolean) {
        mBleDeviceTools.putRemindSkype(paramBoolean);
    }

    public boolean get_whatsapp() {
        return mBleDeviceTools.getRemindWhatsapp();
    }

    public void set_whatsapp(boolean paramBoolean) {
        mBleDeviceTools.putRemindWhatsapp(paramBoolean);
    }

    public boolean get_facebook() {
        return mBleDeviceTools.getRemindFacebook();
    }

    public void set_facebook(boolean paramBoolean) {
        mBleDeviceTools.putRemindFacebook(paramBoolean);
    }

    public boolean get_linkedin() {
        return mBleDeviceTools.getRemindLinkedin();
    }

    public void set_linkedin(boolean paramBoolean) {
        mBleDeviceTools.putRemindLinkedin(paramBoolean);
    }

    public boolean get_twitter() {
        return mBleDeviceTools.getRemindTwitter();
    }

    public void set_twitter(boolean paramBoolean) {
        mBleDeviceTools.putRemindTwitter(paramBoolean);
    }

    public boolean get_viber() {
        return mBleDeviceTools.getRemindViber();
    }

    public void set_viber(boolean paramBoolean) {
        mBleDeviceTools.putRemindViber(paramBoolean);
    }

    public boolean get_line() {
        return mBleDeviceTools.getRemindLine();
    }

    public void set_line(boolean paramBoolean) {
        mBleDeviceTools.putRemindLine(paramBoolean);
    }

    public boolean get_mail() {
        return mBleDeviceTools.getRemindGmail();
    }

    public void set_mail(boolean paramBoolean) {
        mBleDeviceTools.putRemindGmail(paramBoolean);
    }

    public boolean get_outlook() {
        return mBleDeviceTools.getRemindOutlook();
    }

    public void set_outlook(boolean paramBoolean) {
        mBleDeviceTools.putRemindOutlook(paramBoolean);
    }

    public boolean get_instagram() {
        return mBleDeviceTools.getRemindInstagram();
    }

    public void set_instagram(boolean paramBoolean) {
        mBleDeviceTools.putRemindInstagram(paramBoolean);
    }

    public boolean get_snapchat() {
        return mBleDeviceTools.getRemindSnapchat();
    }

    public void set_snapchat(boolean paramBoolean) {
        mBleDeviceTools.putRemindSnapchat(paramBoolean);
    }
}