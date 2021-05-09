package com.ashlikun.okhttputils.simple.retrofit

import com.ashlikun.okhttputils.retrofit.Param
import com.ashlikun.okhttputils.retrofit.Path
import com.ashlikun.okhttputils.retrofit.Url
import com.ashlikun.okhttputils.simple.HttpManage

/**
 * 作者　　: 李坤
 * 创建时间: 2021/5/9　20:57
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
@Url(url = HttpManage.BASE_URL, method = "POST")
@Path(HttpManage.BASE_PATH)
@Param(HttpManage.ACTION)
interface BaseApi {
}