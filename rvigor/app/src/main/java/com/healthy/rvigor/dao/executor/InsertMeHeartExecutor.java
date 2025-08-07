package com.healthy.rvigor.dao.executor;

import android.text.TextUtils;
import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.HeartRateDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.HeartRateDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

//插入心率数据
public class InsertMeHeartExecutor extends AppDaoManager.DBExecutor{

    private static final String TAG = "InsertMeHeartExecutor";

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";


    public int heart;
    
    public long heartTime;

    public InsertMeHeartExecutor(String deviceName, String deviceMacAddress, int heart, long heartTime) {
        this.heart = heart;
        this.heartTime = heartTime;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (heart <= 0 || heartTime <= 0) {
            return;
        }
        long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
        Date tempDate = new Date(heartTime);
        long tempDay = DateTimeUtils.getDateTimeDatePart(tempDate).getTime();
        try {
            //查询已经有的数据
            List<HeartRateDBEntity> tempDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(HeartRateDBEntity.class)
                    .where(HeartRateDBEntityDao.Properties.Uid.eq(uid),
                            HeartRateDBEntityDao.Properties.HeartRateDay.eq(tempDay)).build().list();
            if ((tempDBEntities != null) && (tempDBEntities.size() > 0)) {//查找已有的数据
                HeartRateDBEntity dbEntity = tempDBEntities.get(0);
                dbEntity.heartJsonData = makeJsonData(dbEntity);
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
//                LogUtils.i(TAG, " HeartRateDay " + DateTimeUtils.s_long_2_str(tempDay, DateTimeUtils.f_format) + " json " + dbEntity.heartJsonData);
            } else {
                HeartRateDBEntity dbEntity = new HeartRateDBEntity();
                dbEntity.uid = uid;
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.HeartRateDay = tempDay;
                dbEntity.heartJsonData = makeJsonData(dbEntity);
                JsonArrayUtils tempJson = new JsonArrayUtils(dbEntity.heartJsonData);
                if (tempJson != null && (tempJson.length() > 0)) {
                    dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
                }
//                LogUtils.i(TAG," HeartRateDay " + DateTimeUtils.s_long_2_str(tempDay, DateTimeUtils.f_format) + " json " + dbEntity.heartJsonData);
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    private String makeJsonData(HeartRateDBEntity dbEntity) {
        JsonArrayUtils heatData;
        if (dbEntity == null || TextUtils.isEmpty(dbEntity.heartJsonData)) {
            heatData = new JsonArrayUtils(new JSONArray());
        } else {
            heatData = new JsonArrayUtils(dbEntity.heartJsonData);
        }
        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
        jsonUtils.put("rate", heart);
        jsonUtils.put("datetime", heartTime);
        jsonUtils.put("input", true);
        heatData.putJsonUtils(jsonUtils);
        return heatData.toJsonArray().toString();
    }
}
