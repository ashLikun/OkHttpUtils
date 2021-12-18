package com.ashlikun.okhttputils.simple.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 作者　　: 李坤
 * 创建时间: 2019/5/7　16:12
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
class MarvelSigningInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

//        Request.Builder request = null;
//        chain.proceed(chain.request());
//        try {
//            Thread.sleep(5000);
//            request = HttpUtils.setRequestParams(chain.request(), "accessToken", "aaaaaaaaaaaaaaaa");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Response response = chain.proceed(request.build());
        return chain.proceed(chain.request())
    }
}