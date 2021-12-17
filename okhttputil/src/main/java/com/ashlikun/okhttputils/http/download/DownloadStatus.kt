package com.ashlikun.okhttputils.http.download

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 10:32
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：下载的状态
 */
object DownloadStatus {
    /**
     * 初始化下载
     */
    const val DOWNLOAD_STATUS_INIT = 1

    /**
     * 下载中
     */
    const val DOWNLOAD_STATUS_DOWNLOADING = 2

    /**
     * 下载取消
     */
    const val DOWNLOAD_STATUS_CANCEL = 3

    /**
     * 下载错误
     */
    const val DOWNLOAD_STATUS_ERROR = 4

    /**
     * 下载完成
     */
    const val DOWNLOAD_STATUS_COMPLETED = 5

    /**
     * 下载暂停
     */
    const val DOWNLOAD_STATUS_PAUSE = 6

    /**
     * 下载错误  文件没有发现
     */
    const val DOWNLOAD_ERROR_FILE_NOT_FOUND = 7

    /**
     * 文件io错误
     */
    const val DOWNLOAD_ERROR_IO_ERROR = 8
}