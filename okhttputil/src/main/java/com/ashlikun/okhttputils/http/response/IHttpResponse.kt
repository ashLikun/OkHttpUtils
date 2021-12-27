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
     * 解析数据的时候，也是初始化的时候
     * 会先实例化一个空的本对象然后调用这个方法
     * @param response 网络请求是 Response，缓存是 null
     */
    fun <T> parseData(gson: Gson, json: String, type: Type, response: Response?): T {
        return (gson.fromJson(json, type) as T).apply {
            if (this is IHttpResponse) {
                this.json = json
                if (response != null) {
                    this.httpCode = response.code
                    this.response = response
                }
            }
        }
    }
}