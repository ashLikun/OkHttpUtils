package com.ashlikun.okhttputils.http

import com.ashlikun.okhttputils.http.response.HttpResult
import java.lang.reflect.*
import java.lang.reflect.Array

/**
 * 作者　　: 李坤
 * 创建时间: 2021.12.23　13:27
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：反射和获取泛型类型的工具
 */
object ClassUtils {
    /**
     * 获取HttpResult类型
     */
    fun getHttpResultClass(type: Type): Type? {
        when (type) {
            is ParameterizedType -> {
                val rawType = type.rawType
                //已经是的
                if (rawType is Class<*> && HttpResult::class.java.isAssignableFrom(rawType)) {
                    return type
                }
                if (rawType == HttpResult::class.java) {
                    return type
                }
                return getHttpResultClass(type.rawType)
            }
            is Class<*> -> {
                return if (HttpResult::class.java.isAssignableFrom(type)) {
                    type
                } else if (type == HttpResult::class.java) {
                    type
                } else if (type.genericSuperclass != null && type.genericSuperclass != Any::class.java) {
                    //genericSuperclass 获取带泛型类型
                    getHttpResultClass(type.genericSuperclass!!)
                } else {
                    null
                }
            }
            else -> {
                return null
            }
        }
    }

    /**
     * 获取当前泛型内部data->list或者数组  或者 对象
     * 实例化data，方便使用
     */
    fun getListOrArrayOrObject(resultType: Type?): Any? {
        try {
            return classToListOrArrayOrObject(resultType)?.newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun classToListOrArrayOrObject(superClass: Type?): Class<*>? {
        if (superClass == null) return null
        if (superClass is ParameterizedType && !superClass.actualTypeArguments.isNullOrEmpty()) {
            var type1 = superClass.actualTypeArguments[0]
            return when {
                isTypeToListOrArray(type1) -> getRawType(type1)
                //返回对象
                type1 is Class<*> -> type1
                type1 is ParameterizedType -> type1.rawType as? Class<*>
                //找泛型的泛型父类
                else -> classToListOrArrayOrObject(type1)
            }
        } else if (superClass is Class<*>) {
            //找父类
            return classToListOrArrayOrObject(superClass.genericSuperclass)
        }
        return null
    }

    /**
     * 这个type类型是否是List或者数组
     */
    private fun isTypeToListOrArray(type: Type?): Boolean {
        return when (type) {
            is Class<*> -> List::class.java.isAssignableFrom(type) || Array::class.java.isAssignableFrom(type)
            is ParameterizedType -> {
                if (type.rawType is Class<*>) {
                    if (List::class.java.isAssignableFrom((type.rawType as Class<*>))) {
                        true
                    } else Array::class.java.isAssignableFrom((type.rawType as Class<*>))
                } else type == List::class.java || type == ArrayList::class.java || type == kotlin.Array<Any>::class.java
            }
            is GenericArrayType -> true
            is WildcardType -> isTypeToListOrArray(type.upperBounds[0])
            else -> false
        }
    }

    // type不能直接实例化对象，通过type获取class的类型，然后实例化对象
    fun getRawType(type: Type?): Class<*> {
        when (type) {
            is Class<*> -> return type
            is ParameterizedType -> {
                //防止list为null
                if (type.rawType is Class<*>) {
                    if (List::class.java.isAssignableFrom((type.rawType as Class<*>))) {
                        return ArrayList::class.java
                    }
                } else if (type == List::class.java || type == ArrayList::class.java || type == kotlin.Array<Any>::class.java) {
                    return kotlin.Array<Any>::class.java
                }
                val parameterizedType = type as ParameterizedType?
                val rawType = parameterizedType!!.rawType
                return rawType as Class<*>
            }
            is GenericArrayType -> {
                val componentType = type.genericComponentType
                return Array.newInstance(getRawType(componentType), 0).javaClass
            }
            is TypeVariable<*> -> return Any::class.java
            is WildcardType -> return getRawType(type.upperBounds[0])
            else -> {
                val className = type?.javaClass?.name ?: "null"
                throw IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <$type> is of type $className")
            }
        }
    }
}