package com.healthbit.framework.crash;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import com.healthy.rvigor.R;


public class CrashReportActivity extends AppCompatActivity {

    Toolbar mToolBar;
    TextView tvException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_report);
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        mToolBar.setTitle("崩溃日志");
        tvException = (TextView) findViewById(R.id.tv_exception);
        setSupportActionBar(mToolBar);
        Throwable exception = (Throwable) getIntent().getSerializableExtra("exception");
        String packageName = getIntent().getStringExtra("packageName");
        if (exception != null) {
            tvException.setText(exception.toString());
        }
    }
}
