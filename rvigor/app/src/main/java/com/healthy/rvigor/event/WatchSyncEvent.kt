package com.healthy.rvigor.event

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/29 20:02
 * @UpdateRemark:   是否同步
 */
data class WatchSyncEvent(val isSync : Boolean, val progress : Int)