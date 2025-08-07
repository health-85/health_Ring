package com.healthy.rvigor.dao.executor;

import android.util.Log;

import com.healthy.rvigor.dao.entity.SiestaDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SiestaDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.sw.watches.bean.SiestaInfo;

import java.util.Date;
import java.util.List;

/**
 * 插入午睡数据 SIAT手表
 */
public class InsertSiestaExecutor extends AppDaoManager.DBExecutor {

    /**
     * 午睡信息
     */
    private SiestaInfo siestaInfo = null;


    /**
     * 设备mac地址
     */
    private String macAddress = "";
    /**
     * 设备名称
     */
    private String deviceName = "";

    /**
     * 当前登录人的用户id
     */
    private long userId = 0;

    public InsertSiestaExecutor(SiestaInfo siestaInfo, String macAddress, String deviceName, long userId) {
        this.siestaInfo = siestaInfo;
        this.macAddress = macAddress;
        this.deviceName = deviceName;
        this.userId = userId;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (siestaInfo == null) {
            return;
        }
        Date siestaDate = DateTimeUtils.ConvertStrToDate(siestaInfo.getStartYear() + "-" + siestaInfo.getStartMonth() + "-" + siestaInfo.getStartDay());
        if (siestaDate == null) {
            return;
        }
        if (siestaInfo.getSleepTime() <= 0) {
            return;
        }
        try {
            long siestaDay = DateTimeUtils.getDateTimeDatePart(siestaDate).getTime();
            //查询已经有的数据
            List<SiestaDBEntity> siestaDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(SiestaDBEntity.class)
                    .where(SiestaDBEntityDao.Properties.Uid.eq(userId),
                            /*SiestaDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                            SiestaDBEntityDao.Properties.SiestaDay.eq(siestaDay)).build().list();
            if ((siestaDBEntities != null) && (siestaDBEntities.size() > 0)) {
                SiestaDBEntity siestaDBEntity = siestaDBEntities.get(0);
                siestaDBEntity.siestaDay = siestaDay;
                siestaDBEntity.uid = userId;
                siestaDBEntity.deviceName = deviceName;
                siestaDBEntity.deviceMacAddress = macAddress;
                siestaDBEntity.siestaLength = siestaInfo.getSleepTime();
                siestaDBEntity.startTime = siestaInfo.getStartHour() + ":" + (siestaInfo.getStartMin() < 10 ? "0" + siestaInfo.getStartMin() : siestaInfo.getStartMin());
                siestaDBEntity.endTime = siestaInfo.getEndHour() + ":" + (siestaInfo.getEndMin() < 10 ? "0" + siestaInfo.getEndMin() : siestaInfo.getEndMin());
                dbContext.mWritableDaoMaster.newSession().update(siestaDBEntity);
            } else {
                SiestaDBEntity siestaDBEntity = new SiestaDBEntity();
                siestaDBEntity.siestaDay = siestaDay;
                siestaDBEntity.uid = userId;
                siestaDBEntity.deviceName = deviceName;
                siestaDBEntity.deviceMacAddress = macAddress;
                siestaDBEntity.siestaLength = siestaInfo.getSleepTime();
                siestaDBEntity.startTime = siestaInfo.getStartHour() + ":" + (siestaInfo.getStartMin() < 10 ? "0" + siestaInfo.getStartMin() : siestaInfo.getStartMin());
                siestaDBEntity.endTime = siestaInfo.getEndHour() + ":" + (siestaInfo.getEndMin() < 10 ? "0" + siestaInfo.getEndMin() : siestaInfo.getEndMin());
                dbContext.mWritableDaoMaster.newSession().insert(siestaDBEntity);
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

}
