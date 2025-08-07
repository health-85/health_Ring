package com.healthy.rvigor.mvp.presenter

import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.mvp.contract.IHeartContract
import com.healthy.rvigor.mvp.contract.IHeartMeasureContract

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/24 20:33
 * @UpdateRemark:
 */
class HeartMeasurePresenter: BasePresenterImpl<IHeartMeasureContract.View>(),
    IHeartMeasureContract.Presenter {
}