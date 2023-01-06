package com.ashlikun.okhttputils.http.request

import com.ashlikun.okhttputils.http.ExecuteCall
import com.ashlikun.okhttputils.http.HttpException
import com.ashlikun.okhttputils.http.HttpUtils.handerResult
import com.ashlikun.okhttputils.http.HttpUtils.launch
import com.ashlikun.okhttputils.http.OkHttpManage
import com.ashlikun.okhttputils.http.SuperHttp
import com.ashlikun.okhttputils.http.cache.CacheMode
import com.ashlikun.okhttputils.http.cache.CachePolicy
import com.ashlikun.okhttputils.http.cache.ImlCachePolicy
import com.ashlikun.okhttputils.http.callback.Callback
import com.ashlikun.okhttputils.http.callback.OkHttpCallback
import com.ashlikun.okhttputils.http.response.HttpErrorCode.HTTP_DATA_ERROR
import com.ashlikun.okhttputils.http.response.HttpErrorCode.MSG_DATA_ERROR
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:38
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：根据请求参数封装Okhttp的Request和Call，对外提供更多的接口：cancel(),3个超时时间
 */
open class RequestCall(var httpRequest: HttpRequest, var okHttpManage: OkHttpManage) : SuperHttp {
    private val TIME_OUT = -1
    lateinit var request: Request
        private set
    lateinit var call: Call
    private var readTimeOut = TIME_OUT.toLong()
    private var writeTimeOut = TIME_OUT.toLong()
    private var connTimeOut = TIME_OUT.toLong()
    open var interceptors: MutableList<Interceptor>? = null
    open var networkInterceptors: MutableList<Interceptor>? = null

    /**
     * 缓存代理
     */
    private var cachePolicy: CachePolicy? = null

    /**
     * 是否创建新的OkHttpClient
     */
    private val isNewBuilder: Boolean
        private get() = (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0 || !interceptors.isNullOrEmpty()
                || !networkInterceptors.isNullOrEmpty())

    /**
     * 第一步就得调用
     * 构建一个call用于请求
     * 私有
     */
    private fun buildCall(callback: Callback<*>?): Call {
        //获得请求实体
        request = httpRequest.bulidRequest(callback)
        //如果超时时间大于0,就重新构建OkHttpClient
        if (isNewBuilder) {
            readTimeOut = if (readTimeOut > 0) readTimeOut else OkHttpManage.DEFAULT_MILLISECONDS
            writeTimeOut = if (writeTimeOut > 0) writeTimeOut else OkHttpManage.DEFAULT_MILLISECONDS
            connTimeOut = if (connTimeOut > 0) connTimeOut else OkHttpManage.DEFAULT_MILLISECONDS
            val clone = okHttpManage.okHttpClient.newBuilder()
            if (readTimeOut != TIME_OUT.toLong()) {
                clone.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
            }
            if (writeTimeOut != TIME_OUT.toLong()) {
                clone.writeTimeout(readTimeOut, TimeUnit.MILLISECONDS)
            }
            if (connTimeOut != TIME_OUT.toLong()) {
                clone.connectTimeout(readTimeOut, TimeUnit.MILLISECONDS)
            }
            if (!interceptors.isNullOrEmpty()) {
                clone.interceptors().addAll(interceptors!!)
            }
            if (!networkInterceptors.isNullOrEmpty()) {
                clone.networkInterceptors().addAll(networkInterceptors!!)
            }
            call = clone.build().newCall(request)
        } else {
            call = okHttpManage.okHttpClient.newCall(request)
        }

        if (httpRequest.cacheMode != null) {
            //设置缓存信息
            cachePolicy = ImlCachePolicy(httpRequest).apply {
                cacheMode = httpRequest.cacheMode!!
                cacheTime = httpRequest.cacheTime
            }
        }
        return call
    }


    /**
     * 异步回调
     */
    override fun <T> execute(callback: Callback<T>): ExecuteCall {
        val call = buildCall(callback)
        val exc = ExecuteCall(call)
        //如果缓存 不存在才请求网络，否则使用缓存
        if (cachePolicy?.cacheMode === CacheMode.IF_NONE_CACHE_REQUEST) {
            if (cachePolicy?.cache != null) {
                launch { cachePolicy?.callback(call, callback) }
                return exc
            }
        }
        call.enqueue(OkHttpCallback(exc, callback,httpRequest.parseGson).apply {
            cacheIsCheckSuccess = httpRequest.cacheIsCheckSuccess
            cachePolicy = this@RequestCall.cachePolicy
        })
        return exc
    }

    /**
     * 同步执行
     */
    @Throws(HttpException::class)
    override fun <ResultType> syncExecute(raw: Type, vararg args: Type): ResultType {
        //获取返回值类型
        var type = if (args.isEmpty()) {
            raw
        } else if (args != null && args.size >= 2) {
            type(raw, type(args[0], args[1]))
        } else {
            type(raw, *args)
        }
        return try {
            val call = buildCall(null)
            //如果缓存 不存在才请求网络，否则使用缓存
            if (cachePolicy?.cacheMode !== CacheMode.NO_CACHE) {
                val cacheEntity = cachePolicy?.cache
                if (cacheEntity != null) {
                    //有缓存
                    return handerResult(type, cacheEntity, httpRequest.parseGson)
                }
            }
            val response = call.execute()
            //接口成功
            if (response.isSuccessful) {
                try {
                    val data: ResultType = handerResult(type, response, httpRequest.parseGson)
                        ?: throw HttpException(HTTP_DATA_ERROR, MSG_DATA_ERROR)
                    //保存缓存
                    cachePolicy?.save(response, data, httpRequest.cacheIsCheckSuccess)
                    data
                } catch (e: Exception) {
                    response.close()
                    throw HttpException(HTTP_DATA_ERROR, MSG_DATA_ERROR, e)
                }
            } else {
                response.close()
                throw HttpException.getOnResponseHttpException(response.code, response.message)
            }
        } catch (e: IOException) {
            //网络失败，回掉缓存
            if (cachePolicy?.cacheMode === CacheMode.REQUEST_FAILED_READ_CACHE) {
                val cacheEntity = cachePolicy?.cache
                if (cacheEntity != null) {
                    //有缓存
                    try {
                        handerResult(
                            type,
                            cacheEntity, httpRequest.parseGson
                        )
                    } catch (ioException: IOException) {
                        throw HttpException.handleFailureHttpException(call, ioException)
                    }
                } else {
                    throw HttpException.handleFailureHttpException(call, e)
                }
            } else {
                throw HttpException.handleFailureHttpException(call, e)
            }
        }
    }

    open fun httpRequest(httpRequest: HttpRequest): RequestCall {
        this.httpRequest = httpRequest
        return this
    }

    open fun readTimeOut(readTimeOut: Long): RequestCall {
        this.readTimeOut = readTimeOut
        return this
    }

    open fun writeTimeOut(writeTimeOut: Long): RequestCall {
        this.writeTimeOut = writeTimeOut
        return this
    }

    open fun connTimeOut(connTimeOut: Long): RequestCall {
        this.connTimeOut = connTimeOut
        return this
    }

    open fun addNetworkInterceptor(interceptor: Interceptor): RequestCall {
        if (networkInterceptors == null) {
            networkInterceptors = ArrayList()
        }
        networkInterceptors!!.add(interceptor)
        return this
    }

    open fun addInterceptor(interceptor: Interceptor): RequestCall {
        if (interceptors == null) {
            interceptors = ArrayList()
        }
        interceptors!!.add(interceptor)
        return this
    }

    /**
     * 同步请求的 构建Type   args是泛型数据
     */
    private fun type(raw: Type, vararg args: Type): ParameterizedType {
        return object : ParameterizedType {
            override fun getRawType(): Type {
                return raw
            }

            override fun getActualTypeArguments(): Array<out Type> {
                return args
            }

            override fun getOwnerType(): Type? {
                return null
            }
        }
    }
}