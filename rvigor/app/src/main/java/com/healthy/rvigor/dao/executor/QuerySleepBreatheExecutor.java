package com.healthy.rvigor.dao.executor;

import android.os.Message;

import com.google.gson.Gson;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.bean.ChartBean;
import com.healthy.rvigor.bean.SleepBarBean;
import com.healthy.rvigor.dao.entity.BreatheDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.BreatheDBEntityDao;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.healthy.rvigor.util.LogUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class QuerySleepBreatheExecutor extends AppDaoManager.DBExecutor {

    /**
     * 设备mac地址
     */
    private String macAddress = "";

    /**
     * 当前登录人的用户id
     */
    private long userId = 0;

    /**
     * 起始时间
     */
    private long startTime = 0;


    /**
     * 结束时间
     */
    private long endTime = 0;

    private SleepBarBean barBean;
    /**
     *
     */
    private IResult result = null;


    public QuerySleepBreatheExecutor(long userId, String macAddress, SleepBarBean barBean, long startTime, long endTime, IResult result) {
        this.macAddress = macAddress;
        this.userId = userId;
        this.barBean = barBean;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }

    public QuerySleepBreatheExecutor(String macAddress, long userId, long endTime, IResult result) {
        this.macAddress = macAddress;
        this.userId = userId;
        this.endTime = endTime;
        this.result = result;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
//        if (StringUtils.StringIsEmptyOrNull(macAddress)) {
//            return;
//        }
        if (barBean == null) return;
        try {
            List<BreatheDBEntity> breatheDBEntities;
            if (startTime == 0) {
                breatheDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(BreatheDBEntity.class)
                        .where(BreatheDBEntityDao.Properties.Uid.eq(userId),
                                /*BreatheDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
//                                BreatheDBEntityDao.Properties.TireDay.ge(startTime),
                                BreatheDBEntityDao.Properties.Day.lt(endTime)).build().list();
            } else {
                breatheDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(BreatheDBEntity.class)
                        .where(BreatheDBEntityDao.Properties.Uid.eq(userId),
                                /*BreatheDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                BreatheDBEntityDao.Properties.Day.ge(startTime),
                                BreatheDBEntityDao.Properties.Day.lt(endTime)).build().list();
            }
            if (breatheDBEntities == null) {
                breatheDBEntities = new LinkedList<>();
            }
            Message msg = new Message();
            ChartBean chartBean = getBeanByDB(breatheDBEntities);
//            chartBean.setDataList(getSleepData(barBean, chartBean));
            msg.obj = chartBean;
            sendMessageToUIThread(dbContext.application, msg);
        } catch (Exception ex) {
            Message msg = new Message();
            msg.obj = ex;
            sendMessageToUIThread(dbContext.application, msg);
        }
        result = null;
    }

    private ChartBean getBeanByDB(List<BreatheDBEntity> breatheDBEntities) {
        int max = 0;
        int min = 0;
        int average = 0;
        int index = 0;
        ChartBean chartBean = new ChartBean();
        List<ChartBean.DataItem> itemList = new ArrayList<>();
        if (breatheDBEntities.size() > 0) {
            for (BreatheDBEntity dbEntity : breatheDBEntities) {
                List<ChartBean.DataItem> dataItemList = new ArrayList<>();
                List<Integer> list = new ArrayList<>();
                JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(dbEntity.breatheJsonData);
                if (jsonArrayUtils.length() > 0) {
                    for (int i = 0; i < jsonArrayUtils.length(); i++) {
                        JsonUtils curr = jsonArrayUtils.getJsonObject(i);
                        int temp = curr.getInt("data", 0);
                        long time = curr.getLong("datetime", 0);
                        if (temp > 0) {
                            list.add(temp);
                            ChartBean.DataItem item = new ChartBean.DataItem(temp, time);
                            dataItemList.add(item);
                            average += item.data;
                            index++;
                            min = (min == 0 || min > item.data) ? (int) item.data : min;
                            max = (max == 0 || max < item.data) ? (int) item.data : max;
//                            LogUtils.i(" breatheJsonData Day time == " + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format) + " temp " + temp);
                        }
                    }
                }
                itemList.addAll(dataItemList);
            }

            BreatheDBEntity newDBEntity = breatheDBEntities.get(breatheDBEntities.size() - 1);
            chartBean.setAhi(newDBEntity.hypopnea);
            chartBean.setRdi(newDBEntity.chaosIndex);
            chartBean.setBlockLen(newDBEntity.blockLen);
            chartBean.setPauseCount(newDBEntity.pauseCount);

            LogUtils.i(" newDBEntity " + new Gson().toJson(newDBEntity));
        }
        chartBean.setDataList(itemList);
        if (barBean != null) {
            chartBean.setTime(barBean.getSleepDay());
        }
        if (index == 0) index = 1;
        average /= index;
        chartBean.setData(average);
        chartBean.setMaxData(max);
        chartBean.setMinData(min);
        chartBean.setAverageData(average);

//        LogUtils.i(" breatheJsonData mAverage " + average + " mMax " + max + " mMin " + min + " " + new Gson().toJson(chartBean.getDataList()));

        return chartBean;
    }

    @Override
    protected void onMessageInUI(MyApplication application, Message msg) {
        super.onMessageInUI(application, msg);
        if (result != null) {
            if (msg.obj instanceof Exception) {
                result.OnError((Exception) msg.obj);
            } else {
                if (msg.obj instanceof ChartBean) {
                    result.OnSucceed(msg.obj);
                }
            }
        }
    }
}
