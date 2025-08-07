package com.healthy.rvigor.dao.executor;

import android.text.TextUtils;
import android.util.Log;

import com.healthy.rvigor.dao.entity.XueYaXinLvDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.XueYaXinLvDBEntityDao;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


/**
 * h95s手表血压插入
 */
public class InsertXueYaXinLvManridyWatchExecutor
        extends AppDaoManager.DBExecutor {

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

    /**
     * 测试的那一天  不包含时间
     */
    private long testDay = 0;

    /**
     * 测试的具体时间
     */
    private long testDate = 0;

    /**
     * 收缩压  高压
     */
    private int HBP = 0;

    /**
     * 舒张压  低压
     */
    private int LBP = 0;

    /**
     * 心率
     */
    private int HR = 0;


    public InsertXueYaXinLvManridyWatchExecutor(String macAddress, String deviceName, long userId, long testDay, long testDate, int HBP, int LBP, int HR) {
        this.macAddress = macAddress;
        this.deviceName = deviceName;
        this.userId = userId;
        this.testDay = testDay;
        this.testDate = testDate;
        this.HBP = HBP;
        this.LBP = LBP;
        this.HR = HR;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        try {
            List<XueYaXinLvDBEntity> xueYaXinLvDBEntities
                    = dbContext.mReadableDaoMaster.newSession().queryBuilder(XueYaXinLvDBEntity.class)
                    .where(XueYaXinLvDBEntityDao.Properties.Uid.eq(userId)
                            , XueYaXinLvDBEntityDao.Properties.TestDay.eq(testDay)
                            /*,XueYaXinLvDBEntityDao.Properties.DeviceMacAddress.eq(macAddress)*/)
                    .orderDesc(XueYaXinLvDBEntityDao.Properties.TestDate).limit(1).build().list();
            if ((xueYaXinLvDBEntities != null) && (xueYaXinLvDBEntities.size() > 0)) {
                XueYaXinLvDBEntity xueYaXinLvDBEntity = xueYaXinLvDBEntities.get(0);
//                if ((testDate - xueYaXinLvDBEntity.testDate) >= (15 * (60000))) {//如果时间大于15分钟  插入
                    xueYaXinLvDBEntity = new XueYaXinLvDBEntity();
                    xueYaXinLvDBEntity.uid = userId;
                    xueYaXinLvDBEntity.deviceMacAddress = macAddress;
                    xueYaXinLvDBEntity.deviceName = deviceName;
                    xueYaXinLvDBEntity.testDay = testDay;
                    xueYaXinLvDBEntity.testDate = testDate;
                    JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
                    JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                    jsonUtils.put("HBP", HBP);
                    jsonUtils.put("LBP", LBP);
                    jsonUtils.put("HR", HR);
                    jsonArrayUtils.putJsonUtils(jsonUtils);
                    xueYaXinLvDBEntity.XueYaXinLvJsonArrayData = jsonArrayUtils.toJsonArray().toString();
                    dbContext.mWritableDaoMaster.newSession().insert(xueYaXinLvDBEntity);
//                } else {//如果小于15分钟  则修改
//                    JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
//                    JsonUtils jsonUtils = new JsonUtils(new JSONObject());
//                    jsonUtils.put("HBP", HBP);
//                    jsonUtils.put("LBP", LBP);
//                    jsonUtils.put("HR", HR);
//                    jsonArrayUtils.putJsonUtils(jsonUtils);
//                    xueYaXinLvDBEntity.XueYaXinLvJsonArrayData = jsonArrayUtils.toJsonArray().toString();
//                    dbContext.mWritableDaoMaster.newSession().update(xueYaXinLvDBEntity);
//                }
            } else {//如果没有数据  插入
                XueYaXinLvDBEntity xueYaXinLvDBEntity = new XueYaXinLvDBEntity();
                xueYaXinLvDBEntity.uid = userId;
                xueYaXinLvDBEntity.deviceMacAddress = macAddress;
                xueYaXinLvDBEntity.deviceName = deviceName;
                xueYaXinLvDBEntity.testDay = testDay;
                xueYaXinLvDBEntity.testDate = testDate;
                JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
                JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                jsonUtils.put("HBP", HBP);
                jsonUtils.put("LBP", LBP);
                jsonUtils.put("HR", HR);
                jsonArrayUtils.putJsonUtils(jsonUtils);
                xueYaXinLvDBEntity.XueYaXinLvJsonArrayData = jsonArrayUtils.toJsonArray().toString();
                dbContext.mWritableDaoMaster.newSession().insert(xueYaXinLvDBEntity);
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }
}
