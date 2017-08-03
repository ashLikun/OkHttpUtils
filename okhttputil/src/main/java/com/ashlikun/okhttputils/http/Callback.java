package com.ashlikun.okhttputils.http;

/**
 * 作者　　: 李坤
 * 创建时间:2016/12/30　17:31
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：http返回值的回调接口
 * 泛型可以是
 *
 * @Link {@link String}
 * @Link {@link okhttp3.Response}//对这个操作要在子线程
 * @Link {@link okhttp3.ResponseBody}
 * @Link {@link com.hbung.http.response.HttpResult} 直接序列化的javabean,也可以自定义HttpResult。注意json的键
 * @Link 其他实体类。注意json的键
 */
public interface Callback<ResultType> {

    void onStart();

    void onCompleted();

    void onError(Throwable e);

    void onSuccess(ResultType responseBody);
}
