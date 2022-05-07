package com.ashlikun.okhttputils.retrofit

import android.util.Log
import java.lang.reflect.Type
import kotlin.math.log
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaType

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/16　15:10
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：解析方法,构建请求,执行请求
 */
class HttpServiceMethod<ReturnT>(
    var url: String,
    var method: String,
    //null:默认
    var isJsonRequest: Boolean?,
    var resultType: Type,
    var params: List<ParameterHandler>,
    var urlParams: List<ParameterHandler>,
    //解析Json的类型区别
    var parseType: String
) : ServiceMethod<ReturnT>() {
    override suspend fun invoke(proxy: Any?, args: Array<Any?>?): ReturnT {
        if (Retrofit.get().createRequest == null || Retrofit.get().execute == null) {
            throw java.lang.IllegalArgumentException("必须初始化Retrofit.get().init")
        }
        //替换url里面的变量
        urlParams.forEach {
            url = url.replace("{${it.key}}", args?.getOrNull(it.index).toString())
        }
        //创建请求
        val request = Retrofit.get().createRequest!!.invoke(this).setMethod(method).apply {
            isJson = isJsonRequest
        }
        //添加参数
        params.forEach { itt ->
            itt.apply(request, args)
        }
        return Retrofit.get().execute!!.invoke(request, this, args) as ReturnT
    }

    override fun invokeNoSuspend(proxy: Any?, args: Array<Any?>?): ReturnT {
        throw IllegalArgumentException("必须是协程方法suspend")
    }

    companion object {
        fun <ReturnT> parseAnnotations(
            retrofit: Retrofit, kClass: KClass<*>, method: KFunction<*>)
                : HttpServiceMethod<ReturnT> {


            val handlerAnnotation = HandlerAnnotation(kClass, method)

            //处理接口和类上面的注解
            handlerAnnotation.handleClass()
            //处理方法上面注解
            handlerAnnotation.handleFunction(method)
            //处理url
            handlerAnnotation.handleUrl()
            if (handlerAnnotation.url.isNullOrEmpty()) {
                throw IllegalArgumentException("parseAnnotations no url")
            }
            //处理方法的参数
            handlerAnnotation.handleParams()

            //处理返回值
            val returnType = method.returnType.javaType
            if (hasUnresolvableType(returnType)) {
                throw methodErrorK(
                    kClass,
                    method,
                    null,
                    "Method return type must not include a type variable or wildcard: %s",
                    returnType)
            }
            return HttpServiceMethod(handlerAnnotation.url,
                handlerAnnotation.httpMethod,
                handlerAnnotation.isJson,
                returnType,
                handlerAnnotation.params,
                handlerAnnotation.urlParams,
                handlerAnnotation.parseType)
        }


        fun methodErrorK(kClass: KClass<*>,
            method: KFunction<*>, cause: Throwable?, message: String, vararg args: Any?): RuntimeException {
            var message = message
            message = String.format(message, *args)
            return IllegalArgumentException(
                """$message
    for method ${kClass.simpleName}.${method.name}""",
                cause)
        }
    }


}