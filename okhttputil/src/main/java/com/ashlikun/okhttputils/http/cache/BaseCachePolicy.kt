package com.ashlikun.okhttputils.http.cache

import com.ashlikun.okhttputils.http.request.HttpRequest

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 10:04
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：缓存的基础类
 */

abstract class BaseCachePolicy(override var request: HttpRequest) : CachePolicy {
    override var cacheMode: CacheMode = CacheMode.NO_CACHE
    override var cacheTime: Long = 0
    override val cache: CacheEntity?
        get() {
            var cacheEntity = CacheManage.queryById(CacheManage.getCacheKey(request))
            //检查过期
            if (cacheEntity != null && cacheTime != CacheEntity.CACHE_NEVER_EXPIRE && cacheTime > 0) {
                if (System.currentTimeMillis() - cacheEntity.cacheTime > cacheTime) {
                    //过期的
                    cacheEntity = null
                }
            }
            return cacheEntity
        }

}