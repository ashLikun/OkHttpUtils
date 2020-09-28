package com.ashlikun.okhttputils.http.callback;

import com.ashlikun.okhttputils.http.HttpException;
import com.ashlikun.okhttputils.http.cache.CacheEntity;
import com.ashlikun.okhttputils.http.convert.Converter;

import java.lang.reflect.Type;

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
 * @Link {@link com.ashlikun.okhttputils.http.response.HttpResponse} 只解析了code和msg
 * @Link {@link com.ashlikun.okhttputils.http.response.IHttpResponse} 可以实现这个接口，照样有HttpResponse的功能
 * @Link {@link com.ashlikun.okhttputils.http.response.HttpResult} 直接序列化的javabean,也可以自定义HttpResult。注意json的键
 * @Link 其他实体类。注意json的键
 */
public interface Callback<ResultType> extends Converter<ResultType> {
    /**
     * 指定数据类型，不使用Callback的泛型
     */
    Type getResultType();

    void onStart();

    void onCompleted();

    void onError(HttpException e);

    void onSuccess(ResultType responseBody);

    void onCacheSuccess(CacheEntity entity, ResultType responseBody);

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/9/15 16:36
     * 邮箱　　：496546144@qq.com
     * <p>
     * 方法功能：成功回掉在子线程(当前http执行得线程)
     */
    void onSuccessSubThread(ResultType responseBody);

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/9/6 14:17
     * 邮箱　　：496546144@qq.com
     * 方法功能：接口请求成功了，但是处理code
     *
     * @return true:没问题
     * false:有问题
     */
    boolean onSuccessHandelCode(ResultType responseBody);

    Object getTag();
}
