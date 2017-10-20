package com.ashlikun.okhttputils.http;

import com.ashlikun.okhttputils.http.request.RequestCall;
import com.ashlikun.okhttputils.http.request.RequestParam;

import java.io.IOException;

/**
 * 作者　　: 李坤
 * 创建时间:2017/3/17　15:44
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public interface SuperHttp {

    //默认的超时时间
    public static final long DEFAULT_MILLISECONDS = 20_000L;
    public static final long DEFAULT_MILLISECONDS_LONG = 200_000L;


    //异步回调
    <T> ExecuteCall execute(RequestCall requestCall, Callback<T> callback);

    //异步回调
    <T> ExecuteCall execute(RequestParam requestParam, Callback<T> callback);

    //同步执行
    public <ResultType> ResultType syncExecute(RequestCall requestCall, Class raw, final Class... args) throws IOException;

    //同步执行
    public <ResultType> ResultType syncExecute(RequestParam requestParam, Class raw, final Class... args) throws IOException;

}
