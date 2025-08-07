package com.healthy.rvigor.dao.executor;

import android.os.Message;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.EmotionDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.EmotionDBEntityDao;

import java.util.LinkedList;
import java.util.List;


public class QueryEmotionExecutor extends AppDaoManager.DBExecutor {

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

    public QueryEmotionExecutor(long startTime, long endTime, IResult result) {
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
            List<EmotionDBEntity> tempDBEntities;
            if (startTime > 0) {
                tempDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(EmotionDBEntity.class)
                        .where(EmotionDBEntityDao.Properties.Uid.eq(userId),
                                /*EmotionDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                EmotionDBEntityDao.Properties.TempDay.ge(startTime),
                                EmotionDBEntityDao.Properties.TempDay.lt(endTime)).orderAsc(EmotionDBEntityDao.Properties.TempDay).build().list();
            } else {
                tempDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(EmotionDBEntity.class)
                        .where(EmotionDBEntityDao.Properties.Uid.eq(userId),
                                /*EmotionDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                /*EmotionDBEntityDao.Properties.EnviTempDay.ge(startTime),*/
                                EmotionDBEntityDao.Properties.TempDay.lt(endTime)).orderAsc(EmotionDBEntityDao.Properties.TempDay).build().list();
            }
            if (tempDBEntities == null) {
                tempDBEntities = new LinkedList<>();
            }
            Message msg = new Message();
            msg.obj = tempDBEntities;
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
