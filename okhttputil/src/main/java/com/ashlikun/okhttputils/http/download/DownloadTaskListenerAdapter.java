package com.ashlikun.okhttputils.http.download;

/**
 * 作者　　: 李坤
 * 创建时间: 15:10 Administrator
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：实现接口的抽象类
 */
public abstract class DownloadTaskListenerAdapter implements DownloadTaskListener {
    /**
     * 下载中
     *
     * @param completedSize
     * @param totalSize
     * @param percent
     * @param downloadTask
     */
    @Override
    public void onDownloading(DownloadTask downloadTask, long completedSize, long totalSize, double percent) {

    }

    /**
     * 下载暂停
     *
     * @param downloadTask
     * @param completedSize
     * @param totalSize
     * @param percent
     */
    @Override
    public void onPause(DownloadTask downloadTask, long completedSize, long totalSize, double percent) {

    }

    /**
     * 下载取消
     *
     * @param downloadTask
     */
    @Override
    public void onCancel(DownloadTask downloadTask) {

    }


    /**
     * 下载失败
     *
     * @param downloadTask
     * @param errorCode    {@link DownloadStatus}
     */
    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {

    }


}
