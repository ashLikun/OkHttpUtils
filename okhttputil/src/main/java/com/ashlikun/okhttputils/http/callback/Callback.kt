package com.ashlikun.okhttputils.http.callback

import com.ashlikun.okhttputils.http.HttpException
import com.ashlikun.okhttputils.http.cache.CacheEntity
import com.ashlikun.okhttputils.http.convert.Converter
import com.ashlikun.okhttputils.http.download.DownloadManager
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
 * @Link [com.ashlikun.okhttputils.http.response.IHttpResponse] 可以实现这个接口，照样有HttpResponse的功能
 * @Link [com.ashlikun.okhttputils.http.response.HttpResult] 直接序列化的javabean,也可以自定义HttpResult。注意json的键
 * @Link 其他实体类。注意json的键
 */
typealias ProgressCallBack = (progress: Long, total: Long, done: Boolean, isUpdate: Boolean) -> Unit

interface Callback<ResultType> : Converter<ResultType> {
    /**
     * 指定数据类型，不使用Callback的泛型
     */
    val resultType: Type?

    /**
     * 进度回调，默认没有
     */
    val progressCallBack: ProgressCallBack?
        get() = null

    /**
     * 进度回调速度，默认DownloadManager.DEFAULT_RATE
     */
    val progressRate: Long
        get() = DownloadManager.DEFAULT_RATE

    fun onStart()
    fun onCompleted()
    fun onError(e: HttpException)
    fun onSuccess(responseBody: ResultType)
    fun onCacheSuccess(entity: CacheEntity, responseBody: ResultType)

    /**
     * 成功回掉在子线程(当前http执行得线程)
     */
    fun onSuccessSubThread(responseBody: ResultType)

    /**
     * 接口请求成功了，但是处理code
     *
     * @return true:没问题
     * false:有问题
     */
    fun onSuccessHandelCode(responseBody: ResultType): Boolean
}