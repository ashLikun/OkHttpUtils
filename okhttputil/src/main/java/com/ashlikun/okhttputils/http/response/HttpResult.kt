package com.ashlikun.okhttputils.http.response

import android.util.Log
import com.ashlikun.okhttputils.http.ClassUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:34
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：http返回的基本数据， 用泛型解耦，可以适用于大部分接口
 */
open class HttpResult<T> : HttpResponse() {
    /**
     * 用来模仿Data
     * 可能为null，如果不要null，使用dataX
     */
    @SerializedName("data")
    open var data: T? = null

    /**
     * 新建的数据缓存,是给 HttpResult的data赋值
     */
    open val newData: T by lazy {
        ClassUtils.getListOrArrayOrObject(ClassUtils.getHttpResultClass(currentType)) as T
    }


    /**
     * 空判断，如果是null，就会初始化一个
     */
    open val dataX: T
        get() = (data ?: newData)


    override fun toString(): String {
        return "HttpResult{" +
                "json='" + json + '\'' +
                ", httpcode=" + httpCode +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}'
    }
}