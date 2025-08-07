package com.healthy.rvigor.bean;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/10 22:15
 * @UpdateRemark:
 */
public class MainDataBean {

    //首页表现
    public static int MAIN_EXPRESSION = 1;
    //睡眠
    public static int MAIN_SLEEP = 2;
    //活动
    public static int MAIN_MOTION = 3;
    //心理健康
    public static int MAIN_MENTAL = 4;
    //心率
    public static int MAIN_HEART = 5;
    //血氧
    public static int MAIN_OX = 6;

    //菜单类型
    private int itemType;

    private Object bean;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }
}
