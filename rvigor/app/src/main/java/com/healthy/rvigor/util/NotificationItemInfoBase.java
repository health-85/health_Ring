package com.healthy.rvigor.util;

import android.widget.RemoteViews;

import com.healthy.rvigor.MyApplication;


public  class NotificationItemInfoBase {

    /**
     *  通知id
     */
    private  int  id = 0;

    /**
     *获取  通知id
     * @return
     */
    public  int  getId(){
        return  id;
    }

    /**
     *设置  通知id
     * @param value
     */
    public  void  setId(int value){
        this.id = value;
    }

    /**
     *  通知标题
     */
    private  String  title = "";

    /**
     *获取  通知标题
     * @return
     */
    public  String  getTitle(){
        return  title;
    }

    /**
     *设置  通知标题
     * @param value
     */
    public  void  setTitle(String value){
        this.title = value;
    }

    /**
     *  通知内容
     */
    private  String  content = "";

    /**
     *获取  通知内容
     * @return
     */
    public  String  getContent(){
        return  content;
    }

    /**
     *设置  通知内容
     * @param value
     */
    public  void  setContent(String value){
        this.content = value;
    }


    /**
     *  通知小图标
     */
    private  int  smallIconResId = 0;

    /**
     *获取  通知小图标
     * @return
     */
    public  int  getSmallIconResId(){
        return  smallIconResId;
    }

    /**
     *设置  通知小图标
     * @param value
     */
    public  void  setSmallIconResId(int value){
        this.smallIconResId = value;
    }

    /**
     * 是否用旧的通知实体  默认false
     */
    public  boolean isUserOldNotify=false;


    /**
     *
     * @param id  ID号
     * @param  smallResIcon  小图标的按钮
     * @param title  标题
     * @param content  内容
     */
      public NotificationItemInfoBase(int id,int smallResIcon,String title,String content){
            this.id=id;
            this.title=title;
            this.content=content;
            this.smallIconResId=smallResIcon;
      }

    /**
     * 获取通知回调
     * @return
     */
    public INotificationCallBack getNotifycallback() {
        return notifycallback;
    }

    /**
     * 设置事件回调
     * @param notifycallback
     */
    public void setNotifycallback(INotificationCallBack notifycallback) {
        this.notifycallback = notifycallback;
    }

    private  INotificationCallBack   notifycallback=null;




      public  static   interface  INotificationCallBack{
          /**
           * 通知被取消
           * @param sender
           * @param comm
           */
            public void onclick(NotificationItemInfoBase sender, MyApplication comm);

          /**
           * 通知被取消
           * @param sender
           * @param comm
           */
            public void oncancel(NotificationItemInfoBase sender, MyApplication comm);

          /**
           * 配置notifi的自定义View  没有则为null
           */
          public RemoteViews getNotificationContentView();
      }
}
