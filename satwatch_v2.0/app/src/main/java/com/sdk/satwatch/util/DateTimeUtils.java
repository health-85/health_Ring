package com.sdk.satwatch.util;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间单元
 */
public class DateTimeUtils {
    /**
     * 转换时间
     *
     * @param datestr
     * @return
     */
    public static @Nullable
    Date ConvertStrToDate(String datestr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(datestr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取非null的时间对象
     *
     * @param date
     * @return
     */
    public static Date getNonNullDate(Date date) {
        if (date == null) {
            return new Date(0);
        } else {
            return date;
        }
    }


    /**
     * 获取日期的日期部分  不包括时间小时以后的部分
     *
     * @param date
     * @return
     */
    public static Date getDateTimeDatePart(Date date) {
        return NewDate(getYear(date), getMonth(date), getday(date), 0, 0, 0);
    }


    /**
     * 本项目日期转换
     *
     * @param datestr
     * @return
     */
    public static Date convertStrToDateForThisProject(String datestr) {
        Date date = ConvertStrToDate(datestr, "yyyy-MM-dd HH:mm:ss");
        if (date == null) {
            return ConvertStrToDate(datestr, "yyyy-MM-dd");
        } else {
            return date;
        }
    }

    /**
     * 创建今天零点的时间
     *
     * @return
     */
    public static Date createToday0Dian() {
        Date now = new Date();
        return NewDate(getYear(now), getMonth(now), getday(now), 0, 0, 0);
    }

    /**
     * 转换时间
     *
     * @param datestr
     * @param pattern
     * @return
     */

    public static @Nullable
    Date ConvertStrToDate(String datestr, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(datestr.replace("T", " "));
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取tick
     *
     * @param date
     * @return
     */
    public static long getTime(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return 0;
    }

    /**
     * 转换
     *
     * @param date
     * @return
     */
    public static String toDateString(Date date, String pattern) {
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                return sdf.format(date);
            } catch (Exception ex) {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * 转换成消息时间
     *
     * @param date
     * @return
     */
    public static String makemsgtime(Date date) {
        if (date != null) {
            Date now = new Date();
            String dstr = DateTimeUtils.toDateString(date, "yyyy-MM-dd HH:mm:ss");
            String nstr = DateTimeUtils.toDateString(now, "yyyy-MM-dd HH:mm:ss");
            if (isYesterday(date)) {
                return "昨天";
            }
            if (isTaday(date)) {
                long minite = (now.getTime() - date.getTime()) / (1000 * 60);
                if (minite < 1) {
                    return "刚刚";
                } else {
                    if (minite < 60) {
                        return minite + "分钟前";
                    } else {
                        int hours = (int) (minite / 60);
                        if (hours < 6) {
                            return hours + "小时前";
                        } else {
                            makehours(getHour(date));
                        }
                    }
                }
            }
            return toDateStringForThisProject(date);
        }
        return "";
    }


    /**
     * 转换成消息时间  中文时间
     *
     * @param date
     * @return
     */
    public static String makemsgtimezh(Date date) {
        if (date != null) {
            Date now = new Date();
            if (isYesterday(date)) {
                return "昨天";
            }
            if (isTaday(date)) {
                long minite = (now.getTime() - date.getTime()) / (1000 * 60);
                if (minite < 1) {
                    return "刚刚";
                } else {
                    if (minite < 60) {
                        return minite + "分钟前";
                    } else {
                        int hours = (int) (minite / 60);
                        if (hours < 6) {
                            return hours + "小时前";
                        } else {
                            makehours(getHour(date));
                        }
                    }
                }
            }
            if (isTodayYear(date)) {
                return toDateString(date, "MM月dd日");
            } else {
                return toDateString(date, "yyyy年MM月dd日");
            }
        }
        return "";
    }

    /**
     * 是否是今年
     *
     * @param date
     * @return
     */
    public static boolean isTodayYear(Date date) {
        if (date != null) {
            int yeardate = getYear(date);
            int yearnow = getYear(new Date());
            return (yearnow == yeardate);
        } else {
            return false;
        }
    }


    private static String makehours(int hour) {
        if (hour < 8) {
            return "凌晨" + hour + "点";
        } else {
            if (hour < 9) {
                return "早上" + hour + "点";
            } else {
                if (hour < 12) {
                    return "上午" + hour + "点";
                } else {
                    if (hour == 12) {
                        return "中午" + hour + "点";
                    } else {
                        if (hour < 18) {
                            return "下午" + (hour - 12) + "点";
                        } else {
                            return "晚午" + (hour - 12) + "点";
                        }
                    }
                }
            }
        }
    }


    /**
     * 比较两个时间
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int Compare(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.compareTo(calendar2);
    }

    /**
     * 年
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 或月数
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取日期中的天数部分
     *
     * @param date
     * @return
     */
    public static int getday(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取日期中当月的总天数
     *
     * @param date
     * @return
     */
    public static int getMonthDays(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取日期中的小时
     *
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取日期中的分钟
     *
     * @param date
     * @return
     */
    public static int getMinute(Date date) {
        if (date == null) {
            return 0;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取日期中的秒
     *
     * @param date
     * @return
     */
    public static int getSecond(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 日期是否是今天
     *
     * @param date
     * @return
     */
    public static boolean isTaday(Date date) {
        if (date == null) {
            return false;
        }
        Date nowdate = new Date();
        if (getYear(nowdate) == getYear(date)) {
            if (getMonth(nowdate) == getMonth(date)) {
                if (getday(nowdate) == getday(date)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否为今天凌晨8之前
     *
     * @param date
     * @return
     */
    public static boolean isTadayLingCheng8dian(Date date) {
        if (isTaday(date)) {
            Date now = new Date();
            //早成8点
            Date today8dian = NewDate(getYear(now), getMonth(now), getday(now), 8, 0, 0);
            if (date.getTime() <= today8dian.getTime()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 是否为今天8-12之间
     *
     * @param date
     * @return
     */
    public static boolean isTadayLingCheng8_12dian(Date date) {
        if (isTaday(date)) {
            Date now = new Date();
            //早成8点
            Date today8dian = NewDate(getYear(now), getMonth(now), getday(now), 8, 0, 0);
            //中午12点
            Date today12dian = NewDate(getYear(now), getMonth(now), getday(now), 12, 0, 0);
            if ((date.getTime() > today8dian.getTime()) && (date.getTime() <= today12dian.getTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为今天12-18之间
     *
     * @param date
     * @return
     */
    public static boolean isTadayLingCheng12_18dian(Date date) {
        if (isTaday(date)) {
            Date now = new Date();
            //中午12点
            Date today12dian = NewDate(getYear(now), getMonth(now), getday(now), 12, 0, 0);
            //下午18点
            Date today18dian = NewDate(getYear(now), getMonth(now), getday(now), 18, 0, 0);

            if ((date.getTime() > today12dian.getTime())
                    && (date.getTime() <= today18dian.getTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为今天18之后
     *
     * @param date
     * @return
     */
    public static boolean isTadayafter18dian(Date date) {
        if (isTaday(date)) {
            Date now = new Date();
            //下午18点
            Date today18dian = NewDate(getYear(now), getMonth(now), getday(now), 18, 0, 0);
            //下午23 59 59
            Date today24dian = NewDate(getYear(now), getMonth(now), getday(now), 23, 59, 59);

            if ((date.getTime() > today18dian.getTime()) && (date.getTime() <= today24dian.getTime())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取星期 1-7  （周日到周6）
     *
     * @param date
     * @return
     */
    public static int getWeek(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 增加天数后的日期  负数为减
     *
     * @param date
     * @param days
     * @return
     */
    public static Date AddDay(Date date, int days) {
        if (date == null) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 增加小时后的日期  负数为减
     *
     * @param date
     * @param Hours
     * @return
     */
    public static Date AddHours(Date date, int Hours) {
        if (date == null) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, Hours);
        return calendar.getTime();
    }

    /**
     * 增加分钟
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date AddMinute(Date date, int minutes) {
        if (date == null) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    /**
     * 添加月数
     *
     * @param date
     * @param months
     * @return
     */
    public static Date AddMonth(Date date, int months) {
        if (date == null) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    /**
     * 新的日期
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date NewDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取某周的第一天  以中国为例 星期一为一周的第一天
     *
     * @param date
     * @return
     */
    public static Date getToDayWeekFirstDay(Date date) {
        int week = getWeek(date);
        if (week >= 2) {//周一到周六
            Date firstdate = AddDay(date, -(week - 2));
            return NewDate(getYear(firstdate), getMonth(firstdate), getday(firstdate), 0, 0, 0);
        } else {//周日
            Date firstdate = AddDay(date, -6);
            return NewDate(getYear(firstdate), getMonth(firstdate), getday(firstdate), 0, 0, 0);
        }
    }

    /**
     * 日期是否为本周
     *
     * @param date
     * @return
     */
    public static boolean isToDayWeek(Date date) {
        if (date == null) {
            return false;
        }
        Date first = getToDayWeekFirstDay(new Date());//本周的第一天
        Date nextfirst = AddDay(first, 7);//下周的第一天
        return ((date.getTime() >= first.getTime()) && (date.getTime() < nextfirst.getTime()));
    }

    /**
     * 是否为本月
     *
     * @param date
     * @return
     */
    public static boolean isTodayMonth(Date date) {
        if (date == null) {
            return false;
        }
        Date now = new Date();
        return ((getYear(now) == getYear(date)) && (getMonth(now) == getMonth(date)));
    }


    /**
     * 是否为上周
     *
     * @param date
     * @return
     */
    public static boolean isPrevWeek(Date date) {
        if (date == null) {
            return false;
        }
        Date first = getToDayWeekFirstDay(new Date());//本周的第一天
        Date prevfirst = AddDay(first, -7);//上周的第一天
        return ((date.getTime() >= prevfirst.getTime()) && (date.getTime() < first.getTime()));
    }


    /**
     * 获取日期中当前月的天数
     *
     * @param date
     * @return
     */
    public static int getMonthMaxDays(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 是否为昨天
     *
     * @param date
     * @return
     */
    public static boolean isYesterday(Date date) {
        if (date == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date newdate = calendar.getTime();
        return isTaday(newdate);
    }


    /**
     * 转换
     *
     * @param date
     * @return
     */
    public static String toDateString(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        } else {
            return "";
        }
    }

    /**
     * 转换没有年份的日期格式
     *
     * @param date
     * @return
     */
    public static String toDateStringNoYear(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
            return sdf.format(date);
        } else {
            return "";
        }
    }

    /**
     * 转换为10天几小时几分钟
     *
     * @return
     */
    public static String toDayHoursMinutesString(long timelength) {
        long timeminutes = timelength / 60000;
        long timehours = timeminutes / 60;
        long timedays = timehours / 24;
        StringBuilder stringBuilder = new StringBuilder();
        if (timedays > 0) {
            stringBuilder.append(timedays + "天");
            stringBuilder.append((timehours - (timedays * 24)) + "小时");
            stringBuilder.append((timeminutes - (timehours * 60)) + "分");
        } else {
            if (timehours > 0) {
                stringBuilder.append((timehours - (timedays * 24)) + "小时");
                stringBuilder.append((timeminutes - (timehours * 60)) + "分");
            } else {
                stringBuilder.append((timeminutes - (timehours * 60)) + "分");
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 项目转换
     *
     * @param date
     * @return
     */
    public static String toDateStringForThisProject(Date date) {
        if (isTodayYear(date)) {
            return toDateStringNoYear(date);
        } else {
            return toDateString(date, "yyyy-MM-dd HH:mm:ss");
        }
    }


    /**
     * 时间差天数
     *
     * @param end
     * @param start
     * @return
     */
    public static int diffDays(Date end, Date start) {
        if (end == null) {
            return 0;
        }
        if (start == null) {
            return 0;
        }
        long diff = end.getTime() - start.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    /**
     * 时间转月份
     *
     * @param date
     * @return
     */
    public static String toMonthString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return (calendar.get(Calendar.MONTH) + 1) + "月";
    }

    /**
     * 时间转周07.25-07.29
     *
     * @param date
     * @return
     */
    public static String toWeekString(Date date) {

        Calendar calendar = Calendar.getInstance();
        Date firstDate = getToDayWeekFirstDay(date); //本周的第一天
        calendar.setTime(firstDate);
        StringBuilder builder = new StringBuilder();
        int firstMonth = calendar.get(Calendar.MONTH) + 1;
        if (firstMonth < 10) {
            builder.append("0");
        }
        builder.append(firstMonth);
        builder.append(".");
        int firstDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (firstDay < 10){
            builder.append("0");
        }
        builder.append(firstDay).append("-");

        Date endDate = AddDay(firstDate, 6);
        calendar.setTime(endDate);
        int endMonth = calendar.get(Calendar.MONTH) + 1;
        if (endMonth < 10) {
            builder.append("0");
        }
        builder.append(endMonth);
        builder.append(".");
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (endDay < 10){
            builder.append("0");
        }
        builder.append(endDay);
        return builder.toString();
    }

    public static final SimpleDateFormat day_format = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat year_month_format = new SimpleDateFormat("yyyy-MM");
    public static final SimpleDateFormat day_hm_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat f_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat f_format_ = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    public static final SimpleDateFormat hm_format = new SimpleDateFormat("HH:mm");

    /**
     * @param timestamp
     * @return 转换年月日
     */
    public static String s_long_2_str(long timestamp, SimpleDateFormat format) {
        return format.format(new Date(timestamp));
    }


    /**
     * @param dateString
     * @return 时间转Long
     */
    public static long s_str_to_long(String dateString, SimpleDateFormat format) {
        try {
            Date d = format.parse(dateString);
            return d.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 是否是当天
     *
     * @param date
     * @return
     */
    public static boolean isCurrentDay(String date, SimpleDateFormat format) {
        if (TextUtils.isEmpty(date)) return true;
        long boDate = DateTimeUtils.s_str_to_long(date, format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(boDate));
        Calendar currentDar = Calendar.getInstance();
        currentDar.setTime(new Date(System.currentTimeMillis()));
        if (calendar.get(Calendar.DAY_OF_YEAR) != currentDar.get(Calendar.DAY_OF_YEAR)) {
            return false;
        }
        return true;
    }
}
