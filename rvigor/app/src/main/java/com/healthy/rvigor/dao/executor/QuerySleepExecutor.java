package com.healthy.rvigor.dao.executor;

import android.os.Message;
import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.SleepDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SleepDBEntityDao;

import java.util.ArrayList;
import java.util.List;


/**
 * 查询睡眠数据
 */
public class QuerySleepExecutor extends AppDaoManager.DBExecutor {

    /**
     * 起始时间
     */
    private long startTime = 0;
    /**
     * 结束时间
     */
    private long endTime = 0;

    //0 倒序 1 顺序
    private int orderType = 0;

    /**
     * 设备mac地址
     */
    private String macAddress = "";

    /**
     * 当前登录人的用户id
     */
    private long userId = 0;

    /**
     * 结果
     */
    private IResult result = null;

    public QuerySleepExecutor(long endTime, String macAddress, long userId, String uuid, IResult result) {
        this.endTime = endTime;
        this.macAddress = macAddress;
        this.userId = userId;
        this.result = result;
    }

    public QuerySleepExecutor(long startTime, long endTime, String macAddress, IResult result) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.macAddress = macAddress;
        this.result = result;
    }

    public QuerySleepExecutor(long startTime, long endTime, String macAddress, long userId, String uuid, int orderType, IResult result) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.macAddress = macAddress;
        this.orderType = orderType;
        this.userId = userId;
        this.result = result;
    }

    public QuerySleepExecutor(long startTime, long endTime, IResult result) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }


    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        try {
            /**
             * 查询已经有的数据
             */
            long userId = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
            List<SleepDBEntity> sleepDBEntities;
            if (startTime > 0) {
                if (orderType == 1){
                    sleepDBEntities = dbContext.mReadableDaoMaster
                            .newSession().queryBuilder(SleepDBEntity.class).where(SleepDBEntityDao.Properties.Uid.eq(userId)
                                    , SleepDBEntityDao.Properties.SleepDay.ge(startTime), SleepDBEntityDao.Properties.SleepDay.lt(endTime))
                            .orderAsc(SleepDBEntityDao.Properties.SleepDay)
                            .build().list();
                }else {
                    sleepDBEntities = dbContext.mReadableDaoMaster
                            .newSession().queryBuilder(SleepDBEntity.class).where(SleepDBEntityDao.Properties.Uid.eq(userId)
                                    , SleepDBEntityDao.Properties.SleepDay.ge(startTime), SleepDBEntityDao.Properties.SleepDay.lt(endTime))
                            .orderDesc(SleepDBEntityDao.Properties.SleepDay)
                            .build().list();
                }
            } else {
                sleepDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(SleepDBEntity.class).where(SleepDBEntityDao.Properties.Uid.eq(userId)
                            /*, SleepDBEntityDao.Properties.DeviceMacAddress.eq(macAddress)*/
                                , SleepDBEntityDao.Properties.SleepDay.eq(endTime)).build().list();
            }
            if (sleepDBEntities == null) {
                sleepDBEntities = new ArrayList<>();
            }
            Message msg = new Message();
            msg.obj = sleepDBEntities;
            sendMessageToUIThread(dbContext.application, msg);
        } catch (Exception ex) {
            Message msg = new Message();
            msg.obj = ex;
            sendMessageToUIThread(dbContext.application, msg);
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }


    @Override
    protected void onMessageInUI(MyApplication application, Message msg) {
        super.onMessageInUI(application, msg);
        if (result != null) {
            if (msg.obj instanceof List) {
                result.OnSucceed(msg.obj);
            }
            if (msg.obj instanceof Exception) {
                result.OnError((Exception) msg.obj);
            }
        }
    }
}
