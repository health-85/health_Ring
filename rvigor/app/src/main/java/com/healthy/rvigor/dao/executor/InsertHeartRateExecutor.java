package com.healthy.rvigor.dao.executor;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.HeartRateDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.HeartRateDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.healthy.rvigor.util.LogUtils;
import com.sw.watches.bean.PoHeartInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 插入心率
 */
public class InsertHeartRateExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertHeartRateExecutor";

    /**
     * 设备名称
     */
    public String deviceName = "";

    /**
     * 设备地址
     */
    public String deviceMacAddress = "";

    /**
     * 心率数据
     */
    public PoHeartInfo poHeartInfo = null;

    public InsertHeartRateExecutor(String deviceName, String deviceMacAddress, PoHeartInfo poHeartInfo) {
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.poHeartInfo = poHeartInfo;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (poHeartInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(deviceMacAddress)) {//mac地址不能为空
            return;
        }
        LogUtils.i(TAG + " poHeartInfo " + new Gson().toJson(poHeartInfo));
        Date heartDate = DateTimeUtils.convertStrToDateForThisProject(poHeartInfo.PoHeartDate);
        if (heartDate == null) {//日期时间不能为空
            return;
        }
        long userId = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
        long heartDay = DateTimeUtils.getDateTimeDatePart(heartDate).getTime();
        try {
            //查询已经有的数据
            List<HeartRateDBEntity> heartRateDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(HeartRateDBEntity.class)
                    .where(HeartRateDBEntityDao.Properties.Uid.eq(userId),
                            /* HeartRateDBEntityDao.Properties.DeviceMacAddress.eq(deviceMacAddress),*/
                            HeartRateDBEntityDao.Properties.HeartRateDay.eq(heartDay)).build().list();
            if ((heartRateDBEntities != null) && (heartRateDBEntities.size() > 0)) {//查找已有的数据
                HeartRateDBEntity heartRateDBEntity = heartRateDBEntities.get(0);
//                LogUtils.i(TAG, " HeartRateDay " + DateTimeUtils.s_long_2_str(heartDay, DateTimeUtils.f_format));
//                heartRateDBEntity.heartJsonData = makeJsonData(heartDay);
                heartRateDBEntity.heartJsonData = makeJsonData(heartDay, heartRateDBEntity);
//                heartRateDBEntity.isOneMin = (poHeartInfo.isOneMinRate() || (poHeartInfo.getPoHeartData() != null && poHeartInfo.getPoHeartData().size() > 500));
                dbContext.mWritableDaoMaster.newSession().update(heartRateDBEntity);
//                LogUtils.i(TAG, new Gson().toJson(heartRateDBEntity));
            } else {
//                LogUtils.i(TAG, " HeartRateDay " + DateTimeUtils.s_long_2_str(heartDay, DateTimeUtils.f_format));
                HeartRateDBEntity heartRateDBEntity = new HeartRateDBEntity();
                heartRateDBEntity.deviceName = deviceName;
                heartRateDBEntity.deviceMacAddress = deviceMacAddress;
                heartRateDBEntity.HeartRateDay = heartDay;
                heartRateDBEntity.uid = userId;
                heartRateDBEntity.heartJsonData = makeJsonData(heartDay);
//                heartRateDBEntity.isOneMin = (poHeartInfo.isOneMinRate() || (poHeartInfo.getPoHeartData() != null && poHeartInfo.getPoHeartData().size() > 500));
                JsonArrayUtils heartjson = new JsonArrayUtils(heartRateDBEntity.heartJsonData);
                if ((heartjson != null) && (heartjson.length() > 0)) {
                    dbContext.mWritableDaoMaster.newSession().insert(heartRateDBEntity);
                }
            }

        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }

    }

    /**
     * @param heartday
     * @return
     */
    private String makeJsonData(long heartday) {
        JsonArrayUtils heartRatedata = new JsonArrayUtils(new JSONArray());

        long timeLen = 60000;
        if (poHeartInfo.getPoHeartData() != null && poHeartInfo.getPoHeartData().size() > 0) {
            int len = poHeartInfo.getPoHeartData().size() / 24;
            timeLen = 60 / len;
            for (int i = 0; i < poHeartInfo.getPoHeartData().size(); i++) {
                Object object = poHeartInfo.getPoHeartData().get(i);
                if (object instanceof Integer) {
                    Integer hr = (Integer) object;
                    if (hr > 0) {
                        long datetime = heartday + i * timeLen * 60000;
                        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                        jsonUtils.put("rate", hr);
                        jsonUtils.put("datetime", datetime);
                        jsonUtils.put("oneMin", timeLen == 1);
                        heartRatedata.putJsonUtils(jsonUtils);
//                        LogUtils.i(TAG, " one HeartRateDay " + DateTimeUtils.s_long_2_str(datetime, DateTimeUtils.f_format) +
//                                " rate " + hr + " size " + poHeartInfo.getPoHeartData().size()
//                                + " i " + i);
                    }
                }
            }
        }
        return heartRatedata.toJsonArray().toString();
    }

    /**
     * @param heartday
     * @return
     */
    private String makeJsonData(long heartday, HeartRateDBEntity heartRateDBEntity) {
        JsonArrayUtils heartRatedata = new JsonArrayUtils(new JSONArray());
        Map<Long, Integer> map = new HashMap<>();
        long timeLen = 60000;
        if (poHeartInfo.getPoHeartData() != null && poHeartInfo.getPoHeartData().size() > 0) {
            int len = poHeartInfo.getPoHeartData().size() / 24;
            timeLen = 60 / len;
            for (int i = 0; i < poHeartInfo.getPoHeartData().size(); i++) {
                Object object = poHeartInfo.getPoHeartData().get(i);
                if (object instanceof Integer) {
                    Integer hr = (Integer) object;
                    long datetime = heartday + i * timeLen * 60000;
                    if (hr > 0) {
                        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                        jsonUtils.put("rate", hr);
                        jsonUtils.put("datetime", datetime);
                        jsonUtils.put("oneMin", timeLen == 1);
                        heartRatedata.putJsonUtils(jsonUtils);
//                        LogUtils.i(TAG, " one HeartRateDay " + DateTimeUtils.s_long_2_str(datetime, DateTimeUtils.f_format) +
//                                    " rate " + hr + " size " + poHeartInfo.getPoHeartData().size() + " i " + i + " size " + poHeartInfo.getPoHeartData().size());
                    } /*else {
                        if (map.containsKey(datetime)) {
                            JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                            jsonUtils.put("rate", map.get(datetime));
                            jsonUtils.put("datetime", datetime);
                            jsonUtils.put("oneMin", timeLen == 1);
                            heartRatedata.putJsonUtils(jsonUtils);
                        }
                    }*/
//                    LogUtils.i(TAG, " one HeartRateDay " + DateTimeUtils.s_long_2_str(datetime, DateTimeUtils.f_format) +
//                            " rate " + hr + " size " + poHeartInfo.getPoHeartData().size() + " i " + i);
                }
            }
        }
        return heartRatedata.toJsonArray().toString();
    }

}
