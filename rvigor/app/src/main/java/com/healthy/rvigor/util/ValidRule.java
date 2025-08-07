package com.healthy.rvigor.util;

import java.util.Calendar;

/**
 * 数据有效规则
 */
public class ValidRule {

    private volatile static ValidRule instance = new ValidRule();

    public static ValidRule getInstance() {
        return instance;
    }

    /**
     * 睡眠总时长校验：若超过24小时，显示为24小时
     * @param sleepLen
     * @return
     */
    public long getValidSleepLen(long sleepLen){
        long time24 = 24 * 60 * 60 * 1000;
        if (sleepLen > time24) return time24;
        return sleepLen;
    }

    public boolean isValidSleepLen(long sleepLen){
        long time24 = 24 * 60 * 60 * 1000;
        if (sleepLen > time24) return false;
        return true;
    }

    /**
     * 是否是有效的睡眠开始时间
     * @param startTime
     * @return
     */
    public long getValidStartTime(long startTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 20 || hour <= 8) return startTime;
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        return calendar.getTimeInMillis();
    }

    /**
     * 是否是有效的心率数据
     * 心率：≤40，≥220的数据过滤不展示
     * @param heart
     * @return
     */
    public boolean isValidHeart(int heart){
        if (heart >= 40 && heart <= 220) return true;
        return false;
    }

    public int getValidHeart(int heart){
        if (heart < 40) return 40;
        if (heart > 220) return 220;
        return heart;
    }

    /**
     * 是否有效的血氧
     * 血氧：≥100和≤0的数据为异常数据过滤不展示
     * @param ox
     * @return
     */
    public boolean isValidOx(float ox){
        if (ox >= 0f && ox <= 100f) return true;
        return false;
    }

    public int getValidOx(float ox){
        if (ox < 0) return 0;
        if (ox > 100) return 100;
        return (int) ox;
    }

    /**
     * 获取1小时的有效步数
     * 运动：柱状图一个小时的步数数据≥1万步时最多显示1万步。
     * @param step
     * @return
     */
    public long getValidStep(long step){
        if (step >= 10000) return 10000;
        return step;
    }

    /**
     * 血压：舒张压<30mmHg或>150mmHg，或收缩压>240mmHg或<50mmHg时视为异常数据过滤不展示
     * @param ldh
     * @return
     */

    public int getValidLDHBlood(int ldh){
        if (ldh < 30) return 30;
        if (ldh > 150) return 150;
        return ldh;
    }

    public int getValidHBPBlood(int hbp){
        if (hbp < 50) return 50;
        if (hbp > 240) return 240;
        return hbp;
    }

    //有效的血压
    public boolean isValidBlood(int ldh, int hbp){
        if (isValidLBHBlood(ldh) && isValidHBPBlood(hbp)) return true;
        return false;
    }
    //有效的舒展压
    public boolean isValidLBHBlood(int ldh){
        if (ldh >= 30 && ldh <= 150) return true;
        return false;
    }
    //有效的收缩压
    public boolean isValidHBPBlood(int hbp){
        if (hbp >= 50 && hbp <= 240) return true;
        return false;
    }

    /**
     * @param heat
     * 体温：<34或>42的数据过滤不展示
     * @return
     */
    public boolean isValidHeat(float heat){
        if (heat <= 42f) return true;
        /*if (heat >= 34f && heat <= 42f)*/ return false;
//        return false;
    }

    /**
     * 疲劳/压力：数值范围为0～ 100整数，非该范围的数据过滤不展示
     * @param tire
     * @return
     */
    public boolean isValidTire(int tire){
        if (tire >= 0 && tire <= 100) return true;
        return false;
    }


}
