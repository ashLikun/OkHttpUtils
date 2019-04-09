package com.ashlikun.okhttputils.http.cache;

import com.ashlikun.okhttputils.http.request.HttpRequest;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/4/4　17:47
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public abstract class BaseCachePolicy implements CachePolicy {
    protected HttpRequest request;
    protected CacheMode cacheMode;
    protected long cacheTime;

    public BaseCachePolicy(HttpRequest request) {
        this.request = request;
    }

    @Override
    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    @Override
    public void setCacheTime(long time) {
        cacheTime = time;
    }

    @Override
    public CacheMode getCacheMode() {
        return cacheMode;
    }


    @Override
    public long getCacheTime() {
        return cacheTime;
    }

    @Override
    public CacheEntity getCache() {
        if (request == null) {
            return null;
        }
        CacheEntity cacheEntity = CacheManage.queryById(CacheManage.getCacheKey(request));
        //检查过期
        if (cacheEntity != null && cacheTime != CacheEntity.CACHE_NEVER_EXPIRE && cacheTime > 0) {
            if (System.currentTimeMillis() - cacheEntity.cacheTime > cacheTime) {
                //过期的
                cacheEntity = null;
            }
        }
        return cacheEntity;
    }
}
