package com.ashlikun.okhttputils.http

import com.ashlikun.okhttputils.http.HttpException
import com.ashlikun.okhttputils.http.callback.Callback
import java.lang.reflect.Type

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 19:25
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */

interface SuperHttp {
    //异步回调
    fun <T> execute(callback: Callback<T>): ExecuteCall

    //同步执行
    @Throws(HttpException::class)
    fun <ResultType> syncExecute(rawType: Type, vararg typeArguments: Type): ResultType
}