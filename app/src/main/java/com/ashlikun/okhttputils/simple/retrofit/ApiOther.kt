package com.ashlikun.okhttputils.simple.retrofit

import com.ashlikun.okhttputils.http.request.HttpRequest
import com.ashlikun.okhttputils.http.response.HttpResponse
import com.ashlikun.okhttputils.retrofit.*
import com.ashlikun.okhttputils.simple.data.WeiJinModel


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
interface ApiOther : BaseApi {
    companion object {
        val api: ApiOther by lazy {
            Retrofit.get().create(ApiOther::class.java)
        }
    }

    suspend fun testx(handle: Int
    ): HttpResponse? {
        return HttpResponse("{}")
    }

    @Get("https://api-sip.510gow.com/interface?action=recommend")
    suspend fun test(
            news_id: Int,
            ddd: WeiJinModel
    ): HttpResponse

    suspend fun test2(
            @Field(key = "news_id")
            tikit: Int,
    ): HttpResponse

    @Action("action:recommend")
    suspend fun test3(
            @Field(key = "news_id")
            tikit: Int,
    ): HttpResponse

    @Action("recommend")
    suspend fun test4(
            @Field(key = "news_id")
            tikit: Int,
    ): HttpResponse

    @Action("action:")
    @Path("/interface?")
    suspend fun test5(
            @Field(key = "news_id")
            tikit: Int,
    ): HttpResponse

    @PathReplace("/interface?")
    suspend fun test6(): HttpResponse

    @Path("/{id}/user/me/{sid}?")
    @Action("")
    suspend fun test7(
            @PathField(key = "id")
            id: Int,
            @PathField(key = "sid")
            sid: String,
    ): HttpResponse

}
