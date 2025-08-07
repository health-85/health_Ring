package com.healthy.rvigor.dao.executor

import android.util.Log
import com.healthy.rvigor.MyApplication.Companion.instance
import com.healthy.rvigor.dao.entity.StepDBEntity
import com.healthy.rvigor.dao.util.AppDaoManager
import com.healthy.rvigor.dao.util.AppDaoManager.DBExecutor
import com.healthy.rvigor.greendao.gen.StepDBEntityDao
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.JsonArrayUtils
import com.healthy.rvigor.util.JsonUtils
import com.healthy.rvigor.util.NumberUtils
import com.healthy.rvigor.util.WatchBeanUtil
import com.sw.watches.bean.MotionInfo
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/14 9:11
 * @UpdateRemark:
 */
class InsertStepExecutor : DBExecutor {

    /**
     * 运动信息
     */
    private var motionInfo: MotionInfo? = null

    /**
     * 设备mac地址
     */
    private var macAddress = ""

    /**
     * 设备名称
     */
    private var deviceName = ""

    constructor(motionInfo: MotionInfo, macAddress: String, deviceName: String) {
        this.motionInfo = motionInfo
        this.macAddress = macAddress
        this.deviceName = deviceName
    }

    override fun Execute(dbContext: AppDaoManager.DBContext?) {
        if (motionInfo == null) {
            return
        }
        if ((motionInfo?.getTotalStep() ?: 0) <= 0) { //如果没有步数则不操作
            return
        }
        try {
            var userId = instance().appUserInfo.userInfo.id
            var height = instance().appUserInfo.userInfo.height
            if (height <= 0) height = 170
            val stepDay = DateTimeUtils.getDateTimeDatePart(
                DateTimeUtils
                    .getNonNullDate(DateTimeUtils.ConvertStrToDate(motionInfo!!.getMotionDate()))
            ).time

            if (dbContext == null) return

            /**
             * 查询已经有的数据
             */
            val stepDBEntities = dbContext.mReadableDaoMaster
                .newSession().queryBuilder(StepDBEntity::class.java)
                .where(
                    StepDBEntityDao.Properties.Uid.eq(userId),  /*StepDBEntityDao.Properties.DeviceMacAddress.eq(macAddress),*/
                    StepDBEntityDao.Properties.StepDay.eq(stepDay)
                ).build().list()
            if (stepDBEntities != null && stepDBEntities.size > 0) { //更新里面的步数数据
                val stepDBEntity = stepDBEntities[0]
                stepDBEntity.stepDay = stepDay
                val calorie = motionInfo!!.getCalorie() //卡路里这里的单位是千卡
                stepDBEntity.stepCalorie =
                    NumberUtils.fromStringToDouble(
                        NumberUtils.delNumberUnitStr(calorie, "千卡"),
                        0.0
                    )
                stepDBEntity.stepMileage =
                    NumberUtils.StepToDistanceM(
                        height.toFloat(),
                        motionInfo!!.getTotalStep().toLong()
                    )
                stepDBEntity.totalStep = motionInfo!!.getTotalStep().toLong()
                val jsonArrayUtils = JsonArrayUtils(JSONArray())
                var totalStep: Long = 0
                for (i in 1..24) {
//                    val saveStep: Long = 0
//                    if (saveJsonUtils.length() > 4) {
//                        JsonUtils curr = saveJsonUtils.getJsonObject(i - 1);
//                        saveStep = curr.getLong("step", 0);
//                    }
                    val step: Long = getStepTime(motionInfo!!.getStepData(), i)
                    val jsonUtils = JsonUtils(JSONObject())
                    jsonUtils.put("timestep", i - 1)
//                    if (step == 0L && saveStep > 0) {
//                        totalStep += saveStep
//                        jsonUtils.put("step", saveStep)
//                    } else {
//                        totalStep += step
                        jsonUtils.put("step", step)
//                    }
                    jsonArrayUtils.putJsonUtils(jsonUtils)
                }
                stepDBEntity.totalStep = totalStep
                motionInfo!!.setTotalStep(totalStep.toInt())
                motionInfo!!.setCalorie(
                    WatchBeanUtil.byteToCalorie(totalStep.toInt()).toString() + "千卡"
                )
                stepDBEntity.stepCalorie =
                    NumberUtils.fromStringToDouble(
                        NumberUtils.delNumberUnitStr(calorie, "千卡"),
                        0.0
                    )
                stepDBEntity.stepMileage =
                    NumberUtils.StepToDistanceM(
                        height.toFloat(),
                        motionInfo!!.getTotalStep().toLong()
                    )
//                LogUtils.i(TAG, new Gson().toJson(motionInfo));
                stepDBEntity.stepDataJsonArrayForTime = jsonArrayUtils.toJsonArray().toString()
                dbContext!!.mWritableDaoMaster.newSession().update(stepDBEntity)
            } else { //如果没有数据则直接插入数据
                val stepDBEntity = StepDBEntity()
                stepDBEntity.deviceName = deviceName
                stepDBEntity.deviceMacAddress = macAddress
                stepDBEntity.uid = userId
                stepDBEntity.stepDay = stepDay
                stepDBEntity.stepCalorie =
                    NumberUtils.fromStringToDouble(
                        NumberUtils.delNumberUnitStr(motionInfo!!.getCalorie(), "千卡"),
                        0.0
                    )
                stepDBEntity.stepMileage =
                    NumberUtils.StepToDistanceM(
                        height.toFloat(),
                        motionInfo!!.getTotalStep().toLong()
                    )
                stepDBEntity.totalStep = motionInfo!!.getTotalStep().toLong()
                val jsonArrayUtils = JsonArrayUtils(JSONArray())
                for (i in 1..24) {
                    val step: Long = getStepTime(motionInfo!!.getStepData(), i)
                    val jsonUtils = JsonUtils(JSONObject())
                    jsonUtils.put("timestep", i - 1)
                    jsonUtils.put("step", step)
                    jsonArrayUtils.putJsonUtils(jsonUtils)
                }
                stepDBEntity.stepDataJsonArrayForTime = jsonArrayUtils.toJsonArray().toString()
                dbContext!!.mWritableDaoMaster.newSession().insert(stepDBEntity)
            }
        } catch (ex: Exception) {
            Log.e(this.javaClass.simpleName, ex.message!!)
        }
    }

    private fun getStepTime(stepData: List<Int>, timeMode: Int): Long {
        val tick = timeMode * 60 / 15
        val start = (timeMode - 1) * 60 / 15 //起始点
        var step: Long = 0
        for (i in start until tick) {
            if (stepData.size > i) {
                step += stepData[i].toLong()
            }
        }
        return step
    }
}