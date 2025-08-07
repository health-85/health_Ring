package com.healthy.rvigor.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.healthy.rvigor.R;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.LogUtils;
import com.zyyoona7.picker.DatePickerView;
import com.zyyoona7.picker.listener.OnDateSelectedListener;
import com.zyyoona7.wheel.WheelView;

import java.util.Calendar;
import java.util.Date;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/16 16:50
 * @UpdateRemark:
 */
public class BirthDialog extends Dialog {

    private Context context;

    private DatePickerView pickerView;
    private TextView tvSure;
    private TextView tvCancel;
    private TextView tvTitle;

    public interface OnBtnClickListener {
        void onClick(Dialog dialog, long value);
    }

    public BirthDialog(Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_birth);
        tvTitle = findViewById(R.id.tv_title);
        pickerView = findViewById(R.id.date_picker);
        tvCancel = findViewById(R.id.tv_cancel);
        tvSure = findViewById(R.id.tv_sure);
        //设置点击布局外则Dialog消失
        setCanceledOnTouchOutside(true);
    }

    public void showDialog(long time, OnBtnClickListener listener) {
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.style_dialog);
        //设置Dialog背景色
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置弹窗位置
        wl.gravity = Gravity.BOTTOM;
        window.setAttributes(wl);

        Calendar endCalendar = Calendar.getInstance();
        int currentYear = endCalendar.get(Calendar.YEAR);
        int curMonth = endCalendar.get(Calendar.MONTH) - 1;
        int curDay = endCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.YEAR, 1950);

        Calendar selCalendar = Calendar.getInstance();
        selCalendar.setTimeInMillis(time);
        int selYear = selCalendar.get(Calendar.YEAR);
        int selMonth = selCalendar.get(Calendar.MONTH) + 1;
        int selDay = selCalendar.get(Calendar.DAY_OF_MONTH);

        LogUtils.i(" showDialog " + DateTimeUtils.s_long_2_str(selCalendar.getTimeInMillis(), DateTimeUtils.day_format));

//        pickerView.setDateRange(startCalendar, endCalendar);
//        pickerView.setYearRange(1970, currentYear);
//        pickerView.setDateRange(startCalendar, endCalendar, WheelView.OverRangeMode.HIDE_ITEM);
//        pickerView.setYearRange(startCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.YEAR));
        pickerView.setSelectedDate(selCalendar);
//        pickerView.setSelectedDate(selYear, selMonth, selDay);

        pickerView.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, Date date) {
                LogUtils.i(" onDateSelected year " + year + " month " + month + " day " + day);
                selCalendar.set(Calendar.YEAR, year);
                selCalendar.set(Calendar.MONTH, month - 1);
                selCalendar.set(Calendar.DAY_OF_MONTH, day);
                selCalendar.set(Calendar.HOUR, 0);
                selCalendar.set(Calendar.MINUTE, 0);
                selCalendar.set(Calendar.SECOND, 0);
                selCalendar.set(Calendar.MILLISECOND, 0);
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i(" time " + selCalendar.getTimeInMillis() +
                        " day " + DateTimeUtils.s_long_2_str(selCalendar.getTimeInMillis(), DateTimeUtils.f_format));
                if (listener != null) {
                    listener.onClick(BirthDialog.this, selCalendar.getTimeInMillis());
                }
                dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        show();
    }
}
