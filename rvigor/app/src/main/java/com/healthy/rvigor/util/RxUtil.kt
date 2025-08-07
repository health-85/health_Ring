package com.healthy.rvigor.util

import android.os.Looper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/6 9:49
 * @UpdateRemark:
 */
object RxUtil {

    fun IoToMainObserve(): ObservableTransformer<Any, Any>? {
        return ObservableTransformer<Any, Any> { upstream ->
            if (Looper.myLooper() != null || Looper.myLooper() == Looper.getMainLooper()) {
                return@ObservableTransformer upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            } else {
                return@ObservableTransformer upstream.observeOn(AndroidSchedulers.mainThread())
            }
        }
    }


}