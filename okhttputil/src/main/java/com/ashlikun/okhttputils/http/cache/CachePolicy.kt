package com.ashlikun.okhttputils.http.cache

import com.ashlikun.okhttputils.http.callback.Callback
import com.ashlikun.okhttputils.http.request.HttpRequest
import okhttp3.Call
import okhttp3.Response

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 11:31
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：缓存执行的接口
 */
interface CachePolicy {
    var request: HttpRequest
    val cache: CacheEntity?
    var cacheTime: Long
    var cacheMode: CacheMode
    fun save(response: Response, result: String)

    /**
     * 回调缓存数据
     * 这个方法调用在子线程
     *
     * @param call     可能为null
     * @param callback
     * @param <T>
    </T> */
    fun <T> callback(call: Call, callback: Callback<T>)
}