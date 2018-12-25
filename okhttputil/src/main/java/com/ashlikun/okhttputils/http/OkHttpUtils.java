package com.ashlikun.okhttputils.http;


import com.ashlikun.gson.GsonHelper;
import com.ashlikun.okhttputils.http.request.HttpRequest;
import com.ashlikun.okhttputils.http.request.RequestCall;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * 作者　　: 李坤
 * 创建时间: 10:31 admin
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：http工具类
 */
public final class OkHttpUtils {
    //默认的超时时间
    public static final long DEFAULT_MILLISECONDS = 40_000L;
    public static final long DEFAULT_MILLISECONDS_LONG = 200_000L;
    private volatile static OkHttpUtils INSTANCE = null;
    //okhttp核心类
    private OkHttpClient mOkHttpClient;
    //解析json
    private Gson gson;
    /**
     * 普通键值对公共参数
     */
    private Map<String, Object> commonParams;
    /**
     * 请求头公共参数
     */
    private Map<String, String> commonHeaders;

    /**
     * 获取单例
     *
     * @return
     */
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
        gson = GsonHelper.getGsonNotNull();
    }

    /**
     * 初始化
     *
     * @param okHttpClient
     */
    public static void init(OkHttpClient okHttpClient) {
        INSTANCE = new OkHttpUtils(okHttpClient);
    }


    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 全局设置gson解析
     */
    public OkHttpUtils setParseGson(Gson gson) {
        this.gson = gson;
        return this;
    }

    public Gson getParseGson() {
        return gson;
    }

    /**
     * 全局设置公共参数
     */
    public OkHttpUtils setCommonParams(Map<String, Object> commonParams) {
        if (this.commonParams == null) {
            this.commonParams = new HashMap<>();
        }
        this.commonParams.putAll(commonParams);
        return this;
    }

    /**
     * 全局设置公共头部
     */
    public OkHttpUtils setCommonHeaders(Map<String, String> commonHeaders) {
        if (this.commonHeaders == null) {
            this.commonHeaders = new HashMap<>();
        }
        this.commonHeaders.putAll(commonHeaders);
        return this;
    }

    public Map<String, Object> getCommonParams() {
        return commonParams;
    }

    public Map<String, String> getCommonHeaders() {
        return commonHeaders;
    }

    public boolean isCommonHeaders() {
        return commonHeaders != null && !commonHeaders.isEmpty();
    }

    public boolean isCommonParams() {
        return commonParams != null && !commonParams.isEmpty();
    }

    /**
     * 根据Tag取消请求
     */
    public void cancelTag(Object tag) {
        if (tag == null) {
            return;
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (call != null && tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (call != null && tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 根据Tag获取请求个数
     */
    public long countRequest(Object... tag) {
        if (tag == null) {
            return 0;
        }

        long count = 0;
        List<Call> calls1 = mOkHttpClient.dispatcher().queuedCalls();
        List<Call> calls2 = mOkHttpClient.dispatcher().runningCalls();

        if (tag.length == 0) {
            return (calls1 == null ? 0 : calls1.size()) + (calls2 == null ? 0 : calls2.size());
        }
        for (Call call : calls1) {
            for (Object one : tag) {
                if (one != null && one.equals(call.request().tag())) {
                    count++;
                }
            }

        }
        for (Call call : calls2) {
            for (Object one : tag) {
                if (one != null && one.equals(call.request().tag())) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (call != null) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (call != null) {
                call.cancel();
            }
        }
    }


    /**
     * 开始post请求
     */
    public static HttpRequest post(String url) {
        return HttpRequest.post(url);
    }

    /**
     * 开始get请求
     */
    public static HttpRequest get(String url) {
        return HttpRequest.get(url);
    }

    /**
     * 开始请求
     * 设置参数
     *
     * @param requestParam
     * @return
     */
    public static RequestCall request(HttpRequest requestParam) {
        return new RequestCall(requestParam);
    }
}

