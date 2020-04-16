package com.ashlikun.okhttputils.http.cache;

import android.text.TextUtils;

import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.callback.Callback;
import com.ashlikun.okhttputils.http.request.HttpRequest;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/11 0011　下午 1:36
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class ImlCachePolicy extends BaseCachePolicy {

    public ImlCachePolicy(HttpRequest request) {
        super(request);
    }

    @Override
    public <T> void callback(final Callback<T> callback) {
        final CacheEntity[] entity = {null};
        HttpUtils.runNewThread(new Runnable() {
            @Override
            public void run() {
                entity[0] = getCache();
                //有缓存
                if (entity[0] != null) {
                    HttpUtils.runmainThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                T t = HttpUtils.handerResult(HttpUtils.getType(callback.getClass()),
                                        entity[0], request.getParseGson());
                                callback.onCacheSuccess(entity[0], t);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public void save(Response response, String result) {
        //保存缓存
        if (request != null && cacheMode != null && cacheMode != CacheMode.NO_CACHE && !TextUtils.isEmpty(result)) {
            CacheEntity.createCacheEntity(request, response, result).save();
        }
    }
}
