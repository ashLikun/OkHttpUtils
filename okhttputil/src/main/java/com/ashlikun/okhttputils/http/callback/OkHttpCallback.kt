package com.ashlikun.okhttputils.http.callback

import com.ashlikun.okhttputils.http.ExecuteCall
import com.ashlikun.okhttputils.http.HttpException
import com.ashlikun.okhttputils.http.HttpUtils
import com.ashlikun.okhttputils.http.OkHttpUtils
import com.ashlikun.okhttputils.http.cache.CacheEntity
import com.ashlikun.okhttputils.http.cache.CacheMode
import com.ashlikun.okhttputils.http.cache.CachePolicy
import com.ashlikun.okhttputils.http.response.HttpErrorCode.HTTP_DATA_ERROR
import com.ashlikun.okhttputils.http.response.HttpErrorCode.HTTP_UNKNOWN
import com.ashlikun.okhttputils.http.response.HttpErrorCode.MSG_DATA_ERROR
import com.ashlikun.okhttputils.http.response.HttpErrorCode.MSG_UNKNOWN
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 14:21
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：okhttp的直接回调
 */

open class OkHttpCallback<ResultType>(var exc: ExecuteCall, var callback: Callback<ResultType>) :
    okhttp3.Callback {
    var gson: Gson = OkHttpUtils.get().parseGson
    var cachePolicy: CachePolicy? = null
        set(value) {
            field = value
            //回掉缓存
            if (field?.cacheMode === CacheMode.FIRST_CACHE_THEN_REQUEST) {
                GlobalScope.launch {
                    field?.callback(exc.call, callback)
                }
            }
        }

    init {
        callback.onStart()
    }

    fun setParseGson(gson: Gson) {
        this.gson = gson
    }

    fun checkCanceled(response: Response? = null): Boolean {
        if (exc.isCanceled) {
            exc.isCompleted = true
            if (!HttpUtils.isMainThread) {
                HttpUtils.launchMain { callback.onCompleted() }
            } else {
                callback.onCompleted()
            }
            response?.close()
            return true
        }
        return false
    }


    /**
     * Okhttp失败
     */
    override fun onFailure(call: Call, e: IOException) {
        if (checkCanceled()) return
        //失败，回掉缓存
        if (cachePolicy?.cacheMode === CacheMode.REQUEST_FAILED_READ_CACHE) {
            if (checkCanceled()) return
            GlobalScope.launch {
                cachePolicy?.callback(call, callback)
            }
            return
        }
        postFailure(HttpException.handleFailureHttpException(call, e))
    }

    /**
     * 本框架封装失败
     */
    private fun postFailure(throwable: HttpException) {
        if (exc.isCanceled) return
        GlobalScope.async(Dispatchers.Main + CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
        }) {
            if (checkCanceled()) return@async
            callback.onError(throwable)
            exc.isCompleted = true
            callback.onCompleted()
        }
    }


    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        GlobalScope.launch(CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
            postFailure(HttpException(HTTP_DATA_ERROR, MSG_DATA_ERROR, t))
            response.close()
        }) {
            if (checkCanceled(response)) return@launch
            //错误请求
            if (!response.isSuccessful) {
                postFailure(
                    HttpException.getOnResponseHttpException(response.code, response.message)
                )
                response.close()
                return@launch
            }
            val resultType: ResultType? = callback.convertResponse(response, gson)
            //缓存
            if (cachePolicy?.cacheMode != null && cachePolicy?.cacheMode != CacheMode.NO_CACHE) {
                cachePolicy?.save(response, CacheEntity.getHanderResult(resultType))
            }
            if (resultType == null) {
                postFailure(
                    HttpException(HTTP_DATA_ERROR, MSG_DATA_ERROR)
                )
                response.close()
                return@launch
            }
            postResponse(response, resultType)
        }
    }

    private suspend fun postResponse(response: Response, resultType: ResultType) {
        if (checkCanceled(response)) return
        callback.onSuccessSubThread(resultType)
        GlobalScope.async(Dispatchers.Main + CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
            postFailure(HttpException(HTTP_UNKNOWN, MSG_UNKNOWN, t))
        }) {
            if (checkCanceled(response)) return@async
            if (callback.onSuccessHandelCode(resultType)) {
                if (checkCanceled(response)) return@async
                callback.onSuccess(resultType)
            }
            exc.isCompleted = true
            callback.onCompleted()
            response.close()
        }.await()
    }
}