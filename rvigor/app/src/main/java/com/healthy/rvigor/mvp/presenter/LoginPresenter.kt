package com.healthy.rvigor.mvp.presenter

import com.google.gson.Gson
import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.mvp.contract.ILoginContract
import com.healthy.rvigor.net.BaseResponse
import com.healthy.rvigor.net.BeanObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class LoginPresenter : BasePresenterImpl<ILoginContract.View/*, ILoginContract.Model*/>(),
    ILoginContract.Presenter {

    override fun getCode(phone: String) {
//        MyApplication.instance().getService().getSign(phone)
//            .flatMap {
//                val sign: String? = it.data
//                val map: MutableMap<String, Any> = HashMap()
//                map["mobile"] = phone
//                map["sign"] = sign ?: ""
//                val requestBody =
//                    RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(map))
//                MyApplication.instance().getService().sendAPP(requestBody)
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : BeanObserver<Any>(view) {
//                override fun onFailure(e: Throwable?, rawResponse: String?) {
//
//                }
//
//                override fun onSuccess(bean: BaseResponse<Any>?) {
//
//                }
//            })
    }

}