package com.ashlikun.okhttputils.simple.retrofit

import com.ashlikun.okhttputils.retrofit.*
import com.ashlikun.okhttputils.simple.HttpManage

/**
 * 作者　　: 李坤
 * 创建时间: 2021/5/9　20:57
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：Api接口的基础类
 */
@Url(url = HttpManage.BASE_URL, method = "POST")
@Path(HttpManage.BASE_PATH)
@Action(HttpManage.ACTION)
@Headers(value = ["commonHeader1:111", "commonHeader2:222"])
@Params(value = ["commonParams1:111", "commonParams2:222"])
interface BaseApi {
}