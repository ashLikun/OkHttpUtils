package com.ashlikun.okhttputils.simple

import android.app.Application
import com.ashlikun.okhttputils.http.OkHttpUtils
import com.ashlikun.okhttputils.http.cookie.CookieJarImpl
import com.ashlikun.okhttputils.http.cookie.store.DBCookieStore
import com.ashlikun.okhttputils.http.download.DownloadManager
import com.ashlikun.okhttputils.http.request.HttpRequest
import com.ashlikun.okhttputils.retrofit.Retrofit
import com.ashlikun.okhttputils.simple.interceptor.MarvelSigningInterceptor
import com.ashlikun.orm.LiteOrmUtil
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 3:13
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        LiteOrmUtil.init(this)
        LiteOrmUtil.setVersionCode(11)
        val params: MutableMap<String, Any> = HashMap()
        params["appver"] = "2.6.0"
        params["devicetype"] = "android"
        params["brand"] = "Xiaomi"
        params["deviceMode"] = "MI 5"
        params["sessionid"] = "5BY2XKL8CLDUSXXS7IF2VX55D8O5HHED"
        params["pid"] = "427"
        params["uid"] = "120842840"
        params["Apptag"] = "j725152534447786860o1AKfZFfCT4NWa39"
        params["timestamp"] = System.currentTimeMillis()
        val head: MutableMap<String, String> = HashMap()
        head["appver"] = "2.6.0"
        head["devicetype"] = "android"
        val mOkHttpClient: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(100000, TimeUnit.MILLISECONDS)
            .writeTimeout(100000, TimeUnit.MILLISECONDS)
            .connectTimeout(100000, TimeUnit.MILLISECONDS)
            .cookieJar(CookieJarImpl(DBCookieStore()))
            .addInterceptor(MarvelSigningInterceptor())
            .build()
        OkHttpUtils.init(this, mOkHttpClient)
        OkHttpUtils.get().commonHeaders = head
        OkHttpUtils.get().commonParams = params
        DownloadManager.initPath(this.cacheDir.path)
        Retrofit.get().init(
            createRequest = { MyHttpRequest(it.url) },
            execute = { request, result, params -> request.syncExecute<Any>(result.resultType) }
        )
    }
}

suspend fun <T> testSuspend(): T {
    return withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            continuation.resumeWithException(throw RuntimeException("ddddddd"))
        }
    }
}

class MyHttpRequest(url: String) : HttpRequest(url) {
    override fun onBuildRequestBodyHasCommonParams() {
        super.onBuildRequestBodyHasCommonParams()
        toJson()
    }

}