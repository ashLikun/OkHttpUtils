package com.ashlikun.okhttputils.retrofit

import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.superclasses
import kotlin.reflect.full.valueParameters
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
        var resultType: Type,
        var params: List<ParameterHandler>,
        var urlParams: List<ParameterHandler>
) : ServiceMethod<ReturnT>() {
    override suspend fun invoke(args: Array<Any?>?): ReturnT {
        if (Retrofit.get().createRequest == null || Retrofit.get().execute == null) {
            throw java.lang.IllegalArgumentException("必须初始化Retrofit.get().init")
        }
        //替换url里面的变量
        urlParams.forEach {
            url = url.replace("{${it.key}}", args?.getOrNull(it.index).toString())
        }
        //创建请求
        val request = Retrofit.get().createRequest!!.invoke(this).setMethod(method)
        //添加参数
        params.forEach { itt ->
            itt.apply(request, args)
        }
        return Retrofit.get().execute!!.invoke(request, this, args) as ReturnT
    }

    companion object {
        fun <ReturnT> parseAnnotations(
                retrofit: Retrofit, kClass: KClass<*>, method: KFunction<*>)
                : HttpServiceMethod<ReturnT> {
            var httpMethod = "POST"
            var urlParam = ""
            var path = ""
            var url = ""
            var params = mutableListOf<ParameterHandler>()
            var urlParams = mutableListOf<ParameterHandler>()
            var classAllAnnotations = getClassAllAnnotations(kClass)
            //处理类上的注解
            classAllAnnotations.forEach {
                when (it) {
                    is Url -> if (!it.method.isNullOrEmpty()) httpMethod = it.method
                    is Path -> path = it.value
                }
            }

            //获取方法上面的注解
            method.annotations?.forEach {
                when (it) {
                    is Url -> {
                        url = it.url
                        httpMethod = it.method
                    }
                    is Mehtod -> {
                        httpMethod = it.method
                    }
                    is Get -> {
                        if (url.isNullOrEmpty()) {
                            url = it.url
                        }
                        httpMethod = "GET"
                    }
                    is Post -> {
                        if (url.isNullOrEmpty()) {
                            url = it.url
                        }
                        httpMethod = "POST"
                    }
                    is Param -> urlParam = it.value
                    is Path -> path = it.value
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
            url = handleUrl(kClass, method, classAllAnnotations, url, urlParam, path)
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
            return HttpServiceMethod(url, httpMethod, returnType, params, urlParams)
        }

        fun getClassAllAnnotations(kClass: KClass<*>): List<Annotation> {
            var ans = mutableListOf<Annotation>()
            //查找父类的
            kClass.superclasses.reversed().forEach {
                if (it != Any::class) {
                    ans.addAll(it.annotations)
                }
            }
            //添加当前类的
            ans.addAll(kClass.annotations)
            return ans
        }

        private fun handleUrl(kClass: KClass<*>, method: KFunction<*>, classAllAnnotations: List<Annotation>, url: String, urlParam: String, path: String): String {
            var allUrl = url
            var cUrl = ""
            //第一个参数的key
            var cUrlParamKey = ""
            classAllAnnotations.forEach {
                when (it) {
                    //基础url
                    is Url -> cUrl = it.url
                    is Param -> cUrlParamKey = it.value
                }
            }
            //如果没有方法url
            if (url.isNullOrEmpty()) {
                //如果设置了第一个参数
                if (!urlParam.isNullOrEmpty()) {
                    val urlParamSplit = urlParam.split(":").toMutableList()
                    if (urlParamSplit.size == 2) {
                        //必须有key或者cUrlParamKey
                        if (urlParamSplit[0].isNotEmpty() || cUrlParamKey.isNotEmpty()) {
                            if (urlParamSplit[1].isEmpty()) {
                                //如果没有Value，就是方法的名称
                                urlParamSplit[1] = method.name
                            }
                            allUrl = cUrl + path + urlParamSplit[0] + "=" + urlParamSplit[1]
                        }
                    } else if (urlParamSplit.size == 1 && cUrlParamKey.isNotEmpty()) {
                        //寻找类上的
                        allUrl = cUrl + path + cUrlParamKey + "=" + urlParamSplit[0]
                    }
                } else if (cUrlParamKey.isNotEmpty()) {
                    //如果在接口上设置了key，那么value就是方法名
                    allUrl = cUrl + path + cUrlParamKey + "=" + method.name
                }
                //如果没有url，就用接口上的url加上方法上的path
                if (allUrl.isEmpty()) {
                    allUrl = cUrl + path
                }
            }
            return Retrofit.get().createUrl?.invoke(allUrl) ?: allUrl
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