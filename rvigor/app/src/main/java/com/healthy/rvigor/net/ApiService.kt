package com.healthy.rvigor.net

import com.healthy.rvigor.bean.Aliyun
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/6 9:55
 * @UpdateRemark:
 */
interface ApiService {

    /**
     * 获取签名
     *
     * @return
     */
    @FormUrlEncoded
    @POST("user-service/sms/getSign")
    fun getSign(@Field("mobile") mobile: String): Observable<BaseResponse<String>>

    /**
     * APP端发送验证码
     *
     * @return
     */
    @POST("user-service/sms/sendAPP")
    fun sendAPP(@Body body: RequestBody): Observable<BaseResponse<Any>>

    /**
     * 阿里获取临时签名
     *
     * @return
     */
    @GET("public-service/signature/get")
    fun getAliyunKey(): Observable<BaseResponse<Aliyun>>
}