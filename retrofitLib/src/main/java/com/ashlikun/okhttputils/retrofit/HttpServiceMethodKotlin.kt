package com.ashlikun.okhttputils.retrofit

import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaType

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/16　15:10
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：解析方法,构建请求,执行请求
 */
class HttpServiceMethodKotlin<ReturnT>(
        url: String,
        method: String,
        resultType: Type,
        params: List<ParameterHandler>,
        urlParams: List<ParameterHandler>
) : HttpServiceMethod<ReturnT>(url, method, resultType, params, urlParams) {

    companion object {
        fun <ReturnT> parseAnnotations(
                retrofit: Retrofit, kClass: KClass<*>, method: KFunction<*>)
                : HttpServiceMethodKotlin<ReturnT> {
            var httpMethod = "POST"
            //action默认方法名
            var action = method.name
            var path = ""
            var url = ""
            var params = mutableListOf<ParameterHandler>()
            var urlParams = mutableListOf<ParameterHandler>()

            //获取方法上面的注解
            method.annotations?.forEach {
                when (it) {
                    is URL -> {
                        url = it.url
                        httpMethod = it.method
                    }
                    is METHOD -> {
                        httpMethod = it.method
                    }
                    is GET -> {
                        if (url.isNullOrEmpty()) {
                            url = it.url
                        }
                        httpMethod = "GET"
                    }
                    is POST -> {
                        if (url.isNullOrEmpty()) {
                            url = it.url
                        }
                        httpMethod = "POST"
                    }
                    is ACTION -> action = it.action
                    is PATH -> path = it.path
                    is FieldDefault -> {
                        //默认字段
                        it.value.forEach { itt ->
                            val keyAndValue = itt.split(":")
                            params.add(ParameterHandler(-1, keyAndValue.getOrNull(0)
                                    ?: "", keyAndValue.getOrNull(1)
                                    ?: ""))
                        }
                    }
                    is Headers -> {
                        //默认头
                        it.value.forEach { itt ->
                            val keyAndValue = itt.split(":")
                            params.add(ParameterHandler(-1, keyAndValue.getOrNull(0)
                                    ?: "", keyAndValue.getOrNull(1)
                                    ?: "", isHeader = true))
                        }
                    }
                }
            }
            //处理url
            if (action.isNullOrEmpty() && path.isNullOrEmpty() && url.isNullOrEmpty()) {
                throw IllegalArgumentException("parseAnnotations no url")
            }

            //处理url
            if (url.isNullOrEmpty()) {
                url = Retrofit.get().createUrl?.invoke(RetrofitUrl(url, action, path)) ?: ""
            }
            if (url.isNullOrEmpty()) {
                throw IllegalArgumentException("parseAnnotations no url")
            }
            //处理方法的参数
            val parameters = method?.valueParameters
            parameters?.forEachIndexed { index, rit ->
                val annotations = rit.annotations
                val parameterHandler = ParameterHandler(index, rit.name ?: "")
                params.add(parameterHandler)
                //当前字段的全部注解
                annotations.forEach {
                    when (it) {
                        //被过滤的字段
                        is FieldNo -> {
                            params.remove(parameterHandler)
                        }
                        //字段
                        is Field -> {
                            if (!it.key.isNullOrEmpty()) {
                                parameterHandler.key = it.key
                            }
                            parameterHandler.isFile = it.isFile
                            parameterHandler.isFileArray = it.isFileArray
                        }
                        //请求头
                        is Header -> {
                            if (!it.value.isNullOrEmpty()) {
                                parameterHandler.key = it.value
                            }
                            parameterHandler.isHeader = true
                        }
                        ////匹配url里面的参数
                        is PathField -> {
                            urlParams.add(ParameterHandler(index, it.key))
                        }
                    }
                }
            }


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
            return HttpServiceMethodKotlin(url, httpMethod, returnType, params, urlParams)
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