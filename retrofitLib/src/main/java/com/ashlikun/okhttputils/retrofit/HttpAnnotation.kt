package com.ashlikun.okhttputils.retrofit

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/15　14:02
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：Http仿造Retrofit的注解
 */

//请求的url，可以有占位符
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GET(val url: String = "")

//请求的url，可以有占位符
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class POST(val url: String = "")

//请求的path 可以有占位符 ：/user/{id}
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PATH(val path: String)


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ACTION(val action: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Field(val key: String, val encoded: Boolean = false,
                       val isFile: Boolean = false,
        //如果是多个文件是否用同一个key变成数组
                       val isFileArray: Boolean = false)

//path里面的参数  如 /user/{id}
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class PathField(val key: String)

/**
 * 用于方法体上面
 * * @FieldDefault({
 * "key1:value1"，
 * "key2:value2"
 * })
 *  @FieldDefault("key:value")
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldDefault(vararg val value: String)

/**
 * 用户请求的头,用于方法
 * @Headers({
 * "key1:value1"，
 * "key2:value2"
 * })
 *  @Headers("key:value")
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Headers(vararg val value: String)

/**
 * 用户请求的头,用于参数
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Header(val value: String)