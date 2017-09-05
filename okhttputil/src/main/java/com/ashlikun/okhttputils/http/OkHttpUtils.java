package com.ashlikun.okhttputils.http;


import com.ashlikun.okhttputils.http.request.RequestCall;
import com.ashlikun.okhttputils.http.request.RequestParam;
import com.ashlikun.okhttputils.http.response.HttpResponse;
import com.ashlikun.okhttputils.http.response.HttpResult;
import com.ashlikun.okhttputils.json.GsonHelper;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者　　: 李坤
 * 创建时间: 10:31 admin
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：http工具类
 */

public class OkHttpUtils implements SuperHttp {

    private volatile static OkHttpUtils INSTANCE = null;
    //okhttp核心类
    private OkHttpClient mOkHttpClient;

    //获取单例
    public static OkHttpUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (OkHttpUtils.class) {
                if (INSTANCE == null) {
                    init(null);
                }
            }
        }
        return INSTANCE;
    }

    private OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                    .connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                    .build();
        } else {
            mOkHttpClient = okHttpClient;
        }
    }

    //初始化
    public static void init(OkHttpClient okHttpClient) {
        INSTANCE = new OkHttpUtils(okHttpClient);
    }


    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    //异步请求
    @Override
    public <T> ExecuteCall execute(RequestCall requestCall, Callback<T> callback) {
        Call call = requestCall.buildCall(callback);
        ExecuteCall exc = new ExecuteCall();
        exc.setCall(call);
        call.enqueue(new OkHttpCallback(exc, callback));
        return exc;
    }

    //异步请求
    @Override
    public <T> ExecuteCall execute(RequestParam requestParam, Callback<T> callback) {
        RequestCall requestCall = new RequestCall.Builder(requestParam)
                .build();
        return execute(requestCall, callback);
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/5/27 18:37
     * <p>
     * 方法功能：同步请求
     *
     * @param requestCall 请求参数
     * @param raw         原始数据
     * @param args        内部数据
     */

    @Override
    public <ResultType> ResultType syncExecute(RequestCall requestCall, Class raw, final Type... args) throws IOException {
        Response response = requestCall.buildCall(null).execute();
        return handerResult(type(raw, args), response);
    }

    //同步请求
    @Override
    public <ResultType> ResultType syncExecute(RequestParam requestParam, Class raw, final Type... args) throws IOException {
        RequestCall requestCall = new RequestCall.Builder(requestParam)
                .build();
        return syncExecute(requestCall, raw, args);
    }

    //同步请求的 构建Type   args是泛型数据
    private ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }

    //处理返回值
    public static <T> T handerResult(Type type, final Response response) throws IOException {
        if (type != null) {
            if (type == Response.class) {
                return (T) response;
            } else if (type == ResponseBody.class) {
                return (T) response.body();
            } else {
                String json = response.body().string();
                if (type == String.class) {
                    return (T) json;
                } else {
                    T res = null;
                    try {
                        res = GsonHelper.getGson().fromJson(json, type);
                    } catch (JsonSyntaxException e) {//数据解析异常
                        if (HttpResponse.class == type) {
                            res = (T) new HttpResponse();
                        }
                        if (HttpResult.class == type) {
                            res = (T) new HttpResult();
                        }
                        if (res != null) {
                            ((HttpResponse) res).setOnGsonErrorData(json);
                        }
                    }
                    if (res instanceof HttpResponse) {
                        ((HttpResponse) res).json = json;
                        ((HttpResponse) res).httpcode = response.code();
                        ((HttpResponse) res).response = response;
                    }
                    return res;
                }
            }
        }
        return null;
    }

}

