package com.ashlikun.okhttputils.http.callback

import android.os.Environment
import com.ashlikun.okhttputils.http.convert.FileConvert
import com.ashlikun.okhttputils.http.download.DownloadManager
import com.google.gson.Gson
import okhttp3.Response
import java.io.File

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 14:11
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：文件的回调下载进度监听
 */

abstract class FileCallback(
    folder: String = Environment.getExternalStorageDirectory()
        .toString() + FileConvert.DM_TARGET_FOLDER,
    //目标文件存储的文件名
    var fileName: String = ""
) : AbsCallback<File>() {

    //文件转换类
    private val convert: FileConvert = FileConvert(folder, fileName, progressRate, progressCallBack)

    @Throws(Exception::class)
    override fun convertResponse(response: Response, gosn: Gson): File {
        val file = convert.convertResponse(response, gosn)
        response.close()
        return file
    }


}