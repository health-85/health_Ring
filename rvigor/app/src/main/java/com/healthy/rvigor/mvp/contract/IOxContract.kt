package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.view.SpecDateSelectedView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/22 19:15
 * @UpdateRemark:
 */
interface IOxContract {


    interface View : IBaseView {
        fun onOxData(
            itemList: List<MainViewItem>?,
            lastItem: MainViewItem?,
            min: Int,
            max: Int,
            average: Int
        )
    }


    interface Presenter : IBasePresenter<IBaseView> {

        fun getOxData(sender: SpecDateSelectedView?)

    }
}