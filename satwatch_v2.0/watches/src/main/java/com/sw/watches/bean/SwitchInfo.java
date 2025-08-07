package com.sw.watches.bean;

public class SwitchInfo {

    private boolean isHeartRemind; //心率过高提醒
    private boolean isSitRemind; //久坐提醒
    private boolean isSleepRemind;//睡眠提醒
    private boolean isLowOxRemind;//血氧过低提醒
    private boolean isDisturbRemind;//勿扰模式
    private boolean isLanguageRemind;//设备语言

    public boolean isHeartRemind() {
        return isHeartRemind;
    }

    public void setHeartRemind(boolean heartRemind) {
        isHeartRemind = heartRemind;
    }

    public boolean isSitRemind() {
        return isSitRemind;
    }

    public void setSitRemind(boolean sitRemind) {
        isSitRemind = sitRemind;
    }

    public boolean isSleepRemind() {
        return isSleepRemind;
    }

    public void setSleepRemind(boolean sleepRemind) {
        isSleepRemind = sleepRemind;
    }

    public boolean isLowOxRemind() {
        return isLowOxRemind;
    }

    public void setLowOxRemind(boolean lowOxRemind) {
        isLowOxRemind = lowOxRemind;
    }

    public boolean isDisturbRemind() {
        return isDisturbRemind;
    }

    public void setDisturbRemind(boolean disturbRemind) {
        isDisturbRemind = disturbRemind;
    }

    public boolean isLanguageRemind() {
        return isLanguageRemind;
    }

    public void setLanguageRemind(boolean languageRemind) {
        isLanguageRemind = languageRemind;
    }
}
