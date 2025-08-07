package com.healthy.rvigor.mvp.presenter

import android.graphics.Color
import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.dao.entity.StepDBEntity
import com.healthy.rvigor.dao.entity.StrengthDBEntity
import com.healthy.rvigor.dao.executor.QueryStepExecutor
import com.healthy.rvigor.dao.executor.QueryStrengthExecutor
import com.healthy.rvigor.dao.util.AppDaoManager
import com.healthy.rvigor.mvp.contract.IMotionContract
import com.healthy.rvigor.mvp.contract.IMotionItemContract
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.JsonArrayUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.util.ValidRule
import java.util.Date

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/26 15:19
 * @UpdateRemark:
 */
class MotionItemPresenter : BasePresenterImpl<IMotionItemContract.View>(),
    IMotionItemContract.Presenter {

    override fun queryMotionData(day: Long) {
        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " ${MainItemPresenter.TAG} getMotionData startTime " + DateTimeUtils.s_long_2_str(
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
                        view.onMotionData(null, null, 0, 0, 0)
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

                        view.onMotionData(itemList, lastItem, min, max, totalStep.toInt())
                    }
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }
            }
        )
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(stepExecutor)
    }

    override fun getStrengthData(day: Long) {

        if (day <= 0L) return

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(day)).time
        val endTime = DateTimeUtils.AddDay(Date(startTime), 1).time

        LogUtils.i(
            " ${MainItemPresenter.TAG} getMotionData startTime " + DateTimeUtils.s_long_2_str(
                startTime,
                DateTimeUtils.f_format
            ) + " endTime " + DateTimeUtils.s_long_2_str(endTime, DateTimeUtils.f_format)
        )

        val executor = QueryStrengthExecutor(
            startTime,
            endTime,
            object :
                AppDaoManager.DBExecutor.IResult {
                override fun OnSucceed(result: Any?) {
                    if (result == null) {
                        view.onStrengthData(0,0,0)
                        return
                    }
                    val dbEntityList: List<StrengthDBEntity> = result as List<StrengthDBEntity>
                    if (dbEntityList == null || dbEntityList.isEmpty()) {
                        view.onStrengthData(0,0,0)
                        return
                    }
                    val dbEntity = dbEntityList[0]
                    view.onStrengthData(dbEntity.inLow, dbEntity.inCentre, dbEntity.inHigh)
                }

                override fun OnError(ex: Exception?) {
                    ex?.printStackTrace()
                }
            })
        MyApplication.instance().appDaoManager?.ExecuteDBAsync(executor)
    }
}