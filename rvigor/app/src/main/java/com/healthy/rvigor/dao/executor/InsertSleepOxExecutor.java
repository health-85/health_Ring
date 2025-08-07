package com.healthy.rvigor.dao.executor;

import com.healthy.rvigor.dao.entity.SleepOxDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.SleepOxDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.healthy.rvigor.util.LogUtils;
import com.sw.watches.bean.SleepOxInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InsertSleepOxExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertSleepOxExecutor";

    /**
     * 用户id
     */
    private long uid = 0;

    /**
     * 是否已经同步到服务器了 0未同步  1已同步
     */
    private int isupLoadToServer = 0;

    /**
     * 设备名称
     */
    private String deviceName = "";

    /**
     * 设备地址
     */
    private String deviceMacAddress = "";

    private SleepOxInfo sleepOxInfo;


    public InsertSleepOxExecutor(long uid, String deviceName, String deviceMacAddress, SleepOxInfo info) {
        this.uid = uid;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.sleepOxInfo = info;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
        if (sleepOxInfo == null) {
            return;
        }
        Date tempDay = new Date(sleepOxInfo.getTime());
        if (tempDay == null) {//日期时间不能为空
            return;
        }
        long day = DateTimeUtils.getDateTimeDatePart(tempDay).getTime();
        try {
//            //查询已经有的数据
            List<SleepOxDBEntity> dbEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(SleepOxDBEntity.class)
                    .where(SleepOxDBEntityDao.Properties.Uid.eq(uid),
                            SleepOxDBEntityDao.Properties.Time.eq(day)).build().list();

            if ((dbEntities != null) && (dbEntities.size() > 0)) {//查找已有的数据
                SleepOxDBEntity dbEntity = dbEntities.get(0);
                dbEntity.uid = uid;
                dbEntity.time = day;
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.json = makeJsonData();
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
                LogUtils.i(TAG, " json " + dbEntity.json);
            } else {
                SleepOxDBEntity dbEntity = new SleepOxDBEntity();
                dbEntity.uid = uid;
                dbEntity.time = day;
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.json = makeJsonData();
                JsonArrayUtils jsonUtils = new JsonArrayUtils(dbEntity.json);
                if (jsonUtils.length() > 0) {
                    dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
                }
                LogUtils.i(TAG, " json " + dbEntity.json);
            }
        } catch (Exception ex) {
            LogUtils.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    private String makeJsonData() {
        JsonArrayUtils jsonData = new JsonArrayUtils(new JSONArray());
        if (sleepOxInfo != null && sleepOxInfo.getList() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(sleepOxInfo.getTime());
            long datetime = calendar.getTimeInMillis();
            for (int i = 0; i < sleepOxInfo.getList().size(); i++) {
                Object object = sleepOxInfo.getList().get(i);
                if (object instanceof Integer) {
                    Integer temp = (Integer) object;
                    if (temp > 0) {
                        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                        jsonUtils.put("sleepOx", temp);
                        jsonUtils.put("datetime", datetime);
                        jsonData.putJsonUtils(jsonUtils);
                        LogUtils.i(TAG, " Day " + DateTimeUtils.s_long_2_str(datetime, DateTimeUtils.f_format)
                                + " heat " + temp + " datetime " + datetime + " size " + sleepOxInfo.getList().size());
                    }
                }
                calendar.add(Calendar.MINUTE, 1);
                datetime = calendar.getTimeInMillis();
                calendar.setTimeInMillis(datetime);
            }
        }
        return jsonData.toJsonArray().toString();
    }
}
