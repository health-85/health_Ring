package com.healthbit.framework.bean;

public class ActivityEventBean {

    public static final int REFRESH = 1;
    public static final int CLOSE = 2;

    public Class<?> clazz;

    public int operate;

    public ActivityEventBean(Class<?> clazz, int operate) {
        this.clazz = clazz;
        this.operate = operate;
    }
}
