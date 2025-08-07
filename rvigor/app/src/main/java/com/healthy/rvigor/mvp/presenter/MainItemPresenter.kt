package com.healthy.rvigor.mvp.presenter

import android.graphics.Color
import android.text.TextUtils
import com.google.gson.Gson
import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.MyApplication.Companion.instance
import com.healthy.rvigor.bean.ChartBean
import com.healthy.rvigor.bean.HeartDataInfo
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.bean.SleepBarBean
import com.healthy.rvigor.bean.SleepDayBean
import com.healthy.rvigor.bean.SleepItem
import com.healthy.rvigor.bean.SleepLenItem
import com.healthy.rvigor.dao.entity.AbnormalRateDBEntity
import com.healthy.rvigor.dao.entity.EmotionDBEntity
import com.healthy.rvigor.dao.entity.HeartRateDBEntity
import com.healthy.rvigor.dao.entity.PressureDBEntity
import com.healthy.rvigor.dao.entity.SiestaDBEntity
import com.healthy.rvigor.dao.entity.SleepDBEntity
import com.healthy.rvigor.dao.entity.SpoDBEntity
import com.healthy.rvigor.dao.entity.StepDBEntity
import com.healthy.rvigor.dao.entity.TireDBEntity
import com.healthy.rvigor.dao.executor.QueryAbnormalHeartExecutor
import com.healthy.rvigor.dao.executor.QueryEmotionExecutor
import com.healthy.rvigor.dao.executor.QueryHeartRateExecutor
import com.healthy.rvigor.dao.executor.QueryPressureExecutor
import com.healthy.rvigor.dao.executor.QuerySiestaExecutor
import com.healthy.rvigor.dao.executor.QuerySleepExecutor
import com.healthy.rvigor.dao.executor.QuerySpoInfoExecutor
import com.healthy.rvigor.dao.executor.QueryStepExecutor
import com.healthy.rvigor.dao.executor.QueryTireExecutor
import com.healthy.rvigor.dao.util.AppDaoManager
import com.healthy.rvigor.dao.util.AppDaoManager.DBExecutor.IResult
import com.healthy.rvigor.mvp.contract.IMainItemContract
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.JsonArrayUtils
import com.healthy.rvigor.util.JsonUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.NumberUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.util.ValidRule
import com.healthy.rvigor.view.SpecDateSelectedView
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/20 8:49
 * @UpdateRemark:
 */
class MainItemPresenter : BasePresenterImpl<IMainItemContract.View>(),
    IMainItemContract.Presenter {

    companion object {
        const val TAG = "MainItemPresenter"
    }

    //静息心率
    private var mAverageRate = 0

    //最小心率
    private var mMinRate = 0

    //最大心率
    private var mMaxRate = 0

    //昨天表现
    override fun getLastDayData(day: Long) {
        if (day <= 0L) return

        val endTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val startTime = DateTimeUtils.AddDay(Date(endTime), -1).time

        LogUtils.i(
            " $TAG getLastDayData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        getSleepData(startTime, true)

        getMotionData(startTime, true)

        getHeartData(startTime, true)

        getOxData(startTime, true)

        getMindData(startTime, true)
    }

    override fun getSleepData(day: Long, isLastDay: Boolean) {

        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            (" loadData sleep startTime == " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            )) + " endTime == " + DateTimeUtils.s_long_2_str(
                endTime,
                DateTimeUtils.f_format
            )
        )

        var siestaDBEntity: SiestaDBEntity? = null
        val siestaExecutor = QuerySiestaExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    if (result == null) return
                    var siestaDBEntityList: List<SiestaDBEntity> = result as List<SiestaDBEntity>
                    if (result.isNullOrEmpty()) return
                    siestaDBEntity = siestaDBEntityList[0]
                }

                override fun OnError(ex: java.lang.Exception?) {
                    ex?.printStackTrace()
                }
            })
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(siestaExecutor)

        val sleepExecutor = QuerySleepExecutor(startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    var sleepDBEntity: SleepDBEntity? = null
                    if (result != null) {
                        val sleepDBEntityList: List<SleepDBEntity> = result as List<SleepDBEntity>
                        if (!sleepDBEntityList.isNullOrEmpty()) {
                            sleepDBEntity = sleepDBEntityList[0]
                        }
                    }
                    if (sleepDBEntity == null && siestaDBEntity == null) {
                        if (isLastDay) {
                            view.onLastSleepScore(0)
                        } else {
                            view.onSleepData(null)
                        }
                        return
                    }
                    val bean: SleepBarBean = getDayBean(sleepDBEntity, siestaDBEntity)
                    if (isLastDay) {
                        view.onLastSleepScore(bean.totalScore)
                    } else {
                        view.onSleepData(bean)
                    }
                    //查询睡眠心率
                    querySleepHeartData(day, bean)
                    //查询睡眠血氧
                    querySleepOxData(day, bean)
                }

                override fun OnError(ex: java.lang.Exception?) {
                    ex?.printStackTrace()
                }
            })
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(sleepExecutor)
    }

    override fun getMotionData(day: Long, isLastDay: Boolean) {

        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " $TAG getMotionData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val stepExecutor = QueryStepExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    val stepDBEntities: List<StepDBEntity> = result as List<StepDBEntity>
                    if (stepDBEntities.isEmpty()) {
                        if (isLastDay) {
                            view.onLastMotionScore(0f)
                        } else {
                            view.onMotionData(null, null, 0, 0, 0)
                        }
                    } else {
                        var min = 0
                        var max = 0
                        var totalStep = 0f
                        var lastItem = MainViewItem()
                        val itemList = mutableListOf<MainViewItem>()
                        var stepDBEntity = stepDBEntities[0]
                        val jsonArrayUtils = JsonArrayUtils(stepDBEntity.stepDataJsonArrayForTime)
                        for (i in 0 until jsonArrayUtils.length()) {
                            val curr = jsonArrayUtils.getJsonObject(i)
                            var step = curr.getLong("step", 0)
                            val showTime = i.toString() + ":00-" + (i + 1) + ":00"
                            step = ValidRule.getInstance().getValidStep(step)
                            val item = MainViewItem()
                            if (step > 0) {
                                totalStep += step
                                lastItem = item
                                max = if (max == 0 || max < step) step.toInt() else max
                                min = if (min == 0 || min > step) step.toInt() else min
                            } else {
                                step = MainViewItem.EMPTY.toLong()
                            }
                            item.data = step.toFloat()
                            item.showTimeString = showTime
                            itemList.add(item)
                        }

                        val targetStep: Int =
                            SPUtil.getData(
                                MyApplication.instance(),
                                SpConfig.TARGET_STEP,
                                Constants.DEFAULT_TARGET_STEP
                            ) as Int
                        var motionScore = totalStep / targetStep.toFloat() * 100
                        if (motionScore > 100) motionScore = 100f

                        if (isLastDay) {
                            view.onLastMotionScore(motionScore)
                        } else {
                            view.onMotionData(itemList, lastItem, min, max, totalStep.toInt())
                        }
                    }
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }
            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(stepExecutor)
    }

    override fun getMindData(day: Long, isLastDay: Boolean) {
        //疲劳数据
        getTireData(day, isLastDay)
        //压力数据
        getPressureData(day, isLastDay)
        //情绪数据
        getEmotionData(day)
    }

    override fun getHeartData(day: Long, isLastDay: Boolean) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " $TAG getHeartData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val heartExecutor = QueryHeartRateExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {

                    val heartDBEntities: List<HeartRateDBEntity> = result as List<HeartRateDBEntity>
                    if (heartDBEntities.isEmpty()) {
                        if (isLastDay) {
                            view.onLastHeartScore(0f)
                        } else {
                            view.onHeartData(
                                arrayListOf(),
                                null,
                                0,
                                0,
                                0
                            )
                        }
                        return
                    }

                    var index = 0
                    var max = 0
                    var min = 0
                    var average = 0

                    var isOneMin = true
                    if (heartDBEntities.isNotEmpty() && !TextUtils.isEmpty(heartDBEntities[0].heartJsonData)) {
                        val jsonArrayUtils = JsonArrayUtils(heartDBEntities[0].heartJsonData)
                        isOneMin = jsonArrayUtils.getJsonObject(0).getBoolean("oneMin", true)
                    }

                    LogUtils.i(" queryLocalHeartDayData isOneMin $isOneMin")

                    var lastItem = MainViewItem()
                    val itemList = mutableListOf<MainViewItem>()

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = startTime

                    for (i in heartDBEntities.size - 1 downTo 0) {
                        val dbEntity: HeartRateDBEntity = heartDBEntities[i]
                        if (isOneMin) {
                            if (!TextUtils.isEmpty(dbEntity.heartJsonData)) {
                                val jsonArrayUtils = JsonArrayUtils(dbEntity.heartJsonData)
                                if (jsonArrayUtils != null && jsonArrayUtils.length() > 0) {
                                    var i = 0
                                    var j = 0
                                    val calendar = Calendar.getInstance()
                                    var startTime =
                                        jsonArrayUtils.getJsonObject(0).getLong("datetime", 0)
                                    var endTime =
                                        jsonArrayUtils.getJsonObject(jsonArrayUtils.length() - 1)
                                            .getLong("datetime", 0)
                                    calendar.timeInMillis = startTime

                                    val dataCalendar = Calendar.getInstance()
                                    dataCalendar.timeInMillis = endTime
                                    dataCalendar.set(Calendar.SECOND, 0)
                                    dataCalendar.set(Calendar.MILLISECOND, 0)
                                    endTime = dataCalendar.timeInMillis

                                    val list: MutableList<Int> = ArrayList()
                                    while (startTime <= endTime && endTime > 0) {
                                        val curr = jsonArrayUtils.getJsonObject(j)
                                        val heart = curr.getInt("rate", 0)
                                        var time = curr.getLong("datetime", 0)
                                        dataCalendar.timeInMillis = time
                                        dataCalendar.set(Calendar.SECOND, 0)
                                        dataCalendar.set(Calendar.MILLISECOND, 0)
                                        time = dataCalendar.timeInMillis

                                        if (heart > 0) {
                                            index++
                                            average += heart
                                            min =
                                                if (min == 0 || min > heart) heart else min
                                            max =
                                                if (max == 0 || max < heart) heart else max
                                        }
                                        if (time == startTime) {
                                            list.add(heart)
                                            j++
                                        }
                                        LogUtils.i(
                                            " heart $heart  datetime " + DateTimeUtils.s_long_2_str(
                                                time,
                                                DateTimeUtils.f_format
                                            ) + " time " +
                                                    DateTimeUtils.s_long_2_str(
                                                        calendar.timeInMillis,
                                                        DateTimeUtils.f_format
                                                    ) + " endTime " +
                                                    DateTimeUtils.s_long_2_str(
                                                        endTime,
                                                        DateTimeUtils.f_format
                                                    )
                                        )
                                        if ((i % 5 == 4 || time == endTime) && list.size > 0) {
                                            list.sort()
                                            val rate = list[list.size / 2]
                                            list.clear()
                                            if (rate > 0) {
                                                val item = MainViewItem()
                                                item.data = rate.toFloat()
                                                item.time = calendar.timeInMillis
                                                item.isOneMin = isOneMin
                                                itemList.add(item)
                                                lastItem = item
                                                LogUtils.i(
                                                    " rate $rate datetime " + DateTimeUtils.s_long_2_str(
                                                        time,
                                                        DateTimeUtils.f_format
                                                    ) + " time " +
                                                            DateTimeUtils.s_long_2_str(
                                                                calendar.timeInMillis,
                                                                DateTimeUtils.f_format
                                                            )
                                                )
                                            }
                                        }
                                        calendar.add(Calendar.MINUTE, 1)
                                        startTime = calendar.timeInMillis
                                        i++
                                    }
                                }
                            }
                        } else {
                            if (!TextUtils.isEmpty(dbEntity.heartJsonData)) {
                                val jsonArrayUtils = JsonArrayUtils(dbEntity.heartJsonData)
                                if (jsonArrayUtils != null && jsonArrayUtils.length() > 0) {
                                    for (i in 0 until jsonArrayUtils.length()) {
                                        val curr: JsonUtils = jsonArrayUtils.getJsonObject(i)
                                        val heart = curr.getInt("rate", 0)
                                        val time = curr.getLong("datetime", 0)
                                        if (heart > 0) {
                                            index++
                                            average += heart
                                            min =
                                                if (min == 0 || min > heart) heart else min
                                            max =
                                                if (max == 0 || max < heart) heart else max
                                            val item = MainViewItem()
                                            item.data = heart.toFloat()
                                            item.time = time
                                            item.isOneMin = isOneMin
                                            itemList.add(item)
                                            lastItem = item
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (index == 0) index = 1
                    average /= index
                    if (isLastDay) {
                        getAbnormalHeartData(day, index)
                    } else {
                        view.onHeartData(itemList, lastItem, min, max, average)
                    }
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }
            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(heartExecutor)
    }

    //查询本地异常心率
    override fun getAbnormalHeartData(day: Long, normalHeartCount: Int) {
        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " $TAG getAbnormalHeartData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val sleepExecutor =
            QueryAbnormalHeartExecutor(
                startTime,
                endTime,
                object : IResult {
                    override fun OnSucceed(result: Any) {
                        try {
                            val dBEntityList = result as List<AbnormalRateDBEntity>
                            if (dBEntityList == null || dBEntityList.isEmpty()) {
                                view.onLastHeartScore(100f)
                                return
                            }

                            var max = 0
                            var min = 0

                            var times = 0
                            var dayTimes = 0
                            var abnormalDay = 0
                            var dataList = mutableListOf<MainViewItem>()

                            var index = 0
                            var average = 0f
                            var heartList = mutableListOf<HeartDataInfo>()

                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = startTime
                            var tempTime: Long = calendar.timeInMillis

                            var userId = instance().appUserInfo.userInfo.id
                            var watchBase = instance().bleUtils.getConnectionWatch()

                            while (tempTime < endTime) {
                                dayTimes = 0
                                val item = MainViewItem()
                                item.time = tempTime
                                var dbEntity =
                                    getAbnormalRateDTBEntityByTime(dBEntityList, tempTime)
                                if (dbEntity != null && !TextUtils.isEmpty(dbEntity.heartJsonData)) {
                                    val jsonArrayUtils = JsonArrayUtils(dbEntity.heartJsonData)
                                    if (jsonArrayUtils != null && jsonArrayUtils.length() > 0) {
                                        for (i in 0 until jsonArrayUtils.length()) {
                                            times++
                                            dayTimes++
                                            val curr: JsonUtils =
                                                jsonArrayUtils.getJsonObject(i)
                                            val heart = curr.getInt("heart", 0)
                                            val timeS = curr.getString("time")
                                            if (heart > 0) {
                                                index++
                                                average += heart.toFloat()
                                                var info = HeartDataInfo(
                                                    0,
                                                    userId,
                                                    "",
                                                    timeS,
                                                    timeS,
                                                    watchBase?.deviceName ?: "",
                                                    heart
                                                )
                                                heartList.add(info)
                                            }
                                        }
                                        max = if (max == 0 || dayTimes > max) dayTimes else max
                                        min = if (min == 0 || dayTimes < min) dayTimes else min
                                        abnormalDay++
                                    }
                                }
                                item.data = dayTimes.toFloat()
                                dataList.add(item)
                                tempTime = DateTimeUtils.AddDay(Date(tempTime), 1).time
                            }

                            if (index == 0) index = 1
                            average /= index

                            heartList.sort()
                            val heartScore = (1 - index / (normalHeartCount + index).toFloat()) * 100f
                            LogUtils.i(" getAbnormalHeartData index $index normalHeartCount $normalHeartCount " +
                                    "heartScore $heartScore")
                            view.onLastHeartScore(heartScore)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun OnError(ex: java.lang.Exception) {
                        ex.printStackTrace()
                    }
                })
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(sleepExecutor)
    }

    override fun getOxData(day: Long, isLastDay: Boolean) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " $TAG getMotionData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val executor = QuerySpoInfoExecutor(
            startTime,
            endTime,
            object : IResult {
                override fun OnSucceed(result: Any?) {
                    val dBEntities: List<SpoDBEntity> = result as List<SpoDBEntity>
                    if (dBEntities.isNullOrEmpty()) {
                        if (isLastDay) {
                            view.onLastOxScore(0f)
                        } else {
                            view.onOxData(null, null, 0, 0, 0)
                        }
                    } else {
                        var index = 0
                        var average = 0f
                        var maxOx = 0f
                        var minOx = 0f
                        var high94Count = 0

                        val heatLenMap = mutableMapOf<Int, Int>()
                        val heatMap = mutableMapOf<Int, MainViewItem>()
                        for (i in dBEntities.indices) {
                            val entity: SpoDBEntity = dBEntities[i]
                            val jsonArrayUtils = JsonArrayUtils(entity.spoJsonData)
                            if (jsonArrayUtils.length() > 0) {
                                val calendar = Calendar.getInstance()
                                for (j in 0 until jsonArrayUtils.length()) {
                                    val curr: JsonUtils = jsonArrayUtils.getJsonObject(j)
                                    val spo = NumberUtils.fromStringToFloat(
                                        curr.getString("spo"),
                                        MainViewItem.EMPTY.toFloat()
                                    )
                                    val dateTime = curr.getLong("datetime", 0L)
                                    val isSleepOx = curr.getBoolean("sleepOx", false)
                                    calendar.timeInMillis = dateTime
                                    val hour = calendar[Calendar.HOUR_OF_DAY]
                                    if (spo > 0) {
                                        index++
                                        average += spo.toFloat()
                                        if (spo >= 94) high94Count++
                                        minOx = if (minOx == 0f || minOx > spo) spo else minOx
                                        maxOx = if (maxOx == 0f || maxOx < spo) spo else maxOx
                                        if (heatMap.containsKey(hour)) {
                                            val bean: MainViewItem? = heatMap[hour]
                                            bean?.data = bean?.data?.plus(spo)!!
                                            heatMap[hour] = bean
                                            if (heatLenMap.containsKey(hour)) {
                                                heatLenMap[hour] = heatLenMap[hour]!! + 1
                                            }
                                        } else {
                                            val item = MainViewItem()
                                            item.time = dateTime
                                            item.data = spo.toFloat()
                                            heatMap[hour] = item
                                            heatLenMap[hour] = 1
                                        }
                                    }
                                }
                            }
                        }
                        val integerSet: Set<Int> = heatMap.keys
                        for (key in integerSet) {
                            val bean: MainViewItem = heatMap[key] ?: continue
                            if (heatLenMap.containsKey(key)) {
                                val len = heatLenMap[key]!!
                                bean.data = bean.data / len
                            }
                            heatMap[key] = bean
                        }
                        if (index == 0) index = 1
                        average /= index.toFloat()

                        val viewList = mutableListOf<MainViewItem>()
                        var lastItem: MainViewItem? = null
                        for (i in 0..23) {
                            if (heatMap.containsKey(i) && heatMap[i] != null) {
                                val bean: MainViewItem? = heatMap[i]
                                lastItem = bean
                                if (bean != null) {
                                    viewList.add(bean)
                                }
                            } else {
                                val bean = MainViewItem()
                                viewList.add(bean)
                            }
                        }
                        if (isLastDay) {
                            var oxSore = high94Count / index.toFloat() * 100
                            view.onLastOxScore(oxSore)
                        } else {
                            view.onOxData(viewList, lastItem, 0, 0, average.toInt())
                        }
                    }
                }

                override fun OnError(ex: java.lang.Exception?) {
                    ex?.printStackTrace()
                }

            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }

    private fun getTireData(day: Long, isLastDay: Boolean) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " $TAG getTireData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val executor = QueryTireExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    val dBEntities: List<TireDBEntity> = result as List<TireDBEntity>
                    if (dBEntities.isNullOrEmpty()) {
                        if (isLastDay) {
                            view.onLastAverageTire(0f)
                        } else {
                            view.onTireData(null, 0f)
                        }
                    } else {
                        var index = 0
                        var average = 0f
                        val heatLenMap = mutableMapOf<Int, Int>()
                        val heatMap = mutableMapOf<Int, MainViewItem>()
                        for (i in dBEntities.indices) {
                            val entity: TireDBEntity = dBEntities[i]
                            val jsonArrayUtils = JsonArrayUtils(entity.tireJsonData)
                            if (jsonArrayUtils.length() > 0) {
                                val calendar = Calendar.getInstance()
                                for (j in 0 until jsonArrayUtils.length()) {
                                    val curr: JsonUtils = jsonArrayUtils.getJsonObject(j)
                                    val temp: Int = curr.getInt("tire", MainViewItem.EMPTY)
                                    val time: Long = curr.getLong("datetime", 0)

                                    LogUtils.i(
                                        " getTireData tire $temp time" +
                                                " ${
                                                    DateTimeUtils.s_long_2_str(
                                                        time,
                                                        DateTimeUtils.f_format
                                                    )
                                                } "
                                    )

                                    calendar.timeInMillis = time
                                    val hour = calendar[Calendar.HOUR_OF_DAY]
                                    if (temp > 0 && temp != MainViewItem.EMPTY) {
                                        index++
                                        average += temp.toFloat()
                                        if (heatMap.containsKey(hour)) {
                                            val bean: MainViewItem? = heatMap[hour]
                                            bean?.data = bean?.data?.plus(temp)!!
                                            heatMap[hour] = bean
                                            if (heatLenMap.containsKey(hour)) {
                                                heatLenMap[hour] = heatLenMap[hour]!! + 1
                                            }
                                        } else {
                                            val item = MainViewItem()
                                            item.time = time
                                            item.data = temp.toFloat()
                                            heatMap[hour] = item
                                            heatLenMap[hour] = 1
                                        }
                                    }
                                }
                            }
                        }
                        val integerSet: Set<Int> = heatMap.keys
                        for (key in integerSet) {
                            val bean: MainViewItem = heatMap[key] ?: continue
                            if (heatLenMap.containsKey(key)) {
                                var len = heatLenMap[key]!!
                                if (len == 0) len = 1
                                bean.data = bean.data / len
                            }
                            heatMap[key] = bean
                        }
                        if (index == 0) index = 1
                        average /= index.toFloat()

                        var lastItem: MainViewItem? = null
                        for (i in 0..23) {
                            if (heatMap.containsKey(i)) {
                                val bean: MainViewItem? = heatMap[i]
                                if (bean != null) {
                                    lastItem = bean
                                }
                            }
                        }
                        if (isLastDay) {
                            view.onLastAverageTire(average)
                        } else {
                            view.onTireData(lastItem, average)
                        }
                    }
                }

                override fun OnError(ex: java.lang.Exception?) {
                    ex?.printStackTrace()
                }

            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }

    private fun getPressureData(day: Long, isLastDay: Boolean) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " $TAG getPressureData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val executor = QueryPressureExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    val dBEntities: List<PressureDBEntity> = result as List<PressureDBEntity>
                    if (dBEntities.isNullOrEmpty()) {
                        if (isLastDay) {
                            view.onLastAveragePressure(0f)
                        } else {
                            view.onPressureData(null, 0f)
                        }
                    } else {
                        var index = 0
                        var average = 0f
                        val heatLenMap = mutableMapOf<Int, Int>()
                        val heatMap = mutableMapOf<Int, MainViewItem>()
                        for (i in dBEntities.indices) {
                            val entity: PressureDBEntity = dBEntities[i]
                            val jsonArrayUtils = JsonArrayUtils(entity.pressureJsonData)
                            if (jsonArrayUtils.length() > 0) {
                                val calendar = Calendar.getInstance()
                                for (j in 0 until jsonArrayUtils.length()) {
                                    val curr: JsonUtils = jsonArrayUtils.getJsonObject(j)
                                    val temp =
                                        curr.getInt("pressure", MainViewItem.EMPTY)
                                    val time = curr.getLong("datetime", 0)

                                    LogUtils.i(
                                        " getPressureData pressure $temp time" +
                                                "${
                                                    DateTimeUtils.s_long_2_str(
                                                        time,
                                                        DateTimeUtils.f_format
                                                    )
                                                }"
                                    )

                                    calendar.timeInMillis = time
                                    val hour = calendar[Calendar.HOUR_OF_DAY]
                                    if (temp > 0 && temp != MainViewItem.EMPTY) {
                                        index++
                                        average += temp.toFloat()
                                        if (heatMap.containsKey(hour)) {
                                            val bean: MainViewItem? = heatMap[hour]
                                            bean?.data = bean?.data?.plus(temp)!!
                                            heatMap[hour] = bean
                                            if (heatLenMap.containsKey(hour)) {
                                                heatLenMap[hour] = heatLenMap[hour]!! + 1
                                            }
                                        } else {
                                            val item = MainViewItem()
                                            item.time = time
                                            item.data = temp.toFloat()
                                            heatMap[hour] = item
                                            heatLenMap[hour] = 1
                                        }
                                    }
                                }
                            }
                        }
                        val integerSet: Set<Int> = heatMap.keys
                        for (key in integerSet) {
                            val bean: MainViewItem = heatMap[key] ?: continue
                            if (heatLenMap.containsKey(key)) {
                                val len = heatLenMap[key]!!
                                bean.data = bean.data / len
                            }
                            heatMap[key] = bean
                        }
                        if (index == 0) index = 1
                        average /= index.toFloat()

                        var lastItem: MainViewItem? = null
                        for (i in 0..23) {
                            if (heatMap.containsKey(i)) {
                                val bean: MainViewItem? = heatMap[i]
                                if (bean != null) {
                                    lastItem = bean
                                }
                            }
                        }
                        if (isLastDay) {
                            view.onLastAveragePressure(average)
                        } else {
                            view.onPressureData(lastItem, average)
                        }
                    }
                }

                override fun OnError(ex: java.lang.Exception?) {
                    ex?.printStackTrace()
                }

            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }

    private fun getEmotionData(day: Long) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " $TAG getEmotionData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val executor = QueryEmotionExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    val dBEntities: List<EmotionDBEntity> = result as List<EmotionDBEntity>
                    if (dBEntities.isNullOrEmpty()) {
                        view.onEmotionData(null, 0f)
                    } else {
                        var index = 0
                        var average = 0f
                        var totalEmotion = 0f
                        var lastItem = MainViewItem()

                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = startTime
                        var tempTime = calendar.timeInMillis

                        var dataIndex = 0
                        var timeIndex = 0

                        var dbEntity = dBEntities[dBEntities.size - 1]

                        while (tempTime <= endTime) {
                            var emotion = 0
                            val jsonArrayUtils = JsonArrayUtils(dbEntity.tempJsonData)
                            while (dataIndex < jsonArrayUtils.length()) {
                                val curr: JsonUtils = jsonArrayUtils.getJsonObject(dataIndex)
                                val temp = curr.getInt("temp", 0)
                                val time = curr.getLong("datetime", 0)
                                LogUtils.i(
                                    TAG,
                                    " getEmotionData time ${
                                        DateTimeUtils.s_long_2_str(
                                            time,
                                            DateTimeUtils.f_format
                                        )
                                    } emotion ${temp} dataIndex $dataIndex"
                                )
                                emotion = if (time <= tempTime) {
                                    temp
                                } else {
                                    break
                                }
                                dataIndex++
                            }
                            val showTime = timeIndex.toString() + ":00-" + (timeIndex + 1) + ":00"
                            val item = MainViewItem()
                            item.data = emotion.toFloat()
                            item.time = tempTime
                            item.showTimeString = showTime
                            if (emotion > 0) {
                                index++
                                lastItem = item
                                totalEmotion += emotion
                            }
                            LogUtils.i(
                                TAG,
                                " tempTime " + DateTimeUtils.s_long_2_str(
                                    tempTime,
                                    DateTimeUtils.f_format
                                ) + " emotion " + emotion + " dataIndex " + dataIndex
                            )
                            timeIndex++
                            calendar.add(Calendar.HOUR_OF_DAY, 1)
                            tempTime = calendar.timeInMillis
                        }

                        if (index <= 0) index = 1
                        average = totalEmotion / index.toFloat()
                        view.onEmotionData(lastItem, average)
                    }
                }

                override fun OnError(ex: java.lang.Exception?) {
                    ex?.printStackTrace()
                }

            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }

    /*静息心率>70，显示静心心率过高，夜间恢复不好；55-70，显示静息心率正常，十分健康。≤55，显示静息心率偏低*/
    fun querySleepHeartData(day: Long, barBean: SleepBarBean) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val queryStartTime = DateTimeUtils.AddDay(Date(startTime), -1).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " $TAG getMotionData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val sleepExecutor =
            QueryHeartRateExecutor(
                queryStartTime,
                endTime,
                object : AppDaoManager.DBExecutor.IResult {
                    override fun OnSucceed(result: Any) {
                        if (result == null) {
                            view.onSleepHeartRate(0)
                            return
                        }
                        val dBEntityList = result as List<HeartRateDBEntity>
                        if (dBEntityList == null || dBEntityList.isEmpty()) {
                            view.onSleepHeartRate(0)
                            return
                        }
                        val chartList = getRateByTime(dBEntityList, queryStartTime, endTime)
                        val sleepHeartList: List<ChartBean>? = getDataIntSleep(chartList, barBean)
                        if (sleepHeartList.isNullOrEmpty()) {
                            view.onSleepHeartRate(0)
                            return
                        }
                        var index = 0
                        var average = 0f
                        for (i in sleepHeartList.indices) {
                            sleepHeartList[i].dataList.forEach {
                                index++
                                average += it.data.toFloat()
                            }
                        }
                        if (index == 0) index = 1
                        if (average > 0) {
                            average = average.div(index.toFloat()) + 5
                        }
                        view.onSleepHeartRate(average.toInt())
                    }

                    override fun OnError(ex: Exception) {
                        ex.printStackTrace()
                    }
                })
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(sleepExecutor)
    }

    /*当最低血氧≤94%时，显示疑似呼吸窘迫，当最低血氧≥95%时，显示睡眠呼吸平稳；*/
    fun querySleepOxData(day: Long, barBean: SleepBarBean) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val queryStartTime = DateTimeUtils.AddDay(Date(startTime), -1).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " $TAG querySleepOxData startTime " + DateTimeUtils.s_long_2_str(
                queryStartTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val executor = QuerySpoInfoExecutor(queryStartTime, endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    if (result == null) {
                        view.onSleepOx(0)
                        return
                    }
                    val spoDBEntities = result as List<SpoDBEntity>
                    if (spoDBEntities.isNullOrEmpty()) {
                        view.onSleepOx(0)
                        return
                    }
                    val list: List<ChartBean>? = getOxByTime(spoDBEntities, queryStartTime, endTime)
                    val sleepList = getDataIntSleep(list, barBean)
                    val minOx = getMinData(sleepList)
                    view.onSleepOx(minOx)
                }

                override fun OnError(ex: java.lang.Exception?) {
                    ex?.printStackTrace()
                }
            })
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }

    fun getDayBean(dbEntity: SleepDBEntity?, siestaDBEntity: SiestaDBEntity?): SleepBarBean {
        var barBean = SleepBarBean()
        if (dbEntity != null) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dbEntity.startDateTime
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val date = DateTimeUtils
                .NewDate(
                    DateTimeUtils.getYear(calendar.time),
                    DateTimeUtils.getMonth(calendar.time),
                    DateTimeUtils.getday(calendar.time),
                    0,
                    0,
                    0
                )
            val queryStartTime: Long
            val queryEndTime: Long
            if (hour <= 8) {
                queryStartTime = DateTimeUtils.AddDay(date, -1).time
                queryEndTime = DateTimeUtils.AddDay(date, 1).time
            } else {
                queryStartTime = date.time
                queryEndTime = DateTimeUtils.AddDay(date, 2).time
            }

//            LogUtils.i(" loadData sleep queryStartTime == " + DateTimeUtils.s_long_2_str(queryStartTime, DateTimeUtils.f_format)
//                    + " queryEndTime == " + DateTimeUtils.s_long_2_str(queryEndTime, DateTimeUtils.f_format) + " startDateTime == " +
//                    DateTimeUtils.s_long_2_str(dbEntity.startDateTime, DateTimeUtils.f_format));
            val startTime = Date(dbEntity.startDateTime)
            var endTime = DateTimeUtils.AddHours(startTime, 1)
            var wakeCount = 0 //醒来次数
            var deepLen: Long = 0 //深睡时长
            var lightLen: Long = 0 //浅睡时长
            var wakeLen: Long = 0 //醒来时长
            var auyelen: Long = 0 //熬夜时长
            var fallLen: Long = 0 //入睡时长
            var remLen: Long = 0 //REM时长
            var endDayTime: Long = 0
            val dayBeanList = mutableListOf<SleepDayBean>()
            var dayBean: SleepDayBean? = null
            val itemList: MutableList<SleepItem?> = java.util.ArrayList<SleepItem?>()
            val jsonArrayUtils = JsonArrayUtils(dbEntity.getSleepJsonData())
            //            LogUtils.i(" json " + new Gson().toJson(jsonArrayUtils));
            for (i in 0 until jsonArrayUtils.length()) {
                val jsonUtils = jsonArrayUtils.getJsonObject(i)
                val sleepType = jsonUtils.getInt("sleeptype", 0)
                val itemStartTime = jsonUtils.getLong("starttime", 0)
                val itemEndTime = jsonUtils.getLong("endtime", 0)
                if (itemStartTime in queryStartTime..queryEndTime && itemEndTime >= queryStartTime && itemEndTime <= queryEndTime) {
                    var item: SleepItem? = null
                    if (sleepType == SleepItem.AUYELEN_SLEEP_TYPE) { //熬夜
                        auyelen += itemEndTime - itemStartTime
                        item = SleepItem(
                            itemStartTime,
                            itemEndTime,
                            Color.parseColor("#FF9448"),
                            sleepType
                        )
                        if (dayBean != null) {
                            dayBean.auyelen = dayBean.auyelen + (itemEndTime - itemStartTime)
                        }
                    }
                    if (sleepType == SleepItem.WAKE_SLEEP_TYPE) { //清醒
                        if (i != 0) {
                            wakeCount++ //醒来次数
                        }
                        wakeLen += itemEndTime - itemStartTime
                        item = SleepItem(
                            itemStartTime,
                            itemEndTime,
                            Color.parseColor("#FF9448"),
                            sleepType
                        )
                        if (dayBean != null) {
                            dayBean.weekLen = dayBean.weekLen + (itemEndTime - itemStartTime)
                            dayBean.wakeCount = dayBean.wakeCount + 1
                        }
                    }
                    if (sleepType == SleepItem.FALL_SLEEP_TYPE) { //入睡
                        item = SleepItem(
                            itemStartTime,
                            itemEndTime,
                            Color.parseColor("#3CE3FF"),
                            sleepType
                        )
                        fallLen += itemEndTime - itemStartTime
                        dayBean = SleepDayBean()
                        dayBean.startTime = itemStartTime
                        dayBean.fallLen = dayBean.fallLen + (itemEndTime - itemStartTime)
                        dayBeanList.add(dayBean)
                    }
                    if (sleepType == SleepItem.LIGHT_SLEEP_TYPE) { //浅睡
                        lightLen += itemEndTime - itemStartTime
                        item = SleepItem(
                            itemStartTime,
                            itemEndTime,
                            Color.parseColor("#5D9DFF"),
                            sleepType
                        )
                        if (dayBean != null) {
                            dayBean.lightLen = dayBean.lightLen + (itemEndTime - itemStartTime)
                        }
                    }
                    if (sleepType == SleepItem.DEEP_SLEEP_TYPE) { //深睡
                        deepLen += itemEndTime - itemStartTime
                        item = SleepItem(
                            itemStartTime,
                            itemEndTime,
                            Color.parseColor("#9150E5"),
                            sleepType
                        )
                        if (dayBean != null) {
                            dayBean.deepLen = dayBean.deepLen + (itemEndTime - itemStartTime)
                        }
                    }
                    if (sleepType == SleepItem.REM_SLEEP_TYPE) { //REM
                        remLen += itemEndTime - itemStartTime
                        item = SleepItem(
                            itemStartTime,
                            itemEndTime,
                            Color.parseColor("#4ED89F"),
                            sleepType
                        )
                        if (dayBean != null) {
                            dayBean.remLen = dayBean.remLen + (itemEndTime - itemStartTime)
                        }
                    }
                    if (sleepType == SleepItem.END_SLEEP_TYPE) { //结束睡眠
//                    LogUtils.i(" barBean " + DateTimeUtils.s_long_2_str(itemStartTime, DateTimeUtils.f_format) + " " +
//                            DateTimeUtils.s_long_2_str(itemEndTime, DateTimeUtils.f_format));
                        endDayTime = itemStartTime
                        item = SleepItem(
                            itemStartTime,
                            itemEndTime,
                            Color.parseColor("#9150E5"),
                            sleepType
                        )
                        if (dayBean != null) {
                            dayBean.setEndTime(itemStartTime)
                            dayBean.setSleepLen(dayBean.getFallLen() + dayBean.getLightLen() + dayBean.getDeepLen() + dayBean.getRemLen())
                            if (ValidRule.getInstance().isValidSleepLen(dayBean.getSleepLen())) {
                                val radio =
                                    (Math.round(((dayBean.getSleepLen() * 100) / (dayBean.getEndTime() - dayBean.getStartTime())).toDouble())
                                            / 100f)
                                dayBean.setRadio(radio)
                            } else {
                                dayBean.setSleepLen(0)
                            }
                        }
                    }
                    if (item != null) {
                        itemList.add(item)
                    }
                    if (i == jsonArrayUtils.length() - 1) {
                        endTime = Date(endDayTime)
                    }
                }
            }
            barBean.sleepDay = dbEntity.sleepDay
            barBean.auyelenTime = auyelen
            barBean.startTime = dbEntity.startDateTime
            barBean.endTime = endTime.time
            barBean.list = itemList
            val dayBeans: List<SleepDayBean>? = getResultDayBean(dayBeanList)
            barBean = getSleepBean(dayBeanList, barBean)
            val len: Long =
                barBean.fallLength + barBean.deepLength + barBean.lightLength + barBean.remLength
            barBean.setSleepLength(ValidRule.getInstance().getValidSleepLen(len))
            barBean.dayBeanList = dayBeans
        }
        if (siestaDBEntity != null && siestaDBEntity.getSiestaLength() > 0) {
            val siestaLong = siestaDBEntity.getSiestaLength()
            barBean.siestaLength = siestaLong * 1000 * 60
            barBean.startSiestaTime = siestaDBEntity.getStartTime()
            barBean.endSiestaTime = siestaDBEntity.getEndTime()
        }
        val score: Int = getSleepScore(
            barBean.startTime, (barBean.getSleepLength() / (1000 * 60)).toInt(),
            (barBean.deepLength / (1000 * 60)).toInt(), barBean.wakeCount
        )
        barBean.totalScore = score
        var highScore = 100 - score
        if (highScore <= 0) highScore = 0
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = barBean.startTime
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val min = calendar[Calendar.MINUTE]
        var hairScore: Int
        hairScore = if (hour >= 22) {
            (((hour - 22) * 60 + min) / 2f).roundToInt()
        } else {
            ((hour * 60 + min + 2 * 60) / 2f).roundToInt()
        }
        if (hairScore > 68) hairScore = 68
        if (hairScore < 5) hairScore = 5
        var skinScore =
            (100 - barBean.getSleepLength() / (1000 * 60f) / 6f).roundToInt().toInt()
        if (skinScore > 78) skinScore = 78
        if (skinScore <= 0) skinScore = 0
        val emotionScore = (100 - score * 1.1f + 11).roundToInt()
        barBean.highScore = highScore
        barBean.hairScore = hairScore
        barBean.skinScore = skinScore
        barBean.emotionScore = emotionScore
        return barBean
    }

    private fun getResultDayBean(dayBeanList: List<SleepDayBean>?): List<SleepDayBean>? {
        if (dayBeanList == null || dayBeanList.isEmpty()) return dayBeanList
        val beanList: MutableList<SleepDayBean> = java.util.ArrayList()
        for (dayBean in dayBeanList) {
            if (dayBean.sleepLen > 0) {
                beanList.add(dayBean)
            }
        }
        return beanList
    }

    private fun getSleepBean(
        dayBeanList: List<SleepDayBean>?,
        barBean: SleepBarBean
    ): SleepBarBean {
        if (dayBeanList == null || dayBeanList.isEmpty()) return barBean
        var wakeCount = 0 //醒来次数
        var deepLen: Long = 0 //深睡时长
        var lightLen: Long = 0 //浅睡时长
        var wakeLen: Long = 0 //醒来时长
        var fallLen: Long = 0 //入睡时长
        var remLen: Long = 0 //REM时长
        for (dayBean in dayBeanList) {
            if (dayBean.sleepLen > 0) {
                fallLen += dayBean.fallLen
                lightLen += dayBean.lightLen
                deepLen += dayBean.deepLen
                remLen += dayBean.remLen
                wakeLen += dayBean.weekLen
                wakeCount += dayBean.wakeCount
            }
        }
        barBean.fallLength = fallLen
        barBean.deepLength = deepLen
        barBean.lightLength = lightLen
        barBean.wakeLength = wakeLen
        barBean.wakeCount = wakeCount
        barBean.remLength = remLen
        return barBean
    }

    private fun getSleepScore(
        startTime: Long,
        sleepLen: Int,
        sleepDeep: Int,
        sleepAwake: Int
    ): Int {
        val score = 0
        if (startTime == 0L || sleepLen == 0) return score
        val score11: Int = get11Score(startTime) //早于11点 10 11点后 每半小时扣2分
        var sleepScore = 0f
        val hour = sleepLen / 60 //睡眠总时长
        sleepScore = if (hour >= 8) {
            1f
        } else if (hour >= 6) {
            0.9f
        } else if (hour >= 4) {
            0.85f
        } else {
            0.8f
        }
        var deepScore = (sleepDeep / 60).toFloat()
        deepScore = if (deepScore >= 2) {
            1f
        } else if (deepScore >= 1) {
            0.9f
        } else {
            0.8f
        }
        var weekScore = sleepAwake.toFloat()
        weekScore = if (weekScore > 3) {
            0.8f
        } else if (weekScore >= 2) {
            0.9f
        } else {
            1f
        }
        val total = score11 + 90 * sleepScore * deepScore * weekScore
        return if (total <= 0) {
            0
        } else if (total >= 100) {
            100
        } else {
            Math.round(total)
        }
    }

    //早于11点 10 11点后 每半小时扣2分
    private fun get11Score(startTime: Long): Int {
        var score = 0
        if (startTime == 0L) return score
        val sleepCalendar = Calendar.getInstance()
        sleepCalendar.timeInMillis = startTime
        val hour = sleepCalendar[Calendar.HOUR_OF_DAY]
        val min = sleepCalendar[Calendar.MINUTE]

//        LogUtils.i(" hour == " + hour + " min == " + min);
        if (hour in 20..22) {
            score = 10
        } else if (hour >= 23) {
            score = if (min >= 30) 6 else 8
        } else if (hour in 8..19) {
            score = 0
        } else {
            score = 10 - hour * 4 - (if (min >= 30) 4 else 2) - 4
            if (score < 0) score = 0
        }
        //        LogUtils.i(" 分数 " + score);
        return score
    }

    fun getRateByTime(
        dbEntities: List<HeartRateDBEntity>?,
        startTime: Long,
        endTime: Long
    ): List<ChartBean>? {
        val list = mutableListOf<ChartBean>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTime
        var time = startTime
        while (time < endTime) {
            val heartDBEntity: HeartRateDBEntity? = getRateByTimeDTBEntity(dbEntities, time)
            val bean: ChartBean = getDayBean(heartDBEntity)
            bean.setShowTimeString(DateTimeUtils.getWeekShow(time))
            bean.setBottomString(DateTimeUtils.getWeekShow(time))
            list.add(bean)
            time = DateTimeUtils.AddDay(Date(time), 1).time
        }
        return list
    }

    private fun getRateByTimeDTBEntity(
        dbEntities: List<HeartRateDBEntity>?,
        timeDay: Long
    ): HeartRateDBEntity? {
        if (dbEntities.isNullOrEmpty()) return null
        for (i in dbEntities.indices) {
            val curr = dbEntities[i]
            if (curr.HeartRateDay === timeDay) {
                return curr
            }
        }
        return null
    }

    private fun getDayBean(dbEntity: HeartRateDBEntity?): ChartBean {
        mMaxRate = 0
        mMinRate = 0
        mAverageRate = 0
        val bean = ChartBean()
        if (dbEntity == null) return bean
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dbEntity.HeartRateDay
        calendar[Calendar.HOUR] = 23
        calendar[Calendar.MINUTE] = 59
        var list: List<ChartBean.DataItem>? = null
        val isOneMin: Boolean = SPUtil.getData(
            instance().applicationContext,
            SpConfig.IS_ONE_HEART_RATE,
            true
        ) as Boolean
        if (isOneMin) {
            list = makeOneMinList(dbEntity, calendar.timeInMillis)
        } else {
            list = makeList(dbEntity, calendar.timeInMillis)
        }
        bean.setDataList(list)
        bean.setData(mAverageRate.toFloat())
        bean.setTime(dbEntity.HeartRateDay)
        bean.setMaxData(mMaxRate.toFloat())
        bean.setMinData(mMinRate.toFloat())
        bean.setAverageData(mAverageRate.toFloat())
        return bean
    }

    private fun makeOneMinList(
        dbEntity: HeartRateDBEntity,
        tongEndTime: Long
    ): List<ChartBean.DataItem>? {
        mMaxRate = 0
        mMinRate = 0
        mAverageRate = 0
        var index = 0
        val itemList = mutableListOf<ChartBean.DataItem>()
        val jsonArrayUtils = JsonArrayUtils(dbEntity.heartJsonData)
        if (jsonArrayUtils.length() > 0) {
            var i = 0
            var j = 0
            val calendar = Calendar.getInstance()
            var startTime = jsonArrayUtils.getJsonObject(0).getLong("datetime", 0)
            val endTime =
                jsonArrayUtils.getJsonObject(jsonArrayUtils.length() - 1).getLong("datetime", 0)
            calendar.timeInMillis = startTime
            while (startTime <= endTime && j < jsonArrayUtils.length() && tongEndTime > 0 && startTime < tongEndTime) {
                val curr = jsonArrayUtils.getJsonObject(j)
                val temp = curr.getInt("rate", 0)
                val time = curr.getLong("datetime", 0)
                if (time == startTime && temp > 0) {
                    val item = ChartBean.DataItem(temp.toDouble(), time)
                    itemList.add(item)
                    index++
                    mAverageRate += temp
                    mMinRate = if (mMinRate == 0 || mMinRate > temp) temp else mMinRate
                    mMaxRate = if (mMaxRate == 0 || mMaxRate < temp) temp else mMaxRate
                }
                if (time == startTime) {
                    j++
                }
                calendar.add(Calendar.MINUTE, 1)
                startTime = calendar.timeInMillis
                i++
            }
        }
        if (index == 0) index = 1
        mAverageRate /= index
        return itemList
    }

    private fun makeList(
        dbEntity: HeartRateDBEntity,
        tongEndTime: Long
    ): List<ChartBean.DataItem>? {
        mMaxRate = 0
        mMinRate = 0
        mAverageRate = 0
        var index = 0
        val itemList = mutableListOf<ChartBean.DataItem>()
        val list = mutableListOf<Int>()
        val jsonArrayUtils = JsonArrayUtils(dbEntity.heartJsonData)
        if (jsonArrayUtils.length() > 0) {
            for (i in 0 until jsonArrayUtils.length()) {
                val curr = jsonArrayUtils.getJsonObject(i)
                val rate = curr.getInt("rate", 0)
                val time = curr.getLong("datetime", 0)
                if (tongEndTime in 1..time) continue
                list.add(rate)
                if (rate > 0) {
                    val item = ChartBean.DataItem(rate.toDouble(), time)
                    itemList.add(item)
                    index++
                    mAverageRate += rate
                    mMinRate = if (mMinRate == 0 || mMinRate > rate) rate else mMinRate
                    mMaxRate = if (mMaxRate == 0 || mMaxRate < rate) rate else mMaxRate
                }
            }
        }
        if (index == 0) index = 1
        mAverageRate /= index
        return itemList
    }

    private fun getDataIntSleep(list: List<ChartBean>?, barBean: SleepBarBean?): List<ChartBean>? {
        if (list.isNullOrEmpty() || barBean == null) return null
        val lenItems: List<SleepLenItem>? = getSleepLenItem(barBean)
        for (bean in list) {
            val itemList = mutableListOf<ChartBean.DataItem>()
            if (bean.getDataList() != null && bean.getDataList().isNotEmpty()) {
                for (item in bean.getDataList()) {
                    if (isContain(item.dateTime, lenItems)) {
                        itemList.add(item)
                        LogUtils.i(
                            "$TAG getWatchDayData time " + DateTimeUtils.s_long_2_str(
                                item.dateTime,
                                DateTimeUtils.f_format
                            ) + " data == " + item.data
                        );
                    }
                }
            }
            bean.setDataList(itemList)
        }
        return list
    }

    private fun getSleepLenItem(bean: SleepBarBean?): List<SleepLenItem>? {
        if (bean == null) return null
        val list = mutableListOf<SleepLenItem>()
        if (bean.list != null && bean.list.isNotEmpty()) {
            var lenItem: SleepLenItem? = null
            for (item in bean.list) {
                //1 深睡 //2 浅睡 //3 清醒 //4 入睡 //5 熬夜
                if (item.getSleepType() === SleepItem.DEEP_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.LIGHT_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.WAKE_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.FALL_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.END_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.REM_SLEEP_TYPE
                ) {
                    if (item.getSleepType() === SleepItem.FALL_SLEEP_TYPE) {
                        lenItem = SleepLenItem()
                        lenItem.startTime = item.startTime
                    }
                    if (item.getSleepType() === SleepItem.END_SLEEP_TYPE) {
                        if (lenItem != null && lenItem.endTime === 0L && lenItem.startTime !== 0L) {
                            lenItem.endTime = item.startTime
                            list.add(lenItem)
//                            LogUtils.i(" start 333 " + DateTimeUtils.s_long_2_str(lenItem.getStartTime(), DateTimeUtils.day_hm_format) + " "  +
//                            " end " + DateTimeUtils.s_long_2_str(lenItem.getEndTime(), DateTimeUtils.day_hm_format));
                        }
                    }
                }
            }
        }
        return list
    }

    private fun isContain(time: Long, list: List<SleepLenItem>?): Boolean {
        if (list.isNullOrEmpty()) return false
        for (lenItem in list) {
//            LogUtils.i(" isContain " + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format) + " " + DateTimeUtils.s_long_2_str(lenItem.getStartTime(), DateTimeUtils.f_format)
//                    + " " + DateTimeUtils.s_long_2_str(lenItem.getEndTime(), DateTimeUtils.f_format));
            if (time >= lenItem.startTime && time <= lenItem.endTime) {
                return true
            }
        }
        return false
    }

    private fun getOxByTime(
        spoDBEntities: List<SpoDBEntity>,
        queryTime: Long,
        endTime: Long
    ): List<ChartBean>? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = queryTime
        var time = queryTime
        val list = mutableListOf<ChartBean>()
        while (time < endTime) {
            val spoDBEntity: SpoDBEntity? = getSpoDBEntity(spoDBEntities, time)
            val bean = getDayBean(spoDBEntity)
            bean.setTime(time)
            bean.setShowTimeString(DateTimeUtils.getWeekShow(time))
            bean.setBottomString(DateTimeUtils.getWeekShow(time))
            list.add(bean)
            time = DateTimeUtils.AddDay(Date(time), 1).time
        }
        LogUtils.i(TAG + " " + Gson().toJson(list));
        return list
    }

    private fun getSpoDBEntity(dbEntities: List<SpoDBEntity>?, timeDay: Long): SpoDBEntity? {
        if (dbEntities.isNullOrEmpty()) return null
        for (i in dbEntities.indices) {
            val curr = dbEntities[i]
            if (curr.SpoDay === timeDay) {
                return curr
            }
        }
        return null
    }

    private fun getDayBean(dbEntities: SpoDBEntity?): ChartBean {
        val item = ChartBean()
        if (dbEntities == null) return item
        item.setTime(dbEntities.SpoDay)
        item.showTimeString = DateTimeUtils.getWeekZWShow(dbEntities.SpoDay)
        val itemList = mutableListOf<ChartBean.DataItem>()
        var index = 0
        var averageTemp = 0f
//                List<SleepLenItem> lenItems = SleepAlgorithm.getInstance().getSleepLenItem(mBarBean);
        val jsonArrayUtils = JsonArrayUtils(dbEntities.spoJsonData)
        if (jsonArrayUtils.length() > 0) {
            for (j in 0 until jsonArrayUtils.length()) {
                val jsonObject = jsonArrayUtils.getJsonObject(j)
                val temp: Float = NumberUtils.fromStringToFloat(jsonObject.getString("spo"), 0f)
                val time = jsonObject.getLong("datetime", 0L)
                val isSleepOx = jsonObject.getBoolean("sleepOx", false)
//                LogUtils.i(" Week temp == " + temp);
                if (temp > 0 /*&& isSleepOx*/ /*&& SleepAlgorithm.getInstance().isContain(time, lenItems)*/) {
                    val dataItem = ChartBean.DataItem(temp.toDouble(), time)
                    itemList.add(dataItem)
                    LogUtils.i(
                        TAG,
                        " time " + DateTimeUtils.s_long_2_str(
                            time,
                            DateTimeUtils.f_format
                        ) + " spo " + temp
                    )
                    item.list.add(temp)
                    averageTemp += temp
                    index++
                }
            }
        }
        if (index == 0) index = 1
        item.setData(averageTemp / index.toFloat())
        item.setDataList(itemList)
        return item
    }

    private fun getMinData(list: List<ChartBean>?): Int {
        if (list.isNullOrEmpty()) return 0
        var min = 0f
        for (bean in list) {
            for (item in bean.getDataList()) {
                if (item.data > 0) {
                    min = if (min == 0f || min > item.data) item.data.toFloat() else min
                }
            }
        }
        return Math.round(min)
    }

    private fun getAbnormalRateDTBEntityByTime(
        dbEntities: List<AbnormalRateDBEntity>,
        timeDay: Long
    ): AbnormalRateDBEntity? {
        for (i in dbEntities.indices) {
            val curr = dbEntities[i]
            if (curr.HeartRateDay == timeDay) {
                return curr
            }
        }
        return null
    }
}