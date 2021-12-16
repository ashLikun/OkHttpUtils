package com.ashlikun.okhttputils.http.callback

import com.ashlikun.okhttputils.http.callback.ProgressCallBack
import com.ashlikun.okhttputils.http.download.DownloadManager

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 4:31
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
open class AbsProgressCallback : ProgressCallBack {
    override fun onLoading(progress: Long, total: Long, done: Boolean, isUpdate: Boolean) {}
    override val rate: Long
        get() = DownloadManager.DEFAULT_RATE
}