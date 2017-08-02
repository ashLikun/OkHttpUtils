package com.ashLikun.okhttputils.http.download;

/**
 * description the download status
 * <p/>
 */
public class DownloadStatus {
   /**
    * 作者　　: 李坤
    * 创建时间: 2016/12/2 10:33
    *
    * 方法功能：初始化下载
    */

    public static final int DOWNLOAD_STATUS_INIT = 1;
    /**
     * 作者　　: 李坤
     * 创建时间: 2016/12/2 10:33
     *
     * 方法功能：下载中
     */

    public static final int DOWNLOAD_STATUS_DOWNLOADING = 2;
    /**
     * 作者　　: 李坤
     * 创建时间: 2016/12/2 10:33
     *
     * 方法功能：下载取消
     */

    public static final int DOWNLOAD_STATUS_CANCEL = 3;
   /**
    * 作者　　: 李坤
    * 创建时间: 2016/12/2 10:33
    *
    * 方法功能：下载错误
    */

    public static final int DOWNLOAD_STATUS_ERROR = 4;
    /**
     * 作者　　: 李坤
     * 创建时间: 2016/12/2 10:34
     *
     * 方法功能：下载完成
     */

    public static final int DOWNLOAD_STATUS_COMPLETED = 5;
    /**
     * 作者　　: 李坤
     * 创建时间: 2016/12/2 10:34
     *
     * 方法功能：下载暂停
     */

    public static final int DOWNLOAD_STATUS_PAUSE = 6;
    /**
     * 作者　　: 李坤
     * 创建时间: 2016/12/2 10:34
     *
     * 方法功能：下载错误  文件没有发现
     */

    public static final int DOWNLOAD_ERROR_FILE_NOT_FOUND = 7;
    /**
     * 作者　　: 李坤
     * 创建时间: 2016/12/2 10:34
     *
     * 方法功能：文件io错误
     */

    public static final int DOWNLOAD_ERROR_IO_ERROR = 8;

}
