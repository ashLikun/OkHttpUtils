package com.ashlikun.okhttputils.http.request;

import com.ashlikun.okhttputils.http.Callback;
import com.ashlikun.okhttputils.http.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/3/17 16:20
 * <p>
 * 方法功能：根据请求参数封装Okhttp的Request和Call，对外提供更多的接口：cancel(),3个超时时间
 */

public class RequestCall {
    private Request request;
    private Call call;
    private RequestParam requestParam;
    private long readTimeOut;
    private long writeTimeOut;
    private long connTimeOut;
    List<Interceptor> interceptors;
    List<Interceptor> networkInterceptors;


    private RequestCall(Builder builder) {
        this.requestParam = builder.requestParam;
        this.readTimeOut = builder.readTimeOut;
        this.writeTimeOut = builder.writeTimeOut;
        this.connTimeOut = builder.connTimeOut;
        this.interceptors = builder.interceptors;
        this.networkInterceptors = builder.networkInterceptors;
    }


    //构建一个call用于请求
    public Call buildCall(Callback callback) {
        request = requestParam.bulidRequest(callback);//获得请求实体
        //如果超时时间大于0,就重新构建OkHttpClient
        if (isNewBuilder()) {
            readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            OkHttpClient.Builder clone = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS);
            if (interceptors != null && !interceptors.isEmpty())
                clone.interceptors().addAll(interceptors);
            if (networkInterceptors != null && !networkInterceptors.isEmpty())
                clone.networkInterceptors().addAll(networkInterceptors);
            call = clone.build().newCall(request);
        } else {
            call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
        }
        return call;
    }

    //是否创建新的OkHttpClient
    private boolean isNewBuilder() {
        return readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0
                || (interceptors != null && !interceptors.isEmpty())
                || (networkInterceptors != null && !networkInterceptors.isEmpty());
    }


    public Request getRequest() {
        return request;
    }


    public RequestParam getRequestParam() {
        return requestParam;
    }


    public final static class Builder {
        private RequestParam requestParam;
        private long readTimeOut;
        private long writeTimeOut;
        private long connTimeOut;
        List<Interceptor> interceptors;
        List<Interceptor> networkInterceptors;


        public Builder(RequestParam requestParam) {
            this.requestParam = requestParam;
        }

        public Builder requestParam(RequestParam requestParam) {
            this.requestParam = requestParam;
            return this;
        }

        public Builder readTimeOut(long readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        public Builder writeTimeOut(long writeTimeOut) {
            this.writeTimeOut = writeTimeOut;
            return this;
        }

        public Builder connTimeOut(long connTimeOut) {
            this.connTimeOut = connTimeOut;
            return this;
        }

        public Builder addNetworkInterceptor(Interceptor interceptor) {
            if (networkInterceptors == null) networkInterceptors = new ArrayList<>();
            networkInterceptors.add(interceptor);
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptors == null) interceptors = new ArrayList<>();
            interceptors.add(interceptor);
            return this;
        }

        public RequestCall build() {
            return new RequestCall(this);
        }
    }


}
