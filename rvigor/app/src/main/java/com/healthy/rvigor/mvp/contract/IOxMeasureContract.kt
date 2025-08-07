package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/22 21:31
 * @UpdateRemark:
 */
interface IOxMeasureContract {

    interface View : IBaseView {

    }
    interface Presenter : IBasePresenter<IBaseView> {

    }
}