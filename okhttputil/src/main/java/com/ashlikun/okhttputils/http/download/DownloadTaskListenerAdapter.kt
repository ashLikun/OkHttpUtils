package com.ashlikun.okhttputils.http.download

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 17:34
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：实现接口的抽象类
 */
abstract class DownloadTaskListenerAdapter : DownloadTaskListener {
    /**
     * 下载中
     *
     * @param completedSize
     * @param totalSize
     * @param percent
     * @param downloadTask
     */
    override fun onDownloading(
        downloadTask: DownloadTask,
        completedSize: Long,
        totalSize: Long,
        percent: Double
    ) {
    }

    /**
     * 下载暂停
     *
     * @param downloadTask
     * @param completedSize
     * @param totalSize
     * @param percent
     */
    override fun onPause(
        downloadTask: DownloadTask,
        completedSize: Long,
        totalSize: Long,
        percent: Double
    ) {
    }

    /**
     * 下载取消
     *
     * @param downloadTask
     */
    override fun onCancel(downloadTask: DownloadTask) {}

    /**
     * 下载失败
     *
     * @param downloadTask
     * @param errorCode    [DownloadStatus]
     */
    override fun onError(downloadTask: DownloadTask, errorCode: Int) {}
}