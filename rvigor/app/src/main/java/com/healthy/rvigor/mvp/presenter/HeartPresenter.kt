package com.healthy.rvigor.mvp.presenter

import android.graphics.Color
import android.text.TextUtils
import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.bean.ChartBean
import com.healthy.rvigor.bean.HeartDataInfo
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.bean.SleepBarBean
import com.healthy.rvigor.bean.SleepDayBean
import com.healthy.rvigor.bean.SleepItem
import com.healthy.rvigor.bean.SleepLenItem
import com.healthy.rvigor.dao.entity.AbnormalRateDBEntity
import com.healthy.rvigor.dao.entity.HeartRateDBEntity
import com.healthy.rvigor.dao.entity.SleepDBEntity
import com.healthy.rvigor.dao.executor.QueryAbnormalHeartExecutor
import com.healthy.rvigor.dao.executor.QueryHeartRateExecutor
import com.healthy.rvigor.dao.executor.QuerySleepExecutor
import com.healthy.rvigor.dao.util.AppDaoManager
import com.healthy.rvigor.mvp.contract.IHeartContract
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.JsonArrayUtils
import com.healthy.rvigor.util.JsonUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.util.ValidRule
import com.healthy.rvigor.util.ViewDataUtil
import com.healthy.rvigor.view.SpecDateSelectedView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/24 19:56
 * @UpdateRemark:
 */
class HeartPresenter : BasePresenterImpl<IHeartContract.View>(),
    IHeartContract.Presenter {

    companion object {
        const val TAG = "HeartPresenter"
    }

    private var mAverageRate = 0f

    //最小心率
    private var mMinRate = 0

    //最大心率
    private var mMaxRate = 0

    override fun getHeartData(sender: SpecDateSelectedView?) {
        if (sender?.timeMode == SpecDateSelectedView.TimeMode.Day) { //如果是天
            //日查询
            queryDayData(sender)
        } else if (sender?.timeMode == SpecDateSelectedView.TimeMode.Week) { //如果是周
            //周查询
            queryWeekData(sender)
        } else if (sender?.timeMode == SpecDateSelectedView.TimeMode.Month) { //如果是月
            //月查询
            queryMonthData(sender)
        }
        querySleepHeart(sender)
    }

    //查询本地异常心率
    override fun getAbnormalHeartData(sender: SpecDateSelectedView?) {
        if (sender == null) return
        val startTime = sender.datestart.time
        val endTime = sender.dateend.time
        val sleepExecutor =
            QueryAbnormalHeartExecutor(
                startTime,
                endTime,
                object : AppDaoManager.DBExecutor.IResult {
                    override fun OnSucceed(result: Any) {
                        try {
                            val dBEntityList = result as List<AbnormalRateDBEntity>
                            if (dBEntityList == null || dBEntityList.isEmpty()) {
                                view?.onAbnormalHeartDayResult(ChartBean.EMPTY, null)
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

                            var userId = MyApplication.instance().appUserInfo.userInfo.id
                            var watchBase = MyApplication.instance().bleUtils.getConnectionWatch()

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
                            view?.onAbnormalHeartDayResult(average.toInt(), heartList)
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

    private fun queryDayData(sender: SpecDateSelectedView?) {
        if (sender == null) return
        val startTime = sender.datestart.time
        val endTime = sender.dateend.time
        LogUtils.i(
            "$TAG queryLocalHeartDayData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            )
                    + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )
        val heartExecutor = QueryHeartRateExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {

                    val heartDBEntities: List<HeartRateDBEntity> = result as List<HeartRateDBEntity>

                    if (heartDBEntities.isEmpty()) {
                        view.onHeartDataListener(
                            arrayListOf(),
                            null,
                            0,
                            0,
                            0
                        )
                        return
                    }

                    var index = 0
                    var max = 0
                    var min = 0
                    var average = 0

//                    val watchBase = MainApplication.getInstance().adapetUtils.connectionWatch
                    val isOneRate = SPUtil.getData(
                        MyApplication.instance(),
                        SpConfig.IS_ONE_HEART_RATE,
                        false
                    ) as Boolean

                    var isOneMin = true
                    if (heartDBEntities.isNotEmpty() && !TextUtils.isEmpty(heartDBEntities[0].heartJsonData)) {
                        val jsonArrayUtils = JsonArrayUtils(heartDBEntities[0].heartJsonData)
                        isOneMin = jsonArrayUtils.getJsonObject(0).getBoolean("oneMin", true)
                    }

                    LogUtils.i(" queryLocalHeartDayData isOneMin $isOneMin")

                    var lastItem = MainViewItem()
                    val itemList = mutableListOf<MainViewItem>()

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = sender.datestart.time

                    for (i in heartDBEntities.size - 1 downTo 0) {
                        val dbEntity: HeartRateDBEntity = heartDBEntities[i]
                        if (/*watchBase != null && watchBase.isOneMinRate ||*/ isOneMin) {
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
//                                        dataCalendar.timeInMillis = time
//                                        dataCalendar.set(Calendar.SECOND, 0)
//                                        dataCalendar.set(Calendar.MILLISECOND, 0)
//                                        time = dataCalendar.timeInMillis

                                        if (heart > 10) {
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
//                                        LogUtils.i(
//                                            " heart $heart  datetime " + DateTimeUtils.s_long_2_str(
//                                                time,
//                                                DateTimeUtils.f_format
//                                            ) + " time " +
//                                                    DateTimeUtils.s_long_2_str(
//                                                        calendar.timeInMillis,
//                                                        DateTimeUtils.f_format
//                                                    ) + " endTime " +
//                                                    DateTimeUtils.s_long_2_str(
//                                                        endTime,
//                                                        DateTimeUtils.f_format
//                                                    )
//                                        )
                                        if ((i % 5 == 4 || time == endTime) && list.size > 0) {
                                            list.sort()
                                            val rate = list[list.size / 2]
                                            list.clear()
                                            if (rate > 0) {
                                                val item = MainViewItem()
                                                item.data = rate.toFloat()
                                                item.time = time
                                                item.color = ViewDataUtil.getHeartDataColor(rate)
                                                item.isOneMin = isOneMin
                                                itemList.add(item)
                                                lastItem = item
//                                                LogUtils.i(
//                                                    " rate " + rate + "  datetime " + DateTimeUtils.s_long_2_str(
//                                                        time,
//                                                        DateTimeUtils.f_format
//                                                    ) + " time " +
//                                                            DateTimeUtils.s_long_2_str(
//                                                                calendar.timeInMillis,
//                                                                DateTimeUtils.f_format
//                                                            )
//                                                )
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
                                        if (heart > 10) {
                                            index++
                                            average += heart
                                            min =
                                                if (min == 0 || min > heart) heart else min
                                            max =
                                                if (max == 0 || max < heart) heart else max

                                            val item = MainViewItem()
                                            item.data = heart.toFloat()
                                            item.time = time
                                            item.color = ViewDataUtil.getHeartDataColor(heart)
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

                    view.onHeartDataListener(
                        itemList,
                        lastItem,
                        max,
                        min,
                        average
                    )
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }
            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(heartExecutor)
    }

    private fun queryWeekData(sender: SpecDateSelectedView?) {
        if (sender == null) return

        val startTime = sender.datestart.time
        val endTime = sender.dateend.time

        LogUtils.i(
            "$TAG queryLocalWeekData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            )
                    + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        var index = 0
        var maxValue = 0
        var minValue = 0
        var average = 0
        var lastBean: MainViewItem? = null

        val heartExecutor = QueryHeartRateExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    val heartDBEntities: List<HeartRateDBEntity> = result as List<HeartRateDBEntity>
                    if (heartDBEntities.isEmpty()) {
                        view.onHeartDataListener(
                            arrayListOf(),
                            null,
                            0,
                            0,
                            0,
                        )
                        return
                    }
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = startTime
                    var tempTime = calendar.timeInMillis

                    val heartListBean = mutableListOf<MainViewItem>()

                    while (tempTime < endTime) {
                        var max = 0
                        var min = 0
                        val dbEntity = getRateByTimeDTBEntity(heartDBEntities, tempTime)
                        if (dbEntity != null) {
                            val jsonArrayUtils = JsonArrayUtils(dbEntity.heartJsonData)
                            if (jsonArrayUtils != null && jsonArrayUtils.length() > 0) {
                                for (i in 0 until jsonArrayUtils.length()) {
                                    val curr: JsonUtils = jsonArrayUtils.getJsonObject(i)
                                    val heart = curr.getInt("rate", 0)
                                    val time = curr.getLong("datetime", 0)
                                    if (heart > 10) {
                                        index++
                                        average += heart
                                        min =
                                            if (min == 0 || min > heart) heart else min
                                        max =
                                            if (max == 0 || max < heart) heart else max
                                        minValue =
                                            if (minValue == 0 || minValue > heart) heart else minValue
                                        maxValue =
                                            if (maxValue == 0 || maxValue < heart) heart else maxValue
                                    }
                                }
                            }
                        }
                        val showTime = DateTimeUtils.getWeekShow(tempTime)
                        val bean = MainViewItem()
                        bean.time = tempTime
                        bean.maxData = max.toFloat()
                        bean.minData = min.toFloat()
                        bean.data = ((max + min) / 2).toFloat()
                        bean.showTimeString = showTime
                        bean.color = ViewDataUtil.getHeartDataColor(bean.data.toInt())
                        heartListBean.add(bean)

                        if (bean.data > 0) {
                            lastBean = bean
                        }
                        tempTime = DateTimeUtils.AddDay(Date(tempTime), 1).time
                        calendar.timeInMillis = tempTime
                    }
                    if (index == 0) index = 1
                    average /= index

                    view.onHeartDataListener(
                        heartListBean,
                        lastBean,
                        maxValue,
                        minValue,
                        average
                    )
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }
            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(heartExecutor)
    }

    private fun queryMonthData(sender: SpecDateSelectedView?) {
        if (sender == null) return
        val startTime = sender.datestart.time
        val endTime = sender.dateend.time
        LogUtils.i(
            "$TAG queryLocalMonthData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            )
                    + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        var index = 0
        var maxValue = 0
        var minValue = 0
        var average = 0
        var lastBean: MainViewItem? = null

        val heartExecutor = QueryHeartRateExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    val heartDBEntities: List<HeartRateDBEntity> = result as List<HeartRateDBEntity>
                    if (heartDBEntities.isEmpty()) {
                        view.onHeartDataListener(
                            arrayListOf(),
                            null,
                            0,
                            0,
                            0,
                        )
                        return
                    }
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = startTime
                    var tempTime = calendar.timeInMillis

                    val heartListBean = mutableListOf<MainViewItem>()

                    while (tempTime < endTime) {
                        var max = 0
                        var min = 0
                        val dbEntity = getRateByTimeDTBEntity(heartDBEntities, tempTime)
                        if (dbEntity != null) {
                            val jsonArrayUtils = JsonArrayUtils(dbEntity.heartJsonData)
                            if (jsonArrayUtils != null && jsonArrayUtils.length() > 0) {
                                for (i in 0 until jsonArrayUtils.length()) {
                                    val curr: JsonUtils = jsonArrayUtils.getJsonObject(i)
                                    val heart = curr.getInt("rate", 0)
                                    val time = curr.getLong("datetime", 0)
                                    if (heart > 10) {
                                        index++
                                        average += heart
                                        min =
                                            if (min == 0 || min > heart) heart else min
                                        max =
                                            if (max == 0 || max < heart) heart else max
                                        minValue =
                                            if (minValue == 0 || minValue > heart) heart else minValue
                                        maxValue =
                                            if (maxValue == 0 || maxValue < heart) heart else maxValue
                                    }
                                }
                            }
                        }
                        val showTime = DateTimeUtils.getWeekShow(tempTime)
                        val bean = MainViewItem()
                        bean.time = tempTime
                        bean.maxData = max.toFloat()
                        bean.minData = min.toFloat()
                        bean.data = ((max + min) / 2).toFloat()
                        bean.showTimeString = showTime
                        bean.color = ViewDataUtil.getHeartDataColor(bean.data.toInt())
                        heartListBean.add(bean)
                        if (bean.data > 0) {
                            lastBean = bean
                        }
                        tempTime = DateTimeUtils.AddDay(Date(tempTime), 1).time
                        calendar.timeInMillis = tempTime
                    }
                    if (index == 0) index = 1
                    average /= index

                    view.onHeartDataListener(
                        heartListBean,
                        lastBean,
                        maxValue,
                        minValue,
                        average
                    )
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }
            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(heartExecutor)
    }

    private fun querySleepHeart(
        sender: SpecDateSelectedView?,
    ) {
        if (sender == null) return

        val startTime = sender.datestart.time
        val endTime = sender.dateend.time
        val sleepExecutor = QuerySleepExecutor(startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any) {
                    val sleepDBEntityList = result as List<SleepDBEntity>
                    if (sleepDBEntityList.isNullOrEmpty()) {
                        view.onHeartInSleepResult(0)
                        return
                    }
                    val list = mutableListOf<SleepBarBean>()
                    val calendar = Calendar.getInstance()
                    calendar.time = sender.datestart
                    var date = sender.datestart
                    while (date.time < sender.dateend.time) {
                        val sleepDBEntity = getSleepDBEntity(sleepDBEntityList, date.time)
                        val bean = getDayBean(sleepDBEntity)
                        bean.sleepDay = date.time
                        bean.showTime = DateTimeUtils.getWeekShow(date.time)
                        bean.bottomString =
                            DateTimeUtils.getWeekS(MyApplication.instance(), date.time)
                        date = DateTimeUtils.AddDay(date, 1)
                        list.add(bean)
                    }
                    querySleepHeartData(sender, list)
                }

                override fun OnError(ex: java.lang.Exception) {}
            })
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(sleepExecutor)
    }


    private var mSleepHeartDis: Disposable? = null

    fun querySleepHeartData(
        sender: SpecDateSelectedView,
        sleepList: List<SleepBarBean>,
    ) {
        val startTime = sender.datestart.time
        val queryStartTime = DateTimeUtils.AddDay(Date(startTime), -1).time
        val endTime = sender.dateend.time
        val sleepExecutor =
            QueryHeartRateExecutor(
                queryStartTime,
                endTime,
                object : AppDaoManager.DBExecutor.IResult {
                    override fun OnSucceed(result: Any) {
                        val dBEntityList = result as List<HeartRateDBEntity>
                        if (dBEntityList.isNullOrEmpty()) {
                            view.onHeartInSleepResult(0)
                            return
                        }
                        mSleepHeartDis?.dispose()
                        mSleepHeartDis = Observable.just(sleepList).flatMap {
                            val chartList =
                                getRateByTime(dBEntityList, sleepList, queryStartTime, endTime)
                            val sleepHeartList: List<ChartBean> =
                                getDataIntSleep(chartList, sleepList)
                            Observable.just(sleepHeartList)
                        }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                if (it == null || it.isEmpty()) {
                                    view.onHeartInSleepResult(0)
                                    return@subscribe
                                }
                                var index = 0
                                var average = 0f
                                for (i in it.indices) {
                                    it[i].dataList.forEach {
                                        index++
                                        average += it.data.toFloat()
                                    }
                                }
                                if (index == 0) index = 1
                                if (average > 0) {
                                    average = average.div(index.toFloat()) + 5
                                }
                                view.onHeartInSleepResult(average.roundToInt())
                            }

                    }

                    override fun OnError(ex: java.lang.Exception) {}
                })
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(sleepExecutor)
    }

    fun getDayBean(dbEntity: SleepDBEntity?): SleepBarBean {
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
                            dayBean.setAuyelen(dayBean.getAuyelen() + (itemEndTime - itemStartTime))
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
                            dayBean.setWeekLen(dayBean.getWeekLen() + (itemEndTime - itemStartTime))
                            dayBean.setWakeCount(dayBean.getWakeCount() + 1)
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
                        dayBean.setStartTime(itemStartTime)
                        dayBean.setFallLen(dayBean.getFallLen() + (itemEndTime - itemStartTime))
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
                            dayBean.setLightLen(dayBean.getLightLen() + (itemEndTime - itemStartTime))
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
                            dayBean.setDeepLen(dayBean.getDeepLen() + (itemEndTime - itemStartTime))
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
                            dayBean.setRemLen(dayBean.getRemLen() + (itemEndTime - itemStartTime))
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
            val len =
                barBean.fallLength + barBean.deepLength + barBean.lightLength + barBean.remLength
            barBean.setSleepLength(ValidRule.getInstance().getValidSleepLen(len))
            barBean.dayBeanList = dayBeans
        }
        val score: Int = getSleepScore(
            barBean.startTime, barBean.getSleepLength().toInt() / (1000 * 60),
            barBean.deepLength.toInt() / (1000 * 60), barBean.wakeCount
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
            Math.round(((hour - 22) * 60 + min) / 2f)
        } else {
            Math.round((hour * 60 + min + 2 * 60) / 2f)
        }
        if (hairScore > 68) hairScore = 68
        if (hairScore < 5) hairScore = 5
        var skinScore = Math.round(100 - barBean.getSleepLength() / (1000 * 60f) / 6f)
        if (skinScore > 78) skinScore = 78
        if (skinScore <= 0) skinScore = 0
        val emotionScore = Math.round(100 - score * 1.1f + 11)
        barBean.highScore = highScore
        barBean.hairScore = hairScore
        barBean.skinScore = skinScore
        barBean.emotionScore = emotionScore
        return barBean
    }

    private fun getRateByTimeDTBEntity(
        dbEntities: List<HeartRateDBEntity>?,
        timeDay: Long
    ): HeartRateDBEntity? {
        if (dbEntities.isNullOrEmpty()) return null
        for (i in dbEntities.indices) {
            val curr = dbEntities[i]
            if (curr.HeartRateDay == timeDay) {
                return curr
            }
        }
        return null
    }

    fun getSleepDBEntity(sleepDBEntities: List<SleepDBEntity>, timeSleepday: Long): SleepDBEntity? {
        for (i in sleepDBEntities.indices) {
            val curr = sleepDBEntities[i]
            if (curr.sleepDay === timeSleepday) {
                return curr
            }
        }
        return null
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
        if (hour < 23 && hour >= 20) {
            score = 10
        } else if (hour >= 23) {
            score = if (min >= 30) 6 else 8
        } else if (hour < 20 && hour >= 8) {
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
        sleepBarBeans: List<SleepBarBean?>?,
        startTime: Long,
        endTime: Long
    ): List<ChartBean> {
        val list = mutableListOf<ChartBean>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTime
        var time = startTime
        while (time < endTime) {
            val heartDBEntity: HeartRateDBEntity? =
                getRateByTimeDTBEntity(
                    dbEntities,
                    time
                )
            val bean: ChartBean = getDayBean(heartDBEntity)
            bean.setShowTimeString(DateTimeUtils.getWeekShow(time))
            bean.setBottomString(DateTimeUtils.getWeekShow(time))
            list.add(bean)
            time = DateTimeUtils.AddDay(Date(time), 1).time
        }
        return list
    }

    fun getDataIntSleep(
        list: List<ChartBean>,
        barBeanList: List<SleepBarBean>?
    ): List<ChartBean> {
        if (list.isNullOrEmpty() || barBeanList.isNullOrEmpty()) return list
        val chartBeanList = mutableListOf<ChartBean>()
        for (i in list.indices) {
            val chartBean = list[i]
            mMaxRate = 0
            mMinRate = 0
            mAverageRate = 0f
            if (i == 0) continue
            val barBean: SleepBarBean? = getSleepBarBen(barBeanList, chartBean.time)
            val dataItemList: List<ChartBean.DataItem> = getDataItemInSleep(list, barBean)
            chartBean.setDataList(dataItemList)
            chartBean.setData(mAverageRate)
            chartBean.setMaxData(mMaxRate.toFloat())
            chartBean.setMinData(mMinRate.toFloat())
            chartBean.setAverageData(mAverageRate)
            chartBeanList.add(chartBean)
        }
        return chartBeanList
    }

    private fun getDayBean(dbEntity: HeartRateDBEntity?): ChartBean {
        mMaxRate = 0
        mMinRate = 0
        mAverageRate = 0f
        val bean = ChartBean()
        if (dbEntity == null) return bean
        val watchBase = MyApplication.instance().bleUtils.getConnectionWatch()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dbEntity.HeartRateDay
        calendar[Calendar.HOUR] = 23
        calendar[Calendar.MINUTE] = 59
        var list: List<ChartBean.DataItem?>? = null
//        if (watchBase != null && watchBase.isOneMinRate()) {
//            list = makeOneMinList(dbEntity, calendar.timeInMillis)
//        } else {
            list = makeList(dbEntity, calendar.timeInMillis)
//        }
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
        mAverageRate = 0f
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
        mAverageRate = 0f
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

    private fun getSleepBarBen(list: List<SleepBarBean>?, time: Long): SleepBarBean? {
        if (list == null || list.isEmpty()) return null
        for (barBean in list) {
            if (barBean.sleepDay === time) {
                return barBean
            }
        }
        return null
    }

    private fun getDataItemInSleep(
        list: List<ChartBean>?,
        barBean: SleepBarBean?
    ): List<ChartBean.DataItem> {
        val itemList = mutableListOf<ChartBean.DataItem>()
        if (list == null || list.isEmpty() || barBean == null) return itemList
        var index = 0
        val lenItems: List<SleepLenItem>? = getSleepLenItem(barBean)
        for (bean in list) {
            if (bean.getDataList() != null && !bean.getDataList().isEmpty()) {
                for (item in bean.getDataList()) {
                    if (isContain(item.dateTime, lenItems)) {
                        index++
                        mAverageRate += item.data.toFloat()
                        mMinRate =
                            if (mMinRate == 0 || mMinRate > item.data) item.data.toInt() else mMinRate
                        mMaxRate =
                            if (mMaxRate == 0 || mMaxRate < item.data) item.data.toInt() else mMaxRate
                        itemList.add(item)
                        //                        LogUtils.i(TAG + " sleep time " + DateTimeUtils.s_long_2_str(item.dateTime, DateTimeUtils.f_format) + " data == " + item.data);
                    }
                }
            }
        }
        if (index == 0) index = 1
        mAverageRate /= index
        return itemList
    }

    private fun getSleepLenItem(bean: SleepBarBean?): List<SleepLenItem>? {
        if (bean == null) return null
        val list = mutableListOf<SleepLenItem>()
        if (bean.list != null && !bean.list.isEmpty()) {
            var lenItem: SleepLenItem? = null
            for (item in bean.list) {
                //1 深睡 //2 浅睡 //3 清醒 //4 入睡 //5 熬夜
                if (item.getSleepType() === SleepItem.DEEP_SLEEP_TYPE || item.getSleepType() === SleepItem.LIGHT_SLEEP_TYPE || item.getSleepType() === SleepItem.WAKE_SLEEP_TYPE || item.getSleepType() === SleepItem.FALL_SLEEP_TYPE || item.getSleepType() === SleepItem.END_SLEEP_TYPE || item.getSleepType() === SleepItem.REM_SLEEP_TYPE) {
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
        if (list == null || list.isEmpty()) return false
        for (lenItem in list) {
//            LogUtils.i(" isContain " + DateTimeUtils.s_long_2_str(time, DateTimeUtils.f_format) + " " + DateTimeUtils.s_long_2_str(lenItem.getStartTime(), DateTimeUtils.f_format)
//                    + " " + DateTimeUtils.s_long_2_str(lenItem.getEndTime(), DateTimeUtils.f_format));
            if (time >= lenItem.startTime && time <= lenItem.endTime) {
                return true
            }
        }
        return false
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