package com.healthy.rvigor.dao.executor;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.healthy.rvigor.dao.entity.AbnormalRateDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.AbnormalRateDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.healthy.rvigor.util.LogUtils;
import com.sw.watches.bean.AbnormalHeartInfo;
import com.sw.watches.bean.AbnormalHeartListInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//插入异常心率
public class InsertAbnormalHeartExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertAbnormalHeartExec";

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

    private AbnormalHeartListInfo info;

    public InsertAbnormalHeartExecutor(AbnormalHeartListInfo info, long uid, String deviceName, String deviceMacAddress) {
        this.info = info;
        this.uid = uid;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
    }


    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (info == null || info.getList() == null || info.getList().isEmpty()) return;
        Map<Long, List<AbnormalHeartInfo>> listMap = getDataMap(info.getList());
        if (listMap != null && listMap.keySet() != null && listMap.keySet().size() > 0) {
            for (long key : listMap.keySet()) {
                insertInfo(dbContext, key, listMap.get(key));
            }
        }
    }

    private void insertInfo(AppDaoManager.DBContext dbContext, long boday, List<AbnormalHeartInfo> infoList) {
        List<AbnormalRateDBEntity> dBEntities = dbContext.mReadableDaoMaster
                .newSession().queryBuilder(AbnormalRateDBEntity.class)
                .where(AbnormalRateDBEntityDao.Properties.Uid.eq(uid),
                        /*SpoDBEntityDao.Properties.DeviceMacAddress.eq(deviceMacAddress),*/
                        AbnormalRateDBEntityDao.Properties.HeartRateDay.eq(boday)).build().list();
        if ((dBEntities != null) && (dBEntities.size() > 0)) {
            AbnormalRateDBEntity dBEntity = dBEntities.get(0);
            dBEntity.uid = uid;
            dBEntity.deviceName = deviceName;
            dBEntity.deviceMacAddress = deviceMacAddress;
            dBEntity.HeartRateDay = boday;
            dBEntity.heartJsonData = getDataJson(dBEntity.heartJsonData, infoList);
            dbContext.mWritableDaoMaster.newSession().update(dBEntity);
            LogUtils.i(TAG + " AbnormalHeartInfo " + new Gson().toJson(dBEntity));
        } else {
            AbnormalRateDBEntity dBEntity = new AbnormalRateDBEntity();
            dBEntity.uid = uid;
            dBEntity.deviceName = deviceName;
            dBEntity.deviceMacAddress = deviceMacAddress;
            dBEntity.HeartRateDay = boday;
            JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
            for (int i = 0; i < infoList.size(); i++) {
                AbnormalHeartInfo infoData = infoList.get(i);
                JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                jsonUtils.put("time", infoData.getTime());
                jsonUtils.put("heart", infoData.getHeart());
                jsonArrayUtils.putJsonUtils(jsonUtils);
            }
            dBEntity.heartJsonData = jsonArrayUtils.toJsonArray().toString();
            dbContext.mWritableDaoMaster.newSession().insert(dBEntity);
            LogUtils.i(TAG + " AbnormalHeartInfo " + new Gson().toJson(dBEntity));
        }
    }

    private String getDataJson(String json, List<AbnormalHeartInfo> heartInfoList) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            LogUtils.i(TAG + " json " + json);
            LogUtils.i(TAG + " heartInfoList " + new Gson().toJson(heartInfoList));
            List<AbnormalHeartInfo> infoList = new Gson().fromJson(json, new TypeToken<List<AbnormalHeartInfo>>() {
            }.getType());
            if (infoList == null) {
                infoList = new ArrayList<>();
            }
            if (heartInfoList != null && heartInfoList.size() > 0) {
                infoList.addAll(heartInfoList);
            }

            Map<Long, AbnormalHeartInfo> heartInfoMap = new HashMap<>();
            if (infoList.size() > 0) {
                for (AbnormalHeartInfo heartInfo : infoList) {
                    Date heartDate = DateTimeUtils.convertStrToDateForThisProject(heartInfo.getTime());
                    heartInfoMap.put(heartDate.getTime(), heartInfo);
                }
            }

            LogUtils.i(TAG + " map " + new Gson().toJson(heartInfoMap));

            List<Map.Entry<Long, AbnormalHeartInfo>> lstEntry = new ArrayList<>(heartInfoMap.entrySet());
            Collections.sort(lstEntry, ((o1, o2) -> {
                if (o1.getKey() > o2.getKey()) {
                    return 1;
                } else {
                    return -1;
                }
            }));

            JsonArrayUtils jsonArrayUtils = new JsonArrayUtils(new JSONArray());
            lstEntry.forEach(o -> {
                LogUtils.i(TAG + " map " + DateTimeUtils.s_long_2_str(o.getKey(), DateTimeUtils.f_format) + " " + o.getValue().getHeart());
                JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                if (o.getKey() > 0 && o.getValue() != null && o.getValue().getHeart() > 0) {
                    jsonUtils.put("time", DateTimeUtils.s_long_2_str(o.getKey(), DateTimeUtils.f_format));
                    jsonUtils.put("heart", o.getValue().getHeart());
                    jsonArrayUtils.putJsonUtils(jsonUtils);
                }
            });
            LogUtils.i(TAG + " jsonArrayUtils " + jsonArrayUtils.toJsonArray().toString());
            return jsonArrayUtils.toJsonArray().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<Long, List<AbnormalHeartInfo>> getDataMap(List<AbnormalHeartInfo> infos) {
        if (infos == null || infos.isEmpty()) return null;
        Map<Long, List<AbnormalHeartInfo>> map = new HashMap<>();
        try {
            for (AbnormalHeartInfo data : infos) {
                Date heartDate = DateTimeUtils.convertStrToDateForThisProject(data.getTime());
                if (heartDate == null) {
                    continue;
                }
                long boday = DateTimeUtils.getDateTimeDatePart(heartDate).getTime();
//                LogUtils.i(TAG + " boday " + DateTimeUtils.s_long_2_str(boday, DateTimeUtils.f_format) +
//                        " dataTime " + data.getTime()
//                        + " time " + DateTimeUtils.s_long_2_str(heartDate.getTime(), DateTimeUtils.f_format));
                if (map.containsKey(boday)) {
                    List<AbnormalHeartInfo> list = map.get(boday);
                    list.add(data);
                } else {
                    List<AbnormalHeartInfo> itemList = new ArrayList<>();
                    itemList.add(data);
                    map.put(boday, itemList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
