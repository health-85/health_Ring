package com.healthy.rvigor.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.healthy.rvigor.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 通知基类
 */
public class NotificationUtils {

    private MyApplication mcomm = null;

    private NotificationManager manager = null;

    /**
     * 内容点击的action
     */
    private String content_click_action = "";

    /**
     * 通知删除
     */
    private String notification_delete_action = "";

    /**
     * 注册接收广播
     */
    private void RegisterBoastcast(NotificationBroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        /**
         * 点击广播
         */
        filter.addAction(receiver.getContent_click_action());
        /**
         * 删除广播
         */
        filter.addAction(receiver.getNotification_delete_action());

        mcomm.registerReceiver(receiver, filter);
    }


    /**
     * 注销广播
     *
     * @param receiver
     */
    private void unRegisterBoastcast(NotificationBroadcastReceiver receiver) {
        mcomm.unregisterReceiver(receiver);
    }


    /**
     * 广播类型
     */
    private static class NotificationBroadcastReceiver extends BroadcastReceiver {

        private NotificationItemInfoBase entity = null;

        private String content_click_action = "";

        private String notification_delete_action = "";

        public Notification getNotification() {
            return notification;
        }

        public void setNotification(Notification notification) {
            this.notification = notification;
        }

        private Notification notification = null;


        public NotificationItemInfoBase getEntity() {
            return entity;
        }

        /**
         * 通知
         */
        public int notifyId = 0;

        /**
         * 设置实体类型
         *
         * @param entity
         */
        public void setEntity(NotificationItemInfoBase entity) {
            this.entity = entity;
        }


        public String getContent_click_action() {
            return content_click_action;
        }

        public String getNotification_delete_action() {
            return notification_delete_action;
        }

        private List<NotificationBroadcastReceiver> notificationItemInfoBaseHashMap = null;


        public NotificationBroadcastReceiver(NotificationItemInfoBase entity, String content_click_action, String notification_delete_action, List<NotificationBroadcastReceiver> notificationItemInfoBaseHashMap) {
            this.entity = entity;
            this.content_click_action = content_click_action;
            this.notification_delete_action = notification_delete_action;
            this.notificationItemInfoBaseHashMap = notificationItemInfoBaseHashMap;
        }

        private MyApplication getApp(Context con) {
            if (con instanceof MyApplication) {
                return (MyApplication) con;
            } else {
                if ((con.getApplicationContext() != null) && (con.getApplicationContext() instanceof MyApplication)) {
                    return (MyApplication) con.getApplicationContext();
                }
            }
            return null;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (content_click_action.equals(intent.getAction())) {
                if (entity.getNotifycallback() != null) {
                    entity.getNotifycallback().onclick(entity, getApp(context));
                }
                notificationItemInfoBaseHashMap.remove(this);
                notificationItemInfoBaseHashMap = null;
                LogUtils.e(entity.getTitle(), "点击");
                getApp(context).unregisterReceiver(this);
            }

            if (notification_delete_action.equals(intent.getAction())) {
                if (entity.getNotifycallback() != null) {
                    entity.getNotifycallback().oncancel(entity, getApp(context));
                }
                LogUtils.e(entity.getTitle(), "shanchu");
                notificationItemInfoBaseHashMap.remove(this);
                notificationItemInfoBaseHashMap = null;
                getApp(context).unregisterReceiver(this);
            }
        }
    }


    /**
     * 通知服务
     *
     * @param comm
     */
    public NotificationUtils(MyApplication comm) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("NotificationUtils  必须在UI线程中初始化");
        }
        String uuid = UUID.randomUUID().toString();
        content_click_action = uuid + ".notification.clickaction";
        notification_delete_action = uuid + ".notification.deleteaction";
        mcomm = comm;
    }


    @SuppressLint("NewApi")
    private void createNotificationChannel(String id, String name) {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 200, 200});
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) mcomm.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @SuppressLint("NewApi")
    private Notification.Builder getChannelNotification(String id, int smallResId, String title, String content) {
        return new Notification.Builder(mcomm, id)
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(smallResId)
                .setAutoCancel(true);
    }


    private NotificationCompat.Builder getNotification_25(int smallResId, String title, String content) {
        return new NotificationCompat.Builder(mcomm)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallResId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);
    }

    /**
     * 通知集合
     */
    private List<NotificationBroadcastReceiver> notificationItemInfoBaseHashMap = new ArrayList<>();

    /**
     * 发送通知
     *
     * @param notifyInfo
     */
    public void sendNotification(NotificationItemInfoBase notifyInfo) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("NotificationUtils  必须在UI线程中初始化");
        }
        if (notifyInfo != null) {
            NotificationBroadcastReceiver rec = getNotifyById(notifyInfo.getId());
            if (rec != null) {
                rec.setEntity(notifyInfo);
            } else {
                rec = new NotificationBroadcastReceiver(notifyInfo, content_click_action + notifyInfo.getId(), notification_delete_action + notifyInfo.getId(), notificationItemInfoBaseHashMap);
                this.RegisterBoastcast(rec);
                notificationItemInfoBaseHashMap.add(rec);
            }
            rec.notifyId = notifyInfo.getId();
            sendNotificationprivate(rec.notifyId, rec);
            if (notificationItemInfoBaseHashMap.size() > 5) {
                CancelNotification(notificationItemInfoBaseHashMap.get(0).entity);
            }
        } else {
            LogUtils.e("NotificationUtils", "notifyInfo 实体不能为空");
        }
    }

    private NotificationBroadcastReceiver getNotifyById(int id) {
        for (int i = 0; i < notificationItemInfoBaseHashMap.size(); i++) {
            NotificationBroadcastReceiver curr = notificationItemInfoBaseHashMap.get(i);
            if (curr.notifyId == id) {
                return curr;
            }
        }
        return null;
    }


    /**
     * 内部发送通知
     *
     * @param notifyid
     * @param notifyRec
     */

    private void sendNotificationprivate(int notifyid, NotificationBroadcastReceiver notifyRec) {
        Notification notification = null;
        Intent deleteints = new Intent();
        deleteints.setAction(notifyRec.getNotification_delete_action());
        PendingIntent DelInt = PendingIntent.getBroadcast(mcomm, 1202, deleteints, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        Intent ints = new Intent();
        ints.setAction(notifyRec.getContent_click_action());
        PendingIntent PInt = PendingIntent.getBroadcast(mcomm, 1201, ints, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationItemInfoBase notify = notifyRec.getEntity();
        notification = notifyRec.getNotification();
        if (notifyRec.getNotification() == null) {
            if (Build.VERSION.SDK_INT >= 26) {
                createNotificationChannel("channel_" + notifyid, notify.getTitle());
                Notification.Builder builder = getChannelNotification("channel_" + notifyid, notify.getSmallIconResId(), notify.getTitle(), notify.getContent());
                builder.setContentIntent(PInt);
                builder.setDeleteIntent(DelInt);
                if (notify.getNotifycallback() != null) {
                    if (notify.getNotifycallback().getNotificationContentView() != null) {
                        builder.setCustomContentView(notify.getNotifycallback().getNotificationContentView());
                    }
                }
                builder.setOnlyAlertOnce(true);
                notification = builder.build();
            } else {
                NotificationCompat.Builder builder = getNotification_25(notify.getSmallIconResId(), notify.getTitle(), notify.getContent());
                builder.setContentIntent(PInt);
                if (notify.getNotifycallback() != null) {
                    if (notify.getNotifycallback().getNotificationContentView() != null) {
                        builder.setCustomContentView(notify.getNotifycallback().getNotificationContentView());
                    }
                }
                builder.setOnlyAlertOnce(true);
                builder.setDeleteIntent(DelInt);
                notification = builder.build();
            }
            notifyRec.setNotification(notification);
        } else {
            if (!notify.isUserOldNotify) {
                if (Build.VERSION.SDK_INT >= 26) {
                    createNotificationChannel("channel_" + notifyid, notify.getTitle());
                    Notification.Builder builder = getChannelNotification("channel_" + notifyid, notify.getSmallIconResId(), notify.getTitle(), notify.getContent());
                    builder.setContentIntent(PInt);
                    builder.setDeleteIntent(DelInt);
                    if (notify.getNotifycallback() != null) {
                        if (notify.getNotifycallback().getNotificationContentView() != null) {
                            builder.setCustomContentView(notify.getNotifycallback().getNotificationContentView());
                        }
                    }
                    builder.setOnlyAlertOnce(true);
                    notification = builder.build();
                } else {
                    NotificationCompat.Builder builder = getNotification_25(notify.getSmallIconResId(), notify.getTitle(), notify.getContent());
                    builder.setContentIntent(PInt);
                    builder.setDeleteIntent(DelInt);
                    if (notify.getNotifycallback() != null) {
                        if (notify.getNotifycallback().getNotificationContentView() != null) {
                            builder.setCustomContentView(notify.getNotifycallback().getNotificationContentView());
                        }
                    }
                    builder.setOnlyAlertOnce(true);
                    notification = builder.build();
                }
                notifyRec.setNotification(notification);
            }
        }
        getManager().notify(notifyid, notification);
    }

    /**
     * 取消请求
     *
     * @param notification
     */
    public void CancelNotification(NotificationItemInfoBase notification) {
        if (notification == null) {
            return;
        }
        cancelNotification(notification.getId());
    }


    /**
     * 取消请求
     *
     * @param notificationid 通知Id
     */
    public void cancelNotification(int notificationid) {
//        if (Looper.getMainLooper().getThread()!=Thread.currentThread()){
//            throw  new RuntimeException("NotificationUtils  所有方法必须在UI线程中操作");
//        }
        NotificationBroadcastReceiver curr = getNotifyById(notificationid);
        if (curr != null) {
            mcomm.unregisterReceiver(curr);
            getManager().cancel(curr.notifyId);
            notificationItemInfoBaseHashMap.remove(curr);
            if (curr.getEntity().getNotifycallback() != null) {
                curr.getEntity().getNotifycallback().oncancel(curr.getEntity(), mcomm);
            }
        }
    }


    /**
     * 获取通知提醒权限是否有效
     *
     * @param context
     * @return
     */
    public static boolean isEnabled(Context context) {
        String str2 = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(str2)) {
            String[] arrayOfString = str2.split(":");
            for (byte b = 0; b < arrayOfString.length; b = (byte) (b + 1)) {
                ComponentName componentName = ComponentName.unflattenFromString(arrayOfString[b]);
                if (componentName != null && TextUtils.equals(context.getPackageName(), componentName.getPackageName()))
                    return true;
            }
        }
        return false;
    }

    /**
     * 跳转到设置里的修改通知提醒权限
     *
     * @param context
     */
    public static void openNotificationAccess(Context context) {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        context.startActivity(intent);
    }

}
