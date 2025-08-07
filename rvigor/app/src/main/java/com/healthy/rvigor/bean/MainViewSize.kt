package com.healthy.rvigor.bean

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/17 9:47
 * @UpdateRemark:
 */
class MainViewSize() {

    var x = 0f
    var y = 0f
    var time = 0L
    var data = 0f
    var isOneMin = false
    var item : MainViewItem? = null

    constructor(x: Float, y: Float) : this() {
        this.x = x
        this.y = y
    }

    constructor(x: Float, y: Float, data: Float) : this() {
        this.x = x
        this.y = y
        this.data = data
    }

    constructor(x: Float, y: Float, data: Float, time : Long, isOneMin : Boolean) : this() {
        this.x = x
        this.y = y
        this.time = time
        this.isOneMin = isOneMin
        this.data = data
    }
}