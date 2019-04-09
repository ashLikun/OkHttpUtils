package com.ashlikun.okhttputils.http.cache;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/4/4　17:45
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：缓存模式
 */
public enum CacheMode {

    /**
     * 不使用缓存
     */
    NO_CACHE,

    /**
     * 请求网络失败后，读取缓存
     */
    REQUEST_FAILED_READ_CACHE,

    /**
     * 如果缓存 不存在才请求网络，否则使用缓存
     */
    IF_NONE_CACHE_REQUEST,

    /**
     * 先使用缓存，不管是否存在，仍然请求网络
     */
    FIRST_CACHE_THEN_REQUEST,
}