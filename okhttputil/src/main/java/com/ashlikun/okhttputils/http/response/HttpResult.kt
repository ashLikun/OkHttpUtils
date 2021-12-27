package com.ashlikun.okhttputils.http.response

import com.ashlikun.okhttputils.http.ClassUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Response
import java.lang.reflect.Type

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:34
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：http返回的基本数据， 用泛型解耦，可以适用于大部分接口
 */
open class HttpResult<T> : HttpResponse() {
    //用来模仿Data
    @SerializedName("data")
    open var data: T? = null

    /**
     * 去除空,只有在success的时候才会强制赋值
     */
    open val dataX: T
        get() = data!!

    override fun <M> parseData(gson: Gson, json: String, type: Type, response: Response?): M {
        //如果修改值，只能调用apply,因为这个方法调用后会指向调用的返回值
        return (super.parseData(gson, json, type, response) as M).apply {
            if (this is HttpResult<*>) {
                //防止data是null
                if (this.isSucceed) {
                    data = (data ?: ClassUtils.getListOrArrayOrObject(type)) as Nothing?
                }
            }
        }
    }

    override fun toString(): String {
        return "HttpResult{" +
                "json='" + json + '\'' +
                ", httpcode=" + httpCode +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}'
    }
}