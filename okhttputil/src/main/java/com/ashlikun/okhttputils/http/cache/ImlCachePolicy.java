package com.ashlikun.okhttputils.http.cache;

import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.callback.Callback;
import com.ashlikun.okhttputils.http.request.HttpRequest;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/11 0011　下午 1:36
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class ImlCachePolicy implements CachePolicy {
    HttpRequest request;
    /**
     * 先使用缓存，不管是否存在，仍然请求网络
     * 这是自定义缓存，和http标准的缓存不一样
     */
    private boolean isFirstUseCache = false;

    @Override
    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    @Override
    public void setFirstUseCache(boolean firstUseCache) {
        isFirstUseCache = firstUseCache;
    }

    @Override
    public boolean isFirstUseCache() {
        return isFirstUseCache;
    }

    @Override
    public CacheEntity getCache() {
        if (request == null) {
            return null;
        }
        return CacheManage.queryById(CacheManage.getCacheKey(request));
    }

    @Override
    public <T> void callback(final Callback<T> callback) {
        final CacheEntity[] entity = {null};
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                entity[0] = getCache();
                //有缓存
                if (entity[0] != null) {
                    e.onNext((T) HttpUtils.handerResult(HttpUtils.getType(callback.getClass()),
                            entity[0], request.getParseGson()));
                } else {
                    e.onComplete();
                }

            }
        }).observeOn(Schedulers.computation())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        callback.onCacheSuccess(entity[0], t);
                    }
                });
    }


    @Override
    public void save(Response response, String result) {
        if (isFirstUseCache && request != null) {
            CacheEntity.createCacheEntity(request, response,
                    CacheEntity.getHanderResult(result)).save();
        }
    }
}
