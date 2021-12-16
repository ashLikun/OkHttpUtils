package com.ashlikun.okhttputils.http.callback

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 14:10
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：进度回调
 */

interface ProgressCallBack {
    /**
     * @param progress 已经下载或上传字节数
     * @param total    总字节数
     * @param done     是否完成
     * @param isUpdate 是否是上传
     */
    fun onLoading(progress: Long, total: Long, done: Boolean, isUpdate: Boolean)

    /**
     * 速度
     */
    val rate: Long
}