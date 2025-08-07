package com.healthy.rvigor.dao.executor;

import com.healthy.rvigor.dao.entity.BreatheDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.BreatheDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.healthy.rvigor.util.LogUtils;
import com.sw.watches.bean.BreatheInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class InsertBreatheExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertBreatheExecutor";

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
     * 体温数据
     */
    public BreatheInfo breatheInfo = null;

    public InsertBreatheExecutor(long uid, String deviceName, String deviceMacAddress, BreatheInfo info) {
        this.uid = uid;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.breatheInfo = info;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
//        LogUtils.i(TAG, new Gson().toJson(tireInfo));
        if (breatheInfo == null) {
            return;
        }
//        if (StringUtils.StringIsEmptyOrNull(deviceMacAddress)) {//mac地址不能为空
//            return;
//        }
        Date tempDay = DateTimeUtils.convertStrToDateForThisProject(breatheInfo.getDate());
        if (tempDay == null) {//日期时间不能为空
            return;
        }
        long day = DateTimeUtils.getDateTimeDatePart(tempDay).getTime();
//        LogUtils.i(TAG, " tempDay " + DateTimeUtils.s_long_2_str(tireDay, DateTimeUtils.f_format));
        try {
            //查询已经有的数据
            List<BreatheDBEntity> breatheDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(BreatheDBEntity.class)
                    .where(BreatheDBEntityDao.Properties.Uid.eq(uid),
                            /*BreatheDBEntityDao.Properties.DeviceMacAddress.eq(deviceMacAddress),*/
                            BreatheDBEntityDao.Properties.Day.eq(day)).build().list();

            if ((breatheDBEntities != null) && (breatheDBEntities.size() > 0)) {//查找已有的数据
                BreatheDBEntity dbEntity = breatheDBEntities.get(0);
                dbEntity.hypopnea = breatheInfo.getHypopnea();
                dbEntity.blockLen = breatheInfo.getBlockLen();
                dbEntity.chaosIndex = breatheInfo.getChaosIndex();
                dbEntity.pauseCount = breatheInfo.getPauseCount();
                dbEntity.breatheJsonData = makeJsonData(day);
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
//                LogUtils.i(TAG, " breatheInfo " + new Gson().toJson(breatheInfo));
//                LogUtils.i(TAG, " breatheJsonData " + dbEntity.breatheJsonData);
            } else {
                BreatheDBEntity dbEntity = new BreatheDBEntity();
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.day = day;
                dbEntity.uid = uid;
                dbEntity.hypopnea = breatheInfo.getHypopnea();
                dbEntity.blockLen = breatheInfo.getBlockLen();
                dbEntity.chaosIndex = breatheInfo.getChaosIndex();
                dbEntity.pauseCount = breatheInfo.getPauseCount();
                dbEntity.breatheJsonData = makeJsonData(day);
                JsonArrayUtils breatheJson = new JsonArrayUtils(dbEntity.breatheJsonData);
                if (breatheJson != null && (breatheJson.length() > 0)) {
                    dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
                }
//                LogUtils.i(TAG, " breatheInfo " + new Gson().toJson(breatheInfo));
//                LogUtils.i(TAG, " breatheJsonData " + dbEntity.breatheJsonData);
            }
        } catch (Exception ex) {
            LogUtils.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    /**
     * @param day
     * @return
     */
    private String makeJsonData(long day) {
        int gap = 15;
        if (breatheInfo.getBreathTimeGap() > 0){
            gap = breatheInfo.getBreathTimeGap();
        }
        JsonArrayUtils data = new JsonArrayUtils(new JSONArray());
        if (breatheInfo != null && breatheInfo.getList() != null) {
            for (int i = 0; i < breatheInfo.getList().size(); i++) {
                Object object = breatheInfo.getList().get(i);
                if (object instanceof Integer) {
                    Integer temp = (Integer) object;
                    if (temp > 0) {
                        long datetime = day + i * gap * 60000;
                        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                        jsonUtils.put("data", temp);
                        jsonUtils.put("datetime", datetime);
                        data.putJsonUtils(jsonUtils);
//                        LogUtils.i(TAG, " Day " + DateTimeUtils.s_long_2_str(datetime, DateTimeUtils.f_format)
//                                + " breathe " + temp + " datetime " + datetime);
                    }
                }
            }
        }
        return data.toJsonArray().toString();
    }
}