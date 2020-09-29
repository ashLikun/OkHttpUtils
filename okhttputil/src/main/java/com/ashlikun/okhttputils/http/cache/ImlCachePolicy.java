package com.ashlikun.okhttputils.http.cache;

import android.text.TextUtils;

import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.callback.Callback;
import com.ashlikun.okhttputils.http.request.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

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
    public <T> void callback(Call call, final Callback<T> callback) {
        final Object lock = new Object();

        try {
            final CacheEntity cacheEntity = getCache();
            Response response = new Response.Builder()
                    .code(cacheEntity.code)
                    .message(cacheEntity.message)
                    .protocol(Protocol.get(cacheEntity.protocol))
                    .request(request.getRequest())
                    .body(new CacheResponseBody(cacheEntity))
                    .build();
            T result = callback.convertResponse(response, request.getParseGson());
            //有缓存
            if (cacheEntity != null) {
                HttpUtils.runmainThread(() -> {
                    try {
                        if (call != null && call.isCanceled()) {
                            return;
                        }
                        callback.onCacheSuccess(cacheEntity, result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    } finally {
                        //唤醒子线程
                        synchronized (lock) {//获取对象锁
                            lock.notify();
                        }
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //让子线程等待主线程结果
            try {
                //获取对象锁
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void save(Response response, String result) {
        //保存缓存
        if (request != null && cacheMode != null && cacheMode != CacheMode.NO_CACHE && !TextUtils.isEmpty(result)) {
            CacheEntity.createCacheEntity(request, response, result).save();
        }
    }

    private static class CacheResponseBody extends ResponseBody {
        CacheEntity cacheEntity;

        CacheResponseBody(CacheEntity cacheEntity) {
            this.cacheEntity = cacheEntity;
        }

        @Override
        public MediaType contentType() {
            return MediaType.get(cacheEntity.contentType);
        }

        @Override
        public long contentLength() {
            return cacheEntity.contentLength;
        }

        @Override
        public BufferedSource source() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(cacheEntity.result.getBytes(Charset.forName("UTF-8")));
            return Okio.buffer(new ForwardingSource(Okio.source(inputStream)) {
                @Override
                public void close() throws IOException {
                    super.close();
                    inputStream.close();
                }
            });
        }
    }
}
