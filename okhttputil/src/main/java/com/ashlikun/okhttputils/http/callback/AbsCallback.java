package com.ashlikun.okhttputils.http.callback;

import com.ashlikun.okhttputils.http.HttpException;
import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.cache.CacheEntity;
import com.google.gson.Gson;

import okhttp3.Response;

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
 * @Link {@link com.ashlikun.okhttputils.http.response.HttpResult} 直接序列化的javabean,也可以自定义HttpResult。注意json的键
 * @Link 其他实体类。注意json的键
 */
public abstract class AbsCallback<ResultType> implements Callback<ResultType> {
    @Override
    public ResultType convertResponse(Response response, Gson gosn) throws Exception {
        return HttpUtils.handerResult(HttpUtils.getType(getClass()), response, gosn);
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(HttpException e) {

    }

    @Override
    public void onSuccess(ResultType responseBody) {

    }

    @Override
    public void onCacheSuccess(CacheEntity entity, ResultType responseBody) {

    }

    @Override
    public void onSuccessSubThread(ResultType responseBody) {

    }

    @Override
    public boolean onSuccessHandelCode(ResultType responseBody) {
        return true;
    }
}
