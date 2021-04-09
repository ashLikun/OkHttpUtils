package com.ashlikun.okhttputils.simple

import com.ashlikun.okhttputils.http.request.HttpRequest
import com.ashlikun.okhttputils.retrofit.Retrofit

/**
 * 作者　　: 李坤
 * 创建时间: 2021/4/9　19:02
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
object Init {
    fun init() {
        Retrofit.get().init(
                createRequest = { HttpRequest(it.url) },
                execute = { request, result, params -> request.syncExecute<Any>(result.resultType) }
        )
    }
}