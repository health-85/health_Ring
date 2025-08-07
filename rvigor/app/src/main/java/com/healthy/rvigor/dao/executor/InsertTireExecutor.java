package com.healthy.rvigor.dao.executor;

import com.healthy.rvigor.dao.entity.TireDBEntity;
import com.healthy.rvigor.dao.util.AppDaoManager;
import com.healthy.rvigor.greendao.gen.TireDBEntityDao;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.JsonArrayUtils;
import com.healthy.rvigor.util.JsonUtils;
import com.healthy.rvigor.util.LogUtils;
import com.sw.watches.bean.TireInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class InsertTireExecutor extends AppDaoManager.DBExecutor {

    private static final String TAG = "InsertTireExecutor";

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
    public TireInfo tireInfo = null;

    public InsertTireExecutor(long uid, String deviceName, String deviceMacAddress, TireInfo info) {
        this.uid = uid;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.tireInfo = info;
    }

    @Override
    public void Execute(AppDaoManager.DBContext dbContext) {
//        LogUtils.i(TAG, new Gson().toJson(tireInfo));
        if (tireInfo == null) {
            return;
        }
//        if (StringUtils.StringIsEmptyOrNull(deviceMacAddress)) {//mac地址不能为空
//            return;
//        }
        Date tempDay = DateTimeUtils.convertStrToDateForThisProject(tireInfo.getDate());
        if (tempDay == null) {//日期时间不能为空
            return;
        }
        long tireDay = DateTimeUtils.getDateTimeDatePart(tempDay).getTime();
//        LogUtils.i(TAG, " tempDay " + DateTimeUtils.s_long_2_str(tireDay, DateTimeUtils.f_format));
        try {
            //查询已经有的数据
            List<TireDBEntity> tireDBEntities = dbContext.mReadableDaoMaster
                    .newSession().queryBuilder(TireDBEntity.class)
                    .where(TireDBEntityDao.Properties.Uid.eq(uid),
                            /*TireDBEntityDao.Properties.DeviceMacAddress.eq(deviceMacAddress),*/
                            TireDBEntityDao.Properties.TireDay.eq(tireDay)).build().list();

            if ((tireDBEntities != null) && (tireDBEntities.size() > 0)) {//查找已有的数据
                TireDBEntity dbEntity = tireDBEntities.get(0);
                dbEntity.tireJsonData = makeJsonData(tireDay);
                dbContext.mWritableDaoMaster.newSession().update(dbEntity);
//                LogUtils.i(TAG, " tireJsonData " + dbEntity.tireJsonData);
            } else {
                TireDBEntity dbEntity = new TireDBEntity();
                dbEntity.deviceName = deviceName;
                dbEntity.deviceMacAddress = deviceMacAddress;
                dbEntity.tireDay = tireDay;
                dbEntity.uid = uid;
                dbEntity.tireJsonData = makeJsonData(tireDay);
                JsonArrayUtils tireJson = new JsonArrayUtils(dbEntity.tireJsonData);
                if (tireJson != null && (tireJson.length() > 0)) {
                    dbContext.mWritableDaoMaster.newSession().insert(dbEntity);
                }
//                LogUtils.i(TAG, " tireJsonData " + dbEntity.tireJsonData);
            }
        } catch (Exception ex) {
            LogUtils.e(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    /**
     * @param tireDay
     * @return
     */
    private String makeJsonData(long tireDay) {
        int gap = 60;
        if (tireInfo.getTireTimeGap() > 0){
            gap = tireInfo.getTireTimeGap();
        }
        JsonArrayUtils tireData = new JsonArrayUtils(new JSONArray());
        if (tireInfo != null && tireInfo.getList() != null) {
            for (int i = 0; i < tireInfo.getList().size(); i++) {
                Object object = tireInfo.getList().get(i);
                if (object instanceof Integer) {
                    Integer temp = (Integer) object;
                    if (temp > 0) {
                        long datetime = tireDay + i * gap * 60000;
                        JsonUtils jsonUtils = new JsonUtils(new JSONObject());
                        jsonUtils.put("tire", temp);
                        jsonUtils.put("datetime", datetime);
                        tireData.putJsonUtils(jsonUtils);
//                        LogUtils.i(TAG, " tireDay " + DateTimeUtils.s_long_2_str(datetime, DateTimeUtils.f_format)
//                                + " heat " + temp + " datetime " + datetime + " size " + tireInfo.getList().size());
                    }
                }
            }
        }
        return tireData.toJsonArray().toString();
    }
}