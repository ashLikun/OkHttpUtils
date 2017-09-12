package com.ashlikun.okhttputils.simple.httptest;


import com.ashlikun.okhttputils.http.SimpleCallback;
import com.ashlikun.okhttputils.http.request.ProgressCallBack;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/8/2　11:30
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public abstract class HttpProgressCallBack<T> extends SimpleCallback<T> implements ProgressCallBack {


    @Override
    public long getRate() {
        return 200;
    }

    @Override
    public void setRate(long rate) {

    }
}
