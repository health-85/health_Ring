package com.healthy.rvigor.bean;

import android.text.TextUtils
import com.healthy.rvigor.util.DateTimeUtils

data class HeartDataInfo(val id : Long, val consumerId : Long, val str : String, val created : String, val updated : String, val deviceName : String, val heartRate : Int) : Comparable<HeartDataInfo>{
    override fun compareTo(other: HeartDataInfo): Int {
        if (TextUtils.isEmpty(created)) return 0
        return try {
            if (!TextUtils.isEmpty(this.created) && !TextUtils.isEmpty(other.created)){
                val startTime = DateTimeUtils.s_str_to_long(this.created, DateTimeUtils.f_format)
                val otherStartTime = DateTimeUtils.s_str_to_long(other.created, DateTimeUtils.f_format)
                if (startTime > otherStartTime){
                    1
                }else{
                    -1
                }
            }else{
                return 0
            }
        }catch (e : Exception){
            e.printStackTrace()
            0
        }
    }

}
