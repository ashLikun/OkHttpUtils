package com.ashlikun.okhttputils.simple

import android.util.Log
import com.ashlikun.okhttputils.http.download.DownloadTask
import com.ashlikun.okhttputils.http.download.DownloadTaskListener
import java.io.File

/**
 * 作者　　: 李坤
 * 创建时间: 2021/12/17　21:35
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
var task: DownloadTask = DownloadTask("resmp;csr=1bbd",
//    url = "http://s.shouji.qihucdn.com/200407/28e9b1ac86444ec466f56ff0df9aa7b9/com.qihoo360.mobilesafe_267.apk?en=curpage%3D%26exp%3D1587610389%26from%3Dopenbox_detail_index%26m2%3D%26ts%3D1587005589%26tok%3D8ac5571c83e9bb8497076a3435f3c90d%26f%3Dz.apk",
    url = "http://s.shouji.qihucdn.com/211215/71fcfc885874016c5bbf183c76d08ab7/com.ss.android.ugc.aweme_180901.apk?en=curpage%3D%26exp%3D1640354101%26from%3DAppList_qcms1%26m2%3D%26ts%3D1639749301%26tok%3D9e27d6e102c5921ffa7ef7a03ed91048%26v%3D%26f%3Dz.apk",
    object : DownloadTaskListener {
        override fun onDownloading(
            downloadTask: DownloadTask,
            completedSize: Long,
            totalSize: Long,
            percent: Double
        ) {
            Log.e("aaa", "onDownloading percent = $percent")
        }

        override fun onPause(
            downloadTask: DownloadTask,
            completedSize: Long,
            totalSize: Long,
            percent: Double
        ) {
            Log.e("aaa", "onPause percent = $percent")
        }

        override fun onCancel(downloadTask: DownloadTask) {
            Log.e("aaa", "onCancel ")
        }

        override fun onDownloadSuccess(downloadTask: DownloadTask, file: File) {
            Log.e("aaa", "onDownloadSuccess  " + file.path)
        }

        override fun onError(downloadTask: DownloadTask, errorCode: Int) {
            Log.e("aaa", "onError $errorCode")
        }
    })
