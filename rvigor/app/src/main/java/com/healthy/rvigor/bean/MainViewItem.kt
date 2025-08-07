package com.healthy.rvigor.bean

import android.graphics.Rect

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/17 9:46
 * @UpdateRemark:
 */
class MainViewItem {

    //显示的文字
    var showTimeString = ""

    //数值
    var data = 0f

    var color = 0

    //日期
    var time: Long = 0

    var minData = 0f

    var maxData = 0f

    var isOneMin = false

    var list = mutableListOf<Float>()

    //当前绘制的地方
    var currentDrawRect: Rect = Rect(0, 0, 0, 0)

    companion object {
        var EMPTY = Int.MIN_VALUE
    }

    constructor(){

    }

    constructor(
        data: Float,
        time: Long,
        showTimeString: String
    ) {
        this.data = data
        this.time = time
        this.showTimeString = showTimeString
    }
}