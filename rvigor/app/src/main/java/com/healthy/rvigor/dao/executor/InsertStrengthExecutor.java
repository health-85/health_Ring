package com.healthy.rvigor.dao.executor;

import android.util.Log;

import com.google.gson.Gson;
import com.healthy.rvigor.dao.entity.StrengthDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.StrengthDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.LogUtils;
import com.sw.watches.bean.StrengthInfo;

import java.util.Date;
import java.util.List;

public class InsertStrengthExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertStrengthExecutor";

    /**
     * 用户id
     */
    public long uid = 0;

    /**
     * 是否已经同步到服务器了 0未同步  1已同步
     */
    public int isupLoadToServer = 0;


    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    private StrengthInfo info;

    public InsertStrengthExecutor(long uid, String deviceName, String deviceMacAddress, StrengthInfo info) {
        this.uid = uid;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.info = info;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (info == null) return;
        long tempDay = DateTimeUtils.getDateTimeDatePart(new Date(System.currentTimeMillis())).getTime();
        try {
            //查询已经有的数据
            List<StrengthDBEntity> strengthDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(StrengthDBEntity.class)
                    .where(StrengthDBEntityDao.Properties.Uid.eq(uid),
                            StrengthDBEntityDao.Properties.Day.eq(tempDay)).orderDesc().build().list();
            if ((strengthDBEntities != null) && (strengthDBEntities.size() > 0)) {//查找已有的数据
                StrengthDBEntity dbEntity = strengthDBEntities.get(0);
                dbEntity.inLow = info.getLowTime();
                dbEntity.inCentre = info.getMiddleTime();
                dbEntity.inHigh = info.getHighTime();
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
                LogUtils.i(TAG," StrengthDBEntity " + new Gson().toJson(dbEntity));
            } else {
                StrengthDBEntity dbEntity = new StrengthDBEntity();
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.day = tempDay;
                dbEntity.uid = uid;
                dbEntity.inLow = info.getLowTime();
                dbEntity.inCentre = info.getMiddleTime();
                dbEntity.inHigh = info.getHighTime();
                dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
                LogUtils.i(TAG," StrengthDBEntity " + new Gson().toJson(dbEntity));
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }


}
