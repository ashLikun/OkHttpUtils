package com.ashlikun.okhttputils.http.callback

import com.ashlikun.okhttputils.http.HttpUtils
import com.google.gson.Gson
import okhttp3.Response

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 14:09
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：字符串回调
 */
abstract class StringCallback : AbsCallback<String>() {
    @Throws(Exception::class)
    override fun convertResponse(response: Response, gosn: Gson): String {
        return HttpUtils.handerResult(String::class.java, response, gosn)
    }
}