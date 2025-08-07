package com.healthy.rvigor.dao.executor;

import android.os.Message;
import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.SnoreNewDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SnoreNewDBEntityDao;

import java.util.ArrayList;
import java.util.List;


public class QuerySnoreExecutor extends AppDaoManager.DBExecutor{

    /**
     * 起始时间
     */
    private long startTime = 0;
    /**
     * 结束时间
     */
    private long endTime = 0;

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

    public QuerySnoreExecutor(long endTime, String macAddress, long userId, String uuid, IResult result) {
        this.endTime = endTime;
        this.macAddress = macAddress;
        this.userId = userId;
        this.result = result;
    }

    public QuerySnoreExecutor(long startTime, long endTime, String macAddress, long userId, IResult result){
        this.startTime = startTime;
        this.endTime = endTime;
        this.macAddress = macAddress;
        this.userId = userId;
        this.result = result;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        try {
            /**
             * 查询已经有的数据
             */
            List<SnoreNewDBEntity> snoreDBEntities;
            if (startTime > 0){
                snoreDBEntities  = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(SnoreNewDBEntity.class)
                        .where(SnoreNewDBEntityDao.Properties.Uid.eq(userId)
                                /*, SnoreNewDBEntityDao.Properties.DeviceMacAddress.eq(macAddress)*/
                                , SnoreNewDBEntityDao.Properties.Day.ge(startTime), SnoreNewDBEntityDao.Properties.Day.lt(endTime))
                        .orderAsc(SnoreNewDBEntityDao.Properties.Day).build().list();
            }else {
                snoreDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(SnoreNewDBEntity.class)
                        .where(SnoreNewDBEntityDao.Properties.Uid.eq(userId)
                                /*, SnoreNewDBEntityDao.Properties.DeviceMacAddress.eq(macAddress)*/
                                , SnoreNewDBEntityDao.Properties.Day.lt(endTime)).orderAsc(SnoreNewDBEntityDao.Properties.Day).build().list();
            }
            if (snoreDBEntities == null) {
                snoreDBEntities = new ArrayList<>();
            }
            Message msg = new Message();
            msg.obj = snoreDBEntities;
            sendMessageToUIThread(dbContext.application, msg);
        } catch (Exception ex) {
            Message msg = new Message();
            msg.obj = ex;
            sendMessageToUIThread(dbContext.application, msg);
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
        this.result = null;
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
