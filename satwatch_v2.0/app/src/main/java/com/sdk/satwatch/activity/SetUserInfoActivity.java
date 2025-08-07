package com.sdk.satwatch.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.R;
import com.sw.watches.bean.UserInfo;

public class SetUserInfoActivity extends AppCompatActivity {

    private EditText etHeight;
    private EditText etWeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info);
        initView();
    }

    private void initView(){
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
    }

    public void send(View view){
        if (MyApplication.getZhBraceletService() != null) {
            int height = stringToInt(etHeight.getText().toString());
            int weight = stringToInt(etWeight.getText().toString());
            MyApplication.getZhBraceletService().setUserInfo(new UserInfo(height, weight, 30, false));
        }
    }

    private int stringToInt(String s){
        try {
            return Integer.valueOf(s);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
