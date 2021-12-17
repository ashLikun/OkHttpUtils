package com.ashlikun.okhttputils.http.request

import com.ashlikun.okhttputils.http.HttpUtils
import com.ashlikun.okhttputils.http.callback.ProgressCallBack
import com.ashlikun.okhttputils.http.download.DownloadManager
import kotlinx.coroutines.delay
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:39
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：上传进度封装
 */
open class ProgressRequestBody(
    //待包装的请求体
    val requestBody: RequestBody,
    //进度速度
    var rate: Long = DownloadManager.DEFAULT_RATE,
    //进度回调接口
    val progressCallBack: ProgressCallBack?,
) : RequestBody() {

    //包装完成的BufferedSink
    private var bufferedSink: BufferedSink? = null

    //当前写入字节数
    var bytesWritten = 0L

    //总字节长度，避免多次调用contentLength()方法
    var contentLength = 0L
    var isCancel = false
    var isRun = false

    /**
     * 重写调用实际的响应体的contentType
     */
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    /**
     * 重写调用实际的响应体的contentLength
     */
    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    /**
     * 重写进行写入
     */
    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (bufferedSink == null) {
            //包装
            bufferedSink = sink(sink).buffer()
        }
        //写入
        requestBody.writeTo(bufferedSink!!)
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink?.flush()
    }

    /**
     * 写入，回调进度接口
     */
    private fun sink(sink: Sink): Sink {
        if (progressCallBack != null && !isRun) {
            isRun = true
            HttpUtils.launchMain {
                delay(rate)
                run()
            }
        }
        return object : ForwardingSink(sink) {
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                if (contentLength == 0L) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength()
                }
                //增加当前写入的字节数
                bytesWritten += byteCount
                super.write(source, byteCount)
            }
        }
    }

    fun run() {
        if (contentLength != 0L) {
            progressCallBack?.invoke(
                bytesWritten, contentLength, bytesWritten == contentLength, true
            )
            if (bytesWritten == contentLength) {
                isCancel = true
                isRun = false
            }
        }
        if (!isCancel) {
            HttpUtils.launchMain {
                delay(rate)
                run()
            }
        }
    }
}