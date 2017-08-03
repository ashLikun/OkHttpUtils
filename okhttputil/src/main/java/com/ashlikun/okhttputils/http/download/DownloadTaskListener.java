package com.ashlikun.okhttputils.http.download;

import java.io.File;

/**
 * 作者　　: 李坤
 * 创建时间:2016/12/13　15:09
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public interface DownloadTaskListener {
    /**
     * 下载中
     *
     * @param completedSize
     * @param totalSize
     * @param percent
     * @param downloadTask
     */
    void onDownloading(DownloadTask downloadTask, long completedSize, long totalSize, double percent);

    /**
     * 下载暂停
     *
     * @param downloadTask
     * @param completedSize
     * @param totalSize
     * @param percent
     */
    void onPause(DownloadTask downloadTask, long completedSize, long totalSize, double percent);

    /**
     * 下载取消
     *
     * @param downloadTask
     */
    void onCancel(DownloadTask downloadTask);

    /**
     * 下载成功
     *
     * @param file
     * @param downloadTask
     */
    abstract void onDownloadSuccess(DownloadTask downloadTask, File file);

    /**
     * 下载失败
     *
     * @param downloadTask
     * @param errorCode    {@link DownloadStatus}
     */
    void onError(DownloadTask downloadTask, int errorCode);
}
