package com.ashlikun.okhttputils.simple.retrofit

import androidx.annotation.Keep
import com.ashlikun.okhttputils.http.request.HttpRequest
import com.ashlikun.okhttputils.http.response.HttpResponse
import com.ashlikun.okhttputils.retrofit.*
import com.ashlikun.okhttputils.simple.ParseMulti
import com.ashlikun.okhttputils.simple.data.Adwwww
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

@Keep
interface ApiOther : BaseApi {
    companion object {
        val api: ApiOther by lazy {
            Retrofit.get().create(ApiOther::class.java)
        }
    }

    fun testx(
        handle: Int
    ): String? {
        return "11111111111"
    }

    @Get("https://api-sip.510gow.com/interface?action=recommend")
    @ParseMulti
    suspend fun test(
        news_id: Int,
        ddd: WeiJinModel
    ): HttpResponse

    @Json()
    @Post("https://api-sip.510gow.com/interface?action=recommend")
    @ParseMulti
    suspend fun testList(
        news_id: Int,
        ddd: List<Adwwww>
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

    @Path("/interface?", replace = true)
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
