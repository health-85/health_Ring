package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBaseModel
import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthy.rvigor.net.BaseResponse
import io.reactivex.rxjava3.core.Observable

interface ILoginContract  {

    interface View : IBaseView {

        fun onFailed(e: String?)

        fun onLoginSuccess()
    }


    interface Presenter : IBasePresenter<IBaseView> {

        fun getCode(phone: String)

    }
}