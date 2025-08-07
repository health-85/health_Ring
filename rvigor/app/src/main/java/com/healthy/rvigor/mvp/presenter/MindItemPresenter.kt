package com.healthy.rvigor.mvp.presenter

import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.dao.entity.EmotionDBEntity
import com.healthy.rvigor.dao.entity.PressureDBEntity
import com.healthy.rvigor.dao.entity.TireDBEntity
import com.healthy.rvigor.dao.executor.QueryEmotionExecutor
import com.healthy.rvigor.dao.executor.QueryPressureExecutor
import com.healthy.rvigor.dao.executor.QueryTireExecutor
import com.healthy.rvigor.dao.util.AppDaoManager
import com.healthy.rvigor.mvp.contract.IMindItemContract
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.JsonArrayUtils
import com.healthy.rvigor.util.JsonUtils
import com.healthy.rvigor.util.LogUtils
import java.lang.Exception
import java.util.Calendar
import java.util.Date

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/7/28 13:42
 * @UpdateRemark:
 */
class MindItemPresenter : BasePresenterImpl<IMindItemContract.View>(),
    IMindItemContract.Presenter {

    override fun getMindData(day: Long) {

        getPressureData(day)

        getTireData(day)

        getEmotionData(day)
    }

    private fun getPressureData(day: Long) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " ${MainItemPresenter.TAG} getPressureData startTime " + DateTimeUtils.s_long_2_str(
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
                        view.onPressureData(null, null, 0f)
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
                        val list = mutableListOf<MainViewItem>()
                        for (i in 0..23) {
                            if (heatMap.containsKey(i)) {
                                val bean: MainViewItem? = heatMap[i]
                                if (bean != null) {
                                    lastItem = bean
                                    list.add(bean)
                                }else{
                                    list.add(MainViewItem())
                                }
                            }else{
                                list.add(MainViewItem())
                            }
                        }
                        view.onPressureData(list, lastItem, average)
                    }
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }

            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }

    private fun getTireData(day: Long) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " ${MainItemPresenter.TAG} getTireData startTime " + DateTimeUtils.s_long_2_str(
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
                        view.onTireData(null, null, 0f)
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

                        val list = mutableListOf<MainViewItem>()
                        var lastItem: MainViewItem? = null
                        for (i in 0..23) {
                            if (heatMap.containsKey(i)) {
                                val bean: MainViewItem? = heatMap[i]
                                if (bean != null) {
                                    lastItem = bean
                                    list.add(lastItem)
                                }else{
                                    list.add(MainViewItem())
                                }
                            }else{
                                list.add(MainViewItem())
                            }
                        }
                        view.onTireData(list, lastItem, average)
                    }
                }

                override fun OnError(ex: Exception?) {
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
            " ${MainItemPresenter.TAG} getEmotionData startTime " + DateTimeUtils.s_long_2_str(
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
                        view.onEmotionData(null, null, 0f)
                    } else {
//                        var index = 0
//                        var average = 0f
//                        var totalEmotion = 0f
//                        var lastItem = MainViewItem()
//
//                        val calendar = Calendar.getInstance()
//                        calendar.timeInMillis = startTime
//                        var tempTime = calendar.timeInMillis
//
//                        var dataIndex = 0
//                        var timeIndex = 0
//
//                        val list = mutableListOf<MainViewItem>()
//                        var dbEntity = dBEntities[dBEntities.size - 1]
//
//                        while (tempTime <= endTime) {
//                            var emotion = 0
//                            val jsonArrayUtils = JsonArrayUtils(dbEntity.tempJsonData)
//                            while (dataIndex < jsonArrayUtils.length()) {
//                                val curr: JsonUtils = jsonArrayUtils.getJsonObject(dataIndex)
//                                val temp = curr.getInt("temp", 0)
//                                val time = curr.getLong("datetime", 0)
//                                LogUtils.i(
//                                    MainItemPresenter.TAG,
//                                    " getEmotionData time ${
//                                        DateTimeUtils.s_long_2_str(
//                                            time,
//                                            DateTimeUtils.f_format
//                                        )
//                                    } emotion ${temp} dataIndex $dataIndex"
//                                )
//                                emotion = if (time <= tempTime) {
//                                    temp
//                                } else {
//                                    break
//                                }
//                                dataIndex++
//                            }
//                            val showTime = timeIndex.toString() + ":00-" + (timeIndex + 1) + ":00"
//                            val item = MainViewItem()
//                            item.data = emotion.toFloat()
//                            item.time = tempTime
//                            item.showTimeString = showTime
//                            if (emotion > 0) {
//                                index++
//                                lastItem = item
//                                totalEmotion += emotion
//                            }
//                            list.add(item)
//                            LogUtils.i(
//                                MainItemPresenter.TAG,
//                                " tempTime " + DateTimeUtils.s_long_2_str(
//                                    tempTime,
//                                    DateTimeUtils.f_format
//                                ) + " emotion " + emotion + " dataIndex " + dataIndex
//                            )
//                            timeIndex++
//                            calendar.add(Calendar.HOUR_OF_DAY, 1)
//                            tempTime = calendar.timeInMillis
//                        }
//
//                        if (index <= 0) index = 1
//                        average = totalEmotion / index.toFloat()
//                        view.onEmotionData(list, lastItem, average)

                        var index = 0
                        var average = 0f
                        val heatLenMap = mutableMapOf<Int, Int>()
                        val heatMap = mutableMapOf<Int, MainViewItem>()
                        for (i in dBEntities.indices) {
                            val entity: EmotionDBEntity = dBEntities[i]
                            val jsonArrayUtils = JsonArrayUtils(entity.tempJsonData)
                            if (jsonArrayUtils.length() > 0) {
                                val calendar = Calendar.getInstance()
                                for (j in 0 until jsonArrayUtils.length()) {
                                    val curr: JsonUtils = jsonArrayUtils.getJsonObject(j)
                                    val temp: Int = curr.getInt("temp", MainViewItem.EMPTY)
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

                        val list = mutableListOf<MainViewItem>()
                        var lastItem: MainViewItem? = null
                        for (i in 0..23) {
                            if (heatMap.containsKey(i)) {
                                val bean: MainViewItem? = heatMap[i]
                                if (bean != null) {
                                    lastItem = bean
                                    list.add(lastItem)
                                }else{
                                    list.add(MainViewItem())
                                }
                            }else{
                                list.add(MainViewItem())
                            }
                        }
                        view.onEmotionData(list, lastItem, average)
                    }
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }

            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }
}