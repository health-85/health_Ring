package com.sdk.satwatch.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sdk.satwatch.MyApplication;
import com.sdk.satwatch.R;
import com.sdk.satwatch.view.TiXingZhouQiDialog;
import com.sw.watches.bean.DrinkInfo;
import com.sw.watches.bean.MedicalInfo;
import com.sw.watches.bean.MeetingInfo;
import com.sw.watches.bean.SitInfo;
import com.sw.watches.service.ZhBraceletService;

import java.util.Calendar;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;


public class ReminActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private CheckBox medica_togg, sit_togg, drink_togg, meeting_togg;
    private TextView medica_period, medica_start_time, medica_end_time;
    private TextView sit_period, sit_start_time, sit_end_time;
    private TextView drink_period, drink_start_time, drink_end_time, meeting_hour_time;
    private TextView meeting_time;
    private ZhBraceletService mBleService = MyApplication.getZhBraceletService();
    private MedicalInfo mMedicalInfo;
    private SitInfo mSitInfo;
    private DrinkInfo mDrinkInfo;
    private MeetingInfo mMeetingInfo;

    private View mClickView;

    private TiXingZhouQiDialog tiXingZhouQiDialog = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remin);
        setTitle("ReminActivity");
        initView();
        initData();
    }

    void initView() {
        medica_period = (TextView) findViewById(R.id.medica_period);
        medica_start_time = (TextView) findViewById(R.id.medica_start_time);
        medica_end_time = (TextView) findViewById(R.id.medica_end_time);
        medica_togg = (CheckBox) findViewById(R.id.medica_togg);

        sit_period = (TextView) findViewById(R.id.sit_period);
        sit_start_time = (TextView) findViewById(R.id.sit_start_time);
        sit_end_time = (TextView) findViewById(R.id.sit_end_time);
        sit_togg = (CheckBox) findViewById(R.id.sit_togg);

        drink_period = (TextView) findViewById(R.id.drink_period);
        drink_start_time = (TextView) findViewById(R.id.drink_start_time);
        drink_end_time = (TextView) findViewById(R.id.drink_end_time);
        drink_togg = (CheckBox) findViewById(R.id.drink_togg);

        meeting_time = (TextView) findViewById(R.id.meeting_time);
        meeting_hour_time = (TextView) findViewById(R.id.meeting_hour_time);
        meeting_togg = (CheckBox) findViewById(R.id.meeting_togg);

        tiXingZhouQiDialog = new TiXingZhouQiDialog(this);
        tiXingZhouQiDialog.Add(new TiXingZhouQiDialog.CheckItemEntity("1小时", false, 1));
        tiXingZhouQiDialog.Add(new TiXingZhouQiDialog.CheckItemEntity("2小时", false, 2));
        tiXingZhouQiDialog.Add(new TiXingZhouQiDialog.CheckItemEntity("3小时", false, 3));
        tiXingZhouQiDialog.Add(new TiXingZhouQiDialog.CheckItemEntity("4小时", false, 4));
        tiXingZhouQiDialog.notifyDatasetChanged();
        tiXingZhouQiDialog.resultEvent = new TiXingZhouQiDialog.IResultEvent() {
            @Override
            public void onSelectedItem(TiXingZhouQiDialog.CheckItemEntity sel) {
                if (mClickView.getId() == medica_period.getId()){
                    medica_period.setText((Integer) sel.value + "");
                    mMedicalInfo.setMedicalPeriod((Integer) sel.value);
                }else if (mClickView.getId() == sit_period.getId()){
                    sit_period.setText((Integer) sel.value + "");
                    mSitInfo.setSitPeriod((Integer) sel.value);
                }else if (mClickView.getId() == drink_period.getId()){
                    drink_period.setText((Integer) sel.value + "");
                    mDrinkInfo.setDrinkPeriod((Integer) sel.value);
                }
            }
        };

        medica_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickView = view;
                tiXingZhouQiDialog.show();
            }
        });
        sit_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickView = view;
                tiXingZhouQiDialog.show();
            }
        });
        drink_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickView = view;
                tiXingZhouQiDialog.show();
            }
        });
    }

    void initData() {
        if (mBleService != null) {
            mMedicalInfo = mBleService.getMedicalInfo();
            mMedicalInfo.setMedicalPeriod(1);
            medica_start_time.setText(mMedicalInfo.getMedicalStartHour() + ":" + mMedicalInfo.getMedicalStartMin());
            medica_end_time.setText(mMedicalInfo.getMedicalEndHour() + ":" + mMedicalInfo.getMedicalEndMin());
            medica_period.setText(String.valueOf(mMedicalInfo.getMedicalPeriod()));
            medica_togg.setChecked(mMedicalInfo.getMedicalEnable());

            mSitInfo = mBleService.getSitInfo();
            mSitInfo.setSitPeriod(SitInfo.SitPU1);
            sit_start_time.setText(mSitInfo.getSitStartHour() + ":" + mSitInfo.getSitStartMin());
            sit_end_time.setText(mSitInfo.getSitEndHour() + ":" + mSitInfo.getSitEndMin());
            sit_period.setText(String.valueOf(mSitInfo.getSitPeriod()));
            sit_togg.setChecked(mSitInfo.isSitEnable());

            mDrinkInfo = mBleService.getDrinkInfo();
            mDrinkInfo.setDrinkPeriod(DrinkInfo.DrinkPU1);
            drink_start_time.setText(mDrinkInfo.getDrinkStartHour() + ":" + mDrinkInfo.getDrinkStartMin());
            drink_end_time.setText(mDrinkInfo.getDrinkEndHour() + ":" + mDrinkInfo.getDrinkEndMin());
            drink_period.setText(String.valueOf(mDrinkInfo.getDrinkPeriod()));
            drink_togg.setChecked(mDrinkInfo.getDrinkEnable());

            mMeetingInfo = mBleService.getMeetingInfo();
            meeting_time.setText((mMeetingInfo.getMeetingYear() + 2000) + "-" +(mMeetingInfo.getMeetingMonth())  + "-" + mMeetingInfo.getMeetingDay());
            meeting_hour_time.setText(mMeetingInfo.getMeetingHour() + ":" + mMeetingInfo.getMeetingMin());
            meeting_togg.setChecked(mMeetingInfo.getMeetingEnable());
        }
        medica_togg.setOnCheckedChangeListener(this);
        sit_togg.setOnCheckedChangeListener(this);
        drink_togg.setOnCheckedChangeListener(this);
        meeting_togg.setOnCheckedChangeListener(this);
    }


    protected void onDestroy() {
        super.onDestroy();
    }

    public void setMedicalInfoStart(View view){
        onTimePicker(mMedicalInfo, medica_start_time, true);
    }

    public void setMedicalInfoEnd(View view){
        onTimePicker(mMedicalInfo, medica_end_time, false);
    }

    public void ReminSetMedicalInfo(View view) {
        if (mBleService != null) {
            mBleService.setMedicalInfo(mMedicalInfo);
        }
    }

    public void setInfoStartTime(View view){
        onTimePicker(mSitInfo, sit_start_time, true);
    }

    public void setInfoEndTime(View view){
        onTimePicker(mSitInfo, sit_end_time, false);
    }

    public void ReminSetsIInfo(View view) {
        if (mBleService != null) {
            mBleService.setSitInfo(mSitInfo);
        }
    }

    public void setDrinkStartTime(View view){
        onTimePicker(mDrinkInfo, drink_start_time, true);
    }

    public void setDrinkEndTime(View view){
        onTimePicker(mDrinkInfo, drink_end_time, false);
    }

    public void ReminSetDrinkInfo(View view) {
        if (mBleService != null) {
            mBleService.setDrinkInfo(mDrinkInfo);
        }
    }

    public void setMeetingTime(View view){
        showPick();
    }

    public void setMeetingHour(View view){
        onTimePicker(mMeetingInfo, meeting_time, true);
    }

    public void ReminSetMeetingInfo(View view) {
        if (mBleService != null) {
            mBleService.setMeetingInfo(mMeetingInfo);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.medica_togg:
                mMedicalInfo.setMedicalEnable(isChecked);
                break;
            case R.id.sit_togg:
                mSitInfo.setSitEnable(isChecked);
                break;
            case R.id.drink_togg:
                mDrinkInfo.setDrinkEnable(isChecked);
                break;
            case R.id.meeting_togg:
                mMeetingInfo.setMeetingEnable(isChecked);
                break;
        }
    }

    /**
     * 显示出生年月
     */
    private void showPick() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        final DatePicker picker = new DatePicker(this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        picker.setRangeEnd(2111, 1, 11);
        picker.setRangeStart(2016, 8, 29);
        picker.setSelectedItem(currentYear, currentMonth, currentDay);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                mMeetingInfo.setMeetingYear(Integer.valueOf(year) % 2000);
                mMeetingInfo.setMeetingMonth(Integer.valueOf(month));
                mMeetingInfo.setMeetingDay(Integer.valueOf(day));
                meeting_time.setText((mMeetingInfo.getMeetingYear() + 2000) + "-" + (mMeetingInfo.getMeetingMonth()) + "-" + mMeetingInfo.getMeetingDay());
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    public void onTimePicker(Object info, TextView view, boolean isStart) {
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setUseWeight(false);
        picker.setCycleDisable(false);
        picker.setRangeStart(0, 0);//00:00
        picker.setRangeEnd(23, 59);//23:59
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        picker.setSelectedItem(currentHour, currentMinute);
        picker.setTopLineVisible(false);
        picker.setTextPadding(ConvertUtils.toPx(this, 15));
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {

                int hourInt = Integer.valueOf(hour);
                int min = Integer.valueOf(minute);

                if (info instanceof MedicalInfo){
                    if (isStart){
                        mMedicalInfo.setMedicalStartHour(hourInt);
                        mMedicalInfo.setMedicalStartMin(min);
                    }else {
                        mMedicalInfo.setMedicalEndHour(hourInt);
                        mMedicalInfo.setMedicalEndMin(min);
                    }
                    view.setText(hour + ":" + minute);
                } else if (info instanceof SitInfo){
                    SitInfo info1 = (SitInfo) info;
                    if (isStart){
                        mSitInfo.setSitStartHour(hourInt);
                        mSitInfo.setSitStartMin(min);
                    }else {
                        mSitInfo.setSitEndHour(hourInt);
                        mSitInfo.setSitEndMin(min);
                    }
                    view.setText(hour + ":" + minute);
                }else if (info instanceof DrinkInfo){
                    if (isStart){
                        mDrinkInfo.setDrinkStartHour(hourInt);
                        mDrinkInfo.setDrinkStartMin(min);
                    }else {
                        mDrinkInfo.setDrinkEndHour(hourInt);
                        mDrinkInfo.setDrinkEndMin(min);
                    }
                    view.setText(hour + ":" + minute);
                }else if (info instanceof MeetingInfo){
                    mMeetingInfo.setMeetingHour(hourInt);
                    mMeetingInfo.setMeetingMin(min);
                    meeting_hour_time.setText(hour + ":" + minute);
                }
            }
        });
        picker.show();
    }
}
