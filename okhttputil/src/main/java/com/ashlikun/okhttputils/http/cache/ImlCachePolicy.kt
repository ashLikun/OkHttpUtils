package com.ashlikun.okhttputils.http.cache

import com.ashlikun.okhttputils.http.callback.Callback
import com.ashlikun.okhttputils.http.request.HttpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.Charset

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 11:30
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：缓存的实现
 */
open class ImlCachePolicy(request: HttpRequest) : BaseCachePolicy(request) {
    override suspend fun <T> callback(call: Call?, callback: Callback<T>) {
        try {
            val cacheEntity = cache
            //有缓存
            if (cacheEntity != null) {
                val response: Response = Response.Builder()
                    .code(cacheEntity!!.code)
                    .message(cacheEntity.message)
                    .protocol(Protocol.get(cacheEntity.protocol))
                    .request(request.request)
                    .body(CacheResponseBody(cacheEntity))
                    .build()
                val result = callback.convertResponse(response, request.parseGson)
                withContext(Dispatchers.Main) {
                    if (call?.isCanceled() == false) {
                        callback.onCacheSuccess(cacheEntity, result)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun save(response: Response, result: String) {
        //保存缓存
        if (cacheMode !== CacheMode.NO_CACHE && result.isNotEmpty()) {
            CacheEntity.createCacheEntity(request, response, result).save()
        }
    }

    private class CacheResponseBody(var cacheEntity: CacheEntity) : ResponseBody() {
        override fun contentType(): MediaType {
            return cacheEntity.contentType.toMediaType()
        }

        override fun contentLength(): Long {
            return cacheEntity.contentLength
        }

        override fun source(): BufferedSource {
            val inputStream = ByteArrayInputStream(
                cacheEntity.result.toByteArray(Charset.forName("UTF-8"))
            )
            return object : ForwardingSource(inputStream.source()) {
                @Throws(IOException::class)
                override fun close() {
                    super.close()
                    inputStream.close()
                }
            }.buffer()
        }
    }
}