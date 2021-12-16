package com.ashlikun.okhttputils.http.response

import com.google.gson.Gson
import okhttp3.Response
import java.lang.reflect.Type

/**
 * 作者　　: 李坤
 * 创建时间: 2020/6/5　14:15
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
interface IHttpResponse {
    var code: Int
    var message: String
    var json: String
    fun setResponse(response: Response)

    /**
     * 获取头部code
     */
    var httpCode: Int

    /**
     * 是否成功
     */
    val isSucceed: Boolean

    /**
     * 解析Json的Json
     * 会先实例化一个空的本对象然后调用这个方法
     */
    fun <T> parseData(gson: Gson, json: String?, type: Type?): T {
        return gson.fromJson(json, type)
    }
}