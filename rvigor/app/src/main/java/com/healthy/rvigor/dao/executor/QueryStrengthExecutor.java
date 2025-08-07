package com.healthy.rvigor.dao.executor;

import android.os.Message;
import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.StrengthDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.StrengthDBEntityDao;

import java.util.ArrayList;
import java.util.List;


public class QueryStrengthExecutor extends AppDaoManager.DBExecutor {

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
     * 结果
     */
    private IResult result = null;

    public QueryStrengthExecutor(long startTime, long endTime, IResult result) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }

    public QueryStrengthExecutor(long endTime, IResult result) {
        this.endTime = endTime;
        this.result = result;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        try {
            long userId = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
            List<StrengthDBEntity> strengthDBEntities;
            if (startTime > 0){
                strengthDBEntities  = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(StrengthDBEntity.class)
                        .where(StrengthDBEntityDao.Properties.Uid.eq(userId)
                                , StrengthDBEntityDao.Properties.Day.ge(startTime), StrengthDBEntityDao.Properties.Day.lt(endTime))
                        .orderAsc(StrengthDBEntityDao.Properties.Day)
                        .build().list();
            }else {
                strengthDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(StrengthDBEntity.class)
                        .where(StrengthDBEntityDao.Properties.Uid.eq(userId)
                                , StrengthDBEntityDao.Properties.Day.eq(endTime))
                        .orderAsc(StrengthDBEntityDao.Properties.Day)
                        .build().list();
            }
            if (strengthDBEntities == null) {
                strengthDBEntities = new ArrayList<>();
            }
            Message msg = new Message();
            msg.obj = strengthDBEntities;
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
            if (msg.obj instanceof List) {
                result.OnSucceed(msg.obj);
            }
            if (msg.obj instanceof Exception) {
                result.OnError((Exception) msg.obj);
            }
        }
    }
}
