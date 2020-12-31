package com.ashlikun.okhttputils.retrofit

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/16　14:13
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
import java.lang.reflect.*


abstract class ServiceMethod<T> {
    abstract suspend fun invoke(args: Array<Any?>?): T


    companion object {
        fun <T> parseAnnotations(retrofit: Retrofit, method: Method): ServiceMethod<T> {
            val returnType = method.genericReturnType
            if (returnType === Void.TYPE) {
                throw methodError(method, null, "Service methods cannot return void.")
            }
            return HttpServiceMethod.parseAnnotations(retrofit, method)
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
