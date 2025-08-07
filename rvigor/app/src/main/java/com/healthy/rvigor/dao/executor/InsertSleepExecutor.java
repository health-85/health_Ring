package com.healthy.rvigor.dao.executor;

import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.SleepDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SleepDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.sw.watches.bean.SleepData;
import com.sw.watches.bean.SleepInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 插入睡眠数据 SIAT手表
 */
public class InsertSleepExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertSleepExecutorSIAT";

    /**
     * 睡眠信息
     */
    private SleepInfo sleepInfo = null;
    /**
     * 设备mac地址
     */
    private String macAddress = "";
    /**
     * 设备名称
     */
    private String deviceName = "";

    /**
     * 当前登录人的用户id
     */
    private long userId = 0;

    public InsertSleepExecutor(SleepInfo sleepInfo, String macAddress, String deviceName) {
        this.sleepInfo = sleepInfo;
        this.macAddress = macAddress;
        this.deviceName = deviceName;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (sleepInfo == null) {
            return;
        }
        Date sleepDate = DateTimeUtils.ConvertStrToDate(sleepInfo.getSleepDate());
        if (sleepDate == null) {
            return;
        }

        //sleepDate=DateTimeUtils.AddDay(sleepDate,-1);//正确的时间
        if (sleepInfo.SleepTotalTime <= 0) {
            return;
        }
        if (sleepInfo.getSleepData() == null) {
            return;
        }
        if (sleepInfo.getSleepData().size() == 0) {
            return;
        }

        try {
            long userId = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
            long sleepday = DateTimeUtils.getDateTimeDatePart(sleepDate).getTime();
            //查询已经有的数据
            List<SleepDBEntity> sleepDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(SleepDBEntity.class)
                    .where(SleepDBEntityDao.Properties.Uid.eq(userId),
                            /*SleepDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                            SleepDBEntityDao.Properties.SleepDay.eq(sleepday)).build().list();
            if ((sleepDBEntities != null) && (sleepDBEntities.size() > 0)) {
                SleepDBEntity sleepDBEntity = sleepDBEntities.get(0);
                sleepDBEntity.sleeplength = sleepInfo.getSleepTotalTime() * (60 * 1000);
                List<SleepData> sleepDataList = sleepInfo.getSleepData();
                sleepDBEntity.setSleepJsonData(makesleepjsondata(sleepDBEntity, sleepDataList, sleepday));
//                LogUtils.i(TAG, new Gson().toJson(sleepDBEntity.sleepJsonData));
                dbContext.mWritableDaoMaster.newSession().update(sleepDBEntity);
            } else {
                SleepDBEntity sleepDBEntity = new SleepDBEntity();
                sleepDBEntity.deviceName = deviceName;
                sleepDBEntity.deviceMacAddress = macAddress;
                sleepDBEntity.uid = userId;
                sleepDBEntity.setSleepDay(sleepday);
                sleepDBEntity.sleeplength = sleepInfo.getSleepTotalTime() * (60 * 1000);
                List<SleepData> sleepDataList = sleepInfo.getSleepData();
                sleepDBEntity.setSleepJsonData(makesleepjsondata(sleepDBEntity, sleepDataList, sleepday));
//                LogUtils.i(TAG, new Gson().toJson(sleepDBEntity.sleepJsonData));
                dbContext.mWritableDaoMaster.newSession().insert(sleepDBEntity);
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }

    }

    /**
     * 生成睡眠数据
     *
     * @param sleepDataList
     * @param sleepday
     * @return
     */
    private String makesleepjsondata(SleepDBEntity sleepDBEntity, List<SleepData> sleepDataList, long sleepday) {
        JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
        List<Long> times = new ArrayList<>();
//        LogUtils.i(" sleepday " + DateTimeUtils.s_long_2_str(sleepday, DateTimeUtils.day_hm_format));
//        LogUtils.i(" makesleepjsondata " + new Gson().toJson(sleepDataList));
        for (int i = 0; i < sleepDataList.size(); i++) {
            SleepData sleepData = sleepDataList.get(i);
            long startTime = makeSleepItemTime(sleepData.startTime, new Date(sleepday)).getTime();
            if (i == 0) {
                sleepday = makeSleepDay(sleepday, startTime);
                startTime = makeSleepItemTime(sleepData.startTime, new Date(sleepday)).getTime();

                sleepDBEntity.startDateTime = startTime;
                times.add(startTime);
            } else {
                if (times.size() > 0) {
                    long prestarttime = times.get(times.size() - 1);
                    if (startTime < prestarttime) {
                        startTime = makenewdatedayudengyucomparatime(startTime, prestarttime);
                    }
                }
                times.add(startTime);
            }
            if ((i + 1) < sleepDataList.size()) {
                SleepData nextsleepData = sleepDataList.get(i + 1);
                long endTime = makeSleepItemTime(nextsleepData.startTime, new Date(sleepday)).getTime();
                if (endTime < startTime) {//如果结束时间小于结束时间
                    endTime = makenewdatedayudengyucomparatime(endTime, startTime);
                }

                if (sleepData.sleep_type.equals("00")) {//熬夜
                    JsonUtils item = new JsonUtils(new JSONObject());
                    item.put("starttime", startTime);
                    item.put("endtime", endTime);
                    item.put("sleeptype", 5);
                    jsonArrayUtils.putJsonUtils(item);
                }

                if (sleepData.sleep_type.equals("04")) {//清醒
                    JsonUtils item = new JsonUtils(new JSONObject());
                    item.put("starttime", startTime);
                    item.put("endtime", endTime);
                    item.put("sleeptype", 3);
                    jsonArrayUtils.putJsonUtils(item);
                }

                if (sleepData.sleep_type.equals("01")) {//入睡
                    JsonUtils item = new JsonUtils(new JSONObject());
                    item.put("starttime", startTime);
                    item.put("endtime", endTime);
                    item.put("sleeptype", 4);
                    jsonArrayUtils.putJsonUtils(item);
                }

                if (sleepData.sleep_type.equals("02")) {//潜睡
                    JsonUtils item = new JsonUtils(new JSONObject());
                    item.put("starttime", startTime);
                    item.put("endtime", endTime);
                    item.put("sleeptype", 2);
                    jsonArrayUtils.putJsonUtils(item);
                }
                if (sleepData.sleep_type.equals("03")) {//深睡
                    JsonUtils item = new JsonUtils(new JSONObject());
                    item.put("starttime", startTime);
                    item.put("endtime", endTime);
                    item.put("sleeptype", 1);
                    jsonArrayUtils.putJsonUtils(item);
                }
                if (sleepData.sleep_type.equals("06")) {//深睡
                    JsonUtils item = new JsonUtils(new JSONObject());
                    item.put("starttime", startTime);
                    item.put("endtime", endTime);
                    item.put("sleeptype", 7);
                    jsonArrayUtils.putJsonUtils(item);
                }
                if (sleepData.sleep_type.equals("05")) {//结束睡眠
                    JsonUtils item = new JsonUtils(new JSONObject());
                    item.put("starttime", startTime);
                    item.put("endtime", endTime);
                    item.put("sleeptype", 6);
                    jsonArrayUtils.putJsonUtils(item);
                }
                if ((i + 1) == sleepDataList.size() - 1 && nextsleepData.sleep_type.equals("05")) {
                    JsonUtils item = new JsonUtils(new JSONObject());
                    item.put("starttime", endTime);
                    item.put("endtime", endTime);
                    item.put("sleeptype", 6);
                    jsonArrayUtils.putJsonUtils(item);
                }
            }
        }
        return jsonArrayUtils.toJsonArray().toString();
    }


    /**
     * @param old
     * @param comparatime
     * @return
     */
    private long makenewdatedayudengyucomparatime(long old, long comparatime) {
        long R = old;
        while (true) {
            if (R < comparatime) {
                R = DateTimeUtils.AddDay(new Date(R), 1).getTime();
            } else {
                break;
            }
        }
        return R;
    }

    private long makeSleepDay(long sleepDay, long startTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 20){
            return DateTimeUtils.AddDay(new Date(sleepDay), -1).getTime();
        }
        return sleepDay;
    }


    private Date makeSleepItemTime(String time, Date sleepday) {
        Date sleepdaydatepart = DateTimeUtils.getDateTimeDatePart(sleepday);
        Date date = DateTimeUtils.ConvertStrToDate(time, "HH:mm");
        if (date != null) {
            sleepdaydatepart = DateTimeUtils.AddHours(sleepdaydatepart, DateTimeUtils.getHour(date));
            sleepdaydatepart = DateTimeUtils.AddMinute(sleepdaydatepart, DateTimeUtils.getMinute(date));
        }
        return sleepdaydatepart;
    }

}
