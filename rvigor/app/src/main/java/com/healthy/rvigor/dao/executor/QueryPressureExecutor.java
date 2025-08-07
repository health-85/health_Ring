package com.healthy.rvigor.dao.executor;

import android.os.Message;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.PressureDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.PressureDBEntityDao;

import java.util.LinkedList;
import java.util.List;


public class QueryPressureExecutor extends AppDaoManager.DBExecutor {

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


    public QueryPressureExecutor(long startTime, long endTime, IResult result) {
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
            List<PressureDBEntity> pressureDBEntities;
            if (startTime > 0) {
                pressureDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(PressureDBEntity.class)
                        .where(PressureDBEntityDao.Properties.Uid.eq(userId),
                                /*PressureDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                PressureDBEntityDao.Properties.PressureDay.ge(startTime),
                                PressureDBEntityDao.Properties.PressureDay.lt(endTime)).orderAsc(PressureDBEntityDao.Properties.PressureDay).build().list();
            } else {
                pressureDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(PressureDBEntity.class)
                        .where(PressureDBEntityDao.Properties.Uid.eq(userId),
                                /*PressureDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                /*PressureDBEntityDao.Properties.PressureDay.ge(startTime),*/
                                PressureDBEntityDao.Properties.PressureDay.lt(endTime)).orderAsc(PressureDBEntityDao.Properties.PressureDay).build().list();
            }
            if (pressureDBEntities == null) {
                pressureDBEntities = new LinkedList<>();
            }
            Message msg = new Message();
            msg.obj = pressureDBEntities;
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