package com.ashlikun.okhttputils.http.response

import com.google.gson.Gson
import okhttp3.Response
import java.lang.reflect.Type

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:33
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：Response 的接口
 */
interface IHttpResponse {
    var code: Int
    var message: String
    var json: String
    var response: Response?

    /**
     * 获取http 的 code
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