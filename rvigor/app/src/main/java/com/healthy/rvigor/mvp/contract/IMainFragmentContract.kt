package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthy.rvigor.bean.MainDataBean

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/10 16:44
 * @UpdateRemark:
 */
interface IMainFragmentContract {

    interface View : IBaseView {
//
//        fun onTabData(tabData : List<Long>)
//
//        fun onDataList(dataList : List<MainDataBean>)
    }

    interface Presenter : IBasePresenter<IBaseView> {
//
//        fun getTabDataList()
//
//        fun getDataList()
    }
}