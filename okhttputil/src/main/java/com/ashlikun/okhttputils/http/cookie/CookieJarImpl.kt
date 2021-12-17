package com.ashlikun.okhttputils.http.cookie

import com.ashlikun.okhttputils.http.cookie.store.CookieStore
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 14:57
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：CookieJar的实现类，默认管理了用户自己维护的cookie
 */
class CookieJarImpl(var cookieStore: CookieStore) : CookieJar {

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.saveCookie(url, cookies)
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore.loadCookie(url)
    }
}