package com.healthbit.framework;

/**
* @Description:    基础框架配置管理类
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/4/23
* @UpdateRemark:   无
* @Version:        1.0
*/
public class FrameworkConfig {

    public String cacheDir = "";
    public String preferenceName = "preference";

    public boolean useUnsafeHttps = false;

    public String[] sslCertPath;

    private static class ConfigWrapper {
        private static FrameworkConfig mInstance = new FrameworkConfig();
    }

    private static class CrasgLogConfigWrapper {
        private static CrashLogConfig mInstance = new CrashLogConfig();
    }

    public static FrameworkConfig getConfig() {
        return ConfigWrapper.mInstance;
    }

    public static CrashLogConfig getCrashConfig() {
        return CrasgLogConfigWrapper.mInstance;
    }

    public static class CrashLogConfig {

        public boolean crashCatch = true;

        public boolean showCrashUI = true;

        public long crashFileMaxSize = 10 * 1000 * 1000;

    }
}
