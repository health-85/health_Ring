package com.healthy.rvigor.dao.executor;

import android.text.TextUtils;
import android.util.Log;

import com.healthy.rvigor.dao.entity.PressureDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.PressureDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

//插入压力数据
public class InsertMePressureExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertMePressureExecuto";

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

    public int pressure;
    public long pressureTime;

    public InsertMePressureExecutor(long uid, String deviceName, String deviceMacAddress, int pressure, long pressureTime) {
        this.uid = uid;
        this.pressure = pressure;
        this.pressureTime = pressureTime;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
    }
    
    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (pressure <= 0 || pressureTime <= 0) {
            return;
        }
        Date tempDate = new Date(pressureTime);
        long tempDay = DateTimeUtils.getDateTimeDatePart(tempDate).getTime();
        try {
            //查询已经有的数据
            List<PressureDBEntity> tempDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(PressureDBEntity.class)
                    .where(PressureDBEntityDao.Properties.Uid.eq(uid),
                            PressureDBEntityDao.Properties.PressureDay.eq(tempDay)).build().list();
            if ((tempDBEntities != null) && (tempDBEntities.size() > 0)) {//查找已有的数据
                PressureDBEntity dbEntity = tempDBEntities.get(0);
                dbEntity.pressureJsonData = makeJsonData(dbEntity);
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
//                LogUtils.i(TAG, " PressureDay " + DateTimeUtils.s_long_2_str(tempDay, DateTimeUtils.f_format) + " json " + dbEntity.pressureJsonData);
            } else {
                PressureDBEntity dbEntity = new PressureDBEntity();
                dbEntity.uid = uid;
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.pressureDay = tempDay;
                dbEntity.pressureJsonData = makeJsonData(dbEntity);
                JsonArrayUtils tempJson = new JsonArrayUtils(dbEntity.pressureJsonData);
                if (tempJson != null && (tempJson.length() > 0)) {
                    dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
                }
//                LogUtils.i(TAG," PressureDay " + DateTimeUtils.s_long_2_str(tempDay, DateTimeUtils.f_format) + " json " + dbEntity.pressureJsonData);
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    /**
     * @return
     */
    private String makeJsonData(PressureDBEntity dbEntity) {
        JsonArrayUtils heatData;
        if (dbEntity == null || TextUtils.isEmpty(dbEntity.pressureJsonData)) {
            heatData = new JsonArrayUtils(new JSONArray());
        } else {
            heatData = new JsonArrayUtils(dbEntity.pressureJsonData);
        }
        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
        jsonUtils.put("pressure", pressure);
        jsonUtils.put("datetime", pressureTime);
        jsonUtils.put("input", true);
        heatData.putJsonUtils(jsonUtils);
        return heatData.toJsonArray().toString();
    }
}
