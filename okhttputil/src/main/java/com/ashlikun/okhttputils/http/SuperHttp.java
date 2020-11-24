package com.ashlikun.okhttputils.http;

import com.ashlikun.okhttputils.http.callback.Callback;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 作者　　: 李坤
 * 创建时间:2017/3/17　15:44
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public interface SuperHttp {


    //异步回调
    <T> ExecuteCall execute(Callback<T> callback);


    //同步执行
    <ResultType> ResultType syncExecute(Type rawType, Type... typeArguments) throws IOException;


}
