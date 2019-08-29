package com.ashlikun.okhttputils.http;

import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/8/22　17:18
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：当数据解析错误（数据格式不对）,用于上报服务器
 */
public interface OnDataParseError {
    /**
     * 在子线程
     *
     * @param code      错误code
     * @param exception 异常 一般是JsonSyntaxException
     * @param response  Response
     * @param json      解析的json数据
     */
    void onError(int code, Exception exception, Response response, String json);
}
