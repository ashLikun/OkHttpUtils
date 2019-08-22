package com.ashlikun.okhttputils.http;

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
     * @param code     错误code
     * @param errorMsg 错误消息（异常msg）
     * @param json     解析的json数据
     */
    void onError(int code, String errorMsg, String json);
}
