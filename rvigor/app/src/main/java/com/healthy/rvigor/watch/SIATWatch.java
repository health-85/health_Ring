package com.healthy.rvigor.watch;

import android.os.Environment;

import com.healthbit.framework.util.ToastUtil;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.bean.AppUserInfo;
import com.healthy.rvigor.bean.ImportantItem;
import com.healthy.rvigor.bean.LanguageType;
import com.healthy.rvigor.util.AppUtils;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.LogUtils;
import com.healthy.rvigor.util.SPUtil;
import com.healthy.rvigor.util.SpConfig;
import com.healthy.rvigor.util.WatchBeanUtil;
import com.sw.watches.bean.AlarmInfo;
import com.sw.watches.bean.DrinkInfo;
import com.sw.watches.bean.MedicalInfo;
import com.sw.watches.bean.MeetingInfo;
import com.sw.watches.bean.SitInfo;
import com.sw.watches.bean.UserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * siaT 手表
 */
public class SIATWatch extends WatchBase {

    private static final String TAG = "SIATWatch";

    public SIATWatch(String deviceMacAddress, String deviceName) {
        super(deviceMacAddress, deviceName);
    }

    /**
     * 查找设备
     */
    @Override
    public void findDevice() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().findDevice();
            }
        }
    }

    /**
     * 设置勿扰模式
     *
     * @param notDisturb
     * @return
     */
    @Override
    public boolean setNotDisturb(boolean notDisturb) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setNotDisturb(notDisturb);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取免打扰模式是否打开
     *
     * @return
     */
    @Override
    public boolean getNotDisturb() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                return MyApplication.Companion.instance().getSiatDeviceService().getNotDisturb();
            }
        }
        return false;
    }

    /**
     * 设置整点心率
     *
     * @param value
     */
    @Override
    public boolean setPoHeart(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setPoHeart(value);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取整点心率
     *
     * @return
     */
    @Override
    public boolean getPoHeart() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                return MyApplication.Companion.instance().getSiatDeviceService().getPoHeart();
            }
        }
        return false;
    }


    /**
     * 同步手表数据
     */
    @Override
    public void syncWatch() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
//                ParseWatchLog.getInstance().destroy();
                LogUtils.i(" getSiatDeviceService() syncTime ");
                MyApplication.Companion.instance().getSiatDeviceService().syncTime();//获取历史数据
//                if (AppUserInfo.getInstance().isLogin()) {
//                    long uid = AppUserInfo.getInstance().userInfo.id;
//                    long startTime = DateTimeUtils.getDateTimeDatePart(new Date()).getTime();
//                    long endtime = DateTimeUtils.AddDay(new Date(startTime), 1).getTime();
//                    QueryXueYaXinLvLastRecordExecutor lvLastRecordExecutor = new QueryXueYaXinLvLastRecordExecutor(getDeviceMacAddress(), uid, endtime);
//                    MyApplication.Companion.instance().getAppDaoManager().ExecuteDBAsync(lvLastRecordExecutor);
                setUserInfoToWatch();
//                }
            }
        }
    }

    @Override
    public void StartTestHRV() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().openMeasurement();//主动测量心率数据
            }
        }
    }

    @Override
    public void StopTestHRV() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().closeMeasurement();//主动关闭测量心率数据
            }
        }
    }


    @Override
    public void setHeartBloodAlert(boolean bpon, int bpvalue, boolean hron, int hrvalue) {
        ValidateInUIThread();
        ToastUtil.showToast(MyApplication.Companion.instance(), "设备不支持");
    }

    /**
     * 设置翻腕亮屏
     *
     * @param value
     */
    @Override
    public boolean setWristOnOff(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setTaiWan(value);//设置抬腕亮屏
                return true;
            }
        }
        return false;
    }


    /**
     * 查询翻腕亮屏
     */
    @Override
    public boolean loadWristStatus() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                return MyApplication.Companion.instance().getSiatDeviceService().getTaiWan();//获取抬腕亮屏
            }
        }
        return false;
    }

    @Override
    public boolean loadDisturb() {
        boolean isDisturb = (boolean) SPUtil.getData(MyApplication.Companion.instance().getApplicationContext(), SpConfig.IS_DISTURB_REMIND, false);
        return isDisturb;
    }

    @Override
    public boolean setCalRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendPhoneCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_call(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getCalRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_call();
        }
        return false;
    }

    @Override
    public boolean setSMSRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendSmsCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_sms(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getSMSRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_sms();
        }
        return false;
    }


    @Override
    public boolean SetQQRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendQQCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_qq(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getQQRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_qq();
        }
        return false;
    }

    @Override
    public boolean SetWeChartRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendWeiXinCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_wx(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getWeChartRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_wx();
        }
        return false;
    }

    @Override
    public boolean SetLinkedInRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendLinkedlnCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_linkedin(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getLinkedInRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_linkedin();
        }
        return false;
    }

    @Override
    public boolean SetSkypeRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendSkypeCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_skype(value);
            return true;
        }
        return false;
    }


    @Override
    public boolean getSkypeRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_skype();
        }
        return false;
    }

    @Override
    public boolean SetFaceBookRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendFacebookCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_facebook(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getFaceBookRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_facebook();
        }
        return false;
    }

    @Override
    public boolean SetTwitterRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendTwitterCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_twitter(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getTwitterRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_twitter();
        }
        return false;
    }

    @Override
    public boolean SetWhatsAppRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendWhatsappCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_whatsapp(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getWhatsAppRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_whatsapp();
        }
        return false;
    }

    @Override
    public boolean SetViberRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendViberCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_viber(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getViberRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_viber();
        }
        return false;
    }

    @Override
    public boolean SetLineRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendLineCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_line(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getLineRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_line();
        }
        return false;
    }

    @Override
    public boolean SetGmailRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendGmailCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_mail(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getGmailRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_mail();
        }
        return false;
    }

    @Override
    public boolean SetOutLookRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_outlook(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getOutLookRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_outlook();
        }
        return false;
    }

    @Override
    public boolean SetInstagramRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().sendInstagramCom(value);
        }
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_instagram(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getInstagramRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_instagram();
        }
        return false;
    }

    @Override
    public boolean SetSnapChatRemind(boolean value) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            MyApplication.Companion.instance().getNotificationSetting().set_snapchat(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean getSnapChatRemind() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            return MyApplication.Companion.instance().getNotificationSetting().get_snapchat();
        }
        return false;
    }

    @Override
    public MedicalInfo getMedicaRemindInfo() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                return MyApplication.Companion.instance().getSiatDeviceService().getMedicalInfo();
            }
        }
        return null;
    }

    @Override
    public boolean setMedicaRemindInfo(MedicalInfo medicalInfo) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setMedicalInfo(medicalInfo);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean setMeetingRemindInfo(MeetingInfo meetingInfo) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setMeetingInfo(meetingInfo);
            }
            return true;
        }
        return false;
    }

    @Override
    public MeetingInfo getMeetingRemindInfo() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                return MyApplication.Companion.instance().getSiatDeviceService().getMeetingInfo();
            }
        }
        return null;
    }

    @Override
    public boolean setSitRemindInfo(SitInfo sitInfo, int startMinute, int endMinute, int interval) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setSitInfo(sitInfo);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setHighHeartRemind(boolean b) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setHighHeartRemind(b);
            }
        }
    }

    @Override
    public void setSnoreRemind(boolean b) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setSnoreMonitor(b);
            }
        }
    }

    @Override
    public boolean getSnoreRemind() {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                return MyApplication.Companion.instance().getSiatDeviceService().getSnoreMonitor();
            }
        }
        return false;
    }

    @Override
    public boolean getHighHeartRemind() {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                return MyApplication.Companion.instance().getSiatDeviceService().getHighHeartRemind();
            }
        }
        return false;
    }

    @Override
    public SitInfo getSitRemindInfo() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                return MyApplication.Companion.instance().getSiatDeviceService().getSitInfo();
            }
        }
        return null;
    }

    @Override
    public DrinkInfo getDrinkRemindInfo() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                return MyApplication.Companion.instance().getSiatDeviceService().getDrinkInfo();
            }
        }
        return null;
    }

    @Override
    public boolean setDrinkRemindInfo(DrinkInfo drinkInfo) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setDrinkInfo(drinkInfo);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean surportClockSetting() {
        return true;
    }

    @Override
    public boolean restore_factory() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().restore_factory();
            }
            return true;
        }
        return false;
    }

    @Override
    public void getDeviceInfo() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().getDeviceInfo();
            }
        }
    }

    /**
     * 升级设备
     *
     * @return
     */
    @Override
    public boolean UpgradeDevice() {
        ValidateInUIThread();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory().getPath());
        if (WatchBeanUtil.getWatchStyle() == WatchBeanUtil.C100_WATCH_TYPE) {
            stringBuilder.append("/Download/test.bin");
        } else {
            stringBuilder.append("/Download/test.zip");
        }
        try {
            File file = new File(stringBuilder.toString());
            if (file.exists()) {
                if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
                    if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                        ToastUtil.showToast(MyApplication.Companion.instance(), "开始升级");
                        LogUtils.i(TAG, " path " + file.getPath());
                        if (WatchBeanUtil.getWatchStyle() == WatchBeanUtil.F38_WATCH_TYPE) {
                            MyApplication.Companion.instance().getSiatDeviceService().upgradeDevice();
                        } else if (WatchBeanUtil.getWatchStyle() == WatchBeanUtil.C100_WATCH_TYPE) {
                            MyApplication.Companion.instance().getSiatDeviceService().updateC100Device(file.getPath());
                        } else {
                            MyApplication.Companion.instance().getSiatDeviceService().updateDeviceByFile(file.getPath());
                        }
                        return true;
                    }
                }
            } else {
                ToastUtil.showToast(MyApplication.Companion.instance(), "升级文件不存在");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setChinaLanguage() {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().sendChinaLocalCom();
            }
        }
        return false;
    }

    @Override
    public void setLanguage(int type) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().setLanguagen(type == LanguageType.LANGUAGE_SAMPLE_CHINESE ? 1 : 0);
            }
        }
    }


    @Override
    public boolean sendEncryptData(byte[] bytes) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().sendEncryptCom(bytes);
            }
        }
        return false;
    }

    @Override
    public void setUserInfo(int userHeight, int userWeight, int age, boolean sex) {
        ValidateInUIThread();
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                UserInfo info = new UserInfo(userHeight, userWeight, age, sex);
                MyApplication.Companion.instance().getSiatDeviceService().setUserInfo(info);
            }
        }
    }

    @Override
    public void sendTestData(byte[] bytes) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().test(bytes);
            }
        }
    }

    @Override
    public void sendLifeData(List<Integer> list) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().sendVitality(list);
//                LogUtils.i(" sendLifeData " + ByteToStringUtil.byteToString(bytes));
            }
        }
    }

    @Override
    public void sendMotionStrengthData(int highHour, int hourMin, int midHour, int midMin, int lowHour, int lowMin) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
//            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
//                byte[] bytes = com.sw.watches.bluetooth.SIATCommand.getMotionStrength(highHour, hourMin, midHour, midMin, lowHour, lowMin);
//                MyApplication.Companion.instance().getSiatDeviceService().sendThread(bytes);
//                LogUtils.i(" sendMotionStrengthData " + ByteToStringUtil.byteToString(bytes));
//            }
        }
    }

    @Override
    public void sendOxHeartData(int sleepMaxHeart, int sleepMinHeart, int sleepMaxOx, int sleepMinOx, int maxHeart, int minHeart, int maxOx, int minOx) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().sendOxHeartCommand(sleepMaxHeart, sleepMinHeart, sleepMaxOx, sleepMinOx, maxHeart, minHeart, maxOx, minOx);
//                LogUtils.i(" sendOxHeartData " + ByteToStringUtil.byteToString(bytes));
            }
        }
    }

    @Override
    public void sendSleepAverageOxHeartData(int averageSleepHeart, int averageSleepOx) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().sendSleepHeartAndOx(averageSleepHeart, averageSleepOx);
//                byte[] bytes = SIATCommand.getSendHeartAndOx((byte) -85, averageSleepHeart, averageSleepOx);
                LogUtils.i(" sendOxHeartData " + " averageSleepHeart " + averageSleepHeart + " averageSleepOx " + averageSleepOx);
            }
        }
    }

    @Override
    public void sendSwitchCom(boolean heartSwitchRemind, boolean sitSwitchRemind, boolean sleepSwitchRemind, boolean oxSwitchRemind, boolean disturbSwitchRemind, boolean sleepOxRemind) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().sendSwitchCom(heartSwitchRemind, sitSwitchRemind, sleepSwitchRemind, oxSwitchRemind, disturbSwitchRemind, sleepOxRemind);
//                LogUtils.i(" sendSwitchCom " + ByteToStringUtil.byteToString(SIATCommand.sendSwitchCom((byte) 0xab, heartSwitchRemind, sitSwitchRemind, sleepSwitchRemind, oxSwitchRemind, disturbSwitchRemind)));
            }
        }
    }

    //设置心率过高开关
    @Override
    public void setHeartRemind(boolean isHeartRemind) {
        SPUtil.saveData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_HIGH_HEART_REMIND,
                isHeartRemind
        );
        setWatchSwitch();
    }

    @Override
    public void setAutomaticHeartRemind(boolean isHeartRemind) {

    }

    //设置久坐提醒开关
    @Override
    public void setSitRemind(boolean isSitRemind, int startMinute, int endMinute, int interval) {
        SPUtil.saveData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_SIT_REMIND,
                isSitRemind
        );
        setWatchSwitch();
    }

    //血氧提醒开关
    @Override
    public void setOxRemind(boolean isOxRemind) {
        SPUtil.saveData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_LOW_OX_REMIND,
                isOxRemind
        );
        setWatchSwitch();
    }

    //睡眠提醒开关
    @Override
    public void setSleepRemind(boolean isSleepRemind) {
        SPUtil.saveData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_SLEEP_REMIND,
                isSleepRemind
        );
        setWatchSwitch();
    }

    //勿扰模式开关
    @Override
    public void setDisturbRemind(boolean isDisturbRemind, int startHour, int startMin, int endHour, int endMin) {
        SPUtil.saveData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_DISTURB_REMIND,
                isDisturbRemind
        );
        setWatchSwitch();
    }


    //设置手表开关
    @Override
    public void setWatchSwitch() {
        //心率过高
        boolean isHeartRemind = (boolean) SPUtil.getData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_HIGH_HEART_REMIND,
                false
        );
        //久坐提醒
        boolean isSitRemind = (boolean) SPUtil.getData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_SIT_REMIND,
                false
        );
        //血氧提醒
        boolean isOxRemind = (boolean) SPUtil.getData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_LOW_OX_REMIND,
                false
        );
        //睡眠提醒
        boolean isSleepRemind = (boolean) SPUtil.getData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_SLEEP_REMIND,
                false
        );
        //勿扰模式
        boolean isDisturb = (boolean) SPUtil.getData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_DISTURB_REMIND,
                false
        );
        //勿扰模式
        boolean isSleepOx = (boolean) SPUtil.getData(
                MyApplication.Companion.instance().getApplicationContext(),
                SpConfig.IS_SLEEP_OX,
                false
        );
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().sendSwitchCom(isHeartRemind, isSitRemind, isSleepRemind, isOxRemind, isDisturb, isSleepOx);
//                LogUtils.i(" sendSwitchCom " + ByteToStringUtil.byteToString(SIATCommand.sendSwitchCom((byte) 0xab, isHeartRemind, isSitRemind, isSleepRemind, isOxRemind, isDisturb)));
            }
        }
    }

    @Override
    public void sendStartRunCom() {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().sendStartRunCommand();
            }
        }
    }

    @Override
    public void sendEndRunCom() {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().sendEndRunCommand();
            }
        }
    }

    @Override
    public void measureHeart() {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().measureHeart();
            }
        }
    }

    @Override
    public void stopMeasureHeart() {

    }

    @Override
    public void measureOx() {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().measureOx();
            }
        }
    }

    @Override
    public void measureHeatAndTemp(boolean isHeat) {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
//                MyApplication.Companion.instance().getSiatDeviceService().measureHeat();
                if (isHeat) {
                    MyApplication.Companion.instance().getSiatDeviceService().measureHeat();
                } else {
                    MyApplication.Companion.instance().getSiatDeviceService().measureTemp();
                }
            }
        }
    }

    @Override
    public void stopMeasureHeatAndTemp() {

    }

    @Override
    public void setMeasureHeatEnable(boolean isEnable) {

    }

    @Override
    public void measureTireAndPressure() {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().measureTireAndPressure();
            }
        }
    }

    @Override
    public AlarmInfo saveClock(List<ImportantItem> importantItemList, ImportantItem importantItem, boolean repeat) {
        AlarmInfo info = new AlarmInfo();
        info.setAlarmId(1);
        info.setAlarmtHour(importantItem.getHour());
        info.setAlarmtMin(importantItem.getMin());
        boolean[] alarmData = new boolean[8];
        for (int i = 0; i < 8; i++) {
            if (i == 0) {
                alarmData[i] = importantItem.getOpen();
            } else {
                alarmData[i] = importantItem.getWeekIndexList().contains(i - 1);
            }
        }
        info.setAlarmtData(MyApplication.Companion.instance().getSiatDeviceService().getCheckInt(alarmData));
        ArrayList<AlarmInfo> alarmInfoList = new ArrayList<>();
        alarmInfoList.add(info);
        if (importantItem.getWeekIndexList().size() == 0) {
            MyApplication.Companion.instance().getSiatDeviceService().saveAlarmData(alarmInfoList);
        } else {
            MyApplication.Companion.instance().getSiatDeviceService().saveRepeatAlarmData(alarmInfoList);
        }
        if (!repeat) {
            MyApplication.Companion.instance().getSiatDeviceService().saveAlarmData(alarmInfoList);
        } else {
            MyApplication.Companion.instance().getSiatDeviceService().saveRepeatAlarmData(alarmInfoList);
        }
//        LogUtils.i(" alarmByteArray com " + SIATCommand.getSaveAlarmDataCom(SIATCommand.SIGN_HEAD, alarmInfoList));
        return info;
    }

    @Override
    public void setUnit(boolean isMetric) {
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().setUnit(isMetric);
        }
    }

    @Override
    public void setTempUnit(boolean isCentigrade) {

    }

    @Override
    public void setHourSystem(boolean is24Model) {
        if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
            MyApplication.Companion.instance().getSiatDeviceService().setTimeFormat(is24Model);
        }
    }

    @Override
    public void readBpValue() {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().openMeasurement();//主动测量心率数据
            }
        }
    }

    @Override
    public void stopReadBpValue() {
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().closeMeasurement();//主动关闭测量心率数据
            }
        }
    }

    @Override
    public void setContinueHrp(boolean isOpen, int interval) {

    }

    /**
     * 断开设备链接
     */
    @Override
    public void close() {
        ValidateInUIThread();
        isConnection = true;
        if (MyApplication.Companion.instance().getBleUtils().getConnectionWatch() != null) {
            if (MyApplication.Companion.instance().getSiatDeviceService() != null) {
                MyApplication.Companion.instance().getSiatDeviceService().UnBindDevice();
                isConnection = false;
            }
        }
    }

    /**
     * 设置个人信息到手表
     */
    private void setUserInfoToWatch() {
//        if (AppUserInfo.getInstance().userInfo != null) {
        int age = AppUtils.fromStringToInteger(AppUtils.getAge(MyApplication.Companion.instance().getAppUserInfo().getUserInfo().birthday), 7);
        float height = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().height;
        if (height <= 0) height = 170;
        float weigh = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().weigh;
        if (weigh <= 0) weigh = 65;
        LogUtils.i(TAG, " height == " + (int) height + " weigh == " + (int) weigh + " age == " + age);
        setUserInfo((int) height, (int) weigh, age, "1".equals(MyApplication.Companion.instance().getAppUserInfo().getUserInfo().sex));
//        }
    }
}
