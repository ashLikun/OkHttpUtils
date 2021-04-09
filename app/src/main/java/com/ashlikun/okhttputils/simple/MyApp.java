package com.ashlikun.okhttputils.simple;

import android.app.Application;

import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.cookie.CookieJarImpl;
import com.ashlikun.okhttputils.http.cookie.store.DBCookieStore;
import com.ashlikun.orm.LiteOrmUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 3:13
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        LiteOrmUtil.init(new LiteOrmUtil.OnNeedListener() {
//            @Override
//            public Application getApplication() {
//                return MyApp.this;
//            }
//
//            @Override
//            public boolean isDebug() {
//                return true;
//            }
//
//            @Override
//            public int getVersionCode() {
//                return 2;
//            }
//        });

        LiteOrmUtil.init(this);
        LiteOrmUtil.setVersionCode(11);
        Map<String, Object> params = new HashMap<>();
        params.put("appver", "2.6.0");
        params.put("devicetype", "android");
        params.put("brand", "Xiaomi");
        params.put("deviceMode", "MI 5");
        params.put("sessionid", "5BY2XKL8CLDUSXXS7IF2VX55D8O5HHED");
        params.put("pid", "427");
        params.put("uid", "120842840");
        params.put("Apptag", "j725152534447786860o1AKfZFfCT4NWa39");
        params.put("timestamp", System.currentTimeMillis());
        Map<String, String> head = new HashMap<>();
        head.put("appver", "2.6.0");
        head.put("devicetype", "android");
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(100000, TimeUnit.MILLISECONDS)
                .writeTimeout(100000, TimeUnit.MILLISECONDS)
                .connectTimeout(100000, TimeUnit.MILLISECONDS)
                .cookieJar(new CookieJarImpl(new DBCookieStore()))
                .addInterceptor(new MarvelSigningInterceptor())
                .build();
        OkHttpUtils.init(mOkHttpClient);
        OkHttpUtils.getInstance()
                .setCommonParams(params)
                .setCommonHeaders(head);
        Init.INSTANCE.init();
    }
}
