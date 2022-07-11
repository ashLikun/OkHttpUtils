package com.ashlikun.okhttputils.simple.data

import com.ashlikun.okhttputils.http.response.HttpResult
import com.google.gson.annotations.SerializedName

/**
 * 作者　　: 李坤
 * 创建时间: 2022/7/11　20:51
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
open class HttpPageResultX<T> : HttpResult<HttpPageResult<T>>() {

    val dataList: MutableList<T>
        get() = super.data?.data?.toMutableList() ?: mutableListOf()
}

open class HttpPageResult<T> {
    /**
     * 当前是服务器数据的第几页
     */
    var current = 1

    /**
     * 一共多少页
     */
    var pages = -1

    /**
     * 总数
     */
    var total = -1

    /**
     * 具体数据
     */
    @SerializedName("records")
    var data: List<T>? = null
}
