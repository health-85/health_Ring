package com.healthy.rvigor.adapter

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.healthy.rvigor.R
import com.healthy.rvigor.bean.HeartDataInfo
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.view.SpecDateSelectedView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/25 18:07
 * @UpdateRemark:
 */
class AbnormalHeartAdapter : BaseQuickAdapter<HeartDataInfo, QuickViewHolder>() {


    var mTimeMode: SpecDateSelectedView.TimeMode? = null

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: HeartDataInfo?) {
        if (mTimeMode == SpecDateSelectedView.TimeMode.Day) {
            var heartTimeDate = DateTimeUtils.convertStrToDateForThisProject(item?.created)
            holder.setText(
                R.id.tv_time,
                DateTimeUtils.s_long_2_str(heartTimeDate.time, DateTimeUtils.hms_format)
            )
        } else {
            holder.setText(R.id.tv_time, item?.created)
        }
        holder.setText(R.id.tv_rate, item?.heartRate.toString())
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_abnormal_heart, parent)
    }
}