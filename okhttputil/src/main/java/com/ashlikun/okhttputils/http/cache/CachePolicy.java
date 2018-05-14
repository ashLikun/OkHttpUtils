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
    public void save(Response response, String result);

    public void setRequest(HttpRequest request);

    public void setFirstUseCache(boolean firstUseCache);

    public boolean isFirstUseCache();

    CacheEntity getCache();

    public <T> void callback(final Callback<T> callback);
}
