package com.healthy.rvigor.watch

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/7 19:24
 * @UpdateRemark:   解析数据监听
 */
interface IWatchFunctionDataCallBack {

    /**
     *
     * @param watch
     * @param functionName
     * @param bean
     */
    fun WatchDataArrived(watch: WatchBase?, functionName: String?, bean: Any?)
}