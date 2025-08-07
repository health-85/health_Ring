package com.healthy.rvigor.util;


import android.text.TextUtils;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NumberUtils {

    /**
     * 数字转字符串
     *
     * @param value
     * @param pattern
     * @return
     */
    public static String toIntegerString(int value, String pattern) {
        try {
            DecimalFormat format = new DecimalFormat(pattern);
            return format.format(value);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String toFloatString(float value, String pattern) {
        try {
            DecimalFormat format = new DecimalFormat(pattern);
            return format.format(value);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String toLongString(long value, String pattern) {
        try {
            DecimalFormat format = new DecimalFormat(pattern);
            return format.format(value);
        } catch (Exception ex) {
            return "";
        }
    }

    public static int convertStringToInt(String num, int defaultValue) {
        try {
            return Integer.parseInt(num);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /**
     * 转换文本
     *
     * @param num
     * @param pattern
     * @return
     */
    public static String toString(double num, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(num);
    }

    /**
     * 不做四舍五入，直接去除后面数量
     * @param num
     * @param pattern
     * @return
     */
    public static String toDownString(double num, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(num);
    }

    /**
     * 格式化
     * @param num
     * @param decimalPoint 小数点
     * @return
     */
    public static float formatDecimal(float num, int decimalPoint) {
        double len = Math.pow(10, decimalPoint);
        return (float) (Math.round(num * len) / len);
    }

    /**
     * 是否为正数
     *
     * @param numstr
     * @return
     */
    public static boolean isPositiveInteger(String numstr) {
        try {
            int num = Integer.parseInt(numstr);
            if (num >= 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 是否为double类型
     *
     * @param numstr
     * @return
     */
    public static boolean isPositiveDouble(String numstr) {
        try {
            double num = Double.parseDouble(numstr);
            if (num >= 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 转换int类型
     *
     * @param num
     * @param defaultValue
     * @return
     */
    public static int fromStringToInteger(String num, int defaultValue) {
        try {
            if (TextUtils.isEmpty(num)) return 0;
            return Integer.parseInt(num);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static float fromStringToFloat(String num, float defaultValue) {
        try {
            if (TextUtils.isEmpty(num)) return 0;
            return Float.parseFloat(num);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static double fromStringToDouble(String num, double defaultValue) {
        try {
            return Double.parseDouble(num);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static int stringToInt(String num, int defaultValue) {
        if (TextUtils.isEmpty(num)) return defaultValue;
        if (num.contains(".")) {
            int indexOf = num.indexOf(".");
            num = num.substring(0, indexOf);
        }
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 删除数字的单位字符
     *
     * @param num
     * @param unitstr
     * @return
     */
    public static String delNumberUnitStr(String num, String unitstr) {
        if (TextUtils.isEmpty(num)) {
            return "";
        }
        int uindex = num.indexOf(unitstr);
        if (uindex > 0) {
            return num.substring(0, uindex);
        } else {
            if (uindex > -1) {
                return "";
            } else {
                return num;
            }
        }
    }

    /**
     * 英里到里的转换
     *
     * @param mile
     * @return
     */
    public static double MileToM1(double mile) {
        return mile * (1609.34);
    }

    /**
     * 通过身高步数计算步行距离
     *
     * @param shenggao 单位cm  厘米
     * @param step     步数
     * @return 单位  米
     */
    public static double StepToDistanceM(float shenggao, long step) {
        int sg = (int) shenggao;
        if (sg > 300) {
            sg = 300;
        }
        if (sg < 0) {
            sg = 0;
        }
        if (step < 0) {
            step = 0;
        }
        return sg * 0.41 * step * 0.00001F;
    }

    /**
     * 字符串转long
     *
     * @param num
     * @param defaultValue
     * @return
     */
    public static long fromStringToLong(String num, long defaultValue) {
        try {
            return Long.parseLong(num);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) return false;
        try {
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(str).matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //计算平均差
    public static float calcuateData(List<Float> list) {
        if (list == null || list.isEmpty()) return 0.0f;
        float n = 0f;
        float mean = 0f;
        for (float data : list){
            if (data > 0){
                n++;
                mean += data;
            }
        }
        if (n == 1) return 0.01f; //一個數據的標準差
        mean /= n;
        float sumOfSquares = calculateSumOfSquares(list, mean); // 计算各项差的平方和
        float variance = (sumOfSquares / (n - 1)); // 计算方差
        double standardDeviation = Math.sqrt(variance); // 计算标准差
        System.out.println("标准差为：" + standardDeviation);
        return Math.round(standardDeviation * 100) / 100f;
    }

    // 计算各项差的平方和
    private static float calculateSumOfSquares(List<Float> dataList, float mean) {
        float sum = 0.0f;
        for (int i = 0; i < dataList.size(); i++) {
            sum += Math.pow((dataList.get(i) - mean), 2);
        }
        return sum;
    }

    public static int getStepNumHourTimeSec(List<Integer> stepdata, int timeMode) {
        if (stepdata == null || stepdata.isEmpty()) return 0;
        int tick = timeMode * 60 / 15;
        int start = (timeMode - 1) * 60 / 15 ;//起始点
        int step = 0;
        for (int i = start; i < tick; i++){
            if (stepdata.size() > i) {
                step += stepdata.get(i);
            }
        }
        return step;
    }

    public static void InitBeanStringFieldValueNoneNull(Object bean) {
        if (bean == null) {
            return;
        }
        try {
            Class cls = bean.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field fd = fields[i];
                try {
                    fd.setAccessible(true);
                    if (fd.getType().equals(String.class)) {
                        if (fd.get(bean) == null) {
                            fd.set(bean, "");
                        }
                    }
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {
        }
    }

//    public static List<Float> getDataList(List<ChartBean.DataItem> list){
//        if (list == null || list.isEmpty()) return null;
//        List<Float> dataList = new ArrayList<>();
//        for (ChartBean.DataItem dataItem : list){
//            if (dataItem.data > 0){
//                dataList.add((float) dataItem.data);
//            }
//        }
//        return dataList;
//    }
}
