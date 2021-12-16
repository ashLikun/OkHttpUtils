package com.ashlikun.okhttputils.http.convert

import com.google.gson.Gson
import okhttp3.Response

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 14:13
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：网络数据的转换接口
 */

interface Converter<T> {
    /**
     * 拿到响应后，将数据转换成需要的格式，子线程中执行，可以是耗时操作
     *
     * @param response 需要转换的对象
     * @param gosn     gson解析
     * @return 转换后的结果
     * @throws Exception 转换过程发生的异常
     */
    @Throws(Exception::class)
    fun convertResponse(response: Response, gosn: Gson): T
}