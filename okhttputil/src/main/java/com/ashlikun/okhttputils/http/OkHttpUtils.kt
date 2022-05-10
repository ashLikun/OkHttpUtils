package com.ashlikun.okhttputils.http

import android.content.Context
import com.ashlikun.gson.GsonHelper
import com.ashlikun.okhttputils.http.cache.CacheEntity
import com.ashlikun.okhttputils.http.cache.CacheMode
import com.ashlikun.okhttputils.http.cookie.CookieJarImpl
import com.ashlikun.okhttputils.http.cookie.store.DBCookieStore
import com.ashlikun.okhttputils.http.request.HttpRequest
import com.ashlikun.okhttputils.http.request.RequestCall
import okhttp3.OkHttpClient
import okhttp3.Response
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 17:21
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：http工具类
 */
/**
 * 在子线程
 *
 * @param code      错误code
 * @param exception 异常 一般是JsonSyntaxException
 * @param response  Response
 * @param json      解析的json数据
 */
typealias  OnDataParseError = (code: Int, exception: Exception, response: Response, json: String) -> Unit

class OkHttpUtils private constructor(
    client: OkHttpClient?
) {
    /**
     * okhttp核心类
     */
    var okHttpClient: OkHttpClient

    /**
     * 全局解析Gson
     */
    var parseGson = GsonHelper.getGsonNotNull()

    /**
     * 普通键值对公共参数
     */
    var commonParams = mutableMapOf<String, Any>()

    /**
     * 请求头公共参数
     */
    var commonHeaders = mutableMapOf<String, String>()

    /**
     * 设置全局的缓存模式
     */
    var cacheMode = CacheMode.NO_CACHE

    /**
     * 全局缓存过期时间,默认永不过期
     */
    var cacheTime = CacheEntity.CACHE_NEVER_EXPIRE

    /**
     * 全局缓存是否检测接口成功后才保存,前提是结果实现IHttpResponse
     */
    var cacheIsCheckSuccess = true

    /**
     * 当数据解析错误
     */
    var onDataParseError: OnDataParseError? = null

    /**
     * 是否默认以json提交数据
     */
    var isJsonRequest: Boolean? = null

    /**
     * 是否设置了公共头
     */
    val isCommonHeaders
        get() = commonHeaders.isNotEmpty()

    /**
     * 是否设置了公共参数
     */
    val isCommonParams
        get() = commonParams.isNotEmpty()


    init {
        if (client == null) {
            this.okHttpClient = OkHttpClient.Builder()
                .readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .cookieJar(CookieJarImpl(DBCookieStore()))
                .build()
        } else {
            this.okHttpClient = client
        }
    }


    /**
     * 根据Tag取消请求
     */
    fun cancelTag(tag: Any) {
        okHttpClient.dispatcher.queuedCalls().forEach {
            if (tag === it.request().tag()) it.cancel()
        }
        okHttpClient.dispatcher.runningCalls().forEach {
            if (tag === it.request().tag()) it.cancel()
        }
    }

    /**
     * 根据Tag获取请求个数
     */
    fun countRequest(vararg tag: Any): Int {
        var count = 0
        val queuedCalls = okHttpClient.dispatcher.queuedCalls()
        val runningCalls = okHttpClient.dispatcher.runningCalls()
        if (tag.isEmpty()) {
            return queuedCalls.size + runningCalls.size
        }
        tag.forEach {
            count += queuedCalls.count { itt -> it === itt.request().tag() }
            count += runningCalls.count { itt -> it === itt.request().tag() }
        }
        return count
    }

    /**
     * 取消所有请求请求
     */
    fun cancelAll() {
        okHttpClient.dispatcher.queuedCalls().forEach {
            it.cancel()
        }
        okHttpClient.dispatcher.runningCalls().forEach {
            it.cancel()
        }
    }

    companion object {
        var app: Context? = null
            private set

        //默认的超时时间
        const val DEFAULT_MILLISECONDS = 60000L
        const val DEFAULT_MILLISECONDS_LONG = 200000L
        private val instance by lazy { OkHttpUtils(null) }
        fun get(): OkHttpUtils = instance

        /**
         * 初始化一个全局的OkHttpClient
         */
        fun init(app: Context, okHttpClient: OkHttpClient?) {
            this.app = app.applicationContext
            get().okHttpClient = okHttpClient ?: get().okHttpClient
        }

        /**
         * 开始post请求
         */
        fun post(url: String): HttpRequest {
            return HttpRequest.post(url)
        }

        /**
         * 开始get请求
         */
        operator fun get(url: String): HttpRequest {
            return HttpRequest.get(url)
        }

        /**
         * 开始请求
         * 设置参数
         */
        fun request(requestParam: HttpRequest): RequestCall {
            return RequestCall(requestParam)
        }

        /**
         * 设置解析错误 回调
         */
        fun setOnDataParseError(onDataParseError: OnDataParseError) {
            get().onDataParseError = onDataParseError
        }

        fun getOnDataParseError(): OnDataParseError? {
            return get().onDataParseError
        }

        /**
         * 发送解析错误
         */
        fun sendOnDataParseError(
            code: Int,
            exception: Exception,
            response: Response,
            json: String
        ) {
            get().onDataParseError?.invoke(code, exception, response, json)
        }
    }

}