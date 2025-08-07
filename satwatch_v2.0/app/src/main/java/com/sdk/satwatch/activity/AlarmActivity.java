package com.sdk.satwatch.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.R;
import com.sdk.satwatch.adapter.AlarmListAdapter;
import com.sw.watches.bean.AlarmInfo;
import com.sw.watches.service.ZhBraceletService;
import com.sw.watches.util.LogUtil;

import java.util.ArrayList;


public class AlarmActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ZhBraceletService mBleService = MyApplication.getZhBraceletService();

    private int my_position = 0;
    private AlarmInfo mAlarmInfo;
    private ArrayList<AlarmInfo> AlarmInfoList;
    private ListView alarm_list;
    private AlarmListAdapter mAlarmListAdapter;
    private CheckBox alarm_ch1, alarm_ch2, alarm_ch3, alarm_ch4, alarm_ch5, alarm_ch6, alarm_ch7, alarm_ch_switch;
    private EditText alarm_edit_hour, alarm_edit_min;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setTitle("AlarmActivity");
        initView();
        initSetAdapter();
        initData();

    }

    void initData() {
        AlarmInfoList = new ArrayList<AlarmInfo>();
        if (mBleService != null) {
            AlarmInfoList = mBleService.getAlarmData();
            mAlarmListAdapter.setAlarmInfo(AlarmInfoList);
        }
    }

    void initView() {
        alarm_list = (ListView) findViewById(R.id.alarm_list);
        alarm_ch1 = (CheckBox) findViewById(R.id.alarm_ch1);
        alarm_ch2 = (CheckBox) findViewById(R.id.alarm_ch2);
        alarm_ch3 = (CheckBox) findViewById(R.id.alarm_ch3);
        alarm_ch4 = (CheckBox) findViewById(R.id.alarm_ch4);
        alarm_ch5 = (CheckBox) findViewById(R.id.alarm_ch5);
        alarm_ch6 = (CheckBox) findViewById(R.id.alarm_ch6);
        alarm_ch7 = (CheckBox) findViewById(R.id.alarm_ch7);
        alarm_ch_switch = (CheckBox) findViewById(R.id.alarm_ch_switch);
        alarm_edit_hour = (EditText) findViewById(R.id.alarm_edit_hour);
        alarm_edit_min = (EditText) findViewById(R.id.alarm_edit_min);

        alarm_ch1.setOnCheckedChangeListener(this);
        alarm_ch2.setOnCheckedChangeListener(this);
        alarm_ch3.setOnCheckedChangeListener(this);
        alarm_ch4.setOnCheckedChangeListener(this);
        alarm_ch5.setOnCheckedChangeListener(this);
        alarm_ch6.setOnCheckedChangeListener(this);
        alarm_ch7.setOnCheckedChangeListener(this);
        alarm_ch_switch.setOnCheckedChangeListener(this);
    }

    void initSetAdapter() {
        mAlarmListAdapter = new AlarmListAdapter(AlarmActivity.this);
        alarm_list.setAdapter(mAlarmListAdapter);
        alarm_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        my_position = position;
                        mAlarmInfo = mAlarmListAdapter.getAlarmInfo(position);
//                        setTitle("正在操作...  下标=" + position + "    id=" + mAlarmInfo.getAlarmId());
                        setTitle("Is operating... Subscript=" + position + "    id=" + mAlarmInfo.getAlarmId());
                        setCheck((byte) mAlarmInfo.getAlarmData());
                        alarm_edit_hour.setText(String.valueOf(mAlarmInfo.getAlarmHour()));
                        alarm_edit_min.setText(String.valueOf(mAlarmInfo.getAlarmMin()));
                        alarm_ch1.setEnabled(true);
                        alarm_ch2.setEnabled(true);
                        alarm_ch3.setEnabled(true);
                        alarm_ch4.setEnabled(true);
                        alarm_ch5.setEnabled(true);
                        alarm_ch6.setEnabled(true);
                        alarm_ch7.setEnabled(true);
                        alarm_ch_switch.setEnabled(true);
                        alarm_edit_hour.setEnabled(true);
                        alarm_edit_min.setEnabled(true);
                        findViewById(R.id.change_time).setEnabled(true);
                    }
                });

    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void AddClock(View view) {
        if (mBleService != null) {
            AlarmInfoList = mBleService.addAlarmData(AlarmInfoList, new AlarmInfo(10, 05, 42));
            mAlarmListAdapter.setAlarmInfo(AlarmInfoList);
            mAlarmListAdapter.notifyDataSetChanged();
        }
    }

    public void DeleteClock(View view) {
        if (mBleService != null) {
            AlarmInfoList = mBleService.deleteAlarmData(AlarmInfoList, 0);
            mAlarmListAdapter.setAlarmInfo(AlarmInfoList);
            mAlarmListAdapter.notifyDataSetChanged();
        }
    }

    public void ButtonSave(View view) {
        if (mBleService != null) {
            LogUtil.i("alarmByteArray " + new Gson().toJson(AlarmInfoList));
            mBleService.saveAlarmData(AlarmInfoList);
        }
    }

    public void ChangeTimeSet(View view) {
        mAlarmInfo.setAlarmtHour(Integer.valueOf(alarm_edit_hour.getText().toString().trim()));
        mAlarmInfo.setAlarmtMin(Integer.valueOf(alarm_edit_min.getText().toString().trim()));
        AlarmInfoList = mBleService.updateAlarmData(AlarmInfoList, mAlarmInfo, my_position);
        mAlarmListAdapter.setAlarmInfo(AlarmInfoList);
        mAlarmListAdapter.notifyDataSetChanged();

    }

    public void CheckedChange() {
        mAlarmInfo.setAlarmtData(getCheck());
        AlarmInfoList = mBleService.updateAlarmData(AlarmInfoList, mAlarmInfo, my_position);
        mAlarmListAdapter.setAlarmInfo(AlarmInfoList);
        mAlarmListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        CheckedChange();
    }

    void setCheck(byte data) {
        if (mBleService != null) {
            boolean[] my_data = mBleService.getCheckBoolean(data);
            alarm_ch_switch.setChecked(my_data[0]);
            alarm_ch1.setChecked(my_data[1]);
            alarm_ch2.setChecked(my_data[2]);
            alarm_ch3.setChecked(my_data[3]);
            alarm_ch4.setChecked(my_data[4]);
            alarm_ch5.setChecked(my_data[5]);
            alarm_ch6.setChecked(my_data[6]);
            alarm_ch7.setChecked(my_data[7]);
        }
    }

    int getCheck() {
        boolean[] my_data = new boolean[8];
        my_data[0] = alarm_ch_switch.isChecked();
        my_data[1] = alarm_ch1.isChecked();
        my_data[2] = alarm_ch2.isChecked();
        my_data[3] = alarm_ch3.isChecked();
        my_data[4] = alarm_ch4.isChecked();
        my_data[5] = alarm_ch5.isChecked();
        my_data[6] = alarm_ch6.isChecked();
        my_data[7] = alarm_ch7.isChecked();
        return mBleService.getCheckInt(my_data);
    }

}
