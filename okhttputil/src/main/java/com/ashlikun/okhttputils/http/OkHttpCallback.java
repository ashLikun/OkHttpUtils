package com.ashlikun.okhttputils.http;

import com.ashlikun.okhttputils.http.response.HttpErrorCode;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

class OkHttpCallback<ResultType> implements okhttp3.Callback {
    ExecuteCall exc;
    Callback<ResultType> callback;

    public OkHttpCallback(ExecuteCall exc, Callback<ResultType> callback) {
        this.exc = exc;
        this.callback = callback;
        if (callback != null) {
            callback.onStart();
        }
    }

    @Override
    public void onFailure(Call call, final IOException e) {
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
    }

    private void postFailure(final HttpException throwable) {
        switchThread(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                callback.onError(throwable);
                exc.setCompleted(true);
                callback.onCompleted();
            }
        });
    }


    private void postResponse(final Response response, final ResultType resultType) {

        switchThread(new Consumer<Integer>() {
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
            postFailure(new HttpException(HttpErrorCode.HTTP_CANCELED, HttpErrorCode.MSG_CANCELED));
            response.close();
            return;
        }
        if (response.isSuccessful()) {
            try {
                ResultType resultType = OkHttpUtils.handerResult(getType(), response);
                postResponse(response, resultType);
            } catch (IOException e) {
                e.printStackTrace();
                HttpException res = new HttpException(HttpErrorCode.HTTP_DATA_ERROR, HttpErrorCode.MSG_DATA_ERROR);
                res.setOriginalException(e);
                postFailure(res);
                response.close();
            }
        } else {
            postFailure(getOnResponseHttpException(response.code(), response.message()));
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

    private void switchThread(Consumer next) {
        Observable.just(1).observeOn(AndroidSchedulers.mainThread())
                .subscribe(next);
    }

    /**
     * 获取回调里面的泛型
     */
    private Type getType() {
        Type types = callback.getClass().getGenericSuperclass();
        Type[] parentypes;//泛型类型集合
        if (types instanceof ParameterizedType) {
            parentypes = ((ParameterizedType) types).getActualTypeArguments();
        } else {
            parentypes = callback.getClass().getGenericInterfaces();
            for (Type childtype : parentypes) {
                if (childtype instanceof ParameterizedType) {
                    Type rawType = ((ParameterizedType) childtype).getRawType();
                    if (rawType instanceof Class && Callback.class.isAssignableFrom(((Class) rawType))) {//实现的接口是Callback
                        parentypes = ((ParameterizedType) childtype).getActualTypeArguments();//Callback里面的类型
                    }
                }
            }
        }
        if (parentypes == null || parentypes.length == 0) {
            new Throwable("HttpSubscription  ->>>  callBack回调 不能没有泛型，请查看HttpCallBack是否有泛型");
        } else {
            return parentypes[0];
        }
        return null;
    }
}
