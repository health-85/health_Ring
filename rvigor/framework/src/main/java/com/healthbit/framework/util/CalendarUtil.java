package com.healthbit.framework.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* @Description:    日历工具类
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/4/23
* @UpdateRemark:   无
* @Version:        1.0
*/
public class CalendarUtil {

    private static final long ONE_DAY = 1000 * 3600 * 24;

    public static List<Calendar> getWeekCalendars(Calendar date) {
        List<Calendar> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.getTime());
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        int startDiff = week - 1;
        long startTime = date.getTimeInMillis() - startDiff * ONE_DAY;
        Calendar minCalendar = Calendar.getInstance();
        minCalendar.setTimeInMillis(startTime);
        for (int i = 0; i < 7; i++) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(minCalendar.getTimeInMillis() + i * ONE_DAY);
            list.add(instance);
        }
        return list;
    }

    public static List<Calendar> getLastWeekCalendars(Calendar calendar) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(calendar.getTimeInMillis() - 7 * ONE_DAY);
        return getWeekCalendars(date);
    }

    public static List<Calendar> getNextWeekCalendars(Calendar calendar) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(calendar.getTimeInMillis() + 7 * ONE_DAY);
        return getWeekCalendars(date);
    }

    public static Calendar getMonthFirstDay(Calendar calendar) {
        Calendar instance = Calendar.getInstance();
        instance.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        return instance;
    }

    public static Calendar getMonthLastDay(Calendar calendar) {
        Calendar instance = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        instance.set(year, month, getMonthDaysCount(year, month + 1));
        return instance;
    }

    /**
     * 获取某月的天数
     *
     * @param year  年
     * @param month 月
     * @return 某月的天数
     */
    public static int getMonthDaysCount(int year, int month) {
        int count = 0;
        //判断大月份
        if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            count = 31;
        }

        //判断小月
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            count = 30;
        }

        //判断平年与闰年
        if (month == 2) {
            if (isLeapYear(year)) {
                count = 29;
            } else {
                count = 28;
            }
        }
        return count;
    }


    /**
     * 是否是闰年
     *
     * @param year year
     * @return 是否是闰年
     */
    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }

    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     * @return
     */
    public static String getWeekDay(long seconds) {

        Date date = new Date(seconds);
        String Week = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int wek = c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            Week += "周日";
        }
        if (wek == 2) {
            Week += "周一";
        }
        if (wek == 3) {
            Week += "周二";
        }
        if (wek == 4) {
            Week += "周三";
        }
        if (wek == 5) {
            Week += "周四";
        }
        if (wek == 6) {
            Week += "周五";
        }
        if (wek == 7) {
            Week += "周六";
        }
        return Week;

    }
}
