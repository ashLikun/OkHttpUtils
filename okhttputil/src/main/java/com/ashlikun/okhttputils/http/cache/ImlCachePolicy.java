package com.ashlikun.okhttputils.http.cache;

import android.text.TextUtils;

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
public class ImlCachePolicy extends BaseCachePolicy {

    public ImlCachePolicy(HttpRequest request) {
        super(request);
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
        }).subscribeOn(Schedulers.io())//指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread())//指定回调在主线程
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        callback.onCacheSuccess(entity[0], t);
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
