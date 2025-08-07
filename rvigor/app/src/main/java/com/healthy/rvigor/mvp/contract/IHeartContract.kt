package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthy.rvigor.bean.HeartDataInfo
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.view.SpecDateSelectedView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/24 19:56
 * @UpdateRemark:
 */
interface IHeartContract {

    interface View : IBaseView {

        fun onHeartDataListener(
            itemList: MutableList<MainViewItem>?,
            lastItem: MainViewItem?,
            max: Int,
            min: Int,
            average: Int
        )

        fun onHeartInSleepResult(averageSleepHeart: Int)

        fun onAbnormalHeartDayResult(abnormalHeart: Int, heartList: List<HeartDataInfo>?)

    }

    interface Presenter : IBasePresenter<IBaseView> {

        fun getHeartData(sender: SpecDateSelectedView?)

        fun getAbnormalHeartData(sender: SpecDateSelectedView?)
    }
}