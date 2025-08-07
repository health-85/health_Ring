package com.healthy.rvigor.dao.executor;

import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.SpoDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SpoDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.sw.watches.bean.SpoData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

//插入血氧
public class InsertMeSpoExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertMeSpoExecutor";

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";
    
    private SpoData spoInfo;
    
    public InsertMeSpoExecutor(String deviceName, String deviceMacAddress, SpoData spoData){
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.spoInfo = spoData;
    }
    
    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (spoInfo == null) {
            return;
        }
        Date spoDate = DateTimeUtils.convertStrToDateForThisProject(spoInfo.spoTime);
        if (spoDate == null) {//日期时间不能为空
            return;
        }
        long spoDay = DateTimeUtils.getDateTimeDatePart(spoDate).getTime();
        try {
            long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
            //查询已经有的数据
            List<SpoDBEntity> heatDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(SpoDBEntity.class)
                    .where(SpoDBEntityDao.Properties.Uid.eq(uid),
                            SpoDBEntityDao.Properties.SpoDay.eq(spoDay)).build().list();
            if ((heatDBEntities != null) && (heatDBEntities.size() > 0)) {//查找已有的数据
                SpoDBEntity dbEntity = heatDBEntities.get(0);
                JsonArrayUtils datas = new JsonArrayUtils(dbEntity.spoJsonData);
                combinOrAddJsonData(datas, spoInfo);
                dbEntity.spoJsonData = datas.toJsonArray().toString();
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
//                LogUtils.i(TAG, " spoJsonData " + dbEntity.spoJsonData + " time " + spoInfo.spoTime);
            } else {
                SpoDBEntity dbEntity = new SpoDBEntity();
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.uid = uid;
                dbEntity.SpoDay = spoDay;
                JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
                combinOrAddJsonData(jsonArrayUtils, spoInfo);
                dbEntity.spoJsonData = jsonArrayUtils.toJsonArray().toString();
                dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
//                LogUtils.i(TAG, " spoJsonData " + dbEntity.spoJsonData + " time " + spoInfo.spoTime);
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }


    /**
     * 合并或者添加数据
     *
     * @param datas
     */
    private void combinOrAddJsonData(JsonArrayUtils datas, SpoData spoData) {
        Date boDate = DateTimeUtils.convertStrToDateForThisProject(spoData.spoTime);
        if (boDate == null) {
            boDate = new Date();
        }
        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
        jsonUtils.put("spo", spoData.spoValue + "");
        jsonUtils.put("datetime", boDate.getTime());
        datas.putJsonUtils(jsonUtils);
    }

}
