package com.ashlikun.okhttputils.http.cache

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 13:57
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：缓存模式
 */

enum class CacheMode {
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
    FIRST_CACHE_THEN_REQUEST
}