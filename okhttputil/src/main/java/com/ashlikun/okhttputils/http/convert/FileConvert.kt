package com.ashlikun.okhttputils.http.convert

import android.os.Environment
import com.ashlikun.okhttputils.http.HttpUtils
import com.ashlikun.okhttputils.http.IOUtils
import com.ashlikun.okhttputils.http.callback.ProgressCallBack
import com.ashlikun.okhttputils.http.response.HttpErrorCode
import com.google.gson.Gson
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 14:20
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：文件转换器
 */

class FileConvert(//目标文件存储的文件夹路径
    var folder: String = Environment.getExternalStorageDirectory()
        .toString() + DM_TARGET_FOLDER,
    //目标文件存储的文件名
    var fileName: String = ""
) : Converter<File?> {
    var callBack: ProgressCallBack? = null


    @Throws(Exception::class)
    override fun convertResponse(response: Response, gosn: Gson?): File {
        val url = response.request.url.toString()
        if (fileName.isEmpty()) {
            fileName = HttpUtils.getNetFileName(response, url)
        }
        val dir = File(folder)
        IOUtils.createFolder(dir)
        val file = File(dir, fileName)
        IOUtils.delFileOrFolder(file)
        var bodyStream: InputStream? = null
        val buffer = ByteArray(2 * 1024)
        var fileOutputStream: FileOutputStream? = null
        return try {
            val body = response.body
                ?: throw IOException("${HttpErrorCode.MSG_DATA_ERROR2}  \n file create error")
            val totalSize = body.contentLength()
            var completedSize: Long = 0
            bodyStream = body.byteStream()
            var len: Int
            fileOutputStream = FileOutputStream(file)
            //上一次的时间
            var timeOld = System.currentTimeMillis()
            while (bodyStream.read(buffer).also { len = it } != -1) {
                fileOutputStream.write(buffer, 0, len)
                completedSize += len.toLong()
                //计算是否要回调
                if (Math.abs(System.currentTimeMillis() - timeOld) > callBack!!.rate) {
                    timeOld = System.currentTimeMillis()
                    //回调
                    onProgress(completedSize, totalSize, completedSize == totalSize)
                }
            }
            // 防止最后一次不足rate时间，导致percent无法达到100%
            onProgress(completedSize, totalSize, completedSize == totalSize)
            fileOutputStream.flush()
            file
        } finally {
            IOUtils.closeQuietly(bodyStream)
            IOUtils.closeQuietly(fileOutputStream)
        }
    }

    private fun onProgress(progress: Long, total: Long, done: Boolean) {
        HttpUtils.launchMain { callBack!!.onLoading(progress, total, done, false) }
    }

    fun progressCallback(callBack: ProgressCallBack?) {
        this.callBack = callBack
    }

    companion object {
        //下载目标文件夹
        val DM_TARGET_FOLDER = File.separator + "download" + File.separator
    }
}