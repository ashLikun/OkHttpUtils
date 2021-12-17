package com.ashlikun.okhttputils.http.download

import java.io.File

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 13:52
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：下载监听
 */
interface DownloadTaskListener {
    /**
     * 下载中
     *
     * @param completedSize 完成的大小
     * @param totalSize     总大小
     * @param percent       百分比
     * @param downloadTask
     */
    fun onDownloading(
        downloadTask: DownloadTask,
        completedSize: Long,
        totalSize: Long,
        percent: Double
    )

    /**
     * 下载暂停
     *
     * @param downloadTask
     * @param completedSize
     * @param totalSize
     * @param percent
     */
    fun onPause(downloadTask: DownloadTask, completedSize: Long, totalSize: Long, percent: Double)

    /**
     * 下载取消
     *
     * @param downloadTask
     */
    fun onCancel(downloadTask: DownloadTask)

    /**
     * 下载成功
     *
     * @param file
     * @param downloadTask
     */
    fun onDownloadSuccess(downloadTask: DownloadTask, file: File)

    /**
     * 下载失败
     *
     * @param downloadTask
     * @param errorCode    [DownloadStatus]
     */
    fun onError(downloadTask: DownloadTask, errorCode: Int)
}