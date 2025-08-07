package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/16 9:36
 * @UpdateRemark:
 */
interface IUserInfoContract {

    interface View : IBaseView {

    }

    interface Presenter : IBasePresenter<IBaseView> {

    }
}