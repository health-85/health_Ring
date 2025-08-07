package com.healthy.rvigor.dao.executor;

import android.os.Message;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.AbnormalRateDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.AbnormalRateDBEntityDao;

import java.util.LinkedList;
import java.util.List;


//查询异常心率
public class QueryAbnormalHeartExecutor extends AppDaoManager.DBExecutor {

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

    public QueryAbnormalHeartExecutor(long startTime, long endTime, IResult result) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }
    
    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        try {
            long userId = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
            List<AbnormalRateDBEntity> dBEntities = null;
            if (startTime > 0) {
                dBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(AbnormalRateDBEntity.class)
                        .where(AbnormalRateDBEntityDao.Properties.Uid.eq(userId),
                                /*AbnormalRateDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                AbnormalRateDBEntityDao.Properties.HeartRateDay.ge(startTime),
                                AbnormalRateDBEntityDao.Properties.HeartRateDay.lt(endTime)).orderAsc(AbnormalRateDBEntityDao.Properties.HeartRateDay).build().list();
            } else {
                dBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(AbnormalRateDBEntity.class)
                        .where(AbnormalRateDBEntityDao.Properties.Uid.eq(userId),
                                /*AbnormalRateDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                AbnormalRateDBEntityDao.Properties.HeartRateDay.lt(endTime)).orderAsc(AbnormalRateDBEntityDao.Properties.HeartRateDay).build().list();
            }
            if (dBEntities == null) {
                dBEntities = new LinkedList<>();
            }
            Message msg = new Message();
            msg.obj = dBEntities;
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
