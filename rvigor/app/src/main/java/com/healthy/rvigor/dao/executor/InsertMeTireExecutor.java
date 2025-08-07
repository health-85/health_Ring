package com.healthy.rvigor.dao.executor;

import android.text.TextUtils;
import android.util.Log;

import com.healthy.rvigor.dao.entity.TireDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.TireDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;


//插入疲劳数据
public class InsertMeTireExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertMeTireExecutor";
    
    /**
     * 用户id
     */
    public long uid = 0;
    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    public int tire;
    public long tireTime;

    public InsertMeTireExecutor(long uid, String deviceName, String deviceMacAddress, int tire, long tireTime) {
        this.uid = uid;
        this.tire = tire;
        this.tireTime = tireTime;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
    }
    
    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (tire <= 0 || tireTime <= 0) {
            return;
        }
        Date tempDate = new Date(tireTime);
        long tempDay = DateTimeUtils.getDateTimeDatePart(tempDate).getTime();
        try {
            //查询已经有的数据
            List<TireDBEntity> tempDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(TireDBEntity.class)
                    .where(TireDBEntityDao.Properties.Uid.eq(uid),
                            TireDBEntityDao.Properties.TireDay.eq(tempDay)).build().list();
            if ((tempDBEntities != null) && (tempDBEntities.size() > 0)) {//查找已有的数据
                TireDBEntity dbEntity = tempDBEntities.get(0);
                dbEntity.tireJsonData = makeJsonData(dbEntity);
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
//                LogUtils.i(TAG, " TireDay " + DateTimeUtils.s_long_2_str(tempDay, DateTimeUtils.f_format) + " json " + dbEntity.tireJsonData);
            } else {
                TireDBEntity dbEntity = new TireDBEntity();
                dbEntity.uid = uid;
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.tireDay = tempDay;
                dbEntity.tireJsonData = makeJsonData(dbEntity);
                JsonArrayUtils tempJson = new JsonArrayUtils(dbEntity.tireJsonData);
                if (tempJson != null && (tempJson.length() > 0)) {
                    dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
                }
//                LogUtils.i(TAG," TireDay " + DateTimeUtils.s_long_2_str(tempDay, DateTimeUtils.f_format) + " json " + dbEntity.tireJsonData);
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    private String makeJsonData(TireDBEntity dbEntity) {
        JsonArrayUtils heatData;
        if (dbEntity == null || TextUtils.isEmpty(dbEntity.tireJsonData)) {
            heatData = new JsonArrayUtils(new JSONArray());
        } else {
            heatData = new JsonArrayUtils(dbEntity.tireJsonData);
        }
        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
        jsonUtils.put("tire", tire);
        jsonUtils.put("datetime", tireTime);
        jsonUtils.put("input", true);
        heatData.putJsonUtils(jsonUtils);
        return heatData.toJsonArray().toString();
    }
}
