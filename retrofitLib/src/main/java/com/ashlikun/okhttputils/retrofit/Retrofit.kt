package com.ashlikun.okhttputils.retrofit

import com.ashlikun.okhttputils.http.request.HttpRequest
import java.lang.reflect.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.Continuation

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/15　17:03
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：仿造Retrofit ，这里的接口方法只能是协程suspend
 */
typealias ServiceMethodInvoke<T> = suspend (request: HttpRequest, result: HttpServiceMethod<T>, params: Array<Any?>?) -> T

data class RetrofitUrl(var url: String,
                       var action: String,
                       var path: String) {

}

class Retrofit private constructor() {
    private val serviceMethodCache: ConcurrentHashMap<Method, ServiceMethod<*>> = ConcurrentHashMap()

    /**
     * 创建HttpRequest,必须实现
     */
    public var createRequest: ((it: HttpServiceMethod<*>) -> HttpRequest)? = null

    /**
     * 执行,必须实现
     */
    public var execute: ServiceMethodInvoke<*>? = null

    /**
     * 创建url,可以不实现
     */
    public var createUrl: ((url: RetrofitUrl) -> String)? = null

    fun init(
            createUrl: ((url: RetrofitUrl) -> String)? = null,
            createRequest: (it: HttpServiceMethod<*>) -> HttpRequest,
            execute: ServiceMethodInvoke<*>
    ) {
        this.createUrl = createUrl
        this.createRequest = createRequest
        this.execute = execute
    }

    fun <T> create(service: Class<T>): T {
        validateServiceInterface(service)
        return Proxy.newProxyInstance(
                service.classLoader, arrayOf<Class<*>>(service), MyInvocationHandler(this)) as T
    }

    fun <T> loadServiceMethod(method: Method): ServiceMethod<T> {
        var result: ServiceMethod<T>? = serviceMethodCache[method] as ServiceMethod<T>?
        //同步锁
        return result ?: synchronized(serviceMethodCache) {
            result = serviceMethodCache[method] as ServiceMethod<T>?
            if (result == null) {
                result = ServiceMethod.parse(this, method)
                serviceMethodCache[method] = result!!
            }
            result!!
        }

    }


    companion object {
        private val instance by lazy { Retrofit() }
        fun get(): Retrofit = instance
    }
}