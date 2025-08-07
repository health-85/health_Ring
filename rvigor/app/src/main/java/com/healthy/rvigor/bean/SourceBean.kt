package com.healthy.rvigor.bean

import com.smart.adapter.interf.SmartFragmentTypeExEntity

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/20 11:51
 * @UpdateRemark:
 */
data class SourceBean(
    var id: Int,
    var type: Int,
    var time: Long = System.currentTimeMillis()
) : SmartFragmentTypeExEntity() {
    override fun getFragmentType(): Int {
        return type
    }

}
