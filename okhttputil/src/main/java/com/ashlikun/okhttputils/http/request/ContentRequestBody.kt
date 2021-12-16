package com.ashlikun.okhttputils.http.request

import com.ashlikun.okhttputils.http.HttpUtils
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.IOException

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 19:33
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：data方式的提交，如json
 */
open class ContentRequestBody private constructor(
    private val mediaType: MediaType?,
    private val content: ByteArray
) : RequestBody() {

    private val byteCount: Int = content.size

    fun getContent(): String {
        return String(content, mediaType?.charset() ?: HttpUtils.UTF_8)
    }

    override fun contentType(): MediaType? {
        return mediaType
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return byteCount.toLong()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        sink.write(content, 0, byteCount)
    }

    companion object {
        fun createNew(contentType: MediaType?, content: String): RequestBody {
            var contentType = contentType
            var charset = contentType?.charset() ?: HttpUtils.UTF_8
            if (contentType != null) {
                if (contentType.charset() == null) {
                    contentType = "$contentType; charset=utf-8".toMediaTypeOrNull()
                }
            }
            val bytes = content.toByteArray(charset!!)
            return ContentRequestBody(contentType, bytes)
        }
    }


}