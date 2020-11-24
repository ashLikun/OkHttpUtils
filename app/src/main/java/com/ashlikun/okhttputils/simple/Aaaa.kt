package com.ashlikun.okhttputils.simple

import okhttp3.Interceptor
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 作者　　: 李坤
 * 创建时间: 2019/9/5　16:27
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
object Aaaa {

    fun aaa(): Unit {
        val a = Interceptor {
            it.proceed(it.request())
        }
    }
    /**
     * 获取回调里面的泛型
     */
    open fun getType(mClass: Class<*>): Type? {
        val types = mClass.genericSuperclass
        var parentypes: Array<Type?>? //泛型类型集合
        if (types is ParameterizedType) {
            parentypes = types.actualTypeArguments
        } else {
            parentypes = mClass.genericInterfaces
            parentypes?.forEach {
                if (it is ParameterizedType) {
                    parentypes = it.actualTypeArguments
                    return@forEach
                }
            }
        }
        if (parentypes.isNullOrEmpty()) {
            Throwable("BaseApiService  ->>>  回调 不能没有泛型，请查看execute方法几个回调是否有泛型是否有泛型")
        } else {
            return parentypes!![0]
        }
        return null
    }
}