package com.ashlikun.okhttputils.http;


import android.text.TextUtils;

import com.ashlikun.okhttputils.http.request.RequestCall;
import com.ashlikun.okhttputils.http.request.RequestParam;
import com.ashlikun.okhttputils.http.response.HttpErrorCode;
import com.ashlikun.okhttputils.http.response.HttpResponse;
import com.ashlikun.okhttputils.json.GsonHelper;
import com.google.gson.Gson;
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
    //解析json
    private Gson gson;

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
        gson = GsonHelper.getGson();
    }

    //初始化
    public static void init(OkHttpClient okHttpClient) {
        INSTANCE = new OkHttpUtils(okHttpClient);
    }


    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 全局设置gson解析
     */
    public void setParseGson(Gson gson) {
        this.gson = gson;
    }

    //异步请求
    @Override
    public <T> ExecuteCall execute(RequestCall requestCall, Callback<T> callback) {
        Call call = requestCall.buildCall(callback);
        ExecuteCall exc = new ExecuteCall();
        exc.setCall(call);
        OkHttpCallback okHttpCallback = new OkHttpCallback(exc, callback);
        okHttpCallback.setParseGson(requestCall.getRequestParam().getParseGson());
        call.enqueue(okHttpCallback);

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
    public <ResultType> ResultType syncExecute(RequestCall requestCall, Class raw, final Class... args) throws IOException {
        Response response = requestCall.buildCall(null).execute();
        Type type = null;
        if (args != null && args.length >= 2) {
            type = type(raw, type(args[0], args[1]));
        } else {
            type = type(raw, args);
        }
        return handerResult(type, response, requestCall.getRequestParam().getParseGson());
    }

    //同步请求
    @Override
    public <ResultType> ResultType syncExecute(RequestParam requestParam, Class raw, final Class... args) throws IOException {
        RequestCall requestCall = new RequestCall.Builder(requestParam)
                .build();
        return syncExecute(requestCall, raw, args);
    }


    /**
     * 根据Tag取消请求
     */
    public void cancelTag(Object tag) {
        if (tag == null) {
            return;
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 根据Tag取消请求
     */
    public long count(Object tag) {
        if (tag == null) {
            return 0;
        }
        long count = 0;
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                count++;
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                count++;
            }
        }
        return count;
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
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

    //处理返回值
    public static <T> T handerResult(Type type, final Response response, Gson gson) throws IOException {
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
                        if (TextUtils.isEmpty(json)) {
                            throw new JsonSyntaxException("json length = 0");
                        }
                        if (gson == null) {
                            Class cls = null;
                            try {
                                if (type instanceof Class) {
                                    cls = (Class) type;
                                } else {
                                    cls = (Class) ((ParameterizedType) type).getRawType();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (cls != null) {
                                if (HttpResponse.class.isAssignableFrom(cls)) {
                                    try {
                                        HttpResponse da = (HttpResponse) cls.newInstance();
                                        gson = da.parseGson();
                                    } catch (InstantiationException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (gson == null) {
                            gson = OkHttpUtils.getInstance().gson;
                        }
                        res = gson.fromJson(json, type);
                    } catch (JsonSyntaxException e) {//数据解析异常
                        throw new IOException(HttpErrorCode.MSG_DATA_ERROR2 + "  \n  原异常：" + e.toString() + "\n json = " + json);
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

