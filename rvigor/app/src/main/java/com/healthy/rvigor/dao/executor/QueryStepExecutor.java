package com.healthy.rvigor.dao.executor;

import android.os.Message;
import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.StepDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.StepDBEntityDao;

import java.util.ArrayList;
import java.util.List;


/**
 * 查询步数信息
 */
public class QueryStepExecutor extends AppDaoManager.DBExecutor {

    /**
     * 起始时间
     */
    private long startTime = 0;
    /**
     * 结束时间
     */
    private long endTime = 0;
    /**
     * 结果
     */
    private IResult result = null;

    public QueryStepExecutor(long startTime, long endTime, IResult result) {
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
            List<StepDBEntity> stepDBEntities;
            if (startTime > 0) {
                stepDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(StepDBEntity.class)
                        .where(StepDBEntityDao.Properties.Uid.eq(userId)
                                /*, StepDBEntityDao.Properties.DeviceMacAddress.eq(macAddress)*/
                                , StepDBEntityDao.Properties.StepDay.ge(startTime)
                                , StepDBEntityDao.Properties.StepDay.lt(endTime)).build().list();
            } else {
                stepDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(StepDBEntity.class)
                        .where(StepDBEntityDao.Properties.Uid.eq(userId)
                                /*, StepDBEntityDao.Properties.DeviceMacAddress.eq(macAddress)*/
                                , StepDBEntityDao.Properties.StepDay.lt(endTime)).build().list();
            }
            if (stepDBEntities == null) {
                stepDBEntities = new ArrayList<>();
            }
            Message msg = new Message();
            msg.obj = stepDBEntities;
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
