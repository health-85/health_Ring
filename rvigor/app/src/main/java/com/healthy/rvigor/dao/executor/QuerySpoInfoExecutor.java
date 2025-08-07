package com.healthy.rvigor.dao.executor;

import android.os.Message;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.SpoDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SpoDBEntityDao;

import java.util.LinkedList;
import java.util.List;


/**
 * 血氧查询
 */
public class QuerySpoInfoExecutor extends AppDaoManager.DBExecutor {

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

    public QuerySpoInfoExecutor(long startTime, long endTime, IResult result) {
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
            List<SpoDBEntity> spoDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(SpoDBEntity.class)
                    .where(SpoDBEntityDao.Properties.Uid.eq(userId),
                           /* SpoDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                            SpoDBEntityDao.Properties.SpoDay.ge(startTime),
                            SpoDBEntityDao.Properties.SpoDay.lt(endTime)).build().list();
            if (spoDBEntities == null) {
                spoDBEntities = new LinkedList<>();
            }
            Message msg = new Message();
            msg.obj = spoDBEntities;
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
