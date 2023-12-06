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
typealias  OnDataParseError = (code: Int, throwable: Throwable, response: Response, json: String) -> Unit
/**
 * 结果返回外部处理的时候错
 */
typealias  OnHttpError = (throwable: Throwable) -> Unit

class OkHttpManage private constructor(
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
     * 是否默认以json提交数据
     */
    var isJsonRequest: Boolean = false

    /**
     * Params 一条数据的时候是不是转化为json数组
     * 前提是json请求
     */
    var isOneParamsJsonArray: Boolean = true

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

        /**
         * 当数据解析错误
         */
        var onDataParseError: OnDataParseError? = null

        /**
         * 结果返回外部处理的时候错
         */
        var onHttpError: OnHttpError? = null

        private val instance by lazy { OkHttpManage(null) }
        fun get(): OkHttpManage = instance

        /**
         * 缓存其他的OkHttpManage
         */
        private val manageCache = mutableMapOf<String, OkHttpManage>()

        /**
         * 初始化一个全局的 OkHttpManage
         */
        fun init(app: Context, okHttpClient: OkHttpClient?) {
            this.app = app.applicationContext
            get().okHttpClient = okHttpClient ?: get().okHttpClient
        }

        /**
         * 创建一个新的 OkHttpManage
         */
        fun create(key: String, okHttpClient: OkHttpClient?): OkHttpManage {
            return synchronized(manageCache) {
                val manage = OkHttpManage(okHttpClient)
                manageCache[key] = manage
                manage
            }
        }

        fun getManage(key: String): OkHttpManage? {
            return synchronized(manageCache) { manageCache[key] }
        }

        fun removeManage(key: String): OkHttpManage? {
            return synchronized(manageCache) { manageCache.remove(key) }
        }

        fun removeManage(okHttpManage: OkHttpManage) {
            synchronized(manageCache) {
                manageCache.filter { it.value == okHttpManage }.forEach {
                    manageCache.remove(it.key)
                }
            }
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
            return requestParam.buildCall()
        }

        /**
         * 根据Tag获取请求个数
         */
        fun countRequest(vararg tag: Any): Int {
            var count = get().countRequest(tag)
            if (manageCache.isNotEmpty()) {
                synchronized(manageCache) {
                    manageCache.forEach {
                        count += it.value.countRequest(tag)
                    }
                }
            }
            return count
        }

        /**
         * 根据Tag取消请求
         */
        fun cancelTag(tag: Any) {
            get().cancelTag(tag)
            if (manageCache.isNotEmpty()) {
                synchronized(manageCache) {
                    manageCache.forEach {
                        it.value.countRequest(tag)
                    }
                }
            }
        }

        /**
         * 根据Tag取消请求
         */
        fun cancelAll() {
            get().cancelAll()
            if (manageCache.isNotEmpty()) {
                synchronized(manageCache) {
                    manageCache.forEach {
                        it.value.cancelAll()
                    }
                }
            }
        }
    }

}