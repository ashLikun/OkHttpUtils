package com.ashlikun.okhttputils.retrofit

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.superclasses
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaType

/**
 * 作者　　: 李坤
 * 创建时间: 2021/6/17　10:23
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：处理注解
 */
class HandlerAnnotation(var kClass: KClass<*>,
    var method: KFunction<*>) {
    var httpMethod = "POST"

    //解析json的类型区别
    var parseType = ""
    var urlAction: String? = null
    var path = ""
    var url = ""
    var params = mutableListOf<ParameterHandler>()
    var urlParams = mutableListOf<ParameterHandler>()
    var classAllAnnotations = getClassAllAnnotations(kClass)
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

    //处理接口上的注解
    fun handleClass() {
        handleClassNeibu(classAllAnnotations)
    }

    //处理接口上的注解
    private fun handleClassNeibu(annotations: List<Annotation>) {
        annotations.forEach {
            if (!AnnotationUtils.isInJavaLangAnnotationPackage(it)) {
                when (it) {
                    is Url -> if (!it.method.isNullOrEmpty()) httpMethod = it.method
                    is Mehtod -> httpMethod = it.method
                    is Path -> path += it.value
                    is Parse -> parseType = it.parse
                    //固定头
                    is Headers -> {
                        it.value.forEach { itt ->
                            val keyAndValue = itt.split(":")
                            params.add(ParameterHandler(-1, keyAndValue[0], keyAndValue[1], isHeader = true))
                        }
                    }
                    //固定参数
                    is Params -> {
                        it.value.forEach { itt ->
                            val keyAndValue = itt.split(":")
                            params.add(ParameterHandler(-1, keyAndValue[0], keyAndValue[1]))
                        }
                    }
                }
                //处理注解上面的注解
                handleClassNeibu(it.annotationClass.annotations)
            }
        }
    }


    /**
     * 处理方法上面的注解
     */
    fun handleFunction(method: KFunction<*>) {
        handleFunctionNeibu(method.annotations)
    }

    private fun handleFunctionNeibu(annotations: List<Annotation>) {
        //获取方法上面的注解
        annotations.forEach {
            if (!AnnotationUtils.isInJavaLangAnnotationPackage(it)) {
                when (it) {
                    is Url -> {
                        url = it.url
                        httpMethod = it.method
                    }
                    is Mehtod -> httpMethod = it.method
                    is Parse -> parseType = it.parse
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
                    is Action -> urlAction = it.value
                    is Path -> if (it.replace) path = it.value else path += it.value
                    is FieldDefault -> {
                        //默认字段
                        it.value.forEach { itt ->
                            val keyAndValue = itt.split(":")
                            params.add(ParameterHandler(-1, keyAndValue[0], keyAndValue[1]))
                        }
                    }
                    //固定头
                    is Headers -> {
                        it.value.forEach { itt ->
                            val keyAndValue = itt.split(":")
                            params.add(ParameterHandler(-1, keyAndValue[0], keyAndValue[1], isHeader = true))
                        }
                    }
                    //固定参数
                    is Params -> {
                        it.value.forEach { itt ->
                            val keyAndValue = itt.split(":")
                            params.add(ParameterHandler(-1, keyAndValue[0], keyAndValue[1]))
                        }
                    }
                }
                //处理注解上面的注解
                handleFunctionNeibu(it.annotationClass.annotations)
            }
        }
    }

    //处理url
    fun handleUrl() {
        var allUrl = url
        var cUrl = ""
        //类上的第一个参数的key，表面当前请求添加一个参数
        var cUrlAction = ""

        fun handleUrlNeibu(annotations: List<Annotation>) {
            annotations.forEach {
                if (!AnnotationUtils.isInJavaLangAnnotationPackage(it)) {
                    when (it) {
                        //基础url
                        is Url -> cUrl = it.url
                        //表面当前请求添加一个参数
                        is Action -> cUrlAction = it.value
                    }
                    //处理注解上面的注解
                    handleUrlNeibu(it.annotationClass.annotations)
                }
            }
        }

        handleUrlNeibu(classAllAnnotations)
        //如果没有方法url
        if (url.isNullOrEmpty()) {
            //如果设置了第一个参数
            if (!urlAction.isNullOrEmpty()) {
                val urlParamSplit = urlAction!!.split(":").toMutableList()
                if (urlParamSplit.size == 2) {
                    //必须有key或者cUrlParamKey
                    if (urlParamSplit[0].isNotEmpty() || cUrlAction.isNotEmpty()) {
                        if (urlParamSplit[1].isEmpty()) {
                            //如果没有Value，就是方法的名称
                            urlParamSplit[1] = method.name
                        }
                        allUrl = cUrl + path + urlParamSplit[0] + "=" + urlParamSplit[1]
                    }
                } else if (urlParamSplit.size == 1 && cUrlAction.isNotEmpty()) {
                    //寻找类上的
                    allUrl = cUrl + path + cUrlAction + "=" + urlParamSplit[0]
                }
            } else if (urlAction == null && cUrlAction.isNotEmpty()) {
                //如果在接口上设置了key，那么value就是方法名
                allUrl = cUrl + path + cUrlAction + "=" + method.name
            }
            //如果没有url，就用接口上的url加上方法上的path
            if (allUrl.isEmpty()) {
                allUrl = cUrl + path
            }
        }
        url = Retrofit.get().createUrl?.invoke(allUrl) ?: allUrl
    }

    //处理方法的参数
    fun handleParams() {
        method.valueParameters.forEachIndexed { index, rit ->
            val annotations = rit.annotations
            val paramsType = rit.type.javaType
            //这个参数类型对应的类上面没有添加FieldNo注解的
            var isBreak = paramsType is Class<*> && paramsType.annotations.find { it is FieldNo } != null
            if (!isBreak) {
                val parameterHandler = ParameterHandler(index, rit.name ?: "")
                params.add(parameterHandler)
                //当前字段的全部注解
                handleParamsNebu(annotations, parameterHandler)
            }
        }
    }

    private fun handleParamsNebu(annotations: List<Annotation>, parameterHandler: ParameterHandler) {
        annotations.forEach {
            if (!AnnotationUtils.isInJavaLangAnnotationPackage(it)) {
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
                    //匹配url里面的参数
                    is PathField -> {
                        urlParams.add(ParameterHandler(parameterHandler.index, it.key))
                    }
                }
                //处理注解上面的注解
                handleParamsNebu(it.annotationClass.annotations, parameterHandler)
            }
        }
    }

}