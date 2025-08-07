package com.healthbit.framework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 校验工具类
 * @Author: zxy(1051244836 @ qq.com)
 * @CreateDate: 2019/5/11
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class ValidateUtil {

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }


    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    public static boolean cardCodeVerifySimple(String cardCode) {
        //第一代身份证正则表达式(15位)
        String isIDCard1 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
        //第二代身份证正则表达式(18位)
        String isIDCard2 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[A-Z])$";

        //验证身份证
        if (cardCode.matches(isIDCard1) || cardCode.matches(isIDCard2)) {
            return true;
        }
        return false;
    }

    public static boolean cardCodeVerify(String cardCode) {
        int i = 0;
        String r = "error";
        String lastnumber = "";

        i += Integer.parseInt(cardCode.substring(0, 1)) * 7;
        i += Integer.parseInt(cardCode.substring(1, 2)) * 9;
        i += Integer.parseInt(cardCode.substring(2, 3)) * 10;
        i += Integer.parseInt(cardCode.substring(3, 4)) * 5;
        i += Integer.parseInt(cardCode.substring(4, 5)) * 8;
        i += Integer.parseInt(cardCode.substring(5, 6)) * 4;
        i += Integer.parseInt(cardCode.substring(6, 7)) * 2;
        i += Integer.parseInt(cardCode.substring(7, 8)) * 1;
        i += Integer.parseInt(cardCode.substring(8, 9)) * 6;
        i += Integer.parseInt(cardCode.substring(9, 10)) * 3;
        i += Integer.parseInt(cardCode.substring(10, 11)) * 7;
        i += Integer.parseInt(cardCode.substring(11, 12)) * 9;
        i += Integer.parseInt(cardCode.substring(12, 13)) * 10;
        i += Integer.parseInt(cardCode.substring(13, 14)) * 5;
        i += Integer.parseInt(cardCode.substring(14, 15)) * 8;
        i += Integer.parseInt(cardCode.substring(15, 16)) * 4;
        i += Integer.parseInt(cardCode.substring(16, 17)) * 2;
        i = i % 11;
        lastnumber = cardCode.substring(17, 18);
        if (i == 0) {
            r = "1";
        }
        if (i == 1) {
            r = "0";
        }
        if (i == 2) {
            r = "x";
        }
        if (i == 3) {
            r = "9";
        }
        if (i == 4) {
            r = "8";
        }
        if (i == 5) {
            r = "7";
        }
        if (i == 6) {
            r = "6";
        }
        if (i == 7) {
            r = "5";
        }
        if (i == 8) {
            r = "4";
        }
        if (i == 9) {
            r = "3";
        }
        if (i == 10) {
            r = "2";
        }
        if (r.equals(lastnumber.toLowerCase())) {
            return true;
        }
        return false;
    }

    /**
     * 验证身份证号码
     * @param cardCode
     * @return
     */
    public static boolean verifyCardCode(String cardCode) {
        if (cardCode.length() == 15 || cardCode.length() == 18) {
            if (!cardCodeVerifySimple(cardCode)) {
                return false;
            } else {
                if (cardCode.length() == 18 && !cardCodeVerify(cardCode)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
