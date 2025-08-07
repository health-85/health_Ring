package com.healthy.rvigor.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.Nullable;

import com.healthy.rvigor.R;

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
     *
     * @param datestr
     * @return
     */
    public static long convertStrToLong(String datestr){
        try {
            if (TextUtils.isEmpty(datestr)) return 0;
            Date date = ConvertStrToDate(datestr, "yyyy-MM-dd HH:mm:ss");
            if (date == null) {
                date = ConvertStrToDate(datestr, "yyyy-MM-dd");
            }
            return date.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
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

    public static Date addSecond(Date date, int minutes) {
        if (date == null) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, minutes);
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
     * 添加年
     *
     * @param date
     * @param year
     * @return
     */
    public static Date AddYear(Date date, int year) {
        if (date == null) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
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
    public static String toDayHoursMinutesString(Context context, long timelength) {
        long timeminutes = timelength / 60000;
        long timehours = timeminutes / 60;
        long timedays = timehours / 24;
        StringBuilder stringBuilder = new StringBuilder();
        if (timedays > 0) {
            stringBuilder.append(timedays + context.getResources().getString(R.string.day_uni));
            stringBuilder.append((timehours - (timedays * 24)) + context.getResources().getString(R.string.hour_uni));
            stringBuilder.append((timeminutes - (timehours * 60)) + context.getResources().getString(R.string.minute));
        } else {
            if (timehours > 0) {
                stringBuilder.append((timehours - (timedays * 24)) + context.getResources().getString(R.string.hour_uni));
                stringBuilder.append((timeminutes - (timehours * 60)) + context.getResources().getString(R.string.minute));
            } else {
                stringBuilder.append((timeminutes - (timehours * 60)) + context.getResources().getString(R.string.minute));
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
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(DateTimeUtils.AddDay(date, 29));
        return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) +
                "-" + endCalendar.get(Calendar.YEAR) + "/" + (endCalendar.get(Calendar.MONTH) + 1) + "/" + (endCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String toYearString(Date date, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        return calendar.get(Calendar.YEAR) + "." + (calendar.get(Calendar.MONTH) + 1)/* + "." + calendar.get(Calendar.DAY_OF_MONTH)*/ +
                "-" + endCalendar.get(Calendar.YEAR) + "." + (endCalendar.get(Calendar.MONTH) + 1) /*+ "." + (endCalendar.get(Calendar.DAY_OF_MONTH) - 1)*/;
    }

    public static String toYearString(Context context, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) + context.getResources().getString(R.string.spe_year);
    }

    public static String getShowDayTime(long time){
        if (isCurrentDay(time)){
            return DateTimeUtils.getHourAndMinShow(time);
        }else {
            return DateTimeUtils.s_long_2_str(
                    time,
                    DateTimeUtils.month_day_format_1
            );
        }
    }

    /**
     * 时间转周07.25-07.29
     *
     * @param firstDate
     * @return
     */
    public static String toWeekString(Date firstDate) {

        Calendar calendar = Calendar.getInstance();
//        Date firstDate = getToDayWeekFirstDay(date); //本周的第一天
        calendar.setTime(firstDate);
        StringBuilder builder = new StringBuilder();

        builder.append(calendar.get(Calendar.YEAR) + ".");

        int firstMonth = calendar.get(Calendar.MONTH) + 1;
        if (firstMonth < 10) {
            builder.append("0");
        }
        builder.append(firstMonth);
        builder.append(".");
        int firstDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (firstDay < 10) {
            builder.append("0");
        }
        builder.append(firstDay).append("-");

        Date endDate = AddDay(firstDate, 6);
        calendar.setTime(endDate);
        builder.append(calendar.get(Calendar.YEAR) + ".");
        int endMonth = calendar.get(Calendar.MONTH) + 1;
        if (endMonth < 10) {
            builder.append("0");
        }
        builder.append(endMonth);
        builder.append(".");
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (endDay < 10) {
            builder.append("0");
        }
        builder.append(endDay);
        return builder.toString();
    }

    public static final SimpleDateFormat day_format = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat day_dot_format = new SimpleDateFormat("yyyy.MM.dd");
    public static final SimpleDateFormat day_format_1 = new SimpleDateFormat("yyyy/MM/dd");
    public static final SimpleDateFormat month_day_format = new SimpleDateFormat("MM-dd");
    public static final SimpleDateFormat month_day_format_1 = new SimpleDateFormat("MM/dd");
    public static final SimpleDateFormat year_month_format = new SimpleDateFormat("yyyy-MM");
    public static final SimpleDateFormat day_hm_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat f_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat f_dot_format = new SimpleDateFormat("yyy.MM.dd HH:mm");

    public static final SimpleDateFormat f_format_ = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

    public static final SimpleDateFormat hm_format = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat hms_format = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat year_month_zh_format = new SimpleDateFormat("yyyy年MM月");
    public static final SimpleDateFormat year_month_day_zh_format = new SimpleDateFormat("yyyy年MM月dd日");
    public static final SimpleDateFormat year_month_day_hm_zh_format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    public static final SimpleDateFormat year_month_point_format = new SimpleDateFormat("yyyy.MM");
    public static final SimpleDateFormat month_day_hm_zh_format = new SimpleDateFormat("MM月dd日 HH:mm");
    public static final SimpleDateFormat month_day_hms_zh_format = new SimpleDateFormat("MM月dd日 HH:mm:ss");
    public static final SimpleDateFormat month_day_zh_format = new SimpleDateFormat("MM月dd日");
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

    public static boolean isCurrentDay(long date) {
        if (date == 0) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        Calendar currentDar = Calendar.getInstance();
        currentDar.setTime(new Date(System.currentTimeMillis()));
        if (calendar.get(Calendar.DAY_OF_YEAR) != currentDar.get(Calendar.DAY_OF_YEAR)) {
            return false;
        }
        return true;
    }

    public static String getCurDayString(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getHourAndMinShow(String time) {
        if (TextUtils.isEmpty(time)) return time;
        String hm = time;
        try {
            long hmTime = DateTimeUtils.s_str_to_long(time, DateTimeUtils.f_format);
            hm = DateTimeUtils.s_long_2_str(hmTime, DateTimeUtils.hm_format);
        }catch (Exception e){
            e.printStackTrace();
        }
        return hm;
    }


    public static String getHourAndMinShow(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        String hourS;
        if (hour < 10) {
            hourS = "0" + hour;
        } else {
            hourS = hour + "";
        }
        String minS;
        if (min < 10) {
            minS = "0" + min;
        } else {
            minS = min + "";
        }
//        LogUtils.i(" 时间 == " + hourS + ":" + minS);
        return hourS + ":" + minS;
    }

    //跑步时间
    public static String getRunTimeShow(int time) {

        int hour = time / (60 * 60);
        int min = (time - hour * (60 * 60)) / 60;
        int second = (time - hour * (60 * 60) - min * 60);

        String hourS;
        if (hour < 10) {
            hourS = "0" + hour;
        } else {
            hourS = hour + "";
        }
        String minS;
        if (min < 10) {
            minS = "0" + min;
        } else {
            minS = min + "";
        }
        String secondS;
        if (second < 10){
            secondS = "0" + second;
        }else {
            secondS = second + "";
        }
        if (hour > 0){
            return hourS + ":" + minS + ":" + secondS;
        }
        return minS + ":" + secondS;
    }

    /**
     * 时间转当天分钟
     * @param time
     * @return
     */
    public static float parseLongToHour(long time){
        if (time == 0) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return (float)(hour * 60 + min) / (float)60;
    }

    public static float parseLongToMin(long time){
        if (time == 0) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return (float)(hour * 60 + min);
    }

    /**
     * 获取24小时显示时间格式
     *
     * @param hour
     * @param min
     * @return
     */
    public static String get24ShowTime(int hour, int min) {
        String hourS = null;
        if (hour < 10) {
            hourS = "0" + hour;
        } else {
            hourS = hour + "";
        }
        String minS = null;
        if (min < 10) {
            minS = "0" + min;
        } else {
            minS = min + "";
        }
        return hourS + ":" + minS;
    }

    /**
     * 解析时间为小时分钟
     * @param sleepLength
     * @return
     */
    public static String parseTimeS(Context context, int sleepLength) {
        long hours = sleepLength / 60;
        if (hours <= 0) {
            hours = 0;
        }
        long minite = sleepLength - (hours * 60);
        if (minite < 0) {
            minite = 0;
        }
        if (hours > 0) {
            return hours + context.getResources().getString(R.string.hour_uni) + minite + context.getResources().getString(R.string.minute_uni);
        } else {
            return minite + context.getResources().getString(R.string.minute_uni);
        }
    }

    public static String parseTimeEnS(Context context, int sleepLength) {
        long hours = sleepLength / 60;
        if (hours <= 0) {
            hours = 0;
        }
        long minite = sleepLength - (hours * 60);
        if (minite < 0) {
            minite = 0;
        }
        if (hours > 0) {
            return hours + "h" + minite + "m";
        } else {
            return minite + "m";
        }
    }

    public static String getWeekShow(int week) {
        if (week == 1) {
            return "星期日";
        } else if (week == 2) {
            return "星期一";
        } else if (week == 3) {
            return "星期二";
        } else if (week == 4) {
            return "星期三";
        } else if (week == 5) {
            return "星期四";
        } else if (week == 6) {
            return "星期五";
        } else if (week == 7) {
            return "星期六";
        }
        return "星期六";
    }

    public static String getWeekZWShow(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            return "星期日";
        } else if (week == 2) {
            return "星期一";
        } else if (week == 3) {
            return "星期二";
        } else if (week == 4) {
            return "星期三";
        } else if (week == 5) {
            return "星期四";
        } else if (week == 6) {
            return "星期五";
        } else if (week == 7) {
            return "星期六";
        }
        return "星期六";
    }

    public static String getWeekShow(long day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(day);
        return  (calendar.get(Calendar.MONTH) + 1)  + "/" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getWeekBottomS(int week) {
        if (week == 1) {
            return "日";
        } else if (week == 2) {
            return "一";
        } else if (week == 3) {
            return "二";
        } else if (week == 4) {
            return "三";
        } else if (week == 5) {
            return "四";
        } else if (week == 6) {
            return "五";
        } else if (week == 7) {
            return "六";
        }
        return "六";
    }

    public static String getWeekS(Context context, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
//        LogUtils.i(" getWeekS " + week + " " + DateTimeUtils.s_long_2_str(time, DateTimeUtils.day_format));
        if (week == 1) {
            return context.getResources().getString(R.string.my_sunday);
        } else if (week == 2) {
            return context.getResources().getString(R.string.my_monday);
        } else if (week == 3) {
            return context.getResources().getString(R.string.my_tuesday);
        } else if (week == 4) {
            return context.getResources().getString(R.string.my_wednesday);
        } else if (week == 5) {
            return context.getResources().getString(R.string.my_thursday);
        } else if (week == 6) {
            return context.getResources().getString(R.string.my_friday);
        } else if (week == 7) {
            return context.getResources().getString(R.string.my_saturday);
        }
        return context.getResources().getString(R.string.my_saturday);
    }

    /**
     * 格式化睡眠开始时间 23：20格式为23：00  23：56 格式为23：30
     *
     * @param startTime
     * @return
     */
    public static long formatStartTime(long startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        int min = calendar.get(Calendar.MINUTE);
        if (min >= 30) {
            calendar.set(Calendar.MINUTE, 30);
        } else {
            calendar.set(Calendar.MINUTE, 0);
        }
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long formatEndTime(long endTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        int min = calendar.get(Calendar.MINUTE);
        if (min >= 30) {
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.HOUR, 1);
        } else {
            calendar.set(Calendar.MINUTE, 30);
        }
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 解析时间格式
     * @param time
     * @param timeSize
     * @param uniSize
     * @param timeColor
     * @param uniColor
     * @return
     */
    public static SpannableStringBuilder parseTime(Context context, long time, int timeSize, int uniSize, int timeColor, int uniColor) {
        if (time > 0) {
            long hours = time / 60;
            if (hours <= 0) {
                hours = 0;
            }
            long minite = time - (hours * 60);
            if (minite < 0) {
                minite = 0;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (hours > 0) {
                SpannableString hoursSp = new SpannableString(hours + "");
                hoursSp.setSpan(new ForegroundColorSpan(timeColor), 0, (hours + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                hoursSp.setSpan(new AbsoluteSizeSpan(timeSize, true), 0, (hours + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(hoursSp);
                SpannableString hoursDwSp = new SpannableString(context.getResources().getString(R.string.hour_uni));
                hoursDwSp.setSpan(new ForegroundColorSpan(uniColor), 0, (context.getResources().getString(R.string.hour_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                hoursDwSp.setSpan(new NoBoldStyleSpan(0), 0, (context.getResources().getString(R.string.hour_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                hoursDwSp.setSpan(new AbsoluteSizeSpan(uniSize, true), 0, (context.getResources().getString(R.string.hour_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(hoursDwSp);
            }
            SpannableString minuteSp = new SpannableString(minite + "");
            minuteSp.setSpan(new ForegroundColorSpan(timeColor), 0, (minite + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteSp.setSpan(new AbsoluteSizeSpan(timeSize, true), 0, (minite + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(minuteSp);
            SpannableString minuteDwSp = new SpannableString(context.getResources().getString(R.string.minute_uni));
            minuteDwSp.setSpan(new ForegroundColorSpan(uniColor), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteDwSp.setSpan(new NoBoldStyleSpan(0), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteDwSp.setSpan(new AbsoluteSizeSpan(uniSize, true), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(minuteDwSp);
//            LogUtils.i(" hours " + hours + " minite " + minite);
            return spannableStringBuilder;
        } else {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            SpannableString minuteSp = new SpannableString("--");
            minuteSp.setSpan(new ForegroundColorSpan(timeColor), 0, ("--").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteSp.setSpan(new AbsoluteSizeSpan(timeSize, true), 0, ("--").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(minuteSp);
            SpannableString minuteDwSp = new SpannableString(context.getResources().getString(R.string.minute_uni));
            minuteDwSp.setSpan(new ForegroundColorSpan(uniColor), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteDwSp.setSpan(new NoBoldStyleSpan(0), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteDwSp.setSpan(new AbsoluteSizeSpan(uniSize, true), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(minuteDwSp);
            return spannableStringBuilder;
        }
    }

    public static SpannableStringBuilder parseSleepTime(Context context, long time, int timeSize, int uniSize, int timeColor, int uniColor) {
        if (time > 0) {
            long hours = time / 60;
            if (hours <= 0) {
                hours = 0;
            }
            long minite = time - (hours * 60);
            if (minite < 0) {
                minite = 0;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (hours > 0) {
                SpannableString hoursSp = new SpannableString(hours + "");
                hoursSp.setSpan(new ForegroundColorSpan(timeColor), 0, (hours + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                hoursSp.setSpan(new AbsoluteSizeSpan(timeSize, true), 0, (hours + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(hoursSp);
                SpannableString hoursDwSp = new SpannableString(context.getResources().getString(R.string.hour_uni));
                hoursDwSp.setSpan(new ForegroundColorSpan(uniColor), 0, (context.getResources().getString(R.string.hour_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                hoursDwSp.setSpan(new NoBoldStyleSpan(0), 0, (context.getResources().getString(R.string.hour_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                hoursDwSp.setSpan(new AbsoluteSizeSpan(uniSize, true), 0, (context.getResources().getString(R.string.hour_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(hoursDwSp);
            }
            SpannableString minuteSp = new SpannableString(minite + "");
            minuteSp.setSpan(new ForegroundColorSpan(timeColor), 0, (minite + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteSp.setSpan(new AbsoluteSizeSpan(timeSize, true), 0, (minite + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(minuteSp);
            SpannableString minuteDwSp = new SpannableString(context.getResources().getString(R.string.minute_uni));
            minuteDwSp.setSpan(new ForegroundColorSpan(uniColor), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteDwSp.setSpan(new NoBoldStyleSpan(0), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteDwSp.setSpan(new AbsoluteSizeSpan(uniSize, true), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(minuteDwSp);
//            LogUtils.i(" hours " + hours + " minite " + minite);
            return spannableStringBuilder;
        } else {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

            SpannableString hoursSp = new SpannableString("--");
            hoursSp.setSpan(new ForegroundColorSpan(timeColor), 0, ("--").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            hoursSp.setSpan(new AbsoluteSizeSpan(timeSize, true), 0, ("--").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(hoursSp);
            SpannableString hoursDwSp = new SpannableString(context.getResources().getString(R.string.hour_uni));
            hoursDwSp.setSpan(new ForegroundColorSpan(uniColor), 0, (context.getResources().getString(R.string.hour_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            hoursDwSp.setSpan(new NoBoldStyleSpan(0), 0, (context.getResources().getString(R.string.hour_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            hoursDwSp.setSpan(new AbsoluteSizeSpan(uniSize, true), 0, (context.getResources().getString(R.string.hour_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(hoursDwSp);

            SpannableString minuteSp = new SpannableString("--");
            minuteSp.setSpan(new ForegroundColorSpan(timeColor), 0, ("--").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteSp.setSpan(new AbsoluteSizeSpan(timeSize, true), 0, ("--").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(minuteSp);
            SpannableString minuteDwSp = new SpannableString(context.getResources().getString(R.string.minute_uni));
            minuteDwSp.setSpan(new ForegroundColorSpan(uniColor), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteDwSp.setSpan(new NoBoldStyleSpan(0), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            minuteDwSp.setSpan(new AbsoluteSizeSpan(uniSize, true), 0, (context.getResources().getString(R.string.minute_uni)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(minuteDwSp);
            return spannableStringBuilder;
        }
    }

    //时间转时分 如 18：56
    public static String getHourMinS(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        StringBuilder builder = new StringBuilder();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            builder.append("0" + hour + ":");
        } else {
            builder.append(hour + ":");
        }
        int min = calendar.get(Calendar.MINUTE);
        if (min < 10) {
            builder.append("0" + min + "");
        } else {
            builder.append(min);
        }
        return builder.toString();
    }

    public static String getHourMinS(int time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, time / 60);
        calendar.set(Calendar.MINUTE, time % 60);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        StringBuilder builder = new StringBuilder();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            builder.append("0" + hour + ":");
        } else {
            builder.append(hour + ":");
        }
        int min = calendar.get(Calendar.MINUTE);
        if (min < 10) {
            builder.append("0" + min + "");
        } else {
            builder.append(min);
        }
        return builder.toString();
    }

}
