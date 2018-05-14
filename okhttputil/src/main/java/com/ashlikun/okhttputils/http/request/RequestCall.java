package com.ashlikun.okhttputils.http.request;

import com.ashlikun.okhttputils.http.ExecuteCall;
import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.SuperHttp;
import com.ashlikun.okhttputils.http.cache.CacheEntity;
import com.ashlikun.okhttputils.http.cache.CachePolicy;
import com.ashlikun.okhttputils.http.cache.ImlCachePolicy;
import com.ashlikun.okhttputils.http.callback.Callback;
import com.ashlikun.okhttputils.http.callback.OkHttpCallback;
import com.ashlikun.okhttputils.http.callback.ProgressCallBack;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/3/17 16:20
 * <p>
 * 方法功能：根据请求参数封装Okhttp的Request和Call，对外提供更多的接口：cancel(),3个超时时间
 */
public class RequestCall implements SuperHttp {
    private Request request;
    private Call call;
    private HttpRequest httpRequest;
    private long readTimeOut;
    private long writeTimeOut;
    private long connTimeOut;
    List<Interceptor> interceptors;
    List<Interceptor> networkInterceptors;
    ProgressCallBack progressCallBack;
    /**
     * 先使用缓存，不管是否存在，仍然请求网络
     * 这是自定义缓存，和http标准的缓存不一样
     */
    CachePolicy cachePolicy;

    public RequestCall(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }


    /**
     * 构建一个call用于请求
     * 私有
     */
    private Call buildCall(Callback callback) {
        //获得请求实体
        request = httpRequest.bulidRequest(callback, progressCallBack);
        //如果超时时间大于0,就重新构建OkHttpClient
        if (isNewBuilder()) {
            readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            OkHttpClient.Builder clone = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS);
            if (interceptors != null && !interceptors.isEmpty()) {
                clone.interceptors().addAll(interceptors);
            }
            if (networkInterceptors != null && !networkInterceptors.isEmpty()) {
                clone.networkInterceptors().addAll(networkInterceptors);
            }
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


    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    /**
     * 添加进度回调,页可以在普通回调的时候实现{@link ProgressCallBack}接口
     *
     * @param progressCallBack
     * @return
     */
    public RequestCall progressCallback(ProgressCallBack progressCallBack) {
        this.progressCallBack = progressCallBack;
        return this;
    }

    /**
     * 异步回调
     */
    @Override
    public <T> ExecuteCall execute(Callback<T> callback) {
        Call call = buildCall(callback);
        ExecuteCall exc = new ExecuteCall();
        exc.setCall(call);
        //获取缓存，先回调
        if (cachePolicy != null && cachePolicy.isFirstUseCache()) {
            cachePolicy.callback(callback);
        }
        OkHttpCallback okHttpCallback = new OkHttpCallback(exc, callback);
        okHttpCallback.setParseGson(getHttpRequest().getParseGson());
        okHttpCallback.setCachePolicy(cachePolicy);
        call.enqueue(okHttpCallback);
        return null;
    }

    /**
     * 同步执行
     */
    @Override
    public <ResultType> ResultType syncExecute(Class raw, Class... args) throws IOException {
        Response response = buildCall(null).execute();
        Type type = null;
        if (args != null && args.length >= 2) {
            type = type(raw, type(args[0], args[1]));
        } else {
            type = type(raw, args);
        }
        ResultType data = HttpUtils.handerResult(type, response, getHttpRequest().getParseGson());
        //保存缓存
        if (cachePolicy != null) {
            cachePolicy.save(response,
                    CacheEntity.getHanderResult(data));
        }
        return data;
    }

    public RequestCall httpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        return this;
    }

    public RequestCall readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return this;
    }


    public RequestCall addNetworkInterceptor(Interceptor interceptor) {
        if (networkInterceptors == null) {
            networkInterceptors = new ArrayList<>();
        }
        networkInterceptors.add(interceptor);
        return this;
    }

    public RequestCall addInterceptor(Interceptor interceptor) {
        if (interceptors == null) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(interceptor);
        return this;
    }

    /**
     * 先使用缓存，不管是否存在，仍然请求网络
     * 需要使用'com.github.ashLikun:LiteOrm:1.0.3'库
     * 只会是一般的String返回类型才能缓存（包括封装对象）,File，Bitmap不可以
     */
    public RequestCall firstUseCache() {
        if (cachePolicy == null) {
            cachePolicy = new ImlCachePolicy();
        }
        cachePolicy.setFirstUseCache(true);
        cachePolicy.setRequest(getHttpRequest());
        return this;
    }

    public RequestCall setCachePolicy(CachePolicy policy) {
        if (policy != null) {
            cachePolicy = policy;
        }
        return this;
    }


    //同步请求的 构建Type   args是泛型数据
    private ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            @Override
            public Type getRawType() {
                return raw;
            }

            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }


}
