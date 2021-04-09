package com.ashlikun.okhttputils.simple.retrofit

import com.ashlikun.okhttputils.http.ExecuteCall
import com.ashlikun.okhttputils.http.HttpUtils
import com.ashlikun.okhttputils.http.request.HttpRequest
import com.ashlikun.okhttputils.http.response.HttpResponse
import com.ashlikun.okhttputils.http.response.HttpResult
import com.ashlikun.okhttputils.retrofit.*
import com.ashlikun.orm.db.annotation.Default


/**
 * 作者　　: 李坤
 * 创建时间: 2017/8/10　9:31
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：请求mode类
 */
fun String.requestGet(): HttpRequest = HttpRequest.get(this)
fun String.requestPost(): HttpRequest = HttpRequest.post(this)
interface ApiOther {
    companion object {
        val api: ApiOther by lazy { Retrofit.get().create(ApiOther::class.java) }
    }

    suspend fun testx(handle: Int
    ): HttpResponse? {
        return HttpResponse("{}")
    }

    @GET("https://tapi-sip.510gow.com/{aaa}?action=recommend")
    suspend fun test(
            @PathField("aaa")
            aaa:String,
            @Field(key = "news_id")
            tikit: Int,
    ): HttpResponse

}
