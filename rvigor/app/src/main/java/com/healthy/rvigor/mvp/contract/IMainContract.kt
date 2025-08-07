package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBaseModel
import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/6 18:20
 * @UpdateRemark:
 */
interface IMainContract {

    interface View : IBaseView {

    }

    interface Presenter : IBasePresenter<IBaseView> {

    }

}