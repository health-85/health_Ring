package com.healthy.rvigor.dao.executor;

import android.util.Log;

import com.healthy.rvigor.dao.entity.SpoDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SpoDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.healthy.rvigor.util.LogUtils;
import com.sw.watches.bean.SpoData;
import com.sw.watches.bean.SpoInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * siat手表血氧数据同步
 */
public class InsertSpoExecutorSIATWatch extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertSpoExecutorSIATWa";

    private SpoInfo spoInfo = null;

    /**
     * 用户id
     */
    public long uid = 0;

    /**
     * 是否已经同步到服务器了 0未同步  1已同步
     */
    public int isupLoadToServer = 0;

    //是否是睡眠血氧
    public boolean isSleepOx = false;


    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    public InsertSpoExecutorSIATWatch(SpoInfo spoInfo, long uid, String deviceName, String deviceMacAddress) {
        this.spoInfo = spoInfo;
        this.uid = uid;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
    }

    public InsertSpoExecutorSIATWatch(SpoInfo spoInfo, long uid, String deviceName, String deviceMacAddress, boolean isSleepOx) {
        this.uid = uid;
        this.spoInfo = spoInfo;
        this.isSleepOx = isSleepOx;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
    }


    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (spoInfo == null) {
            return;
        }
        if (spoInfo.spoList == null) {
            return;
        }
        if (spoInfo.spoList.size() == 0) {
            return;
        }

        try {
            Map<Long, List<SpoData>> listMap = getSpoDataMap(spoInfo.spoList);
//            LogUtils.i(TAG + " " + new Gson().toJson(listMap));
            if (listMap != null && listMap.keySet() != null && listMap.keySet().size() > 0) {
                for (long key : listMap.keySet()) {
//                    LogUtils.i(TAG + " " + DateTimeUtils.s_long_2_str(key, DateTimeUtils.day_format) + " " + new Gson().toJson(listMap.get(key)));
                    insertSpoInfo(dbContext, key, listMap.get(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Date spodate = DateTimeUtils.convertStrToDateForThisProject(spoInfo.spoList.get(0).spoTime);
//        if (spodate == null) {
//            return;
//        }
//        /**
//         * 血氧的哪一天
//         */
//        long boday = DateTimeUtils.getDateTimeDatePart(spodate).getTime();
//        try {
//            List<SpoDBEntity> spoDBEntities = dbContext.mReadableDaoMaster
//                    .newSession().queryBuilder(SpoDBEntity.class)
//                    .where(SpoDBEntityDao.Properties.Uid.eq(uid),
//                            /*SpoDBEntityDao.Properties.DeviceMacAddress.eq(deviceMacAddress),*/
//                            SpoDBEntityDao.Properties.SpoDay.eq(boday)).build().list();
//            if ((spoDBEntities != null) && (spoDBEntities.size() > 0)) {
//                SpoDBEntity spoDBEntity = spoDBEntities.get(0);
//                JsonArrayUtils datas = new JsonArrayUtils(spoDBEntity.spoJsonData);
//                for (int i = 0; i < spoInfo.spoList.size(); i++) {
//                    SpoData spoData = spoInfo.spoList.get(i);
//                    combinOrAddJsonData(datas, spoData);
//                }
//                spoDBEntity.spoJsonData = datas.toJsonArray().toString();
//                dbContext.mWritableDaoMaster.newSession().update(spoDBEntity);
//            } else {
//                SpoDBEntity spoDBEntity = new SpoDBEntity();
//                spoDBEntity.uid = uid;
//                spoDBEntity.deviceName = deviceName;
//                spoDBEntity.deviceMacAddress = deviceMacAddress;
//                spoDBEntity.SpoDay = boday;
//                JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
//                for (int i = 0; i < spoInfo.spoList.size(); i++) {
//                    SpoData spoData = spoInfo.spoList.get(i);
//                    combinOrAddJsonData(jsonArrayUtils, spoData);
//                }
//                spoDBEntity.spoJsonData = jsonArrayUtils.toJsonArray().toString();
//                dbContext.mWritableDaoMaster.newSession().insert(spoDBEntity);
//            }
//        } catch (Exception ex) {
//            Log.e(this.getClass().getSimpleName(), StringUtils.getNonNullString(ex.getMessage()));
//        }
    }

    private Map<Long, List<SpoData>> getSpoDataMap(List<SpoData> spoDataList) {
        if (spoDataList == null || spoDataList.isEmpty()) return null;
        Map<Long, List<SpoData>> map = new HashMap<>();
        try {
            for (SpoData data : spoDataList) {
                Date spodate = DateTimeUtils.convertStrToDateForThisProject(data.spoTime);
                if (spodate == null) {
                    continue;
                }
                long boday = DateTimeUtils.getDateTimeDatePart(spodate).getTime();
                if (map.containsKey(boday)) {
                    List<SpoData> list = map.get(boday);
                    list.add(data);
                } else {
                    List<SpoData> spoItemList = new ArrayList<>();
                    spoItemList.add(data);
                    map.put(boday, spoItemList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private void insertSpoInfo(AppDaoManager.DBContext dbContext, long boday, List<SpoData> spoDataList) {
        try {
            List<SpoDBEntity> spoDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(SpoDBEntity.class)
                    .where(SpoDBEntityDao.Properties.Uid.eq(uid),
                            /*SpoDBEntityDao.Properties.DeviceMacAddress.eq(deviceMacAddress),*/
                            SpoDBEntityDao.Properties.SpoDay.eq(boday)).build().list();
            if ((spoDBEntities != null) && (spoDBEntities.size() > 0)) {
                SpoDBEntity spoDBEntity = spoDBEntities.get(0);
                JsonArrayUtils datas = new JsonArrayUtils(spoDBEntity.spoJsonData);
                if (isSleepOx){
                    datas = getJsonArrayUtil(datas);
                }
                for (int i = 0; i < spoDataList.size(); i++) {
                    SpoData spoData = spoDataList.get(i);
                    combinOrAddJsonData(datas, spoData);
                }
                spoDBEntity.spoJsonData = datas.toJsonArray().toString();
                LogUtils.i(TAG + " spoJsonData " + spoDBEntity.spoJsonData);
                dbContext.mWritableDaoMaster.newSession().update(spoDBEntity);
            } else {
                SpoDBEntity spoDBEntity = new SpoDBEntity();
                spoDBEntity.uid = uid;
                spoDBEntity.deviceName = deviceName;
                spoDBEntity.deviceMacAddress = deviceMacAddress;
                spoDBEntity.SpoDay = boday;
                JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
                for (int i = 0; i < spoDataList.size(); i++) {
                    SpoData spoData = spoDataList.get(i);
                    combinOrAddJsonData(jsonArrayUtils, spoData);
                }
                spoDBEntity.spoJsonData = jsonArrayUtils.toJsonArray().toString();
                LogUtils.i(TAG + " spoJsonData " + spoDBEntity.spoJsonData);
                dbContext.mWritableDaoMaster.newSession().insert(spoDBEntity);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    private JsonArrayUtils getJsonArrayUtil(JsonArrayUtils jsonDatas) {
        JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
        if (jsonDatas == null || jsonDatas.length() <= 0) return jsonArrayUtils;
        for (int i = 0; i < jsonDatas.length(); i++) {
            JsonUtils jsonUtils = jsonDatas.getJsonObject(i);
            boolean sleepOx = jsonUtils.getBoolean("sleepOx", false);
            if (!sleepOx){
                jsonArrayUtils.putJsonUtils(jsonUtils);
            }
        }
        return jsonArrayUtils;
    }

    /**
     * 合并或者添加数据
     *
     * @param datas
     */
    private void combinOrAddJsonData(JsonArrayUtils datas, SpoData spoData) {
        if (datas.length() > 0) {
            JsonUtils last = datas.getJsonObject(datas.length() - 1);//获取最后一个数据
            long dateTime = last.getLong("datetime", 0);
            int rate = last.getInt("rate", 0);
            Date boDate = DateTimeUtils.convertStrToDateForThisProject(spoData.spoTime);
            if (boDate == null) {
                boDate = new Date();
            }
//            if ((boDate.getTime() - dateTime) >= (15 * 60 * 1000)) {//如果大于等于15分钟
            JsonUtils jsonUtils = new JsonUtils(new JSONObject());
            jsonUtils.put("spo", spoData.spoValue + "");
            jsonUtils.put("datetime", boDate.getTime());
            jsonUtils.put("sleepOx", isSleepOx);
            datas.putJsonUtils(jsonUtils);
//            } else {
//                last.put("spo", spoData.spoValue + "");
//            }
        } else {
            Date boDate = DateTimeUtils.convertStrToDateForThisProject(spoData.spoTime);
            if (boDate == null) {
                boDate = new Date();
            }
            JsonUtils jsonUtils = new JsonUtils(new JSONObject());
            jsonUtils.put("spo", spoData.spoValue + "");
            jsonUtils.put("datetime", boDate.getTime());
            jsonUtils.put("sleepOx", isSleepOx);
            datas.putJsonUtils(jsonUtils);
        }
    }

}
