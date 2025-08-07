package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthy.rvigor.bean.SleepBarBean

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/26 15:07
 * @UpdateRemark:
 */
interface ISleepItemContract {

    interface View : IBaseView {
        fun onSleepData(barBean: SleepBarBean?)
    }

    interface Presenter : IBasePresenter<IBaseView> {

        fun querySleepData(day : Long)
    }
}