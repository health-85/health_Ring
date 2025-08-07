package com.healthy.rvigor.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.haibin.calendarview.CalendarView;
import com.healthbit.framework.util.DeviceUtil;
import com.healthy.rvigor.R;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.WatchBeanUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 特殊日期选择
 */
public class SpecDateSelectedView extends RelativeLayout {

    public SpecDateSelectedView(Context context) {
        super(context);
        init(context, null);
    }

    public SpecDateSelectedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpecDateSelectedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SpecDateSelectedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * 模式
     */
    public static enum TimeMode {
        Day, Week, Month, Year
    }


    private View rootview = null;

    /**
     * 左边的箭头
     */
    private ImageView left_jt = null;

    private ImageView right_jt = null;

    private TextView tm_view = null;

    /**
     * 时间模式
     */
    private TimeMode timeMode = TimeMode.Day;


    /**
     * 起始时间
     *
     * @return
     */
    public Date getDatestart() {
        return datestart;
    }

    /**
     * 结束时间
     *
     * @return
     */
    public Date getDateend() {
        return dateend;
    }

    /**
     * 当前起始时间
     */
    private Date datestart = new Date();

    /**
     * 当前结束日期
     */
    private Date dateend = new Date();

    private Date now = new Date();

    private Date endDate = null;

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SpecDateSelectedView);
        Drawable timeRightImg = a.getDrawable(R.styleable.SpecDateSelectedView_timeRightImg);
        a.recycle();

        rootview = LayoutInflater.from(getContext()).inflate(R.layout.specdateselectedview_layout, null);
        if (rootview != null) {
            this.addView(rootview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            left_jt = rootview.findViewById(R.id.img_left);
            if (left_jt != null) {
                left_jt.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        left_jt_click();
                    }
                });
            }
            right_jt = rootview.findViewById(R.id.img_right);
            if (right_jt != null) {
                right_jt.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        right_jt_click();
                    }
                });
            }

            tm_view = rootview.findViewById(R.id.tm_view);
            if (tm_view != null) {
                tm_view.setText(DateTimeUtils.toDateString(datestart, "yyyy/MM/dd"));
            }

            if (timeRightImg != null) {
                timeRightImg.setBounds(0, 0, timeRightImg.getIntrinsicWidth(), timeRightImg.getIntrinsicHeight());
                tm_view.setCompoundDrawables(null, null, timeRightImg, null);
            }
            tm_view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCalendarDialog(getContext(), datestart.getTime(), DeviceUtil.dip2px(context, 90));
                }
            });
            initState(now);
        }
    }

    /**
     * 设置模式
     *
     * @param mode
     */
    public void setTimeMode(TimeMode mode) {
        if (!timeMode.equals(mode)) {
            timeMode = mode;
            initState(now);
            onTimeChanged();
        }
    }

    public void updateTime(Date date) {
        if (date == null) return;
        now = date;
        initState(now);
        onTimeChanged();
    }

    /**
     * 时间模式
     *
     * @return
     */
    public TimeMode getTimeMode() {
        return timeMode;
    }

    /**
     * 事件
     */
    private IEvent event = null;

    /**
     * 设置事件
     *
     * @param event
     */
    public void setEvent(IEvent event) {
        this.event = event;
    }

    /**
     * 回调事件
     */
    public static interface IEvent {
        public void onTimeChanged(SpecDateSelectedView sender);
    }


    /**
     * 时间发生改变之后
     */
    private void onTimeChanged() {
        setTm_view_Text();//设置状态
        if (event != null) {
            event.onTimeChanged(this);
        }
    }

    /**
     * 设置新的时间
     *
     * @param date
     */
    public void setNewDate(Date date) {
        if (timeMode.equals(TimeMode.Day)) {//如果是天数模式才能设置
            Date newdate = newDate(date);
            if (DateTimeUtils.Compare(newdate, newDate(new Date())) < 0) {//如果小于当前时间
                if (DateTimeUtils.Compare(newdate, datestart) != 0) {
                    datestart = newdate;
                    dateend = DateTimeUtils.AddDay(datestart, 1);
                    onTimeChanged();
                }
            }
        }
    }

    /**
     * 新的时间
     *
     * @param date
     * @return
     */
    private Date newDate(Date date) {
        return DateTimeUtils
                .NewDate(DateTimeUtils.getYear(date)
                        , DateTimeUtils.getMonth(date)
                        , DateTimeUtils.getday(date), 0, 0, 0);
    }

    /**
     * 初始化时间范围状态状态
     */
    private void initState(Date now) {
        if (timeMode.equals(TimeMode.Day)) {//天数模式
            datestart = DateTimeUtils
                    .NewDate(DateTimeUtils.getYear(now)
                            , DateTimeUtils.getMonth(now)
                            , DateTimeUtils.getday(now)
                            , 0
                            , 0, 0);
            dateend = DateTimeUtils.AddDay(datestart, 1);
        }
        if (timeMode.equals(TimeMode.Week)) {//周模式
//            int week = DateTimeUtils.getWeek(now);
//            Date temp = DateTimeUtils.getToDayWeekFirstDay(now);
            Date temp = DateTimeUtils
                    .NewDate(DateTimeUtils.getYear(now)
                            , DateTimeUtils.getMonth(now)
                            , DateTimeUtils.getday(now)
                            , 0
                            , 0, 0);
            dateend = DateTimeUtils.AddDay(temp, 1);
            datestart = DateTimeUtils.AddDay(temp, -6);
        }
        if (timeMode.equals(TimeMode.Month)) {//月模式
            Date temp = DateTimeUtils
                    .NewDate(DateTimeUtils.getYear(now)
                            , DateTimeUtils.getMonth(now)
                            , DateTimeUtils.getday(now)
                            , 0
                            , 0, 0);
            dateend = DateTimeUtils.AddDay(temp, 1);
            datestart = DateTimeUtils.AddDay(temp, -29);
        }
        if (timeMode.equals(TimeMode.Year)) {//年模式
            Date temp = DateTimeUtils
                    .NewDate(DateTimeUtils.getYear(now)
                            , DateTimeUtils.getMonth(now)
                            , DateTimeUtils.getday(now)
                            , 0
                            , 0, 0);
            Date temp1 = DateTimeUtils
                    .NewDate(DateTimeUtils.getYear(now)
                            , DateTimeUtils.getMonth(now)
                            , 1
                            , 0
                            , 0, 0);
            dateend = DateTimeUtils.AddDay(temp, 1);
            datestart = DateTimeUtils.AddMonth(temp1, -11);
//            LogUtils.i(" now " + DateTimeUtils.s_long_2_str(now.getTime(), DateTimeUtils.day_format)
//                    + " datestart " + DateTimeUtils.s_long_2_str(datestart.getTime(), DateTimeUtils.day_format)
//                    + " dateend " + DateTimeUtils.s_long_2_str(dateend.getTime(), DateTimeUtils.day_format));
        }
        setTm_view_Text();
    }


    /**
     * 设置显示文本
     */
    private void setTm_view_Text() {
        if (tm_view != null) {
            if (timeMode.equals(TimeMode.Day)) {//天数模式
                tm_view.setText(DateTimeUtils.toDateString(datestart, "yyyy/MM/dd"));
            }
            if (timeMode.equals(TimeMode.Week)) {//周模式
//                if (DateTimeUtils.isToDayWeek(datestart)) {//本周
//                    tm_view.setText("本周");
//                } else {
//                    if (DateTimeUtils.isPrevWeek(datestart)) {//上周
//                        tm_view.setText("上周");
//                    } else {
//                        tm_view.setText(DateTimeUtils.toDateString(datestart, "yyyy-MM-dd"));
//                    }
//                }
                tm_view.setText(DateTimeUtils.toWeekString(datestart));
            }
            if (timeMode.equals(TimeMode.Month)) {//月模式
//                tm_view.setText(DateTimeUtils.toDateString(datestart, "yyyy-MM-dd")); //
                tm_view.setText(DateTimeUtils.toMonthString(datestart)); //
            }
            if (timeMode.equals(TimeMode.Year)) {//月模式
//                tm_view.setText(DateTimeUtils.toDateString(datestart, "yyyy-MM-dd")); //
//                tm_view.setText(DateTimeUtils.toYearString(datestart)); //
                tm_view.setText(DateTimeUtils.toYearString(datestart, dateend));
            }
        }

//        if (right_jt != null) {
//            if (mayNext()) {
//                right_jt.setImageResource(R.mipmap.ic_arrow_right);
//                right_jt.setImageTintList(ColorStateList.valueOf(Color.parseColor("#222222")));
//            } else {
//                right_jt.setImageTintList(ColorStateList.valueOf(Color.parseColor("#dadada")));
//                right_jt.setImageResource(R.mipmap.ic_arrow_right);
//            }
//        }

    }


    /**
     * 左边点击
     */
    private void left_jt_click() {
        if (timeMode.equals(TimeMode.Day)) {//天
            datestart = DateTimeUtils.AddDay(datestart, -1);
            dateend = DateTimeUtils.AddDay(datestart, 1);
        }

        if (timeMode.equals(TimeMode.Week)) {//周
            datestart = DateTimeUtils.AddDay(datestart, -7);
            dateend = DateTimeUtils.AddDay(datestart, 7);
        }

        if (timeMode.equals(TimeMode.Month)) {//月
            datestart = DateTimeUtils.AddDay(datestart, -30);
            dateend = DateTimeUtils.AddDay(datestart, 30);
        }
        if (timeMode.equals(TimeMode.Year)) {//年
            datestart = DateTimeUtils.AddMonth(datestart, -12);
            dateend = DateTimeUtils.AddMonth(datestart, 12);
            dateend = DateTimeUtils.AddDay(dateend, -1);
        }
//        LogUtils.i(" datestart " + DateTimeUtils.s_long_2_str(datestart.getTime(), DateTimeUtils.day_format)
//                + " dateend " + DateTimeUtils.s_long_2_str(dateend.getTime(), DateTimeUtils.day_format));
        onTimeChanged();
    }

    /**
     * 是否可以
     *
     * @return
     */
    private boolean mayNext() {
        if (timeMode.equals(TimeMode.Day)) {//天模式
//            if (DateTimeUtils.isTaday(datestart) || (endDate != null && datestart.getTime() >= endDate.getTime())) {//如果是今天
//                return false;
//            } else {
            return true;
//            }
        }

        if (timeMode.equals(TimeMode.Week)) {//周模式
//            LogUtils.i(" mayNext == " + DateTimeUtils.s_long_2_str(dateend.getTime(), DateTimeUtils.f_format));
//            if (DateTimeUtils.isTaday(DateTimeUtils.AddDay(dateend, -1))/* || DateTimeUtils.isTaday(dateend)*/) {//如果是本周
//                return false;
//            } else {
            return true;
//            }
        }

        if (timeMode.equals(TimeMode.Month)) {//月模式
//            if (DateTimeUtils.isTaday(DateTimeUtils.AddDay(dateend, -1)) || DateTimeUtils.isTaday(dateend)) {//如果是本月
//                return false;
//            } else {
            return true;
//            }
        }
        if (timeMode.equals(TimeMode.Year)) {//年模式
//            LogUtils.i(" dateend " + DateTimeUtils.s_long_2_str(dateend.getTime(), DateTimeUtils.day_format)
//                    /*+ " endDate " + DateTimeUtils.s_long_2_str(endDate.getTime(), DateTimeUtils.day_format)*/);
//            if (DateTimeUtils.isTodayMonth(dateend) || (endDate != null && dateend.getTime() >= endDate.getTime())) {//如果是本年
//                return false;
//            } else {
            return true;
//            }
        }
        return false;
    }

    /**
     * right
     */
    private void right_jt_click() {
        if (mayNext()) {
            if (timeMode.equals(TimeMode.Day)) {//天
                datestart = DateTimeUtils.AddDay(datestart, 1);
                dateend = DateTimeUtils.AddDay(datestart, 1);
            }

            if (timeMode.equals(TimeMode.Week)) {//周
                datestart = DateTimeUtils.AddDay(datestart, 7);
                dateend = DateTimeUtils.AddDay(datestart, 7);
            }

            if (timeMode.equals(TimeMode.Month)) {//月
                datestart = DateTimeUtils.AddDay(datestart, 30);
                dateend = DateTimeUtils.AddDay(datestart, 30);
            }
            if (timeMode.equals(TimeMode.Year)) {//月
                datestart = DateTimeUtils.AddMonth(datestart, 12);
                dateend = DateTimeUtils.AddMonth(datestart, 12);
                dateend = DateTimeUtils.AddDay(dateend, -1);
            }
//            LogUtils.i(" datestart " + DateTimeUtils.s_long_2_str(datestart.getTime(), DateTimeUtils.day_format)
//                    + " dateend " + DateTimeUtils.s_long_2_str(dateend.getTime(), DateTimeUtils.day_format));
            onTimeChanged();
        }
    }

    public void setDateRange(Date curDate) {
        now = curDate;
        endDate = now;
//        LogUtils.i("setDateRange 选项时间 " + DateTimeUtils.s_long_2_str(endDate.getTime(), DateTimeUtils.f_format));
        initState(now);
        postInvalidate();
    }

//    public Date getEndDate() {
//        return endDate;
//    }

    public Date getNow() {
        return now;
    }

    public void showCalendarDialog(Context activity, long selTime, int paddingTop) {
        Dialog dialog = new Dialog(activity, R.style.dialog_style);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_calendar, null, false);

        Window window = dialog.getWindow();
        if (window.getDecorView() != null) {
            window.getDecorView().setPadding(DeviceUtil.dip2px(activity, 10), paddingTop,
                    DeviceUtil.dip2px(activity, 10), 0);
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        window.setAttributes(lp);

        dialog.setContentView(contentView);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH);

        Calendar selCalendar = Calendar.getInstance();
        selCalendar.setTimeInMillis(selTime);

        ConstraintLayout content = contentView.findViewById(R.id.content);

        TextView tvMonth = contentView.findViewById(R.id.tv_month);
//        if (WatchBeanUtil.isEnglishApp()) {
            tvMonth.setText(DateTimeUtils.s_long_2_str(selTime, DateTimeUtils.year_month_point_format));
//        } else {
//            tvMonth.setText(DateTimeUtils.s_long_2_str(selTime, DateTimeUtils.year_month_zh_format));
//        }

        ImageView imgRight = contentView.findViewById(R.id.img_right);
        ImageView imgLeft = contentView.findViewById(R.id.img_left);

        Calendar curCalendar = Calendar.getInstance();
        curCalendar.setTimeInMillis(System.currentTimeMillis());
        int curDay = curCalendar.get(Calendar.DAY_OF_MONTH);

        CalendarView calendarView = contentView.findViewById(R.id.calendarView);
        calendarView.setWeekBar(MixWeekBar.class);
        calendarView.setSelected(true);

        Map<String, com.haibin.calendarview.Calendar> map = new HashMap<>();
        if (selCalendar.get(Calendar.YEAR) == calendarView.getCurYear() && (selCalendar.get(Calendar.MONTH) + 1) == calendarView.getCurMonth() &&
                calendarView.getCurDay() == selCalendar.get(Calendar.DAY_OF_MONTH)) {
            map.put(getSchemeCalendar(calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay(),
                            ContextCompat.getColor(activity, R.color.calendar_cur_day_color), "当前选择").toString(),
                    getSchemeCalendar(calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay(),
                            ContextCompat.getColor(activity, R.color.calendar_cur_day_color), "当前选择"));
        } else {
            map.put(getSchemeCalendar(calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay(),
                            ContextCompat.getColor(activity, R.color.calendar_cur_day_color), "当前").toString(),
                    getSchemeCalendar(calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay(),
                            ContextCompat.getColor(activity, R.color.calendar_cur_day_color), "当前"));
            map.put(getSchemeCalendar(selCalendar.get(Calendar.YEAR), (selCalendar.get(Calendar.MONTH) + 1),
                            (selCalendar.get(Calendar.DAY_OF_MONTH)),
                            ContextCompat.getColor(activity, R.color.calendar_cur_day_color), "选择").toString(),
                    getSchemeCalendar(selCalendar.get(Calendar.YEAR), (selCalendar.get(Calendar.MONTH) + 1),
                            (selCalendar.get(Calendar.DAY_OF_MONTH)),
                            ContextCompat.getColor(activity, R.color.calendar_cur_day_color), "选择"));
        }
        calendarView.setSchemeDate(map);
        calendarView.setRange(2000, 1, 1,
                calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay()
        );
        calendarView.post(new Runnable() {
            @Override
            public void run() {
                calendarView.scrollToCalendar(selCalendar.get(Calendar.YEAR), selCalendar.get(Calendar.MONTH) + 1,
                        selCalendar.get(Calendar.DAY_OF_MONTH));
            }
        });
        calendarView.setOnCalendarMultiSelectListener(new CalendarView.OnCalendarMultiSelectListener() {
            @Override
            public void onCalendarMultiSelectOutOfRange(com.haibin.calendarview.Calendar calendar) {

            }

            @Override
            public void onMultiSelectOutOfSize(com.haibin.calendarview.Calendar calendar, int maxSize) {

            }

            @Override
            public void onCalendarMultiSelect(com.haibin.calendarview.Calendar calendar, int curSize, int maxSize) {
                int year = calendar.getYear();
                int month = calendar.getMonth();
                tvMonth.setText(year + activity.getResources().getString(R.string.spe_year) + month + activity.getResources().getString(R.string.spe_month));
                selCalendar.set(Calendar.YEAR, year);
                selCalendar.set(Calendar.MONTH, month - 1);
                if (curMonth == selCalendar.get(Calendar.MONTH) && curYear == selCalendar.get(Calendar.YEAR)) {
                    imgRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#dadada")));
                } else {
                    imgRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#222222")));
                }
                if (mCalendarListener != null) {
                    mCalendarListener.onCalendarListener(null, calendar.getTimeInMillis());
                }
                dialog.dismiss();
            }
        });
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                tvMonth.setText(year + activity.getResources().getString(R.string.spe_year) + month + activity.getResources().getString(R.string.spe_month));
                selCalendar.set(Calendar.YEAR, year);
                selCalendar.set(Calendar.MONTH, month - 1);
                if (curMonth == selCalendar.get(Calendar.MONTH) && curYear == selCalendar.get(Calendar.YEAR)) {
                    imgRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#dadada")));
                } else {
                    imgRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#222222")));
                }

            }
        });
//        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
//            @Override
//            public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {
//
//            }
//
//            @Override
//            public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {
//                if (listener != null && isClick){
//                    listener.onPositiveClickListener(null, calendar.getTimeInMillis());
//                    dialog.dismiss();
//                }
//                LogUtils.i(" isClick " + isClick + " onCalendarSelect " + DateTimeUtils.s_long_2_str(calendar.getTimeInMillis(), DateTimeUtils.f_format));
//            }
//        });
        window.getDecorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curMonth == selCalendar.get(Calendar.MONTH) && curYear == selCalendar.get(Calendar.YEAR)) {
                    imgRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#dadada")));
                } else {
                    imgRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#222222")));
                    selCalendar.add(Calendar.MONTH, 1);
                    calendarView.scrollToCalendar(selCalendar.get(Calendar.YEAR), selCalendar.get(Calendar.MONTH) + 1, 1);
                }
            }
        });
        imgLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selCalendar.add(Calendar.MONTH, -1);
                calendarView.scrollToCalendar(selCalendar.get(Calendar.YEAR), selCalendar.get(Calendar.MONTH) + 1, 1);
            }
        });
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.show();
    }

    public static com.haibin.calendarview.Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    public interface OnCalendarListener{
        void onCalendarListener(View v, Object msg);
    }

    private OnCalendarListener mCalendarListener;

    public void setCalendarListener(OnCalendarListener mCalendarListener) {
        this.mCalendarListener = mCalendarListener;
    }
}
