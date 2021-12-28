package com.ashlikun.okhttputils.simple.data

import com.ashlikun.okhttputils.http.response.HttpResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Response
import java.lang.reflect.Type

/**
 * 作者　　: 李坤
 * 创建时间: 2018/4/26 0026　上午 10:14
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
class HomeHttpResult<T> : HttpResponse() {
    override fun <T> parseData(gson: Gson, json: String, type: Type, response: Response?): T {
        return super.parseData(GsonBuilder().create(), json, type, response)
    }
}