package com.ashlikun.okhttputils.simple.httptest;


import com.ashlikun.okhttputils.http.Callback;

/**
 * 作者　　: 李坤
 * 创建时间:2017/6/15　15:29
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public abstract class HttpCallBack<T> implements Callback<T> {



    @Override
    public void onStart() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onSuccess(T responseBody) {

    }
}
