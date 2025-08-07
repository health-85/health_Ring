package com.healthy.rvigor.net

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/6 9:56
 * @UpdateRemark:
 */
class BaseResponse<T>{

    var code = 0

    var msg: String? = null

    var message: String? = null

    private val status = 0

    private val path: String? = null

    private val timestamp: String? = null

    private val success: String? = null

    var data: T? = null

    var created: Long = 0

    var result: String? = null
}
