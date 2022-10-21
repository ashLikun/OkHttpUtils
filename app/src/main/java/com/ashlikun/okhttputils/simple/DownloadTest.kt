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
var task: DownloadTask = DownloadTask(
//    url = "http://s.shouji.qihucdn.com/200407/28e9b1ac86444ec466f56ff0df9aa7b9/com.qihoo360.mobilesafe_267.apk?en=curpage%3D%26exp%3D1587610389%26from%3Dopenbox_detail_index%26m2%3D%26ts%3D1587005589%26tok%3D8ac5571c83e9bb8497076a3435f3c90d%26f%3Dz.apk",
    url = "https://www.pgyer.com/apiv2/app/install?_api_key=b6291f8ab5dab403b1cbb2344bed368f&appKey=d600ce66c1ae2a77073faf94167fb3b3",
    listener = object : DownloadTaskListener {
        override fun onDownloading(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
            Log.e("aaa", "onDownloading percent = $percent")
        }

        override fun onPause(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
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
var task2: DownloadTask = DownloadTask(
//    url = "http://s.shouji.qihucdn.com/200407/28e9b1ac86444ec466f56ff0df9aa7b9/com.qihoo360.mobilesafe_267.apk?en=curpage%3D%26exp%3D1587610389%26from%3Dopenbox_detail_index%26m2%3D%26ts%3D1587005589%26tok%3D8ac5571c83e9bb8497076a3435f3c90d%26f%3Dz.apk",
    url = "https://www.pgyer.com/apiv2/app/install?_api_key=b6291f8ab5dab403b1cbb2344bed368f&appKey=d87aa6d4fa2de890eaacce5c3ffd7ce3",
    listener = object : DownloadTaskListener {
        override fun onDownloading(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
            Log.e("aaa", "onDownloading percent = $percent")
        }

        override fun onPause(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
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

var task3: DownloadTask = DownloadTask(
//    url = "http://s.shouji.qihucdn.com/200407/28e9b1ac86444ec466f56ff0df9aa7b9/com.qihoo360.mobilesafe_267.apk?en=curpage%3D%26exp%3D1587610389%26from%3Dopenbox_detail_index%26m2%3D%26ts%3D1587005589%26tok%3D8ac5571c83e9bb8497076a3435f3c90d%26f%3Dz.apk",
    url = "https://www.pgyer.com/apiv2/app/install?_api_key=b6291f8ab5dab403b1cbb2344bed368f&appKey=b9d14f3f6c6e0d342675d894ba7cb799",
    listener = object : DownloadTaskListener {
        override fun onDownloading(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
            Log.e("aaa", "onDownloading percent = $percent")
        }

        override fun onPause(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
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
var task4: DownloadTask = DownloadTask(
//    url = "http://s.shouji.qihucdn.com/200407/28e9b1ac86444ec466f56ff0df9aa7b9/com.qihoo360.mobilesafe_267.apk?en=curpage%3D%26exp%3D1587610389%26from%3Dopenbox_detail_index%26m2%3D%26ts%3D1587005589%26tok%3D8ac5571c83e9bb8497076a3435f3c90d%26f%3Dz.apk",
    url = "https://www.pgyer.com/apiv2/app/install?_api_key=b6291f8ab5dab403b1cbb2344bed368f&appKey=693b78ba49c36b79c3f1602aaab0cb74",
    listener = object : DownloadTaskListener {
        override fun onDownloading(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
            Log.e("aaa", "onDownloading percent = $percent")
        }

        override fun onPause(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
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

var task5: DownloadTask = DownloadTask(
//    url = "http://s.shouji.qihucdn.com/200407/28e9b1ac86444ec466f56ff0df9aa7b9/com.qihoo360.mobilesafe_267.apk?en=curpage%3D%26exp%3D1587610389%26from%3Dopenbox_detail_index%26m2%3D%26ts%3D1587005589%26tok%3D8ac5571c83e9bb8497076a3435f3c90d%26f%3Dz.apk",
    url = "https://www.pgyer.com/apiv2/app/install?_api_key=b6291f8ab5dab403b1cbb2344bed368f&appKey=b393d811f4cefce59ea819363ee10c89",
    listener = object : DownloadTaskListener {
        override fun onDownloading(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
            Log.e("aaa", "onDownloading percent = $percent")
        }

        override fun onPause(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
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
var task6: DownloadTask = DownloadTask(
//    url = "http://s.shouji.qihucdn.com/200407/28e9b1ac86444ec466f56ff0df9aa7b9/com.qihoo360.mobilesafe_267.apk?en=curpage%3D%26exp%3D1587610389%26from%3Dopenbox_detail_index%26m2%3D%26ts%3D1587005589%26tok%3D8ac5571c83e9bb8497076a3435f3c90d%26f%3Dz.apk",
    url = "https://www.pgyer.com/apiv2/app/install?_api_key=b6291f8ab5dab403b1cbb2344bed368f&appKey=02046ff1a7f97a7e7cdd37811cabd835",
    listener = object : DownloadTaskListener {
        override fun onDownloading(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
            Log.e("aaa", "onDownloading percent = $percent")
        }

        override fun onPause(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double) {
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