package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthy.rvigor.bean.MainViewItem

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/26 15:18
 * @UpdateRemark:
 */
interface IMotionItemContract {

    interface View : IBaseView {
        fun onMotionData(
            itemList: List<MainViewItem>?,
            lastItem: MainViewItem?,
            min: Int,
            max: Int,
            totalStep: Int
        )

        fun onStrengthData(low : Int, middle : Int, high : Int)
    }

    interface Presenter : IBasePresenter<IBaseView> {

        fun queryMotionData(day: Long)

        fun getStrengthData(day: Long)
    }
}