package com.ashlikun.okhttputils.http.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by likun
 * http返回的基本数据， 用泛型解耦，可以适用于大部分接口
 */
public class HttpResult<T> extends HttpResponse {


    //用来模仿Data
    @SerializedName("data")
    public T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "HttpResult{" +
                "json='" + json + '\'' +
                ", httpcode=" + httpcode +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
