package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthy.rvigor.bean.MainViewItem

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/7/28 13:41
 * @UpdateRemark:
 */
interface IMindItemContract {

    interface View : IBaseView {
        fun onTireData(
            itemList: MutableList<MainViewItem>?,
            lastItem: MainViewItem?,
            average: Float
        )

        fun onPressureData(
            itemList: MutableList<MainViewItem>?,
            lastItem: MainViewItem?,
            average: Float
        )

        fun onEmotionData(
            itemList: MutableList<MainViewItem>?,
            lastItem: MainViewItem?,
            average: Float
        )
    }

    interface Presenter : IBasePresenter<IBaseView> {

        fun getMindData(day: Long)
    }

}