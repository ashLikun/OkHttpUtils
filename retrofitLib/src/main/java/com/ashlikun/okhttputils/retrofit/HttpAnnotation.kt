package com.ashlikun.okhttputils.retrofit

import java.lang.annotation.Inherited

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
annotation class Get(val url: String = "")

//请求的url，可以有占位符
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Post(val url: String = "")

/**
 *  请求的url，可以有占位符
 * 1：在方法上  当前
 * 2：在接口上  全部
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Url(val url: String, val method: String = "POST")

/**
 * 请求的方法
 * 1：在方法上  当前
 * 2：在接口上  全部
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mehtod(val method: String = "POST")

/**
 * 使用json请求,不加注解就使用全局的
 * 1：在方法上  当前
 * 2：在接口上  全部
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Json(val value: Boolean = true)

/**
 * 请求的path 可以有占位符 ：/user/{id}
 * @param replace 是否替换父类的
 * 如果接口上也有配置，那么默认会相加，如果replace=true那么就替换
 * 1：在方法上  当前,
 * 2：在接口上  全部
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Path(val value: String, val replace: Boolean = false)

/**
 * 1：在方法上，当前请求会加上一个参数，格式可以是（ key:valye）或者（value）
 *            当格式是value的时候他的key就是在当前类的@Param
 *            如果只设置了一个如（key:)那么另一个就是方法名,如果（:value）另一个就是接口上的@Param
 *            如果为value ="" 就清空
 * 2：在接口上，表示当前全部方法的第一个参数的key
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Action(val value: String)

/**
 * 数据解析的区别
 * 1：在方法上  当前
 * 2：在接口上  全部
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class Parse(val parse: String)

/**
 * 提交的字段
 * 1：基本数据类型，直接就是具体值
 * 2：File 类型，就会强制使用File
 * 3：Map和其他对象，就会使用json序列化成多个字段
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class Field(val key: String = "",
    val encoded: Boolean = false,
    //是否是文件
    val isFile: Boolean = false,
    //是否作为一个data提交，当只有一个参数的时候生效,只支持String，其他对象会调用toString
    val isBody: Boolean = false,
    //如果是多个文件是否用同一个key变成数组
    val isFileArray: Boolean = false,
    //对象是否变成释放变成根节点
    val isObjToRoot: Boolean = true)

/**
 * 这个字段不用提交,用于回调的时候处理
 * 可以作用在类上
 */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldNo

//path里面的参数  如 /user/{id}
@Target(AnnotationTarget.VALUE_PARAMETER)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class PathField(val key: String)

/**
 * 用于方法体上面
 * * @FieldDefault({
 * "key1:value1"，
 * "key2:value2"
 * })
 * * @FieldDefault("key:value")
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldDefault(vararg val value: String)

/**
 * 用户请求的固定参数
 * *@Params({
 * "key1:value1"，
 * "key2:value2"
 * })
 *  *@Params("key:value")
 *  1:用在方法上，当前请求
 *  2:用在接口上，当前接口全部请求
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class Params(vararg val value: String)

/**
 * 用户请求的头
 * * @Headers({
 * "key1:value1"，
 * "key2:value2"
 * })
 * * @Headers("key:value")
 *  1:用在方法上，当前请求
 *  2:用在接口上，当前接口全部请求
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class Headers(vararg val value: String)

/**
 * 用户请求的头,用于参数
 * 如果是空，会取参数的名字
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class Header(val value: String = "")