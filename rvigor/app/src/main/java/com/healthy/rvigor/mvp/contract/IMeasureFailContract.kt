package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/23 10:15
 * @UpdateRemark:
 */
interface IMeasureFailContract {

    interface View : IBaseView {

    }

    interface Presenter : IBasePresenter<IBaseView> {

    }
}