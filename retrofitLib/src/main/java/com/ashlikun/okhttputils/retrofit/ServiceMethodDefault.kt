package com.ashlikun.okhttputils.retrofit

import java.lang.reflect.Method
import kotlin.coroutines.Continuation

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/16　15:10
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：解析方法,构建请求,执行请求
 */
class ServiceMethodDefault<ReturnT>(
        //kotlin接口默认方法的实现
        var methodDefault: Method
) : ServiceMethod<ReturnT>() {
    /**
     * 增加item
     * @item：需添加数值
     * */
    fun addItem(args: Array<Any?>?, item: Any?): Array<Any?> {
        return Array<Any?>((args?.size ?: 0) + 1) {
            if (it == 0) {
                item
            } else {
                args!![it - 1]
            }
        }
    }

    override suspend fun invoke(args: Array<Any?>?): ReturnT {
        return methodDefault.invoke(this, *addItem(args, null)) as ReturnT
    }

    companion object {
        fun <T> parseDefault(retrofit: Retrofit, method: Method): ServiceMethod<T>? {
            val innerClazz = method.declaringClass.declaredClasses
            for (cls in innerClazz) {
                if (cls.simpleName == "DefaultImpls") {
                    val methods = cls.methods
                    for (m in methods) {
                        if (m.name == method.name) {
                            return ServiceMethodDefault(m)
                        }
                    }
                }
            }
            return null
        }
    }
}