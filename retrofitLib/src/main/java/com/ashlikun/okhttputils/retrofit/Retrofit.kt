package com.ashlikun.okhttputils.retrofit

import com.ashlikun.okhttputils.http.request.HttpRequest
import java.lang.reflect.*
import java.util.concurrent.ConcurrentHashMap

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
     * 开始动态代理的时候回调,可以不实现
     */
    var onProxyStart: ((method: Method, args: Array<Any>) -> Unit)? = null

    /**
     * 执行,必须实现
     */
    var execute: ServiceMethodInvoke<*>? = null

    /**
     * 创建HttpRequest,必须实现
     */
    var createRequest: ((it: HttpServiceMethod<*>) -> HttpRequest)? = null

    /**
     * 创建url,可以不实现
     */
    var createUrl: ((url: String) -> String)? = null

    fun init(createRequest: (it: HttpServiceMethod<*>) -> HttpRequest,
             execute: ServiceMethodInvoke<*>
    ) {
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

    /**
     * 开始代理的回调
     */
    fun proxyStart(method: Method, args: Array<Any>) {
        onProxyStart?.invoke(method, args)
    }


    companion object {
        private val instance by lazy { Retrofit() }
        fun get(): Retrofit = instance
    }
}