package com.ashlikun.okhttputils.http.request

import com.ashlikun.okhttputils.http.HttpUtils.getMimeType
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.lang.Exception

/**
 * 作者　　: 李坤
 * 创建时间:2017/3/17　15:56
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
class FileInput(
    var key: String,
    var file: File,
    var filename: String = "",
    var contentType: MediaType? = null
) {

    init {
        if (exists()) {
            if (filename.isEmpty()) {
                filename = file.name
            }
            if (contentType == null) {
                contentType = getMimeType(file.absolutePath).toMediaTypeOrNull()
            }
        }
    }

    fun exists(): Boolean {
        try {
            return file.exists()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun toString(): String {
        return "FileInput{" +
                "key='" + key + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", file=" + file +
                '}'
    }
}