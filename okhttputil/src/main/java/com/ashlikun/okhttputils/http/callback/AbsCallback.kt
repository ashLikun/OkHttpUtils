package com.ashlikun.okhttputils.http.callback

import kotlin.Throws
import com.google.gson.Gson
import com.ashlikun.okhttputils.http.HttpException
import com.ashlikun.okhttputils.http.HttpUtils
import com.ashlikun.okhttputils.http.cache.CacheEntity
import okhttp3.Response
import java.lang.Exception
import java.lang.reflect.Type

/**
 * 作者　　: 李坤
 * 创建时间:2016/12/30　17:31
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：http返回值的回调接口
 * 泛型可以是
 *
 * @Link [String]
 * @Link [okhttp3.Response]//对这个操作要在子线程
 * @Link [okhttp3.ResponseBody]
 * @Link [com.ashlikun.okhttputils.http.response.HttpResponse] 只解析了code和msg
 * @Link [com.ashlikun.okhttputils.http.response.HttpResult] 直接序列化的javabean,也可以自定义HttpResult。注意json的键
 * @Link 其他实体类。注意json的键
 */
abstract class AbsCallback<ResultType> : Callback<ResultType> {
    /**
     * 指定数据类型，不使用Callback的泛型
     */
    override var resultType: Type? = null

    @Throws(Exception::class)
    override fun convertResponse(response: Response, gosn: Gson): ResultType {
        return if (resultType != null) HttpUtils.handerResult(resultType!!, response, gosn)
        else HttpUtils.handerResult(HttpUtils.getType(javaClass), response, gosn)
    }

    override fun onStart() {}
    override fun onCompleted() {}
    override fun onError(e: HttpException) {}
    override fun onSuccess(responseBody: ResultType) {}
    override fun onCacheSuccess(entity: CacheEntity, responseBody: ResultType) {}
    override fun onSuccessSubThread(responseBody: ResultType) {}
    override fun onSuccessHandelCode(responseBody: ResultType): Boolean {
        return true
    }
}