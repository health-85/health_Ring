package com.healthy.rvigor.adapter

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.healthy.rvigor.R
import com.healthy.rvigor.bean.SleepOxBean

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/26 14:14
 * @UpdateRemark:
 */
class BreatheOxAdapter : BaseQuickAdapter<SleepOxBean, QuickViewHolder>(){

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: SleepOxBean?) {
        holder.setText(R.id.tv_oxygen_level, item?.oxTitle)
        holder.setText(R.id.tv_proportion, item?.oxProportion.toString())
        holder.setText(R.id.tv_duration, item?.duration)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_breathe_ox, parent)
    }
}