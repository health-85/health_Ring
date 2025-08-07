package com.healthy.rvigor.watch;


import android.text.TextUtils;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.bean.ImportantItem;
import com.healthy.rvigor.util.SPUtil;
import com.healthy.rvigor.util.SpConfig;
import com.sw.watches.bean.AlarmInfo;
import com.sw.watches.bean.DrinkInfo;
import com.sw.watches.bean.MedicalInfo;
import com.sw.watches.bean.MeetingInfo;
import com.sw.watches.bean.SitInfo;

import java.util.List;


/**
 * 手表基础对象
 */
public abstract class WatchBase {

    private String deviceMacAddress = "";

//    private boolean isOneMinRate;

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public String getDeviceName() {
        if (!TextUtils.isEmpty(deviceName)) {
            return deviceName;
        } else {
            return (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.DEVICE_NAME, "");
        }
    }

    private String deviceName = "";

    /**
     * 版本号
     */
    public int versoinNumber = 0;


    /**
     * 版本名称
     */
    public String deviceVersion = "";


    /**
     * 是否设备处于链接状态
     */
    protected boolean isConnection = true;

    public WatchBase(String deviceMacAddress, String deviceName) {
        this.deviceMacAddress = deviceMacAddress;
        this.deviceName = deviceName;
    }


    /**
     * 查找设备
     */
    public abstract void findDevice();

    /**
     * 设置免打扰模式
     *
     * @param notDisturb
     */
    public abstract boolean setNotDisturb(boolean notDisturb);

    /**
     * 获取勿扰模式配置情况
     *
     * @return
     */
    public abstract boolean getNotDisturb();

    /**
     * 设置整点心率
     *
     * @param value
     */
    public abstract boolean setPoHeart(boolean value);

    /**
     * 获取整点心率设置
     *
     * @return
     */
    public abstract boolean getPoHeart();


    /**
     * 同步手表数据
     */
    public abstract void syncWatch();

    /**
     * 开始测量心脏
     */
    public abstract void StartTestHRV();

    /**
     * 停止测量心脏
     */
    public abstract void StopTestHRV();


    /**
     * 设置心跳血压提醒
     *
     * @param bpon
     * @param bpvalue
     * @param hron
     * @param hrvalue
     */
    public abstract void setHeartBloodAlert(boolean bpon, int bpvalue, boolean hron, int hrvalue);


    /**
     * 设置翻腕亮屏
     *
     * @param value
     */
    public abstract boolean setWristOnOff(boolean value);

    /**
     * 查询翻腕亮屏设置
     */
    public abstract boolean loadWristStatus();

    public abstract boolean loadDisturb();

    /**
     * 电话提醒
     *
     * @param value
     * @return
     */
    public abstract boolean setCalRemind(boolean value);

    /**
     * 获取电话提醒
     *
     * @return
     */
    public abstract boolean getCalRemind();

    /**
     * 短信提醒
     *
     * @param value
     * @return
     */
    public abstract boolean setSMSRemind(boolean value);

    /**
     * 短信提醒
     *
     * @return
     */
    public abstract boolean getSMSRemind();

    /**
     * QQ提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetQQRemind(boolean value);

    /**
     * QQ提醒
     *
     * @return
     */
    public abstract boolean getQQRemind();


    /**
     * 微信提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetWeChartRemind(boolean value);


    /**
     * 微信提醒
     *
     * @return
     */
    public abstract boolean getWeChartRemind();

    /**
     * 领英提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetLinkedInRemind(boolean value);

    /**
     * 领英提醒
     *
     * @return
     */
    public abstract boolean getLinkedInRemind();

    /**
     * Skype提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetSkypeRemind(boolean value);

    /**
     * Skype提醒
     *
     * @return
     */
    public abstract boolean getSkypeRemind();

    /**
     * FaceBook提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetFaceBookRemind(boolean value);

    /**
     * FaceBook提醒
     *
     * @return
     */
    public abstract boolean getFaceBookRemind();

    /**
     * Twitter提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetTwitterRemind(boolean value);

    /**
     * Twitter提醒
     *
     * @return
     */
    public abstract boolean getTwitterRemind();

    /**
     * WhatsApp提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetWhatsAppRemind(boolean value);

    /**
     * WhatsApp提醒
     *
     * @return
     */
    public abstract boolean getWhatsAppRemind();

    /**
     * Viber提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetViberRemind(boolean value);

    /**
     * Viber提醒
     *
     * @return
     */
    public abstract boolean getViberRemind();

    /**
     * Line提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetLineRemind(boolean value);


    /**
     * Line提醒
     *
     * @return
     */
    public abstract boolean getLineRemind();

    /**
     * Gmail 提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetGmailRemind(boolean value);

    /**
     * Gmail 提醒
     *
     * @return
     */
    public abstract boolean getGmailRemind();

    /**
     * OutLook 提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetOutLookRemind(boolean value);

    /**
     * OutLook 提醒
     *
     * @return
     */
    public abstract boolean getOutLookRemind();

    /**
     * Instagram  提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetInstagramRemind(boolean value);

    /**
     * Instagram 提醒
     *
     * @return
     */
    public abstract boolean getInstagramRemind();

    /**
     * SnapChat 提醒
     *
     * @param value
     * @return
     */
    public abstract boolean SetSnapChatRemind(boolean value);

    /**
     * SnapChat 提醒
     *
     * @return
     */
    public abstract boolean getSnapChatRemind();

    /**
     * 获取吃药信息
     *
     * @return
     */
    public abstract MedicalInfo getMedicaRemindInfo();

    /**
     * 设置吃药提醒
     *
     * @param medicalInfo
     */
    public abstract boolean setMedicaRemindInfo(MedicalInfo medicalInfo);

    /**
     * 设置会议提醒
     *
     * @param meetingInfo
     * @return
     */
    public abstract boolean setMeetingRemindInfo(MeetingInfo meetingInfo);


    /**
     * 获取会议提醒信息
     *
     * @return
     */
    public abstract MeetingInfo getMeetingRemindInfo();

    /**
     * 久坐提醒
     *
     * @param sitInfo
     * @return
     */
    public abstract boolean setSitRemindInfo(SitInfo sitInfo, int startMinute, int endMinute, int interval);

    /**
     * 心率过高提醒
     *
     * @return
     * @param b
     */
    public abstract void setHighHeartRemind(boolean b);

    /**
     * 打鼾提醒
     *
     * @return
     * @param b
     */
    public abstract void setSnoreRemind(boolean b);

    public abstract boolean getSnoreRemind();
    public abstract boolean getHighHeartRemind();

    /**
     * 久坐提醒
     *
     * @return
     */
    public abstract SitInfo getSitRemindInfo();

    /**
     * 喝水提醒
     *
     * @return
     */
    public abstract DrinkInfo getDrinkRemindInfo();

    /**
     * 喝水提醒
     *
     * @param drinkInfo
     * @return
     */
    public abstract boolean setDrinkRemindInfo(DrinkInfo drinkInfo);

    /**
     * 是否支持闹钟设置
     *
     * @return
     */
    public abstract boolean surportClockSetting();

    /**
     * 恢复出厂设置
     *
     * @return
     */
    public abstract boolean restore_factory();

    /**
     * 获取设备 信息
     */
    public abstract void getDeviceInfo();

    /**
     * 升级设备
     *
     * @return
     */
    public abstract boolean UpgradeDevice();

    /**
     * 设置中文语言
     *
     * @return
     */
    public abstract boolean setChinaLanguage();

    //设置语言
    public abstract void setLanguage(int type);

    /**
     * 发送加密数据
     *
     * @return
     */
    public abstract boolean sendEncryptData(byte[] bytes);

    /**
     * 设置用户信息
     *
     * @param userHeight
     * @param userWeight
     * @param age
     * @param sex
     */
    public abstract void setUserInfo(int userHeight, int userWeight, int age, boolean sex);


    /**
     * 发送测试数据
     *
     * @return
     */
    public abstract void sendTestData(byte[] bytes);

    /**
     * 发送生命力数据
     * @param list
     */
    public abstract void sendLifeData(List<Integer> list);

    /**
     * 发送运动强度
     */
    public abstract void sendMotionStrengthData(int highHour, int hourMin, int midHour, int midMin, int lowHour, int lowMin);

    /**
     * 发送血氧心率
     */
    public abstract void sendOxHeartData(int sleepMaxHeart, int sleepMinHeart, int sleepMaxOx, int sleepMinOx, int maxHeart, int minHeart, int maxOx, int minOx);

    //发送睡眠心率和血氧
    public abstract void sendSleepAverageOxHeartData(int averageSleepHeart, int averageSleepOx);

    //发送开关命令
    public abstract void sendSwitchCom(boolean heartSwitchRemind, boolean sitSwitchRemind, boolean sleepSwitchRemind, boolean oxSwitchRemind, boolean disturbSwitchRemind, boolean sleepOxRemind);

    public abstract void setWatchSwitch();

    //设置心率过高开关
    public abstract void setHeartRemind(boolean isHeartRemind);

    //心率自动检测
    public abstract void setAutomaticHeartRemind(boolean isHeartRemind);

    //设置久坐提醒开关
    public abstract void setSitRemind(boolean isSitRemind, int startMinute, int endMinute, int interval);
    //血氧提醒开关
    public abstract void setOxRemind(boolean isOxRemind);
    //睡眠提醒开关
    public abstract void setSleepRemind(boolean isSleepRemind);

    //勿扰模式开关
    public abstract void setDisturbRemind(boolean isDisturbRemind, int startHour, int startMin, int endHour, int endMin);


    //开始跑步
    public abstract void sendStartRunCom();

    //结束跑步
    public abstract void sendEndRunCom();

    //开始心率测量
    public abstract void measureHeart();

    public abstract void stopMeasureHeart();


    //开始血氧测量
    public abstract void measureOx();

    //开始体温和环境温度测量
    public abstract void measureHeatAndTemp(boolean isHeat);
    public abstract void stopMeasureHeatAndTemp();

    public abstract void setMeasureHeatEnable(boolean isEnable);

    //开始疲勞和压力测量
    public abstract void measureTireAndPressure();

    //保存闹钟 是否重复
    public abstract AlarmInfo saveClock(List<ImportantItem> importantItemList, ImportantItem importantItem, boolean repeat);

    //设置单位
    public abstract void setUnit(boolean isMetric);

    //设置温度单位 摄氏度 华氏度
    public abstract void setTempUnit(boolean isCentigrade);
    //设置24小时模式
    public abstract void setHourSystem(boolean is24Model);
    //开始测量血压
    public abstract void readBpValue();
    //停止测量血压
    public abstract void stopReadBpValue();
    //采集频率
    public abstract void setContinueHrp(boolean isOpen, int interval);

    public void Release() {
        ValidateInUIThread();
        isConnection = false;
    }

    /**
     * 验证是否在UI中操作
     */
    protected void ValidateInUIThread() {
//        if (!MainApplication.getInstance().IsUIThread()) {
//            throw new RuntimeException("必须在UI线程中操作");
//        }
    }

    /**
     * 关闭设备
     */
    public abstract void close();

//    public boolean isOneMinRate() {
//        return isOneMinRate;
//    }

//    public void setOneMinRate(boolean oneMinRate) {
//        this.isOneMinRate = oneMinRate;
//    }
}
