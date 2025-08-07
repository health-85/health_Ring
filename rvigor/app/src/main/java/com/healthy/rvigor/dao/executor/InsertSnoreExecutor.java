package com.healthy.rvigor.dao.executor;

import com.healthy.rvigor.dao.entity.SnoreNewDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SnoreNewDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.sw.watches.bean.SnoreInfo;

import java.util.Date;
import java.util.List;

/**
 * 插入打鼾数据
 */
public class InsertSnoreExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertSnoreExecutor";

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

    public SnoreInfo snoreInfo;

    public InsertSnoreExecutor(long uid, String deviceName, String deviceMacAddress, SnoreInfo info) {
        this.uid = uid;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.snoreInfo = info;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (snoreInfo == null) return;
        Date date = DateTimeUtils.convertStrToDateForThisProject(snoreInfo.getDate());
        if (date == null) {//日期时间不能为空
            return;
        }
        long day = DateTimeUtils.getDateTimeDatePart(date).getTime();
        try {
            List<SnoreNewDBEntity> snoreDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(SnoreNewDBEntity.class)
                    .where(SnoreNewDBEntityDao.Properties.Uid.eq(uid),
                            /*SnoreNewDBEntityDao.Properties.DeviceMacAddress.eq(deviceMacAddress),*/
                            SnoreNewDBEntityDao.Properties.Day.eq(day)).build().list();
            if ((snoreDBEntities != null) && (snoreDBEntities.size() > 0)) {//查找已有的数据
                SnoreNewDBEntity dbEntity = snoreDBEntities.get(0);
                dbEntity.snoreLen = snoreInfo.getSnoreLen();
                dbEntity.maxDbF = snoreInfo.getMaxDbF();
                dbEntity.averageDb = snoreInfo.getAverageDb();
                dbEntity.snoreIndex = snoreInfo.getSnoreIndex();
                dbEntity.snoreFrequency = snoreInfo.getSnoreFrequency();
                dbEntity.snoreNormal = snoreInfo.getSnoreNormal();
                dbEntity.snoreMild = snoreInfo.getSnoreMild();
                dbEntity.snoreMiddle = snoreInfo.getSnoreMiddle();
                dbEntity.snoreSerious = snoreInfo.getSnoreSerious();
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
//                LogUtils.i(TAG, " snoreInfo " + new Gson().toJson(dbEntity));
            } else {
                SnoreNewDBEntity dbEntity = new SnoreNewDBEntity();
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.day = day;
                dbEntity.uid = uid;
                dbEntity.snoreLen = snoreInfo.getSnoreLen();
                dbEntity.maxDbF = snoreInfo.getMaxDbF();
                dbEntity.averageDb = snoreInfo.getAverageDb();
                dbEntity.minDbF = snoreInfo.getMinDb();
                dbEntity.snoreIndex = snoreInfo.getSnoreIndex();
                dbEntity.snoreFrequency = snoreInfo.getSnoreFrequency();
                dbEntity.snoreNormal = snoreInfo.getSnoreNormal();
                dbEntity.snoreMild = snoreInfo.getSnoreMild();
                dbEntity.snoreMiddle = snoreInfo.getSnoreMiddle();
                dbEntity.snoreSerious = snoreInfo.getSnoreSerious();
                dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
//                LogUtils.i(TAG, " snoreInfo " + new Gson().toJson(dbEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
