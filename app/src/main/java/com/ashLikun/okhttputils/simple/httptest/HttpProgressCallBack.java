package com.ashLikun.okhttputils.simple.httptest;


import com.ashLikun.okhttputils.http.request.ProgressCallBack;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/8/2　11:30
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public abstract class HttpProgressCallBack<T> extends HttpCallBack<T> implements ProgressCallBack {


    @Override
    public long getRate() {
        return 200;
    }

    @Override
    public void setRate(long rate) {

    }
}
