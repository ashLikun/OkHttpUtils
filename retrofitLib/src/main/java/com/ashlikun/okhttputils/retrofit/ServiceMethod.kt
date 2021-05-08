package com.ashlikun.okhttputils.retrofit

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/16　14:13
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
import java.lang.NullPointerException
import java.lang.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod


abstract class ServiceMethod<T> {
    abstract suspend fun invoke(args: Array<Any?>?): T


    companion object {
        fun <T> parse(retrofit: Retrofit, method: Method): ServiceMethod<T> {
            val returnType = method.genericReturnType
            if (returnType === Void.TYPE) {
                throw methodError(method, null, "Service methods cannot return void.")
            }
            try {
                val kClass = method.declaringClass.kotlin
                val kMethod = kClass.memberFunctions.find { it.javaMethod == method }
                        ?: //java方式
                        return HttpServiceMethod.parseAnnotations(retrofit, method)
                try {
                    //kotlin方式
                    return if (kMethod.isAbstract) {
                        //抽象方法
                        HttpServiceMethodKotlin.parseAnnotations(retrofit, kClass, kMethod)
                    } else {
                        //默认方法
                        parseDefault(retrofit, method)!!
                    }
                } catch (e: Exception) {
                    throw e;
                }
            } catch (e: Exception) {
                //java方式
                return parseDefault(retrofit, method)
                        ?: HttpServiceMethod.parseAnnotations(retrofit, method)
            }
        }

        //解析默认方法实现
        fun <T> parseDefault(retrofit: Retrofit, method: Method): ServiceMethod<T>? {
            return ServiceMethodDefault.parseDefault(retrofit, method)
        }


        fun methodError(
                method: Method, cause: Throwable?, message: String, vararg args: Any?): RuntimeException {
            var message = message
            message = String.format(message, *args)
            return IllegalArgumentException(
                    """$message
    for method ${method.declaringClass.simpleName}.${method.name}""",
                    cause)
        }

        open fun hasUnresolvableType(type: Type?): Boolean {
            if (type is Class<*>) {
                return false
            }
            if (type is ParameterizedType) {
                for (typeArgument: Type? in type.actualTypeArguments) {
                    if (hasUnresolvableType(typeArgument)) {
                        return true
                    }
                }
                return false
            }
            if (type is GenericArrayType) {
                return hasUnresolvableType(type.genericComponentType)
            }
            if (type is TypeVariable<*>) {
                return true
            }
            if (type is WildcardType) {
                return true
            }
            val className = type?.javaClass?.name ?: "null"
            throw IllegalArgumentException(
                    "Expected a Class, ParameterizedType, or "
                            + "GenericArrayType, but <"
                            + type
                            + "> is of type "
                            + className)
        }
    }


}
