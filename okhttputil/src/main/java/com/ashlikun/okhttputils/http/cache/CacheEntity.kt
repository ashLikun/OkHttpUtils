package com.ashlikun.okhttputils.http.cache

import com.ashlikun.okhttputils.http.request.HttpRequest
import com.ashlikun.okhttputils.http.response.IHttpResponse
import com.ashlikun.orm.LiteOrmUtil
import com.ashlikun.orm.db.annotation.PrimaryKey
import com.ashlikun.orm.db.annotation.Table
import com.ashlikun.orm.db.enums.AssignType
import com.google.gson.Gson
import okhttp3.Response
import java.util.*

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 13:55
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：缓存的实体类,只保存数据的String
 */
@Table("HttpCacheEntity")
open class CacheEntity {
    /**
     * 缓存的key
     * */
    @PrimaryKey(AssignType.BY_MYSELF)
    var key: String = ""

    /**
     * 缓存的时间
     */
    var cacheTime: Long = 0

    var contentType: String = ""

    var contentLength: Long = 0

    var protocol: String = ""

    /**
     * 缓存的头部json
     */
    var head: String = ""

    /**
     * 缓存的实体数据
     */
    var result: String = ""

    var code = 0

    var message: String = ""
    fun save() {
        LiteOrmUtil.get().save(this)
    }

    companion object {
        /**
         * 缓存永不过期
         */
        const val CACHE_NEVER_EXPIRE: Long = -1

        /**
         * 根据返回值，取String,只缓存这些
         * 或者实现tostring方法
         *
         * @param resultType
         * @param <ResultType>
         * @return
        </ResultType> */
        fun <ResultType> getHanderResult(resultType: ResultType?): String {
            if (resultType == null) {
                return ""
            }
            return if (resultType is IHttpResponse) {
                (resultType as IHttpResponse).json
            } else {
                resultType.toString()
            }
        }

        fun createCacheEntity(
            request: HttpRequest,
            response: Response,
            result: String
        ): CacheEntity {
            val cacheEntity = CacheEntity()
            cacheEntity.key = CacheManage.getCacheKey(request)
            cacheEntity.cacheTime = System.currentTimeMillis()
            if (response.body != null) {
                if (response.body!!.contentType() != null) {
                    cacheEntity.contentType = response.body!!.contentType().toString()
                }
                cacheEntity.contentLength = response.body!!.contentLength()
            }
            cacheEntity.protocol = response.protocol.toString()
            val headMap: MutableMap<String, String> = HashMap()
            for (headerName in response.headers.names()) {
                headMap[headerName] = response.headers[headerName]!!
            }
            cacheEntity.head = Gson().toJson(headMap)
            cacheEntity.result = result
            cacheEntity.code = response.code
            cacheEntity.message = response.message
            return cacheEntity
        }
    }
}