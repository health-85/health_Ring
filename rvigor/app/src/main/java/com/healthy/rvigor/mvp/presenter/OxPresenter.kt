package com.healthy.rvigor.mvp.presenter

import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.dao.entity.SpoDBEntity
import com.healthy.rvigor.dao.executor.QuerySpoInfoExecutor
import com.healthy.rvigor.dao.util.AppDaoManager
import com.healthy.rvigor.mvp.contract.IOxContract
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.JsonArrayUtils
import com.healthy.rvigor.util.JsonUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.NumberUtils
import com.healthy.rvigor.util.ValidRule
import com.healthy.rvigor.util.ViewDataUtil
import com.healthy.rvigor.view.SpecDateSelectedView
import java.util.Calendar
import java.util.Date

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/22 19:16
 * @UpdateRemark:
 */
class OxPresenter : BasePresenterImpl<IOxContract.View>(),
    IOxContract.Presenter {

    companion object {
        const val TAG = "OxPresenter"
    }

    private var mMaxOx = 0f
    private var mMinOx = 0f
    private var mAverageOx = 0f
    private var mIndexOx = 0f

    override fun getOxData(sender: SpecDateSelectedView?) {
        if (sender?.timeMode == SpecDateSelectedView.TimeMode.Day) { //如果是天
            //日血氧查询
            queryOxDayData(sender)
        } else if (sender?.timeMode == SpecDateSelectedView.TimeMode.Week) { //如果是周
            //周血氧查询
            queryOxWeekData(sender)
        } else if (sender?.timeMode == SpecDateSelectedView.TimeMode.Month) { //如果是月
            //月血氧查询
            queryOxMonthData(sender)
        }
    }

    private fun queryOxDayData(sender: SpecDateSelectedView?) {

        if (sender == null) return

        val startTime = sender.datestart.time
        val endTime = sender.dateend.time

        LogUtils.i(
            " ${TAG} queryOxDayData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val executor = QuerySpoInfoExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    val dBEntities: List<SpoDBEntity> = result as List<SpoDBEntity>
                    if (dBEntities.isNullOrEmpty()) {
                        view.onOxData(null, null, 0, 0, 0)
                    } else {
                        var index = 0
                        var average = 0f
                        var maxOx = 0f
                        var minOx = 0f
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
                                    var dateTime = curr.getLong("datetime", 0L)
                                    val isSleepOx = curr.getBoolean("sleepOx", false)
                                    calendar.timeInMillis = dateTime
                                    calendar.set(Calendar.MINUTE, 0)
                                    calendar.set(Calendar.SECOND, 0)
                                    dateTime = calendar.timeInMillis

                                    LogUtils.i(
                                        " queryOxDayData dateTime " + DateTimeUtils.s_long_2_str(
                                            dateTime,
                                            DateTimeUtils.f_format
                                        )
                                                + " spo " + spo
                                    )

                                    val hour = calendar[Calendar.HOUR_OF_DAY]
                                    if (spo > 10) {
                                        index++
                                        average += spo.toFloat()
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
                                bean.color = ViewDataUtil.getOxDataColor(bean.data.toInt())
                            }
                            heatMap[key] = bean
                        }
                        if (index == 0) index = 1
                        average /= index.toFloat()

                        if (minOx < 10) minOx = maxOx

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
                        view.onOxData(
                            viewList,
                            lastItem,
                            minOx.toInt(),
                            maxOx.toInt(),
                            average.toInt()
                        )
                    }
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }

            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }

    private fun queryOxWeekData(sender: SpecDateSelectedView?) {

        if (sender == null) return

        val startTime = sender.datestart.time
        val endTime = sender.dateend.time

        LogUtils.i(
            " $TAG queryOxDayData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val executor = QuerySpoInfoExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    val dBEntities: List<SpoDBEntity> = result as List<SpoDBEntity>
                    if (dBEntities.isNullOrEmpty()) {
                        view.onOxData(null, null, 0, 0, 0)
                    } else {
                        var lastBean: MainViewItem? = null
                        val calendar = Calendar.getInstance()
                        calendar.time = sender.datestart
                        var date: Date = sender.datestart

                        mIndexOx = 0f
                        mAverageOx = 0f

                        mMinOx = 0f
                        mMaxOx = 0f

                        var viewList = mutableListOf<MainViewItem>()

                        while (date.time < sender.dateend.time) {
                            val item: MainViewItem = getSpoDBEntity(dBEntities, date.time)
                            item.showTimeString = DateTimeUtils.getWeekShow(date.time)
                            viewList.add(item)
                            if (item.data > 0 && item.data !== MainViewItem.EMPTY.toFloat()) {
                                lastBean = item
                            }
                            date = DateTimeUtils.AddDay(date, 1)
                        }

                        if (mIndexOx == 0f) mIndexOx = 1f
                        mAverageOx /= mIndexOx

                        if (mMinOx < 10) mMinOx = mMaxOx

                        view.onOxData(
                            viewList, lastBean, mMinOx.toInt(), mMaxOx.toInt(),
                            mAverageOx.toInt()
                        )
                    }
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }

            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }

    private fun queryOxMonthData(sender: SpecDateSelectedView?) {
        if (sender == null) return

        val startTime = sender.datestart.time
        val endTime = sender.dateend.time

        LogUtils.i(
            " $TAG queryOxDayData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val executor = QuerySpoInfoExecutor(
            startTime,
            endTime,
            object : AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    val dBEntities: List<SpoDBEntity> = result as List<SpoDBEntity>
                    if (dBEntities.isNullOrEmpty()) {
                        view.onOxData(null, null, 0, 0, 0)
                    } else {

                        var lastBean: MainViewItem? = null

                        mIndexOx = 0f
                        mAverageOx = 0f
                        mMaxOx = 0f
                        mMinOx = 0f

                        val startdate: Date = sender.datestart
                        val calendar = Calendar.getInstance()
                        calendar.time = startdate

                        var index = 1
                        var viewList = mutableListOf<MainViewItem>()

                        while (calendar.timeInMillis < sender.dateend.time) {
                            val bean: MainViewItem =
                                getSpoDBEntity(dBEntities, calendar.timeInMillis)
                            bean.showTimeString =
                                (calendar[Calendar.MONTH] + 1).toString() + "/" + calendar[Calendar.DAY_OF_MONTH]
                            viewList.add(bean)
                            if (bean.data !== MainViewItem.EMPTY.toFloat() && bean.data > 0) {
                                lastBean = bean
                            }
                            calendar.time = DateTimeUtils.getDateTimeDatePart(
                                DateTimeUtils.AddDay(
                                    sender.datestart,
                                    index
                                )
                            )
                            index++
                        }
                        if (mIndexOx == 0f) mIndexOx = 1f
                        mAverageOx /= mIndexOx

                        if (mMinOx < 10) mMinOx = mMaxOx

                        view.onOxData(
                            viewList, lastBean, mMinOx.toInt(), mMaxOx.toInt(),
                            mAverageOx.toInt()
                        )
                    }
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }

            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }

    private fun getSpoDBEntity(dbEntities: List<SpoDBEntity>?, timeDay: Long): MainViewItem {
        val item = MainViewItem()
        item.time = timeDay
        item.data = MainViewItem.EMPTY.toFloat()

        var min = 0f
        var max = 0f

        if (!dbEntities.isNullOrEmpty()) {
            for (i in dbEntities.indices) {
                val curr = dbEntities[i]
                if (curr.SpoDay === timeDay) {
                    var averageTemp = 0f
                    var index = 0
                    val jsonArrayUtils = JsonArrayUtils(dbEntities[i].spoJsonData)
                    if (jsonArrayUtils.length() > 0) {
                        for (j in 0 until jsonArrayUtils.length()) {
                            val jsonObject = jsonArrayUtils.getJsonObject(j)
                            val temp = NumberUtils.fromStringToFloat(
                                jsonObject.getString("spo"),
                                MainViewItem.EMPTY.toFloat()
                            )
                            val time = jsonObject.getLong("datetime", 0L)
                            val isSleepOx = jsonObject.getBoolean("sleepOx", false)
                            //                            LogUtils.i(" Week temp == " + temp);
                            if (temp != MainViewItem.EMPTY.toFloat() && temp > 10 && ValidRule.getInstance()
                                    .isValidOx(temp) /*&& !isSleepOx*/
                            ) {
//                                LogUtils.i(" time " + DateTimeUtils.s_long_2_str(time, DateTimeUtils.day_format) + " spo " + temp );
                                mIndexOx++
                                mAverageOx += temp
                                mMinOx = if (mMinOx == 0f || mMinOx > temp) temp else mMinOx
                                mMaxOx = if (mMaxOx == 0f || mMaxOx < temp) temp else mMaxOx

                                min = if (min == 0f || min > temp) temp else min
                                max = if (max == 0f || max < temp) temp else max

                                item.list.add(temp)
                                averageTemp += temp
                                index++
                            }
                        }
                    }
                    if (averageTemp == 0f) {
                        item.data = MainViewItem.EMPTY.toFloat()
                    } else {
                        item.data = averageTemp / index
                    }
                    break
                }
            }
        }
        item.minData = min
        item.maxData = max
        return item
    }
}