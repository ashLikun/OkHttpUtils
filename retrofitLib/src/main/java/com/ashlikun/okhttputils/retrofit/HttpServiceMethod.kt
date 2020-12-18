package com.ashlikun.okhttputils.retrofit

import java.lang.reflect.Method
import java.lang.reflect.Type

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
        var resultType: Type,
        var params: List<ParameterHandler>
) : ServiceMethod<ReturnT>() {
    override suspend fun invoke(args: Array<Any?>?): ReturnT? {
        return Retrofit.get().methodInvoke?.invoke(this,args) as ReturnT?
    }


    companion object {
        fun <ReturnT> parseAnnotations(
                retrofit: Retrofit, method: Method)
                : HttpServiceMethod<ReturnT> {
            var httpMethod = "POST"
            var action = ""
            var path = ""
            var url = ""
            var params = mutableListOf<ParameterHandler>()
            //获取方法上面的注解
            method.annotations?.forEach {
                when (it) {
                    is GET -> {
                        url = it.url
                        httpMethod = "GET"
                    }
                    is POST -> {
                        url = it.url
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
            if (action.isNullOrEmpty() && path.isNullOrEmpty() && url.isNullOrEmpty()) {
                throw IllegalArgumentException("parseAnnotations no url")
            }
            //处理url
            if (url.isNullOrEmpty()) {
                url = Retrofit.get().createUrl?.invoke(url, action, path) ?: ""
            }
            if (url.isNullOrEmpty()) {
                throw IllegalArgumentException("parseAnnotations no url")
            }
            method.parameterAnnotations.forEachIndexed { index, annotations ->
                if (!annotations.isNullOrEmpty()) {
                    annotations.forEach {
                        when (it) {
                            is Field -> {
                                //字段
                                params.add(ParameterHandler(index, it.key, isFile = it.isFile, isFileArray = it.isFileArray))
                            }
                            is Header -> {
                                //请求头
                                params.add(ParameterHandler(index, it.value, isHeader = true))
                            }
                        }
                    }
                }
            }
            val returnType = getParameterLowerBound(method)
            if (hasUnresolvableType(returnType)) {
                throw methodError(
                        method,
                        null,
                        "Method return type must not include a type variable or wildcard: %s",
                        returnType)
            }
            return HttpServiceMethod(url, httpMethod, returnType, params)
        }
    }
}