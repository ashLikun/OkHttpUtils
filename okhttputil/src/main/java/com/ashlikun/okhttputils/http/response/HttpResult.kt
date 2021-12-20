package com.ashlikun.okhttputils.http.response

import com.google.gson.annotations.SerializedName

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

    override fun toString(): String {
        return "HttpResult{" +
                "json='" + json + '\'' +
                ", httpcode=" + httpCode +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}'
    }
}