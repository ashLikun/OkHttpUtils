package com.ashlikun.okhttputils.retrofit

import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.*
import kotlin.reflect.KClass

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/18　16:23
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
internal fun validateServiceInterface(service: Class<*>) {
    require(service.isInterface) { "API declarations must be interfaces." }
    val check: Deque<Class<*>> = ArrayDeque(1)
    check.add(service)
    while (!check.isEmpty()) {
        val candidate = check.removeFirst()
        if (candidate.typeParameters.isNotEmpty()) {
            val message = StringBuilder("Type parameters are unsupported on ").append(candidate.name)
            if (candidate != service) {
                message.append(" which is an interface of ").append(service.name)
            }
            throw IllegalArgumentException(message.toString())
        }
        Collections.addAll(check, *candidate.interfaces)
    }
}


fun getParameterLowerBound(method: Method): Type {
    val parameterTypes = method.genericParameterTypes
    if (!parameterTypes.isNullOrEmpty()) {
        //取出最后一个协程的回调参数
        var type = parameterTypes[parameterTypes.size - 1]
        //协程的回调参数是泛型的，获取泛型的类型
        if (type is ParameterizedType && !type.actualTypeArguments.isNullOrEmpty()) {
            val paramType = type.actualTypeArguments[0]
            return if (paramType is WildcardType) {
                paramType.lowerBounds[0]
            } else paramType
        }
    }
    throw IllegalArgumentException("接口返回必须为协程并且携带返回值")
}