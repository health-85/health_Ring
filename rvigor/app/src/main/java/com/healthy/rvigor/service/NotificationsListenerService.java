package com.healthy.rvigor.service;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;


import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.bean.NotifyType;
import com.healthy.rvigor.util.AppUtils;
import com.healthy.rvigor.util.LogUtils;
import com.healthy.rvigor.util.SPUtil;
import com.healthy.rvigor.util.SpConfig;
import com.healthy.rvigor.util.SpNoticeTool;
import com.sw.watches.application.ZhbraceletApplication;
import com.sw.watches.service.ZhBraceletService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NotificationsListenerService extends NotificationListenerService {
    //
    private static final String TAG = "NotificationsListenerService";

    public int disTime = 150;

    public long preTime = 0L;

    public String title = "";

    public String content = "";

    public String packageName = "";

    private ExecutorService mECGExecutor = Executors.newSingleThreadExecutor();

    private void sendNotify(NotifyType type, String msg, int id) {
        if (!AppUtils.isEffectiveClick()) {
            return;
        }
        ZhBraceletService zhBraceletService = ZhbraceletApplication.getZhBraceletService();
        if (zhBraceletService != null) {
            ZhbraceletApplication.getZhBraceletService().notifyData(msg, id);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification notification, RankingMap rankingMap) {
        super.onNotificationPosted(notification, rankingMap);
        if (notification == null || notification.getNotification() == null || notification.getNotification().extras == null)
            return;
        boolean isDisturb = (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_DISTURB_REMIND, false);
        if (isDisturb) return;
        Bundle bundle = notification.getNotification().extras;
        String title = bundle.getString("android.title");
        String content = bundle.getString("android.text");
        if ((title == null || title.equals("") || content == null || content.equals("")))
            return;
        LogUtils.i(TAG, " title == " + title + " content == " + content);
        if (!content.contains("QQ正在后台运行") && !content.contains("了解详情或停止应用")) {
            if (!this.packageName.equals(notification.getPackageName()) || !this.title.equals(title) ||
                    !this.content.equals(content) || notification.getPostTime() - this.preTime >= (long) this.disTime) {
                this.preTime = notification.getPostTime();
                this.title = title;
                this.content = content;
                this.packageName = notification.getPackageName();
                if (notification.getPackageName().equals("com.tencent.mobileqq")) {
                    if (SpNoticeTool.getQQNoticeEnable()) {
                        sendNotify(NotifyType.QQ, title + ":" + content, 2);
                    }
                } else if (notification.getPackageName().equals("com.whatsapp")) {
                    if (SpNoticeTool.getWhatsappNoticeEnable()) {
                        sendNotify(NotifyType.WHATSAPP, title + ":" + content, 6);
                    }
                }
                if (notification.getPackageName().equals("com.linkedin.android")) {
                    if (SpNoticeTool.getLinkedLnNoticeEnable()) {
                        sendNotify(NotifyType.LINKEDIN, title + ":" + content, 6);
                    }
                } else if (notification.getPackageName().equals("com.twitter.android")) {
                    if (SpNoticeTool.getTwitterNoticeEnable()) {
                        sendNotify(NotifyType.TWITTER, title + ":" + content, 6);
                    }
                } else if (notification.getPackageName().equals("com.viber.voip")) {
                    if (SpNoticeTool.getViberNoticeEnable()) {
                        sendNotify(NotifyType.VIBER, title + ":" + content, 10);
                    }
                } else if (notification.getPackageName().equals("jp.naver.line.android")) {
                    if (SpNoticeTool.getLineNoticeEnable()) {
                        sendNotify(NotifyType.LINE, title + ":" + content, 11);
                    }
                } else if (notification.getPackageName().equals("com.google.android.gm")) {
                    if (SpNoticeTool.getGaimlNoticeEnable()) {
                        sendNotify(NotifyType.GMAIL, title + ":" + content, 14);
                    }
                } else if (notification.getPackageName().equals("com.instagram.android")) {
                    if (SpNoticeTool.getInstagramMNoticeEnable()) {
                        sendNotify(NotifyType.INSTAGRAM, title + ":" + content, 16);
                    }
                } else if (notification.getPackageName().equals("com.google.android.youtube")) {
                    if (SpNoticeTool.getYouTubeNoticeEnable()) {
                        sendNotify(NotifyType.OTHER, title + ":" + content, 17);
                    }
                } else if (notification.getPackageName().equals("com.kakao.talk")) {
                    if (SpNoticeTool.getKakaoNoticeEnable()) {
                        sendNotify(NotifyType.KAKAOTALK, title + ":" + content, 18);
                    }
                } else if (notification.getPackageName().equals("com.vkontakte.android")) {
                    if (SpNoticeTool.getKakaoNoticeEnable()) {
                        sendNotify(NotifyType.VKONTAKTE, title + ":" + content, 19);
                    }
                } else if (notification.getPackageName().contains("calendar")) {
                    if (SpNoticeTool.getAppleCalendarNoticeEnable()) {
                        sendNotify(NotifyType.OTHER, title + ":" + content, 20);
                    }
                } else if (notification.getPackageName().contains("mail")) {
                    if (SpNoticeTool.getAppleMailNoticeEnable()) {
                        sendNotify(NotifyType.OTHER, title + ":" + content, 21);
                    }
                } else if (notification.getPackageName().contains("facetime")) {
                    if (SpNoticeTool.getAppleFaceNoticeEnable()) {
                        sendNotify(NotifyType.OTHER, title + ":" + content, 22);
                    }
                } else if (notification.getPackageName().contains("com.tencent.tim")) {
                    if (SpNoticeTool.getTimeNoticeEnable()) {
                        sendNotify(NotifyType.TIM, title + ":" + content, 21);
                    }
                } else if (notification.getPackageName().contains("com.google.android.gm")) {
                    if (SpNoticeTool.getTimeNoticeEnable()) {
                        sendNotify(NotifyType.GMAIL, title + ":" + content, 22);
                    }
                } else if (notification.getPackageName().contains("com.alibaba.android.rimet")) {
                    if (SpNoticeTool.getDingTalkNoticeEnable()) {
                        sendNotify(NotifyType.DINGTALK, title + ":" + content, 23);
                    }
                } else if (notification.getPackageName().contains("com.lianjia.alliance")) {
                    if (SpNoticeTool.getAADDNoticeEnable()) {
                        sendNotify(NotifyType.OTHER, title + ":" + content, 24);
                    }
                } else if (notification.getPackageName().contains("com.lianjia.beike")) {
                    if (SpNoticeTool.getBeikeNoticeEnable()) {
                        sendNotify(NotifyType.OTHER, title + ":" + content, 25);
                    }
                } else if (notification.getPackageName().contains("com.homelink.android")) {
                    if (SpNoticeTool.getLianjiaNoticeEnable()) {
                        sendNotify(NotifyType.OTHER, title + ":" + content, 26);
                    }
                } else if (notification.getPackageName().contains("jp.naver.line.android")) {
                    if (SpNoticeTool.getLianjiaNoticeEnable()) {
                        sendNotify(NotifyType.OTHER, title + ":" + content, 27);
                    }
                } else if (notification.getPackageName().equals("com.facebook.katana") || notification.getPackageName().equals("com.facebook.orca")) {
                    if (SpNoticeTool.getFacebookNoticeEnable()) {
                        sendNotify(NotifyType.FACEBOOK, title + ":" + content, 7);
                    }
                } else if (notification.getPackageName().equals("com.skype.raider") || notification.getPackageName().equals("com.skype.rover") ||
                        notification.getPackageName().equals("com.skype.insiders")) {
                    if (SpNoticeTool.getSkypeNoticeEnable()) {
                        sendNotify(NotifyType.SKYPE, title + ":" + content, 5);
                    }
                } else if (notification.getPackageName().equals("com.android.mms") || notification.getPackageName().equals("com.android.mms.service") ||
                        notification.getPackageName().equals("com.samsung.android.messaging")) {
                    if (SpNoticeTool.getSMSNoticeEnable()) {
                        sendNotify(NotifyType.SMS, title + ":" + content, 4);
                    }
                } else if (notification.getPackageName().equals("com.tencent.mm") || notification.getPackageName().equals("com.weipin1.mm")
                        || notification.getPackageName().equals("com.weipin2.mm")) {
                    if (SpNoticeTool.getWeixinNoticeEnable() || SpNoticeTool.getWorkChatNoticeEnable()) {
                        sendNotify(NotifyType.WECHAT, title + ":" + content, 4);
                    }
                } else if (notification.getPackageName().contains("com.facebook.orca")) {
                    if (SpNoticeTool.getMessengerNoticeEnable()) {
                        sendNotify(NotifyType.MESSENGER, title + ":" + content, 27);
                    }
                }

                /*else if (notification.getPackageName().equals("com.microsoft.office.outlook")) {
                    sendNotify(NotifyType.OTHER,title + ":" + content, 15);
                }else if (notification.getPackageName().equals("com.snapchat.android")) {
                    sendNotify(NotifyType.OTHER,title + ":" + content, 15);
                }*/
            }
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationChannelGroupModified(String pkg, UserHandle user, NotificationChannelGroup group, int modificationType) {
        super.onNotificationChannelGroupModified(pkg, user, group, modificationType);
    }

    @Override
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        super.onNotificationRankingUpdate(rankingMap);
    }

    @Override
    public void onNotificationChannelModified(String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
        super.onNotificationChannelModified(pkg, user, channel, modificationType);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        super.onNotificationRemoved(sbn, rankingMap, reason);
    }

}

