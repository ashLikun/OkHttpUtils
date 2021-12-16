package com.ashlikun.okhttputils.http.request

import com.ashlikun.okhttputils.http.HttpUtils.launch
import com.ashlikun.okhttputils.http.HttpUtils.handerResult
import com.ashlikun.okhttputils.http.SuperHttp
import com.ashlikun.okhttputils.http.callback.ProgressCallBack
import com.ashlikun.okhttputils.http.cache.CachePolicy
import com.ashlikun.okhttputils.http.OkHttpUtils
import com.ashlikun.okhttputils.http.cache.ImlCachePolicy
import com.ashlikun.okhttputils.http.ExecuteCall
import com.ashlikun.okhttputils.http.callback.OkHttpCallback
import kotlin.Throws
import com.ashlikun.okhttputils.http.HttpException
import com.ashlikun.okhttputils.http.cache.CacheEntity
import com.ashlikun.okhttputils.http.cache.CacheMode
import com.ashlikun.okhttputils.http.callback.Callback
import com.ashlikun.okhttputils.http.response.HttpErrorCode.*
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.Request
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * 作者　　: 李坤
 * 创建时间: 2017/3/17 16:20
 *
 *
 * 方法功能：根据请求参数封装Okhttp的Request和Call，对外提供更多的接口：cancel(),3个超时时间
 */
class RequestCall(var httpRequest: HttpRequest) : SuperHttp {
    private val TIME_OUT = -1
    lateinit var request: Request
        private set
    lateinit var call: Call
    private var readTimeOut = TIME_OUT.toLong()
    private var writeTimeOut = TIME_OUT.toLong()
    private var connTimeOut = TIME_OUT.toLong()
    var interceptors: MutableList<Interceptor>? = null
    var networkInterceptors: MutableList<Interceptor>? = null

    /**
     * 添加进度回调,页可以在普通回调的时候实现[ProgressCallBack]接口
     */
    var progressCallBack: ProgressCallBack? = null

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
        request = httpRequest.bulidRequest(callback, progressCallBack)
        //如果超时时间大于0,就重新构建OkHttpClient
        if (isNewBuilder) {
            readTimeOut = if (readTimeOut > 0) readTimeOut else OkHttpUtils.DEFAULT_MILLISECONDS
            writeTimeOut = if (writeTimeOut > 0) writeTimeOut else OkHttpUtils.DEFAULT_MILLISECONDS
            connTimeOut = if (connTimeOut > 0) connTimeOut else OkHttpUtils.DEFAULT_MILLISECONDS
            val clone = OkHttpUtils.get().okHttpClient.newBuilder()
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
            call = OkHttpUtils.get().okHttpClient.newCall(request)
        }
        //设置缓存信息
        cachePolicy = ImlCachePolicy(httpRequest)
        if (httpRequest.cacheMode != null) {
            cachePolicy?.cacheMode = httpRequest.cacheMode
            cachePolicy?.cacheTime = httpRequest.cacheTime
        }
        return call
    }


    /**
     * 异步回调
     */
    override fun <T> execute(callback: Callback<T>): ExecuteCall {
        val call = buildCall(callback)
        val exc = ExecuteCall()
        exc.call = call
        //如果缓存 不存在才请求网络，否则使用缓存
        if (cachePolicy?.cacheMode === CacheMode.IF_NONE_CACHE_REQUEST) {
            if (cachePolicy?.cache != null) {
                launch { cachePolicy?.callback(call, callback) }
                return exc
            }
        }
        call.enqueue(OkHttpCallback(exc, callback).apply {
            gson = httpRequest.parseGson
            cachePolicy = cachePolicy
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
                    cachePolicy?.save(
                        response,
                        CacheEntity.getHanderResult(data)
                    )
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

    fun httpRequest(httpRequest: HttpRequest): RequestCall {
        this.httpRequest = httpRequest
        return this
    }

    fun readTimeOut(readTimeOut: Long): RequestCall {
        this.readTimeOut = readTimeOut
        return this
    }

    fun writeTimeOut(writeTimeOut: Long): RequestCall {
        this.writeTimeOut = writeTimeOut
        return this
    }

    fun connTimeOut(connTimeOut: Long): RequestCall {
        this.connTimeOut = connTimeOut
        return this
    }

    fun addNetworkInterceptor(interceptor: Interceptor): RequestCall {
        if (networkInterceptors == null) {
            networkInterceptors = ArrayList()
        }
        networkInterceptors!!.add(interceptor)
        return this
    }

    fun addInterceptor(interceptor: Interceptor): RequestCall {
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