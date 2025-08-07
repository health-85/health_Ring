package com.healthy.rvigor.net.http;

import com.healthy.rvigor.MyApplication;

/**
 * 基础组件
 */
public abstract class AppComponentBase {

    protected String name = "";

    private MyApplication mcon = null;

    public AppComponentBase(MyApplication con, String serviceName) {
        name = serviceName;
        mcon = con;
    }

    /**
     * 返回CommonApplication
     *
     * @return
     */
    protected MyApplication getCommonApplication() {
        return mcon;
    }

    /**
     * 获取主键服务名称
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 组件被添加进去时被触发
     */
    protected abstract void onComponentAdded();

    /**
     * 组件被移除被触发
     */
    protected abstract void onComponentRemoved();
}
