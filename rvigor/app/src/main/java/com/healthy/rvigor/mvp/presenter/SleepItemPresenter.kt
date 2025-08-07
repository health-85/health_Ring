package com.healthy.rvigor.mvp.presenter

import android.graphics.Color
import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.bean.SleepBarBean
import com.healthy.rvigor.bean.SleepDayBean
import com.healthy.rvigor.bean.SleepItem
import com.healthy.rvigor.dao.entity.SiestaDBEntity
import com.healthy.rvigor.dao.entity.SleepDBEntity
import com.healthy.rvigor.dao.executor.QuerySiestaExecutor
import com.healthy.rvigor.dao.executor.QuerySleepExecutor
import com.healthy.rvigor.dao.util.AppDaoManager
import com.healthy.rvigor.mvp.contract.IMotionContract
import com.healthy.rvigor.mvp.contract.ISleepItemContract
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.JsonArrayUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.ValidRule
import java.lang.Exception
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/26 15:07
 * @UpdateRemark:
 */
class SleepItemPresenter : BasePresenterImpl<ISleepItemContract.View>(),
    ISleepItemContract.Presenter {

    override fun querySleepData(day: Long) {
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

                override fun OnError(ex: Exception?) {
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
                        view.onSleepData(null)
                        return
                    }
                    val bean: SleepBarBean = getDayBean(sleepDBEntity, siestaDBEntity)
                    view.onSleepData(bean)
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }
            })
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(sleepExecutor)
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
                        item.lightColor = Color.parseColor("#55FF9448")
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
                            Color.parseColor("#FF6B4F"),
                            sleepType
                        )
                        item.lightColor = Color.parseColor("#55FF6B4F")
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
                        item.lightColor = Color.parseColor("#553CE3FF")
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
                            Color.parseColor("#0A85FF"),
                            sleepType
                        )
                        item.lightColor = Color.parseColor("#550A85FF")
                        if (dayBean != null) {
                            dayBean.lightLen = dayBean.lightLen + (itemEndTime - itemStartTime)
                        }
                    }
                    if (sleepType == SleepItem.DEEP_SLEEP_TYPE) { //深睡
                        deepLen += itemEndTime - itemStartTime
                        item = SleepItem(
                            itemStartTime,
                            itemEndTime,
                            Color.parseColor("#A25DFF"),
                            sleepType
                        )
                        item.lightColor = Color.parseColor("#55A25DFF")
                        if (dayBean != null) {
                            dayBean.deepLen = dayBean.deepLen + (itemEndTime - itemStartTime)
                        }
                    }
                    if (sleepType == SleepItem.REM_SLEEP_TYPE) { //REM
                        remLen += itemEndTime - itemStartTime
                        item = SleepItem(
                            itemStartTime,
                            itemEndTime,
                            Color.parseColor("#39CAFF"),
                            sleepType
                        )
                        item.lightColor = Color.parseColor("#5539CAFF")
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
                        item.lightColor = Color.parseColor("#5539CAFF")
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


}