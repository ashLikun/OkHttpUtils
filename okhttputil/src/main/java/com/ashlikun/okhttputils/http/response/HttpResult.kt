package com.ashlikun.okhttputils.http.response

import com.google.gson.annotations.SerializedName

/**
 * Created by likun
 * http返回的基本数据， 用泛型解耦，可以适用于大部分接口
 */
class HttpResult<T> : HttpResponse() {
    //用来模仿Data
    @SerializedName("data")
    var data: T? = null
    fun setData(data: T) {
        this.data = data
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