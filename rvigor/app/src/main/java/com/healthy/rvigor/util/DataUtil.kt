package com.healthy.rvigor.util

import com.healthy.rvigor.bean.SourceBean
import com.smart.adapter.SmartViewPager2Adapter
import com.smart.adapter.interf.SmartFragmentTypeExEntity
import java.util.Date
import kotlin.math.abs
import kotlin.random.Random

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/20 12:03
 * @UpdateRemark:
 */
object DataUtil {

    fun productDatas(
        index: Int,
        isLoadMore: Boolean,
        produceSize: Int = 10
    ): MutableList<SmartFragmentTypeExEntity> {
        var newDats = mutableListOf<SmartFragmentTypeExEntity>()

        if (isLoadMore) {
            for (i in index..index + (produceSize - 1)) {
                val time = DateTimeUtils.AddDay(Date(System.currentTimeMillis()), i).time
                newDats.add(SourceBean(i, 1, time))
            }
        } else {
            for (i in index - (produceSize - 1)..index) {
                val time = DateTimeUtils.AddDay(Date(System.currentTimeMillis()), i).time
                newDats.add(SourceBean(i, 1, time))
                LogUtils.i(" productDatas $i ")
            }
        }

        return newDats
    }

    fun productFrontDatas(mAdapter: SmartViewPager2Adapter): MutableList<SmartFragmentTypeExEntity> {
        return productDatas((mAdapter.getItem(0) as SourceBean).id - 1, false)
    }
}