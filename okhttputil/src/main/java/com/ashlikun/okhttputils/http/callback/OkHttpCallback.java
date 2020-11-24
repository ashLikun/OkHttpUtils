package com.ashlikun.okhttputils.http.callback;

import com.ashlikun.okhttputils.http.ExecuteCall;
import com.ashlikun.okhttputils.http.HttpException;
import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.cache.CacheEntity;
import com.ashlikun.okhttputils.http.cache.CacheMode;
import com.ashlikun.okhttputils.http.cache.CachePolicy;
import com.ashlikun.okhttputils.http.response.HttpErrorCode;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间:2017/3/23　14:14
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：okhttp的直接回调
 */

public class OkHttpCallback<ResultType> implements okhttp3.Callback {
    ExecuteCall exc;
    Callback<ResultType> callback;
    Gson gson;
    CachePolicy cachePolicy;

    public OkHttpCallback(ExecuteCall exc, Callback<ResultType> callback) {
        this.exc = exc;
        this.callback = callback;
        if (callback != null) {
            callback.onStart();
        }
    }

    public void setCachePolicy(CachePolicy cachePolicy) {
        this.cachePolicy = cachePolicy;
        //回掉缓存
        if (cachePolicy.getCacheMode() == CacheMode.FIRST_CACHE_THEN_REQUEST) {
            HttpUtils.runNewThread(() -> {
                cachePolicy.callback(exc.getCall(), callback);
            });
        }
    }

    public boolean checkCanceled() {
        if (exc.getCall().isCanceled()) {
            exc.setCompleted(true);
            if (!HttpUtils.isMainThread()) {
                HttpUtils.runmainThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onCompleted();
                    }
                });
            } else {
                callback.onCompleted();
            }
            return true;
        }
        return false;
    }

    public void setParseGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        if (checkCanceled()) {
            return;
        }
        //网络失败，回掉缓存
        if (cachePolicy != null && cachePolicy.getCacheMode() == CacheMode.REQUEST_FAILED_READ_CACHE) {
            if (checkCanceled()) {
                return;
            }
            cachePolicy.callback(call, callback);
            return;
        }
        HttpException res = HttpException.handleFailureHttpException(call, e);
        res.setOriginalException(e);
        postFailure(res);
    }

    private void postFailure(final HttpException throwable) {
        if (exc.getCall().isCanceled()) {
            return;
        }
        final Object lock = new Object();
        HttpUtils.runmainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (checkCanceled()) {
                        return;
                    }
                    callback.onError(throwable);
                    exc.setCompleted(true);
                    callback.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    //唤醒子线程
                    synchronized (lock) {//获取对象锁
                        lock.notify();
                    }
                }

            }
        });
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


    private void postResponse(final Response response, final ResultType resultType) {
        if (checkCanceled()) {
            response.close();
            return;
        }
        callback.onSuccessSubThread(resultType);
        final Object lock = new Object();

        HttpUtils.runmainThread(() -> {
            try {
                if (checkCanceled()) {
                    response.close();
                    return;
                }
                if (callback.onSuccessHandelCode(resultType)) {
                    if (checkCanceled()) {
                        response.close();
                        return;
                    }
                    callback.onSuccess(resultType);
                }
                exc.setCompleted(true);
                callback.onCompleted();
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                //唤醒子线程//获取对象锁
                synchronized (lock) {
                    lock.notify();
                }
            }
        });

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

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        if (checkCanceled()) {
            response.close();
            return;
        }
        //错误请求

        if (!response.isSuccessful()) {
            postFailure(HttpException.getOnResponseHttpException(response.code(), response.message()));
            response.close();
            return;
        }
        try {
            ResultType resultType = callback.convertResponse(response, gson);
            //缓存
            cachePolicy.save(response,
                    CacheEntity.getHanderResult(resultType));
            if (resultType == null) {
                postFailure(new HttpException(HttpErrorCode.HTTP_DATA_ERROR, HttpErrorCode.MSG_DATA_ERROR));
                response.close();
                return;
            }
            postResponse(response, resultType);
        } catch (Exception e) {
            e.printStackTrace();
            HttpException res = new HttpException(HttpErrorCode.HTTP_DATA_ERROR, HttpErrorCode.MSG_DATA_ERROR);
            res.setOriginalException(e);
            postFailure(res);
            response.close();
        }

    }
}
