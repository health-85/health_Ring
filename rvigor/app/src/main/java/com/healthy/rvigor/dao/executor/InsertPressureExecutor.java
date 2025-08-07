package com.healthy.rvigor.dao.executor;

import android.util.Log;

import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.dao.entity.PressureDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.PressureDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.sw.watches.bean.PressureInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class InsertPressureExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertPressureExecutor";

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
     * 体温数据
     */
    public PressureInfo pressureInfo = null;

    public InsertPressureExecutor(String deviceName, String deviceMacAddress, PressureInfo info) {
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.pressureInfo = info;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
//        LogUtils.i(TAG, new Gson().toJson(tempInfo));
        if (pressureInfo == null) {
            return;
        }
//        if (StringUtils.StringIsEmptyOrNull(deviceMacAddress)) {//mac地址不能为空
//            return;
//        }
        long uid = MyApplication.Companion.instance().getAppUserInfo().getUserInfo().id;
        Date date = DateTimeUtils.convertStrToDateForThisProject(pressureInfo.getPressureDate());
        if (date == null) {//日期时间不能为空
            return;
        }
        long pressureDay = DateTimeUtils.getDateTimeDatePart(date).getTime();
        try {
            //查询已经有的数据
            List<PressureDBEntity> pressureDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(PressureDBEntity.class)
                    .where(PressureDBEntityDao.Properties.Uid.eq(uid),
                            /*PressureDBEntityDao.Properties.DeviceMacAddress.eq(deviceMacAddress),*/
                            PressureDBEntityDao.Properties.PressureDay.eq(pressureDay)).build().list();

            if ((pressureDBEntities != null) && (pressureDBEntities.size() > 0)) {//查找已有的数据
                PressureDBEntity dbEntity = pressureDBEntities.get(0);
//                LogUtils.i(TAG, " tempDay " + DateTimeUtils.s_long_2_str(pressureDay, DateTimeUtils.day_format));
                dbEntity.pressureJsonData = makeJsonData(pressureDay);
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
            } else {
//                LogUtils.i(TAG," tempDay " + DateTimeUtils.s_long_2_str(pressureDay, DateTimeUtils.day_format));
                PressureDBEntity dbEntity = new PressureDBEntity();
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.pressureDay = pressureDay;
                dbEntity.uid = uid;
                dbEntity.pressureJsonData = makeJsonData(pressureDay);
                JsonArrayUtils pressureJson = new JsonArrayUtils(dbEntity.pressureJsonData);
                if (pressureJson != null && (pressureJson.length() > 0)) {
                    dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
                }
            }
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    /**
     * @param pressureDay
     * @return
     */
    private String makeJsonData(long pressureDay) {
        int gap = 60;
        if (pressureInfo.getPressureTimeGap() > 0){
            gap = pressureInfo.getPressureTimeGap();
        }
        JsonArrayUtils pressureData = new JsonArrayUtils(new JSONArray());
        if (pressureInfo != null && pressureInfo.getPressureList() != null) {
            for (int i = 0; i < pressureInfo.getPressureList().size(); i++) {
                Object object = pressureInfo.getPressureList().get(i);
                if (object instanceof Integer) {
                    Integer temp = (Integer) object;
                    if (temp > 0) {
                        long datetime = pressureDay + i * gap * 60000;
                        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                        jsonUtils.put("pressure", temp);
                        jsonUtils.put("datetime", datetime);
                        pressureData.putJsonUtils(jsonUtils);
//                        LogUtils.i(TAG, " pressureDay " + DateTimeUtils.s_long_2_str(datetime, DateTimeUtils.f_format)
//                                + " pressure " + temp + " datetime " + datetime + " size " + pressureInfo.getPressureList().size());
                    }
                }
            }
        }
        return pressureData.toJsonArray().toString();
    }
}