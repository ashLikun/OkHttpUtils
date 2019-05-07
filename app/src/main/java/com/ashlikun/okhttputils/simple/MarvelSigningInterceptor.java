package com.ashlikun.okhttputils.simple;

import com.ashlikun.okhttputils.http.HttpUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/5/7　16:12
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class MarvelSigningInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = null;
        chain.proceed(chain.request());
        try {
            Thread.sleep(5000);
            request = HttpUtils.setRequestParams(chain.request(), "accessToken", "aaaaaaaaaaaaaaaa");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Response response = chain.proceed(request.build());
        return response;
    }
}
