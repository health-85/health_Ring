package com.healthy.rvigor

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/6 9:59
 * @UpdateRemark:
 */
object Constants {

    //开发环境
    private val ENV: Int = BuildConfig.Environment
    //1表示测试环境，2表示生产
    private const val ENV_TEST = 1
    private const val ENV_PRODUCT = 2

    //TAB开始时间
    const val START_TIME = "2024-05-01"

    const val EXTRA = "extra"
    const val EXTRA_BOOLEAN = "extra_boolean"

    const val FRAGMENT_TAG = "content_fragment_tag"
    //游客用户ID
    const val TOURIST_USER_ID = 1234567890L
    const val TOURIST_USER_ID_2 = 12345678900L
    //首页TAG
    const val MAIN_FRAGMENT_TAG = "main_fragment_tag"
    const val SLEEP_FRAGMENT_TAG = "sleep_fragment_tag"
    const val MOTION_FRAGMENT_TAG = "motion_fragment_tag"
    const val MY_FRAGMENT_TAG = "my_fragment_tag"

    //链接状态
    //扫描中
    const val WATCH_SCANNING = 1
    //扫描停止
    const val WATCH_SCAN_STOP = 2
    //链接中
    const val WATCH_CONNECTING = 3
    //已链接
    const val WATCH_CONNECTED = 4
    //链接失败
    const val WATCH_CONNECT_FAIL = 5
    //断开链接
    const val WATCH_DISCONNECT = 6

    //性别
    const val MALE = 1
    const val FEMALE = 2
    const val SECRECY = 3

    //默认步数
    const val DEFAULT_TARGET_STEP = 8000

    //所有类型
    const val ALL_TYPE = 0
    //首页数据类型
    const val MAIN_TYPE = 1
    //睡眠类型
    const val SLEEP_TYPE = 2
    //活动类型
    const val MOTION_TYPE = 3
    //心理健康
    const val MIND_TYPE = 4

    fun baseURL(): String {
        if (ENV == ENV_TEST) {
            return "http://47.106.141.138:9000/"
        }else if (ENV == ENV_PRODUCT) {
            return "https://app.uhealthtime.com/" // 生产
        }
        return "https://app.uhealthtime.com/"
    }

}