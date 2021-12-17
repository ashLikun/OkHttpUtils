package com.ashlikun.okhttputils.http

import okhttp3.Call

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:30
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：异步请求后返回值，用于控制请求，如果请求完成 那么call自动清空
 */
class ExecuteCall(var call: Call? = null) {

    //是否完成
    var isCompleted = false
        set(completed) {
            if (completed) {
                call = null
            }
            field = completed
        }

    fun cancel() {
        if (call?.isCanceled() == false) {
            call?.cancel()
        }
    }

    val isCanceled: Boolean
        get() = call?.isCanceled() ?: true
}