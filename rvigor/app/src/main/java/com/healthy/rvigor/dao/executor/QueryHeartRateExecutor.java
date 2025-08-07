package com.healthy.rvigor.dao.executor;

import android.os.Message;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.HeartRateDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.HeartRateDBEntityDao;

import java.util.LinkedList;
import java.util.List;


/**
 * 心率查询
 */
public class QueryHeartRateExecutor extends AppDaoManager.DBExecutor {

    /**
     * 设备mac地址
     */
    private String macAddress = "";

    /**
     * 起始时间
     */
    private long startTime = 0;

    /**
     * 结束时间
     */
    private long endTime = 0;


    /**
     *
     */
    private IResult result = null;

    public QueryHeartRateExecutor(String macAddress, long endTime, IResult result) {
        this.macAddress = macAddress;
        this.endTime = endTime;
        this.result = result;
    }

    public QueryHeartRateExecutor(long startTime, long endTime, IResult result) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
//        if (StringUtils.StringIsEmptyOrNull(macAddress)) {
//            return;
//        }
        try {
            long userId = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
            List<HeartRateDBEntity> heartRateDBEntities;
            if (startTime == 0){
                heartRateDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(HeartRateDBEntity.class)
                        .where(HeartRateDBEntityDao.Properties.Uid.eq(userId),
                                /*HeartRateDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
//                                HeartRateDBEntityDao.Properties.HeartRateDay.ge(startTime),
                                HeartRateDBEntityDao.Properties.HeartRateDay.lt(endTime)).build().list();
            }else {
                heartRateDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(HeartRateDBEntity.class)
                        .where(HeartRateDBEntityDao.Properties.Uid.eq(userId),
                                /*HeartRateDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                HeartRateDBEntityDao.Properties.HeartRateDay.ge(startTime),
                                HeartRateDBEntityDao.Properties.HeartRateDay.lt(endTime)).build().list();
            }
            if (heartRateDBEntities == null) {
                heartRateDBEntities = new LinkedList<>();
            }
            Message msg = new Message();
            msg.obj = heartRateDBEntities;
//            LogUtils.i(" startTime " + DateTimeUtils.s_long_2_str(startTime, DateTimeUtils.day_hm_format) + " "
//                    + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.day_hm_format) + " " +
//                     " Execute " + heartRateDBEntities.size());
            sendMessageToUIThread(dbContext.application, msg);
        } catch (Exception ex) {
            Message msg = new Message();
            msg.obj = ex;
            sendMessageToUIThread(dbContext.application, msg);
        }
        result = null;
    }


    @Override
    protected void onMessageInUI(MyApplication application, Message msg) {
        super.onMessageInUI(application, msg);
        if (result != null) {
            if (msg.obj instanceof Exception) {
                result.OnError((Exception) msg.obj);
            } else {
                if (msg.obj instanceof List) {
                    result.OnSucceed(msg.obj);
                }
            }
        }
    }
}
