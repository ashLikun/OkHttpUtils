package com.ashlikun.okhttputils.http;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/9/12　10:25
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：对Callback的实现，只抽象onSccess方法
 */

public abstract class SimpleCallback<ResultType> implements Callback<ResultType> {
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
    @Override
    public boolean onSuccessHandelCode(ResultType responseBody) {
        return true;
    }

    @Override
    public void onSuccessSubThread(ResultType responseBody) {

    }
}
