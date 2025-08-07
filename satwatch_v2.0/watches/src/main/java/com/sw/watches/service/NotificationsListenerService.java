package com.sw.watches.service;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.sw.watches.application.ZhbraceletApplication;
import com.sw.watches.bleUtil.SpDeviceTools;
import com.sw.watches.bleUtil.TelephonyUtil;
import com.sw.watches.util.LogUtil;

import java.util.List;

public class NotificationsListenerService /*extends NotificationListenerService*/ {

//    private static final String TAG = "NotificationsListenerSe";
//
//    public SpDeviceTools spDeviceTools;
//
//    public int disTime = 150;
//
//    public long preTime = 0L;
//
//    public String title = "";
//
//    public String content = "";
//
//    public String packageName = "";
//
//    private void sendNotificationType(String msg, int id) {
//        if (!TelephonyUtil.isEffectiveClick()) return;
//        ZhBraceletService zhBraceletService = ZhbraceletApplication.getZhBraceletService();
//        if (zhBraceletService != null && !zhBraceletService.getNotDisturb()) {
//            ZhbraceletApplication.getZhBraceletService().notifyData(msg, id);
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return super.onBind(intent);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return START_STICKY;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//    }
//
//    @Override
//    public void onNotificationPosted(StatusBarNotification notification, RankingMap rankingMap) {
//        super.onNotificationPosted(notification, rankingMap);
//        if (notification == null || notification.getNotification() == null || notification.getNotification().extras == null)
//            return;
//        if (spDeviceTools == null) {
//            spDeviceTools = new SpDeviceTools(this);
//        }
//        Bundle bundle = notification.getNotification().extras;
//        String title = bundle.getString("android.title");
//        String content = bundle.getString("android.text");
//        if ((title == null || title.equals("") || content == null || content.equals("")))
//            return;
//        Log.i(TAG, " title == " + title + " content == " + content);
//        if (!content.contains("QQ正在后台运行") && !content.contains("了解详情或停止应用")) {
//            if (!this.packageName.equals(notification.getPackageName()) || !this.title.equals(title) ||
//                    !this.content.equals(content) || notification.getPostTime() - this.preTime >= (long) this.disTime) {
//                this.preTime = notification.getPostTime();
//                this.title = title;
//                this.content = content;
//                this.packageName = notification.getPackageName();
//                if (notification.getPackageName().equals("com.tencent.mobileqq")) {
//                    if (spDeviceTools.getRemindQQ()) {
//                        sendNotificationType(title + ":" + content, 2);
//                    }
//                } else if (!notification.getPackageName().equals("com.tencent.mm") && !notification.getPackageName().equals("com.weipin1.mm") && !notification.getPackageName().equals("com.weipin2.mm")) {
//                    if (!notification.getPackageName().equals("com.android.mms") && !notification.getPackageName().equals("com.android.mms.service") && !notification.getPackageName().equals("com.samsung.android.messaging")) {
//                        if (!notification.getPackageName().equals("com.skype.raider") && !notification.getPackageName().equals("com.skype.rover") && !notification.getPackageName().equals("com.skype.insiders")) {
//                            if (notification.getPackageName().equals("com.whatsapp")) {
//                                if (spDeviceTools.getRemindWhatsapp()) {
//                                    sendNotificationType(title + ":" + content, 6);
//                                }
//                            } else if (!notification.getPackageName().equals("com.facebook.katana") && !notification.getPackageName().equals("com.facebook.orca")) {
//                                if (notification.getPackageName().equals("com.linkedin.android")) {
//                                    if (spDeviceTools.getRemindLinkedin()) {
//                                        sendNotificationType(title + ":" + content, 8);
//                                    }
//                                } else if (notification.getPackageName().equals("com.twitter.android")) {
//                                    if (spDeviceTools.getRemindTwitter()) {
//                                        sendNotificationType(title + ":" + content, 9);
//                                    }
//                                } else if (notification.getPackageName().equals("com.viber.voip")) {
//                                    if (spDeviceTools.getRemindViber()) {
//                                        sendNotificationType(title + ":" + content, 10);
//                                    }
//                                } else if (notification.getPackageName().equals("jp.naver.line.android") && spDeviceTools.getRemindLine()) {
//                                    sendNotificationType(title + ":" + content, 11);
//                                } else if (notification.getPackageName().equals("com.google.android.gm") && spDeviceTools.getRemindGmail()) {
//                                    sendNotificationType(title + ":" + content, 14);
//                                } else if (notification.getPackageName().equals("com.microsoft.office.outlook") && spDeviceTools.getRemindOutlook()) {
//                                    sendNotificationType(title + ":" + content, 15);
//                                } else if (notification.getPackageName().equals("com.instagram.android") && spDeviceTools.getRemindInstagram()) {
//                                    sendNotificationType(title + ":" + content, 16);
//                                } else if (notification.getPackageName().equals("com.snapchat.android") && spDeviceTools.getRemindSnapchat()) {
//                                    sendNotificationType(title + ":" + content, 17);
//                                }
//                            } else if (spDeviceTools.getRemindFacebook()) {
//                                sendNotificationType(title + ":" + content, 7);
//                            }
//                        } else if (spDeviceTools.getRemindSkype()) {
//                            sendNotificationType(title + ":" + content, 5);
//                        }
//                    } else if (spDeviceTools.getRemindMms()) {
//                        sendNotificationType(title + ":" + content, 4);
//                    }
//                } else if (spDeviceTools.getRemindWx()) {
//                    sendNotificationType(title + ":" + content, 3);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onNotificationPosted(StatusBarNotification sbn) {
//        super.onNotificationPosted(sbn);
//    }
//
//    @Override
//    public void onNotificationChannelGroupModified(String pkg, UserHandle user, NotificationChannelGroup group, int modificationType) {
//        super.onNotificationChannelGroupModified(pkg, user, group, modificationType);
//    }
//
//    @Override
//    public void onNotificationRankingUpdate(RankingMap rankingMap) {
//        super.onNotificationRankingUpdate(rankingMap);
//    }
//
//    @Override
//    public void onNotificationChannelModified(String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
//        super.onNotificationChannelModified(pkg, user, channel, modificationType);
//    }
//
//    @Override
//    public void onNotificationRemoved(StatusBarNotification sbn) {
//        super.onNotificationRemoved(sbn);
//    }
//
//    @Override
//    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
//        super.onNotificationRemoved(sbn, rankingMap);
//    }
//
//    @Override
//    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
//        super.onNotificationRemoved(sbn, rankingMap, reason);
//    }

}

