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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;
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
            cachePolicy.callback(callback);
        }
    }

    public void setParseGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        if (exc.getCall().isCanceled()) {
            return;
        }
        HttpException res;
        if (e instanceof ConnectException) {
            res = new HttpException(HttpErrorCode.HTTP_NO_CONNECT, HttpErrorCode.MSG_NO_CONNECT);
        } else if (e instanceof SocketException) {
            //java.net.SocketException: sendto failed: ECONNRESET (Connection reset by peer)
            //服务器的并发连接数超过了其承载量，服务器会将其中一些连接关闭；
            //如果知道实际连接服务器的并发客户数没有超过服务器的承载量，则有可能是中了病毒或者木马，引起网
            res = new HttpException(HttpErrorCode.HTTP_SOCKET_ERROR, HttpErrorCode.MSG_SOCKET_ERROR);
        } else if (e instanceof SocketTimeoutException) {
            res = new HttpException(HttpErrorCode.HTTP_TIME_OUT, HttpErrorCode.MSG_TIME_OUT);
        } else if (e instanceof UnknownHostException) {
            res = new HttpException(HttpErrorCode.HTTP_UNKNOWN_HOST, HttpErrorCode.MSG_UNKNOWN_HOST);
        } else {
            e.printStackTrace();
            res = new HttpException(HttpErrorCode.HTTP_UNKNOWN, HttpErrorCode.MSG_UNKNOWN);
        }
        res.setOriginalException(e);
        postFailure(res);
        //网络失败，回掉缓存
        if (cachePolicy.getCacheMode() == CacheMode.REQUEST_FAILED_READ_CACHE) {
            cachePolicy.callback(callback);
        }
    }

    private void postFailure(final HttpException throwable) {
        if (exc.getCall().isCanceled()) {
            return;
        }
        HttpUtils.runmainThread(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                callback.onError(throwable);
                exc.setCompleted(true);
                callback.onCompleted();
            }
        });
    }


    private void postResponse(final Response response, final ResultType resultType) {
        if (exc.getCall().isCanceled()) {
            response.close();
            return;
        }
        callback.onSuccessSubThread(resultType);
        HttpUtils.runmainThread(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (callback.onSuccessHandelCode(resultType)) {
                    callback.onSuccess(resultType);
                }
                exc.setCompleted(true);
                callback.onCompleted();
                response.close();
            }
        });
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        if (call.isCanceled()) {
            response.close();
            return;
        }
        //错误请求
        if (response.code() == 404 || response.code() >= 500) {
            postFailure(getOnResponseHttpException(response.code(), response.message()));
            response.close();
            return;
        }
        try {
            ResultType resultType = callback.convertResponse(response, gson);
            //缓存
            if (cachePolicy != null) {
                cachePolicy.save(response,
                        CacheEntity.getHanderResult(resultType));
            }
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

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/8/9 15:23
     * 邮箱　　：496546144@qq.com
     * 方法功能：当成功的时候返回的.但是code不是200的时候
     */
    private HttpException getOnResponseHttpException(int code, String message) {
        if (message != null && message.contains("timed out") || code == HttpURLConnection.HTTP_CLIENT_TIMEOUT || code == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
            return new HttpException(HttpErrorCode.HTTP_TIME_OUT, HttpErrorCode.MSG_TIME_OUT);
        } else if (code == HttpURLConnection.HTTP_INTERNAL_ERROR || code == 0) {//500
            return new HttpException(code, HttpErrorCode.MSG_INTERNAL_ERROR);
        } else if (code == HttpURLConnection.HTTP_FORBIDDEN) {//403
            return new HttpException(code, HttpErrorCode.MSG_FORBIDDEN);
        } else if (code == HttpURLConnection.HTTP_NOT_FOUND) {//404
            return new HttpException(code, HttpErrorCode.MSG_NOT_FOUND);
        } else if (code == HttpURLConnection.HTTP_BAD_METHOD) {//405
            return new HttpException(code, HttpErrorCode.MSG_BAD_METHOD);
        } else if (code == HttpURLConnection.HTTP_MULT_CHOICE || code == HttpURLConnection.HTTP_MOVED_PERM
                || code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_SEE_OTHER) {//300
            return new HttpException(code, HttpErrorCode.MSG_REDIRRECT);
        } else {
            //其他错误
            return new HttpException(code, HttpErrorCode.MSG_UNKNOWN_NETWORK);
        }
    }

}
