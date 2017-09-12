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
 * @Link {@link com.ashlikun.okhttputils.http.response.HttpResponse} 只是序列化了code和msg，但是内部有json字符串可供解析其他数据
 * @Link {@link com.ashlikun.okhttputils.http.response.HttpResult} 直接序列化的javabean,也可以自定义HttpResult。注意json的键
 * @Link 其他实体类。注意json的键
 */
public abstract class Callback<ResultType> {

    public void onStart() {

    }

    public void onCompleted() {

    }

    public void onError(HttpException e) {

    }

    public abstract void onSuccess(ResultType responseBody);

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/9/6 14:17
     * 邮箱　　：496546144@qq.com
     * 方法功能：接口请求成功了，但是处理code
     *
     * @return true:没问题
     * false:有问题
     */
    public boolean onSuccessHandelCode(ResultType responseBody) {
        return true;
    }
}
