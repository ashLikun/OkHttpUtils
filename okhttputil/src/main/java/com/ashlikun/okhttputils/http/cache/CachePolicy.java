package com.ashlikun.okhttputils.http.cache;

import com.ashlikun.okhttputils.http.callback.Callback;
import com.ashlikun.okhttputils.http.request.HttpRequest;

import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/11 0011　下午 1:31
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：缓存执行的接口
 */
public interface CachePolicy {
    void save(Response response, String result);

    void setRequest(HttpRequest request);

    CacheEntity getCache();

    void setCacheMode(CacheMode cacheMode);

    void setCacheTime(long time);

    long getCacheTime();

    CacheMode getCacheMode();

    /**
     * 回调缓存数据
     *
     * @param callback
     * @param <T>
     */
    <T> void callback(final Callback<T> callback);


}
