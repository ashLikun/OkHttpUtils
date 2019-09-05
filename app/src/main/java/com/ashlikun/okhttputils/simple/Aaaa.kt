package com.ashlikun.okhttputils.simple

import okhttp3.Interceptor

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
}