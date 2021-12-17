package com.ashlikun.okhttputils.http.cache

import android.util.Log
import com.ashlikun.okhttputils.http.HttpUtils
import com.ashlikun.okhttputils.http.cache.CacheEntity
import com.ashlikun.okhttputils.http.request.HttpRequest
import com.ashlikun.orm.LiteOrmUtil
import com.ashlikun.orm.db.assit.WhereBuilder

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 13:57
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：缓存管理器
 */
object CacheManage {
    fun update(entity: CacheEntity): Boolean {
        return try {
            LiteOrmUtil.get().update(entity)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun delete(entity: CacheEntity): Boolean {
        return try {
            LiteOrmUtil.get().delete(entity)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun delete(key: String): Boolean {
        return try {
            LiteOrmUtil.get().delete(
                WhereBuilder(
                    CacheEntity::class.java
                ).where("key=?", key)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun delete(request: HttpRequest): Boolean {
        return delete(getCacheKey(request))
    }

    fun deleteAll() {
        try {
            LiteOrmUtil.get().delete(CacheEntity::class.java)
        } catch (e: Exception) {
        }
    }

    fun queryById(id: String): CacheEntity? {
        return LiteOrmUtil.get().queryById(id, CacheEntity::class.java)
    }

    fun queryById(request: HttpRequest): CacheEntity? {
        return LiteOrmUtil.get().queryById(getCacheKey(request), CacheEntity::class.java)
    }

    fun queryAll(): List<CacheEntity> {
        return LiteOrmUtil.get().query(CacheEntity::class.java)
    }

    fun getCacheKey(request: HttpRequest): String {
        return request.cacheKey
    }
}