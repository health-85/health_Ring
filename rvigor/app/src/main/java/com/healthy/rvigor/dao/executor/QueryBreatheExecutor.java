package com.healthy.rvigor.dao.executor;

import android.os.Message;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.BreatheDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.BreatheDBEntityDao;

import java.util.LinkedList;
import java.util.List;


public class QueryBreatheExecutor extends AppDaoManager.DBExecutor {

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


    /**
     *
     */
    private IResult result = null;


    public QueryBreatheExecutor(String macAddress, long userId, long startTime, long endTime, IResult result) {
        this.macAddress = macAddress;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }

    public QueryBreatheExecutor(String macAddress, long userId, long endTime, IResult result) {
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
        try {
            List<BreatheDBEntity> breatheDBEntities;
            if (startTime == 0) {
                breatheDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(BreatheDBEntity.class)
                        .where(BreatheDBEntityDao.Properties.Uid.eq(userId),
                                /*BreatheDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
//                                BreatheDBEntityDao.Properties.TireDay.ge(startTime),
                                BreatheDBEntityDao.Properties.Day.lt(endTime)).orderAsc(BreatheDBEntityDao.Properties.Day).build().list();
            } else {
                breatheDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(BreatheDBEntity.class)
                        .where(BreatheDBEntityDao.Properties.Uid.eq(userId),
                                /*BreatheDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                BreatheDBEntityDao.Properties.Day.ge(startTime),
                                BreatheDBEntityDao.Properties.Day.lt(endTime)).orderAsc(BreatheDBEntityDao.Properties.Day).build().list();
            }
            if (breatheDBEntities == null) {
                breatheDBEntities = new LinkedList<>();
            }
            Message msg = new Message();
            msg.obj = breatheDBEntities;
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
