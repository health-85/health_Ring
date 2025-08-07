package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.bean.SleepBarBean
import com.healthy.rvigor.view.SpecDateSelectedView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/20 8:49
 * @UpdateRemark:
 */
interface IMainItemContract {

    interface View : IBaseView {

        fun onMotionData(
            itemList: List<MainViewItem>?,
            lastItem: MainViewItem?,
            min: Int,
            max: Int,
            totalStep: Int
        )

        fun onTireData(
            lastItem: MainViewItem?,
            average: Float
        )

        fun onPressureData(
            lastItem: MainViewItem?,
            average: Float
        )

        fun onEmotionData(
            lastItem: MainViewItem?,
            average: Float
        )

        fun onHeartData(
            itemList: List<MainViewItem>?,
            lastItem: MainViewItem?,
            min: Int,
            max: Int,
            average: Int
        )

        fun onOxData(
            itemList: List<MainViewItem>?,
            lastItem: MainViewItem?,
            min: Int,
            max: Int,
            average: Int
        )

        fun onSleepData(barBean: SleepBarBean?)

        fun onSleepHeartRate(rate: Int)

        fun onSleepOx(ox: Int)

        fun onLastSleepScore(sleepScore : Int)

        fun onLastMotionScore(motionScore : Float)

        fun onLastHeartScore(heartScore : Float)

        fun onLastOxScore(oxScore : Float)

        fun onLastAveragePressure(averagePressure : Float)

        fun onLastAverageTire(averageTire : Float)

    }

    interface Presenter : IBasePresenter<IBaseView> {

        fun getLastDayData(day: Long)

        fun getMotionData(day: Long, isLastDay: Boolean)

        fun getMindData(day: Long, isLastDay: Boolean)

        fun getHeartData(day: Long, isLastDay: Boolean)

        fun getSleepData(day: Long, isLastDay: Boolean)

        fun getOxData(day: Long, isLastDay: Boolean)

        fun getAbnormalHeartData(day: Long, normalHeartCount : Int)
    }

}