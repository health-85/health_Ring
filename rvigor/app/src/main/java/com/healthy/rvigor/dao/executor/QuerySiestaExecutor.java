package com.healthy.rvigor.dao.executor;

import android.os.Message;
import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.SiestaDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SiestaDBEntityDao;

import java.util.ArrayList;
import java.util.List;


public class QuerySiestaExecutor extends AppDaoManager.DBExecutor {

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

    public QuerySiestaExecutor(long startTime, long endTime, IResult result) {
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
            List<SiestaDBEntity> siestaDBEntityList;
            if (startTime > 0) {
                siestaDBEntityList = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(SiestaDBEntity.class).where(SiestaDBEntityDao.Properties.Uid.eq(userId)
                                /* , SiestaDBEntityDao.Properties.DeviceMacAddress.eq(macAddress)*/
                                , SiestaDBEntityDao.Properties.SiestaDay.ge(startTime), SiestaDBEntityDao.Properties.SiestaDay.lt(endTime)).build().list();
            } else {
                siestaDBEntityList = dbContext.mReadableDaoMaster
                        .newSession().queryBuilder(SiestaDBEntity.class).where(SiestaDBEntityDao.Properties.Uid.eq(userId)
                                /*, SiestaDBEntityDao.Properties.DeviceMacAddress.eq(macAddress)*/
                                , SiestaDBEntityDao.Properties.SiestaDay.lt(endTime)).build().list();
            }
            if (siestaDBEntityList == null) {
                siestaDBEntityList = new ArrayList<>();
            }
            Message msg = new Message();
            msg.obj = siestaDBEntityList;
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
