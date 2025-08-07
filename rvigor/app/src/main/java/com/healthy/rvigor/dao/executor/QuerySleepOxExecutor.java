package com.healthy.rvigor.dao.executor;

import android.os.Message;
import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.SleepOxDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SleepOxDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class QuerySleepOxExecutor extends AppDaoManager.DBExecutor {

    /**
     * 当前登录人的用户id
     */
    private long userId = 0;

    private long time = 0;

    /**
     * 结果
     */
    private IResult result = null;

    /**
     * 设备mac地址
     */
    private String macAddress = "";

    public QuerySleepOxExecutor(long userId, long time, String macAddress, IResult result) {
        this.userId = userId;
        this.time = time;
        this.macAddress = macAddress;
        this.result = result;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        try {
            long day = DateTimeUtils.getDateTimeDatePart(new Date(time)).getTime();
            List<SleepOxDBEntity> sleepOxDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(SleepOxDBEntity.class).where(SleepOxDBEntityDao.Properties.Uid.eq(userId)
                            , SleepOxDBEntityDao.Properties.Time.ge(day)).build().list();
            if (sleepOxDBEntities == null) {
                sleepOxDBEntities = new ArrayList<>();
            }
            Message msg = new Message();
            msg.obj = sleepOxDBEntities;
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
