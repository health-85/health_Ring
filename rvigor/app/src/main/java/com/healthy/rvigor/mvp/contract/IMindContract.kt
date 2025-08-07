package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/7/28 13:11
 * @UpdateRemark:
 */
interface IMindContract {

    interface View : IBaseView {

    }

    interface Presenter : IBasePresenter<IBaseView> {

    }
}