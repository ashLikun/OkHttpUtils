package com.ashlikun.okhttputils.http;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
        postFailure(e);
    }

    private void postFailure(final Throwable throwable) {
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
                callback.onSuccess(resultType);
                exc.setCompleted(true);
                callback.onCompleted();
                response.close();
            }
        });
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        if (response.isSuccessful()) {
            try {
                ResultType resultType = OkHttpUtils.handerResult(getType(), response);
                postResponse(response, resultType);
            } catch (IOException e) {
                e.printStackTrace();
                onFailure(call, e);
                response.close();
            }
        } else {
            postFailure(new HttpException(response));
            response.close();
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
