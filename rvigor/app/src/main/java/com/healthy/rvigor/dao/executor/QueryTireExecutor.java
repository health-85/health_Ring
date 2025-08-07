package com.healthy.rvigor.dao.executor;

import android.os.Message;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.TireDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.TireDBEntityDao;

import java.util.LinkedList;
import java.util.List;


public class QueryTireExecutor extends AppDaoManager.DBExecutor {

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
     * 用户界面的uuid
     */
    private String uuid = "";

    /**
     *
     */
    private IResult result = null;

    public QueryTireExecutor(long startTime, long endTime, IResult result) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }

    public QueryTireExecutor(String macAddress, long userId, long startTime, long endTime, String uuid, IResult result) {
        this.macAddress = macAddress;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.uuid = uuid;
        this.result = result;
    }

    public QueryTireExecutor(String macAddress, long userId, long endTime, String uuid, IResult result) {
        this.macAddress = macAddress;
        this.userId = userId;
        this.endTime = endTime;
        this.uuid = uuid;
        this.result = result;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
//        if (StringUtils.StringIsEmptyOrNull(macAddress)) {
//            return;
//        }
        try {
            userId = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
            List<TireDBEntity> tireDBEntities;
            if (startTime == 0){
                tireDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(TireDBEntity.class)
                        .where(TireDBEntityDao.Properties.Uid.eq(userId),
                                /*TireDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
//                                TireDBEntityDao.Properties.TireDay.ge(startTime),
                                TireDBEntityDao.Properties.TireDay.lt(endTime)).orderAsc(TireDBEntityDao.Properties.TireDay).build().list();
            }else {
                tireDBEntities = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(TireDBEntity.class)
                        .where(TireDBEntityDao.Properties.Uid.eq(userId),
                                /*TireDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                                TireDBEntityDao.Properties.TireDay.ge(startTime),
                                TireDBEntityDao.Properties.TireDay.lt(endTime)).orderAsc(TireDBEntityDao.Properties.TireDay).build().list();
            }
            if (tireDBEntities == null) {
                tireDBEntities = new LinkedList<>();
            }
            Message msg = new Message();
            msg.obj = tireDBEntities;
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