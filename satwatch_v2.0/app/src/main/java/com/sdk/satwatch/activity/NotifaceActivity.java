package com.sdk.satwatch.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.util.PermissionUtil;
import com.sdk.satwatch.R;
import com.sw.watches.notification.NotificationSetting;
import com.sw.watches.notification.NotificationUtils;


public class NotifaceActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    private NotificationSetting mNotificationSetting = MyApplication.getInstance().getNotificationSetting();

    private CheckBox togg_call, togg_sms, togg_qq, togg_wx, togg_skype, togg_wahtsapp,
            togg_facebook, togg_linkedin, togg_twitter, togg_viber, togg_line;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notiface);
        setTitle("NotifaceActivity");
        initView();
        initData();
    }

    void initView() {
        togg_call = (CheckBox) findViewById(R.id.togg_call);
        togg_sms = (CheckBox) findViewById(R.id.togg_sms);
        togg_qq = (CheckBox) findViewById(R.id.togg_qq);
        togg_wx = (CheckBox) findViewById(R.id.togg_wx);
        togg_skype = (CheckBox) findViewById(R.id.togg_skype);
        togg_wahtsapp = (CheckBox) findViewById(R.id.togg_wahtsapp);
        togg_facebook = (CheckBox) findViewById(R.id.togg_facebook);
        togg_linkedin = (CheckBox) findViewById(R.id.togg_linkedin);
        togg_twitter = (CheckBox) findViewById(R.id.togg_twitter);
        togg_viber = (CheckBox) findViewById(R.id.togg_viber);
        togg_line = (CheckBox) findViewById(R.id.togg_line);
    }

    private void initData() {
        if (!NotificationUtils.isEnabled(NotifaceActivity.this)) {
            OpenNoticeDialog();
        }
        togg_call.setChecked(mNotificationSetting.get_call());
        togg_sms.setChecked(mNotificationSetting.get_sms());
        togg_qq.setChecked(mNotificationSetting.get_qq());
        togg_wx.setChecked(mNotificationSetting.get_wx());
        togg_skype.setChecked(mNotificationSetting.get_skype());
        togg_wahtsapp.setChecked(mNotificationSetting.get_whatsapp());
        togg_facebook.setChecked(mNotificationSetting.get_facebook());
        togg_linkedin.setChecked(mNotificationSetting.get_linkedin());
        togg_twitter.setChecked(mNotificationSetting.get_twitter());
        togg_viber.setChecked(mNotificationSetting.get_viber());
        togg_line.setChecked(mNotificationSetting.get_line());

        togg_call.setOnCheckedChangeListener(this);
        togg_sms.setOnCheckedChangeListener(this);
        togg_qq.setOnCheckedChangeListener(this);
        togg_wx.setOnCheckedChangeListener(this);
        togg_skype.setOnCheckedChangeListener(this);
        togg_wahtsapp.setOnCheckedChangeListener(this);
        togg_facebook.setOnCheckedChangeListener(this);
        togg_twitter.setOnCheckedChangeListener(this);
        togg_linkedin.setOnCheckedChangeListener(this);
        togg_viber.setOnCheckedChangeListener(this);
        togg_line.setOnCheckedChangeListener(this);


    }

    void OpenNoticeDialog() {
        new android.app.AlertDialog.Builder(NotifaceActivity.this)
                .setTitle(getString(R.string.dailog2_title))
                .setMessage(getString(R.string.dailog2_message))
                .setPositiveButton(getString(R.string.dailog2_positive), new DialogInterface.OnClickListener() {//添加确定按钮
                    public void onClick(DialogInterface dialog, int which) {
                        NotificationUtils.openNotificationAccess(NotifaceActivity.this);
                    }
                }).setNegativeButton(getString(R.string.dailog2_negative), new DialogInterface.OnClickListener() {//添加返回按钮

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }


    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        switch (compoundButton.getId()) {
            case R.id.togg_call:
                if (PermissionUtil.checkSelfPermissions(NotifaceActivity.this, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)) {
                    mNotificationSetting.set_call(isChecked);
                } else {
                    togg_call.setChecked(false);
                    PermissionUtil.requestPermissions(NotifaceActivity.this, 2222, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS);
                }
                break;
            case R.id.togg_sms:
                if (PermissionUtil.checkSelfPermissions(NotifaceActivity.this, Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS)) {
                    mNotificationSetting.set_sms(isChecked);
                } else {
                    togg_sms.setChecked(false);
                    PermissionUtil.requestPermissions(NotifaceActivity.this, 2222, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS);
                }
                break;
            case R.id.togg_qq:
                mNotificationSetting.set_qq(isChecked);
                break;
            case R.id.togg_wx:
                mNotificationSetting.set_wx(isChecked);
                break;
            case R.id.togg_skype:
                mNotificationSetting.set_skype(isChecked);
                break;
            case R.id.togg_wahtsapp:
                mNotificationSetting.set_whatsapp(isChecked);
                break;
            case R.id.togg_facebook:
                mNotificationSetting.set_facebook(isChecked);
                break;
            case R.id.togg_linkedin:
                mNotificationSetting.set_linkedin(isChecked);
                break;
            case R.id.togg_twitter:
                mNotificationSetting.set_twitter(isChecked);
                break;
            case R.id.togg_viber:
                mNotificationSetting.set_viber(isChecked);
                break;
            case R.id.togg_line:
                mNotificationSetting.set_line(isChecked);
                break;
        }
    }

}
