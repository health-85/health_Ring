package com.healthy.rvigor.dao.executor;

import android.util.Log;

import com.healthy.rvigor.dao.entity.EmotionDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.EmotionDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.healthy.rvigor.util.LogUtils;
import com.sw.watches.bean.EmotionInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class InsertEmotionExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertEmotionExecutor";

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

    /**
     * 情绪数据
     */
    public EmotionInfo tempInfo = null;

    public InsertEmotionExecutor(long uid, String deviceName, String deviceMacAddress, EmotionInfo info) {
        this.uid = uid;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.tempInfo = info;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
//        LogUtils.i(TAG, new Gson().toJson(tempInfo));
        if (tempInfo == null) {
            return;
        }
//        if (StringUtils.StringIsEmptyOrNull(deviceMacAddress)) {//mac地址不能为空
//            return;
//        }
        Date tempDate = DateTimeUtils.convertStrToDateForThisProject(tempInfo.getEmotionDate());
        if (tempDate == null) {//日期时间不能为空
            return;
        }
        long tempDay = DateTimeUtils.getDateTimeDatePart(tempDate).getTime();
        try {
            //查询已经有的数据
            List<EmotionDBEntity> tempDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(EmotionDBEntity.class)
                    .where(EmotionDBEntityDao.Properties.Uid.eq(uid),
                            /*EnviTempDBEntityDao.Properties.DeviceMacAddress.eq(deviceMacAddress),*/
                            EmotionDBEntityDao.Properties.TempDay.eq(tempDay)).build().list();

            if ((tempDBEntities != null) && (tempDBEntities.size() > 0)) {//查找已有的数据
                EmotionDBEntity dbEntity = tempDBEntities.get(0);
                LogUtils.i(TAG, " tempDay " + DateTimeUtils.s_long_2_str(tempDay, DateTimeUtils.f_format));
                dbEntity.tempJsonData = makeJsonData(tempDay);
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
            } else {
                LogUtils.i(TAG," tempDay " + DateTimeUtils.s_long_2_str(tempDay, DateTimeUtils.f_format));
                EmotionDBEntity dbEntity = new EmotionDBEntity();
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.tempDay = tempDay;
                dbEntity.uid = uid;
                dbEntity.tempJsonData = makeJsonData(tempDay);
                JsonArrayUtils heartjson = new JsonArrayUtils(dbEntity.tempJsonData);
                if (heartjson != null && (heartjson.length() > 0)) {
                    dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
                }
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    /**
     * @param tempDay
     * @return
     */
    private String makeJsonData(long tempDay) {
        int gap = 60;
        if (tempInfo.getEmotionTimeGap() > 0){
            gap = tempInfo.getEmotionTimeGap();
        }
        JsonArrayUtils heatData = new JsonArrayUtils(new JSONArray());
        if (tempInfo != null && tempInfo.getEmotionList() != null) {
            for (int i = 0; i < tempInfo.getEmotionList().size(); i++) {
                Object object = tempInfo.getEmotionList().get(i);
                if (object instanceof Integer) {
                    int temp = (int) object;
                    if (temp != 0) {
                        long datetime = tempDay + i * gap * 60000;
                        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                        jsonUtils.put("temp", temp);
                        jsonUtils.put("datetime", datetime);
                        heatData.putJsonUtils(jsonUtils);
                        LogUtils.i(TAG, " TempDay " + DateTimeUtils.s_long_2_str(datetime, DateTimeUtils.f_format)
                                + " heat " + temp + " datetime " + datetime);
                    }
                }
            }
        }
        return heatData.toJsonArray().toString();
    }
}