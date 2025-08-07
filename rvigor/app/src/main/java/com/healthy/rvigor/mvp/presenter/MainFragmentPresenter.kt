package com.healthy.rvigor.mvp.presenter

import android.content.Context
import android.graphics.Color
import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.bean.MainDataBean
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.dao.entity.StepDBEntity
import com.healthy.rvigor.dao.executor.QueryStepExecutor
import com.healthy.rvigor.dao.util.AppDaoManager
import com.healthy.rvigor.mvp.contract.IMainContract
import com.healthy.rvigor.mvp.contract.IMainFragmentContract
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/10 16:45
 * @UpdateRemark:
 */
class MainFragmentPresenter  : BasePresenterImpl<IMainFragmentContract.View>(),
    IMainFragmentContract.Presenter {

//    override fun getTabDataList() {
//        val endTime = System.currentTimeMillis()
//        var startTime = DateTimeUtils.AddDay(DateTimeUtils.getDateTimeDatePart(Date(endTime)), -30).time
//        var tempTime = startTime
//        val list = mutableListOf<Long>()
//        while (tempTime <= endTime){
//            list.add(tempTime)
//            tempTime = DateTimeUtils.AddDay(Date(tempTime), 1).time
//        }
//        list.add(-1)
//        view.onTabData(list)
//    }
//
//    override fun getDataList() {
//
//        val endTime = System.currentTimeMillis()
//        var startTime = DateTimeUtils.AddDay(DateTimeUtils.getDateTimeDatePart(Date(endTime)), -30).time
//
//
//
//        val list = mutableListOf<MainDataBean>()
//
//        val mainDataBean = MainDataBean()
//
//        mainDataBean.itemType = MainDataBean.MAIN_EXPRESSION
//        list.add(mainDataBean)
//
//        view.onDataList(list)
//    }

//    //查询本地运动数据
//    fun queryDayData(
//        context: Context?,
//        startTime: Long,
//        endTime: Long
//    ) {
//        if (sender == null) return
//        val watchBase = MainApplication.getInstance().adapetUtils.connectionWatch
//        val startTime = DateTimeUtils.getDateTimeDatePart(sender?.datestart).time
//        val endTime = DateTimeUtils.getDateTimeDatePart(sender?.dateend).time
//        LogUtils.i(
//            " startTime " + DateTimeUtils.s_long_2_str(startTime, DateTimeUtils.f_format)
//                    + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
//        )
//        val stepExecutor = QueryStepExecutor(
//            startTime,
//            endTime,
//            if (watchBase == null) "" else watchBase.deviceMacAddress,
//            userId,
//            "",
//            object : AppDaoManager.DBExecutor.IResult {
//                override fun OnSucceed(result: Any?) {
//                    val stepDBEntities: List<StepDBEntity> = result as List<StepDBEntity>
//                    if (stepDBEntities.isEmpty()) {
//                        listener.onQueryDayListener(null, null, 0, 0, 0, 0f, 0f)
//                    } else {
//
//                        var max = 0
//                        var min = 0
//                        var totalStep = 0f
//
//                        var stepDBEntity = stepDBEntities[0]
//                        var lastItem = BarLineUiView.DataItem()
//                        val itemList = mutableListOf<BarLineUiView.DataItem>()
//
//                        var calorie = stepDBEntity.stepCalorie.toFloat()
//                        var mileage = stepDBEntity.stepMileage.toFloat()
//
//                        val jsonArrayUtils = JsonArrayUtils(stepDBEntity.stepDataJsonArrayForTime)
//                        for (i in 0 until jsonArrayUtils.length()) {
//                            val curr = jsonArrayUtils.getJsonObject(i)
//                            var step = curr.getLong("step", 0)
//                            val showTime = i.toString() + ":00-" + (i + 1) + ":00"
//                            step = ValidRule.getInstance().getValidStep(step)
//                            val item = BarLineUiView.DataItem()
//                            if (step > 0) {
//                                totalStep += step
//                                lastItem = item
//                                max = if (max == 0 || max < step) step.toInt() else max
//                                min = if (min == 0 || min > step) step.toInt() else min
//                            } else {
//                                step = BarLineUiView.DataItem.EMPTY.toLong()
//                            }
//                            item.color = Color.parseColor("#F59519")
//                            item.data = step.toFloat()
//                            item.showTimeString = showTime
//                            itemList.add(item)
//                        }
//                        for (i in 1 until 100) {
//                            if (max < i * 1000) {
//                                max = i * 1000
//                                break
//                            }
//                        }
//
//                        if (!WatchBeanUtil.isV101Watch("") && !WatchBeanUtil.isTK12Watch("")){
//                            calorie = byteToCalorie(totalStep.toInt(), null)
//                            mileage = byteToKm(totalStep.toInt())
//                        }
//
//                        listener.onQueryDayListener(
//                            itemList,
//                            lastItem,
//                            max,
//                            min,
//                            totalStep.toInt(),
//                            calorie,
//                            mileage
//                        )
//                    }
//                }
//
//                override fun OnError(ex: Exception?) {
//                    ex?.printStackTrace()
//                }
//            }
//        )
//        MainApplication.getInstance().appDaoManager.ExecuteDBAsync(stepExecutor)
//    }
//
//    //查询本地月运动数据
//    fun queryMonthData(
//        context: Context?,
//        startTime: Long,
//        endTime: Long
//    ) {
//        LogUtils.i(
//            "queryMonthData startTime " + DateTimeUtils.s_long_2_str(
//                startTime,
//                DateTimeUtils.f_format
//            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
//        )
//        val stepExecutor = QueryStepExecutor(
//            startTime,
//            endTime,
//            object : AppDaoManager.DBExecutor.IResult {
//                override fun OnSucceed(result: Any?) {
//                    val stepDBEntities: List<StepDBEntity> = result as List<StepDBEntity>
//                    if (stepDBEntities.isEmpty()) {
//                        listener.onQueryMonthResult(
//                            arrayListOf(), null, 0, 0, 0, 0, 0,
//                            0, 0, 0, 0, 0, null, null
//                        )
//                        return
//                    }
//
//                    val calendar = Calendar.getInstance()
//                    calendar.timeInMillis = startTime
//                    var tempTime = calendar.timeInMillis
//
//                    //最大步数
//                    var max = 0
//                    var maxMsg = ""
//                    var maxStepTime = 0L
//                    //最小步数
//                    var min = 0
//                    //最大消耗
//                    var maxCalorie = 0
//                    var maxCalorieTime = 0L
//                    var maxCalorieMsg = ""
//
//                    //最小消耗
//                    var minCalorie = 0
//
//                    var index = 0
//
//                    //总步数
//                    var totalStep = 0f
//                    //平均步数
//                    var averageStep = 0
//                    //平均运动消耗
//                    var averageCalorie = 0
//                    //总运动消耗
//                    var totalCalorie = 0f
//                    //平均消耗
//                    var averageConsume = 0
//                    //总消耗
//                    var totalConsume = 0f
//
//                    var lastItem = MainViewItem()
//                    val itemList = mutableListOf<MainViewItem>()
//
//                    var dataIndex = 0
//
//                    //达标天数
//                    var userId = MyApplication.instance().appUserInfo.userInfo.id
//                    var reachDay = 0
//                    val reachStep = SPUtil.getData(
//                        MyApplication.instance(),
//                        SpConfig.TARGET_STEP + "_" + userId,
//                        5000
//                    ) as Int
//
//                    var consumeSize = 0
//                    while (tempTime < endTime) {
//                        consumeSize++
//                        var step = 0
//                        val dbEntity: StepDBEntity? = getStepDBEntity(stepDBEntities, tempTime)
//                        if (dbEntity != null && dbEntity.totalStep > 0) {
//                            step = dbEntity.totalStep.toInt()
//                            dataIndex++
//                        }
//                        val showTime = DateTimeUtils.getWeekShow(tempTime)
//                        val item = MainViewItem()
//                        item.data = step.toFloat()
//                        item.time = tempTime
//                        item.showTimeString = showTime
//                        itemList.add(item)
//                        if (step > 0) {
//                            index++
//                            lastItem = item
//                            totalStep += step
//                            if (step >= reachStep) {
//                                reachDay++
//                            }
//                            if (max == 0 || max < step) {
//                                maxStepTime = tempTime
//                            }
//                            max = if (max == 0 || max < step) step else max
//                            min = if (min == 0 || min > step) step else min
//
//                            if (maxCalorie == 0 || maxCalorie < item.calorie) {
//                                maxCalorieTime = tempTime
//                            }
//                            maxCalorie =
//                                if (maxCalorie == 0 || maxCalorie < item.calorie) item.calorie.roundToInt() else maxCalorie
//                            minCalorie =
//                                if (minCalorie == 0 || minCalorie > item.calorie) item.calorie.roundToInt() else minCalorie
//
//                        } else {
//                            totalConsume += item.metabolism
//                        }
//                        LogUtils.i(
//                            TAG,
//                            " tempTime " + DateTimeUtils.s_long_2_str(
//                                tempTime,
//                                DateTimeUtils.f_format
//                            ) + " step " + step + " dataIndex " + dataIndex + " consumeSize " + consumeSize
//                        )
//                        tempTime = DateTimeUtils.AddDay(Date(tempTime), 1).time
//                        calendar.timeInMillis = tempTime
//                    }
//
//                    if (itemList == null || itemList.isEmpty()) reachDay = ChartBean.EMPTY
//
//                    if (index == 0) index = 1
//                    averageStep = (totalStep / index.toFloat()).roundToInt()
//                    averageCalorie = (totalCalorie / index.toFloat()).roundToInt()
//                    averageConsume = (totalConsume / consumeSize).roundToInt()
//
//                    var stepReportBean: MotionReportBean? = null
//                    if (max > 0) {
//                        stepReportBean = MotionReportBean(
//                            maxStepTime,
//                            context.resources.getString(R.string.motion_month_max_step),
//                            max.toLong(),
//                            context.resources.getString(R.string.motion_step),
//                            MotionReportBean.MOTION_STEP_TYPE
//                        )
//                    }
//                    var calorieReportBean: MotionReportBean? = null
//                    if (maxCalorie > 0) {
//                        calorieReportBean = MotionReportBean(
//                            maxCalorieTime,
//                            context.resources.getString(R.string.motion_month_max_calorie),
//                            maxCalorie.toLong(),
//                            context.resources.getString(R.string.motion_kilocalorie),
//                            MotionReportBean.MOTION_CALORIE_TYPE
//                        )
//                    }
//
//                    listener.onQueryMonthResult(
//                        itemList,
//                        lastItem,
//                        totalStep.toInt(),
//                        averageStep,
//                        totalConsume.roundToInt(),
//                        averageConsume,
//                        averageCalorie,
//                        reachDay,
//                        if (max < 5000) 5000
//                        else if (max < 10000) 10000
//                        else max,
//                        min,
//                        if (maxCalorie < 100) 100 else maxCalorie,
//                        minCalorie,
//                        stepReportBean,
//                        calorieReportBean
//                    )
//
//                    LogUtils.i("$TAG cur totalStep $totalStep averageStep $averageStep averageConsume $averageConsume averageCalorie $averageCalorie reachDay $reachDay")
//
//                }
//
//                override fun OnError(ex: Exception?) {
//                    ex?.printStackTrace()
//                }
//            }
//        )
//        MyApplication.instance().appDaoManager.ExecuteDBAsync(stepExecutor)
//    }

    private fun getStepDBEntity(
        stepDBEntitys: List<StepDBEntity>,
        timeSleepday: Long
    ): StepDBEntity? {
        for (i in stepDBEntitys.indices) {
            val curr = stepDBEntitys[i]
            if (curr.stepDay == timeSleepday) {
                return curr
            }
        }
        return null
    }

}