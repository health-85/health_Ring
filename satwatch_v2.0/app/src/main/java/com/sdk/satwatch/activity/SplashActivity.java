package com.sdk.satwatch.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.sdk.satwatch.BuildConfig;
import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.util.PermissionUtil;
import com.sdk.satwatch.R;
import com.sw.watches.bean.DeviceModule;
import com.sw.watches.notification.NotificationUtils;

import java.util.List;


public class SplashActivity extends AppCompatActivity {


    private MyApplication mMyApplication;

    private Handler myHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setTitle("SplashActivity");
        if (PermissionUtil.checkSelfPermissions(SplashActivity.this, Manifest.permission.READ_PHONE_STATE)) {
            MyApplication.getInstance().startZhBraceletService(this);
            startNextActivity();
        }else {
            PermissionUtil.requestPermissions(SplashActivity.this, 2222,  Manifest.permission.READ_PHONE_STATE);
        }
    }

    private void startNextActivity(){
//        if (NotificationUtils.isBindDeviceMac(this) || BuildConfig.DEBUG) {
//            Intent intent = new Intent(SplashActivity.this, BraceletActivity.class);
//            intent.putExtra(BraceletActivity.EXTRA_DEVICE, new DeviceModule(null, null));
//            startActivity(intent);
//            finish();
//        } else {
            Intent intent = new Intent(SplashActivity.this, ScanDeviceActivity.class);
            startActivity(intent);
            finish();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionUtil.OnRequestPermissionsResultCallbacks() {
            @Override
            public void onPermissionsGranted(int requestCode, List<String> perms, boolean isAllGranted) {
                if (perms != null && perms.contains(Manifest.permission.READ_PHONE_STATE)) {
                    MyApplication.getInstance().startZhBraceletService(SplashActivity.this);
                    startNextActivity();
                }
            }

            @Override
            public void onPermissionsDenied(int requestCode, List<String> perms, boolean isAllDenied) {
            }
        });
    }


}
