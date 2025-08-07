package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/24 20:33
 * @UpdateRemark:
 */
interface IHeartMeasureContract {
    interface View : IBaseView {

    }

    interface Presenter : IBasePresenter<IBaseView> {

    }
}