package com.healthy.rvigor.adapter

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.healthy.rvigor.R
import com.healthy.rvigor.util.LogUtils
import java.util.Calendar

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/10 21:28
 * @UpdateRemark:
 */
class DayAdapter : BaseQuickAdapter<Long, QuickViewHolder>() {

    var selDay = 0L

    var selPosition = 0

    var calendar: Calendar? = null

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Long?) {

        if (calendar == null) calendar = Calendar.getInstance()

        calendar?.timeInMillis = item ?: 0L
        var year = calendar?.get(Calendar.YEAR)
        var month = calendar?.get(Calendar.MONTH)?.plus(1)
        var day = calendar?.get(Calendar.DAY_OF_MONTH)

        calendar?.timeInMillis = System.currentTimeMillis()
        var curYear = calendar?.get(Calendar.YEAR)
        var curMonth = calendar?.get(Calendar.MONTH)?.plus(1)
        var curDay = calendar?.get(Calendar.DAY_OF_MONTH)

        calendar?.timeInMillis = if (selDay == 0L) System.currentTimeMillis() else selDay
        var selYear = calendar?.get(Calendar.YEAR)
        var selMonth = calendar?.get(Calendar.MONTH)?.plus(1)
        var selDay = calendar?.get(Calendar.DAY_OF_MONTH)

        if (year == curYear && month == curMonth && day == curDay) {
            holder.setText(R.id.tv_day, context.resources.getString(R.string.today))
        }else if (item == -1L){
            holder.setText(R.id.tv_day, "")
        } else {
            holder.setText(R.id.tv_day, "$month/$day")
        }
        if (year == selYear && month == selMonth && day == selDay){
            selPosition = holder.absoluteAdapterPosition
//            LogUtils.i(" PageAdapter absoluteAdapterPosition $selPosition ")
        }
        holder.setVisible(R.id.img_line, (year == selYear && month == selMonth && day == selDay))
        holder.setGone(R.id.img_date, !(year == selYear && month == selMonth && day == selDay))
        holder.setTextColor(R.id.tv_day, if ((year == selYear && month == selMonth && day == selDay)) Color.parseColor("#6DFFE9")
        else Color.parseColor("#7C7D8C"))
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_day, parent)
    }
}