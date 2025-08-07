package com.healthbit.framework.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @Description: 日期工具类
 * @Author: zxy(1051244836 @ qq.com)
 * @CreateDate: 2019/4/23
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class DateUtil {
    public static SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-M-dd", Locale.CHINA);
    public static SimpleDateFormat sdfYMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    public static SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    public static SimpleDateFormat sdfYMDHMS2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
    public static SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    public static SimpleDateFormat sdfMD = new SimpleDateFormat("MM-dd", Locale.CHINA);
    public static SimpleDateFormat sdfHM = new SimpleDateFormat("HH:mm", Locale.CHINA);
    public static SimpleDateFormat sdfHMS = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    public static SimpleDateFormat sdfMDHM = new SimpleDateFormat("MM/dd HH:mm", Locale.CHINA);
    public static SimpleDateFormat sdfMDAndHM = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
    public static SimpleDateFormat sdfYM = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
    public static SimpleDateFormat sdfMD2 = new SimpleDateFormat("M月d日");
    public static SimpleDateFormat sdfUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private final static String[] weekOfDays = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 直接输出【"X秒前"】或【"X分钟前"】或【"X小时前"(X<=5)】或【今天/昨天/前天"HH:mm"】或【"MM-dd"】格式
     */
    public static String getExactDate(long date) {
        if (sdfYMD.format(date).equals(sdfYMD.format(new Date()))) {
            long range = System.currentTimeMillis() - date;
            if (range < 1000) {//避免1秒内完成了所有流程而显示负数
                return "1秒前";
            } else if (range <= 60 * 1000 && range >= 1000) {
                return range / 1000 + "秒前";
            } else if (range <= 60 * 60 * 1000 && range >= 60 * 1000) {
                return range / 60000 + "分钟前";
            } else if (range <= 6 * 60 * 60 * 1000 && range >= 60 * 60 * 1000) {
                return range / 3600000 + "小时前";//小于6小时
            } else {
                return "今天" + sdfHM.format(date);
            }
        } else if (sdfYMD.format(date).equals(sdfYMD.format(new Date().getTime() - 24 * 60 * 60 * 1000))) {
            return "昨天" + sdfHM.format(date);
        } else if (sdfYMD.format(date).equals(sdfYMD.format(new Date().getTime() - 2 * 24 * 60 * 60 * 1000))) {
            return "前天" + sdfHM.format(date);
        } else {
            return sdfMD.format(date);
        }
    }

    /**
     * 直接输出【今天/昨天/前天"HH:mm"】或【"yyyy-MM-dd"】格式
     */
    public static String getYMDTDate(long date) {
        if (sdfYMD.format(date).equals(sdfYMD.format(new Date()))) {
            return "今天" + sdfHM.format(date);
        } else if (sdfYMD.format(date).equals(sdfYMD.format(new Date().getTime() - 24 * 60 * 60 * 1000))) {
            return "昨天" + sdfHM.format(date);
        } else if (sdfYMD.format(date).equals(sdfYMD.format(new Date().getTime() - 2 * 24 * 60 * 60 * 1000))) {
            return "前天" + sdfHM.format(date);
        } else {
            return sdfYMD.format(date);
        }
    }

    /**
     * 直接输出"yyyy-MM-dd"格式
     */
    public static String getYMDDate(long date) {
        return sdfYMD.format(date);
    }

    /**
     * 直接输出"HH:mm:ss"格式
     */
    public static String getHMSDate(long date) {
        return sdfHMS.format(date);
    }

    /**
     * 直接输出【"X秒前"】或【"X分钟前"】或"MM-dd HH:mm"格式
     */
    public static String getMDHMDate(long date) {
        if (sdfMD.format(date).equals(sdfMD.format(new Date()))) {
            long range = System.currentTimeMillis() - date;
            if (range < 1000) {//避免1秒内完成了所有流程而显示负数
                return "1秒前";
            } else if (range <= 60 * 1000 && range >= 1000) {
                return range / 1000 + "秒前";
            } else if (range <= 60 * 60 * 1000 && range >= 60 * 1000) {
                return range / 60000 + "分钟前";
            } else {
                return sdfMDHM.format(date);
            }
        } else {
            return sdfMDHM.format(date);
        }
    }

    /**
     * 直接输出"MM-dd"格式或【今天】
     */
    public static String getMDDate(long date) {
        if (sdfYMD.format(date).equals(sdfYMD.format(new Date()))) {
            return "今天";
        }
        return sdfMD.format(date);
    }

    /**
     * 直接输出【今天/昨天/前天"HH:mm"】或【"MM-dd"】格式
     */
    public static String getMDTDate(long date) {
        if (sdfYMD.format(date).equals(sdfYMD.format(new Date()))) {
            return "今天" + sdfHM.format(date);
        } else if (sdfYMD.format(date).equals(sdfYMD.format(new Date().getTime() - 24 * 60 * 60 * 1000))) {
            return "昨天" + sdfHM.format(date);
        } else if (sdfYMD.format(date).equals(sdfYMD.format(new Date().getTime() - 2 * 24 * 60 * 60 * 1000))) {
            return "前天" + sdfHM.format(date);
        } else {
            return sdfMD.format(date);
        }
    }

    /**
     * 返回"某月某日"
     *
     * @param date
     * @return
     */
    public static String getMDDate2(long date) {
        return sdfMD2.format(date);
    }

    /**
     * 直接输出【昨天/前天"HH:mm"】或【"MM-dd"】格式
     */
    public static String getYMDHMTDate(long date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date(date));
        Calendar nowCal1 = Calendar.getInstance();
        nowCal1.setTime(new Date());
        if (sdfYMD.format(date).equals(sdfYMD.format(new Date()))) {
            return sdfHM.format(date);
        } else if (sdfYMD.format(date).equals(sdfYMD.format(new Date().getTime() - 24 * 60 * 60 * 1000))) {
            return "昨天" + sdfHM.format(date);
        } else if (sdfYMD.format(date).equals(sdfYMD.format(new Date().getTime() - 2 * 24 * 60 * 60 * 1000))) {
            return "前天" + sdfHM.format(date);
        } else if (cal1.get(Calendar.YEAR) != nowCal1.get(Calendar.YEAR)) {
            return sdfYMDHM.format(date);
        } else {
            return sdfMDAndHM.format(date);
        }
    }


    /**
     * 直接输出"yyyy-MM-dd HH:mm"格式
     */
    public static String getYMDHMDate(long date) {
        return sdfYMDHM.format(date);
    }

    /**
     * 直接输出"yyyy-MM"格式
     */
    public static String getYMDate(long date) {
        return sdfYM.format(date);
    }

    /**
     * 直接输出"HH:mm"格式
     */
    public static String getHMDate(long date) {
        return sdfHM.format(date);
    }

    /**
     * 直接输出"yyyy-MM-dd HH:mm:ss"格式
     */
    public static String getYMDHMSDate(long date) {
        return sdfYMDHMS.format(date);
    }

    /**
     * 直接输出"yyyyMMddHHmmss"格式
     */
    public static String getYMDHMSDate2(long date) {
        return sdfYMDHMS2.format(date);
    }

    /**
     * 格式为yyyy-M-dd才可以使用，yyyy-MM-dd或会失败
     */
    public static long parseLong(String date) {
        try {
            return sdfParse.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 格式为yyyy-MM-dd才可以使用
     */
    public static long parseLong2(String date) {
        try {
            return sdfYMD.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 格式为yyyy-MM-dd HH:mm:ss才可以使用
     */
    public static long parseLong3(String date) {
        try {
            return sdfYMDHMS.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 格式为yyyy-MM-dd HH:mm才可以使用
     */
    public static long parseLong4(String date) {
        try {
            return sdfYMDHM.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 自定义日期解析格式
     */
    public static long parseLong(String date, SimpleDateFormat format) {
        try {
            return format.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 获取指定日期是星期几 参数为null时表示获取当前日期是星期几
     *
     * @param milliseconds
     * @return
     */
    public static String getWeekOfDate(Long milliseconds) {
        return getWeekOfDate(milliseconds, weekOfDays);
    }

    public static String getWeekOfDate(Long milliseconds, String[] weekOfDays) {
        Calendar calendar = Calendar.getInstance();
        if (milliseconds != null) {
            calendar.setTimeInMillis(milliseconds);
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekOfDays[w];
    }

    /**
     * 计算date之后n天的日期
     */
    public static Date getDateAfter(Date date, int n) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + n);
        return now.getTime();
    }

    /**
     * 计算date之前n天的日期
     */
    public static Date getDateBefore(Date date, int n) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - n);
        return now.getTime();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    /**
     * 比较两个日期相差的天数，有正负
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int calcIntervalDays(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        int days = (int) ((calendar2.getTime().getTime() - calendar1.getTime().getTime()) / (1000 * 3600 * 24));
        return days;
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {

        return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
    }

    public static long parseUTCTime(String utc) {
        sdfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdfUTC.parse(utc).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 秒转成 00：00：00
     * @param seconds
     * @return
     */
    public static String formatTime(long seconds) {
        int temp;
        StringBuffer sb = new StringBuffer();
        if (seconds > 3600) {
            temp = (int) (seconds / 3600);
            sb.append((seconds / 3600) < 10 ? "0" + temp + ":" : temp + ":");
            temp = (int) (seconds % 3600 / 60);
            changeSeconds(seconds, temp, sb);
        } else {
            temp = (int) (seconds % 3600 / 60);
            changeSeconds(seconds, temp, sb);
        }
        return sb.toString();
    }

    private static void changeSeconds(long seconds, int temp, StringBuffer sb) {
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");
        temp = (int) (seconds % 3600 % 60);
        sb.append((temp < 10) ? "0" + temp : "" + temp);
    }

}
