package com.healthy.rvigor.util

import android.graphics.Color
import java.util.Calendar
import java.util.Date

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/23 17:49
 * @UpdateRemark:
 */
object ViewDataUtil {

    //获取血氧数据颜色
    fun getOxDataColor(data: Int) : Int{
        if (data in 90 .. 100){
            return Color.parseColor("#6DFFE9")
        }else if (data in 70 .. 89){
            return Color.parseColor("#FFC049")
        }else{
            return Color.parseColor("#BF3131")
        }
    }

    //获取心率数据颜色
    fun getHeartDataColor(data: Int) : Int{
        return Color.parseColor("#FE475A")
    }


    fun getWeekBottomString(startDate: Date?): List<String>? {
        val list: MutableList<String> = ArrayList()
        for (i in 0..6) {
            val time = DateTimeUtils.AddDay(startDate, i).time
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            val week =
                (calendar[Calendar.MONTH] + 1).toString() + "/" + calendar[Calendar.DAY_OF_MONTH]
            list.add(week)
        }
        return list
    }

    fun getMonthBottomString(startDate: Date?, endDate: Date?): List<String>? {
        val list: MutableList<String> = java.util.ArrayList()
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val month = calendar[Calendar.MONTH] + 1
        val days = DateTimeUtils.getMonthMaxDays(startDate) //天数
        var index = 1
        while (calendar.timeInMillis < (endDate?.time ?: 0)) {
            var barBottomS = ""
            if (index % 7 == 1) {
                barBottomS =
                    (calendar[Calendar.MONTH] + 1).toString() + "/" + calendar[Calendar.DAY_OF_MONTH]
            }
            list.add(barBottomS)
            val date = DateTimeUtils.AddDay(startDate, index)
            calendar.time = date
            index++
        }
        return list
    }
}