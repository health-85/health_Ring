package com.healthy.rvigor.dao.executor;

import android.util.Log;

import com.healthy.rvigor.dao.entity.XueYaXinLvDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.XueYaXinLvDBEntityDao;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.LogUtils;

import org.json.JSONArray;

import java.util.List;


/**
 * 插入血压心率数据
 */
public class InsertXueYaXinLvExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertXueYaXinLvExecuto";

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

//    /**
//     * 测试uuid
//     */
//    private String testUUID = "";

    /**
     * 测试的那一天  不包含时间
     */
    private long testDay = 0;

    /**
     * 测试的具体时间
     */
    private long testDate = 0;

    /**
     * 血压心率数据
     */
    private JsonArrayUtils xueyaxinlvdatas = new JsonArrayUtils(new JSONArray());

    public InsertXueYaXinLvExecutor(String macAddress, String deviceName, long userId, long testDay
            , long testDate, JsonArrayUtils xueyaxinlvdatas) {
        this.macAddress = macAddress;
        this.deviceName = deviceName;
        this.userId = userId;
        this.testDay = testDay;
        this.testDate = testDate;
        this.xueyaxinlvdatas = xueyaxinlvdatas;
    }

    public InsertXueYaXinLvExecutor(String macAddress, String deviceName, long userId, String testUUID, long testDay
            , long testDate, JsonArrayUtils xueyaxinlvdatas) {
        this.macAddress = macAddress;
        this.deviceName = deviceName;
        this.userId = userId;
//        this.testUUID = testUUID;
        this.testDay = testDay;
        this.testDate = testDate;
        this.xueyaxinlvdatas = xueyaxinlvdatas;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if ((xueyaxinlvdatas == null) || (xueyaxinlvdatas.length() == 0)) {//如果没有数据则不插入
            return;
        }
//        if (StringUtils.StringIsEmptyOrNull(testUUID)) {//测试的uuid不能为空
//            return;
//        }

        try {
            List<XueYaXinLvDBEntity> xueYaXinLvDBEntities
                    = dbContext.mReadableDaoMaster.newSession().queryBuilder(XueYaXinLvDBEntity.class)
                    .where(XueYaXinLvDBEntityDao.Properties.Uid.eq(userId)
                            , XueYaXinLvDBEntityDao.Properties.TestDate.eq(testDate)
                            /*, XueYaXinLvDBEntityDao.Properties.DeviceMacAddress.eq(macAddress)*/
                           /* , XueYaXinLvDBEntityDao.Properties.TestUUID.eq(testUUID)*/).build().list();
            if ((xueYaXinLvDBEntities != null) && (xueYaXinLvDBEntities.size() > 0)) {
                XueYaXinLvDBEntity xueYaXinLvDBEntity = xueYaXinLvDBEntities.get(0);
                xueYaXinLvDBEntity.XueYaXinLvJsonArrayData = xueyaxinlvdatas.toJsonArray().toString();
                LogUtils.i(TAG + " " + xueYaXinLvDBEntity.XueYaXinLvJsonArrayData);
                dbContext.mWritableDaoMaster.newSession().update(xueYaXinLvDBEntity);
            } else {
                XueYaXinLvDBEntity xueYaXinLvDBEntity = new XueYaXinLvDBEntity();
                xueYaXinLvDBEntity.uid = userId;
                xueYaXinLvDBEntity.deviceMacAddress = macAddress;
                xueYaXinLvDBEntity.deviceName = deviceName;
//                xueYaXinLvDBEntity.testUUID = testUUID;
                xueYaXinLvDBEntity.testDay = testDay;
                xueYaXinLvDBEntity.testDate = testDate;
                xueYaXinLvDBEntity.XueYaXinLvJsonArrayData = xueyaxinlvdatas.toJsonArray().toString();
                LogUtils.i(TAG + " " + xueYaXinLvDBEntity.XueYaXinLvJsonArrayData);
                dbContext.mWritableDaoMaster.newSession().insert(xueYaXinLvDBEntity);
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }

    }
}
