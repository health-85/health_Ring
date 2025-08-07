package com.healthy.rvigor.bean;

import com.sw.watches.bean.AlarmInfo

data class ImportantItem(var id : Long, var time : Long, var hour : Int, var min : Int, var name : String, var open : Boolean, var data : AlarmInfo, var weekIndexList : List<Int>){
    data class ImportantWeek(var week : String, var check: Boolean)
}
