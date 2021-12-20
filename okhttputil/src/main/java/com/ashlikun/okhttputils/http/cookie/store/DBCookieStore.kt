package com.ashlikun.okhttputils.http.cookie.store

import com.ashlikun.okhttputils.http.cookie.CookieManager
import com.ashlikun.okhttputils.http.cookie.SerializableCookie
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 14:55
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：使用数据库 持久化存储 cookie
 */
open class DBCookieStore : CookieStore {
    /**
     * 数据结构如下
     * Url.host -> cookie1.name@cookie1.domain,cookie2.name@cookie2.domain,cookie3.name@cookie3.domain
     * cookie_cookie1.name@cookie1.domain -> cookie1
     * cookie_cookie2.name@cookie2.domain -> cookie2
     */
    private val cookies: MutableMap<String, ConcurrentHashMap<String, Cookie>> = mutableMapOf()

    init {
        val cookieList = CookieManager.get().queryAll()
        for (serializableCookie in cookieList) {
            if (!cookies.containsKey(serializableCookie.host)) {
                cookies[serializableCookie.host] = ConcurrentHashMap()
            }
            val cookie = serializableCookie.getCookie()
            if (cookie != null) {
                cookies[serializableCookie.host]?.put(getCookieToken(cookie), cookie)
            }
        }
    }

    private fun getCookieToken(cookie: Cookie): String {
        return cookie.name + "@" + cookie.domain
    }

    /**
     * 将url的所有Cookie保存在本地
     */
    @Synchronized
    override fun saveCookie(url: HttpUrl, urlCookies: List<Cookie>) {
        for (cookie in urlCookies) {
            saveCookie(url, cookie)
        }
    }

    @Synchronized
    override fun saveCookie(url: HttpUrl, cookie: Cookie) {
        if (!cookies.containsKey(url.host)) {
            cookies[url.host] = ConcurrentHashMap()
        }
        //当前cookie是否过期
        if (isCookieExpired(cookie)) {
            removeCookie(url, cookie)
        } else {
            //内存缓存
            cookies[url.host]?.put(getCookieToken(cookie), cookie)
            //数据库缓存
            SerializableCookie(url.host, cookie).save()
        }
    }

    /**
     * 根据当前url获取所有需要的cookie,只返回没有过期的cookie
     */
    @Synchronized
    override fun loadCookie(url: HttpUrl): List<Cookie> {
        val ret: MutableList<Cookie> = ArrayList()
        if (!cookies.containsKey(url.host)) {
            return ret
        }
        val query = CookieManager.get().query("host=?", arrayOf(url.host))
        for (serializableCookie in query) {
            val cookie = serializableCookie.getCookie()
            if (cookie != null) {
                if (isCookieExpired(cookie)) {
                    removeCookie(url, cookie)
                } else {
                    ret.add(cookie)
                }
            }
        }
        return ret
    }

    /**
     * 根据url移除当前的cookie
     */
    @Synchronized
    override fun removeCookie(url: HttpUrl, cookie: Cookie): Boolean {
        if (!cookies.containsKey(url.host)) {
            return false
        }
        val cookieToken = getCookieToken(cookie)
        if (cookies[url.host]?.containsKey(cookieToken) != true) {
            return false
        }

        //内存移除
        cookies[url.host]?.remove(cookieToken)
        //数据库移除
        val whereClause = "host=? and name=? and domain=?"
        val whereArgs = arrayOf(url.host, cookie.name, cookie.domain)
        CookieManager.get().delete(whereClause, whereArgs)
        return true
    }

    @Synchronized
    override fun removeCookie(url: HttpUrl): Boolean {
        if (!cookies.containsKey(url.host)) {
            return false
        }

        //内存移除
        cookies.remove(url.host)
        //数据库移除
        val whereClause = "host=?"
        val whereArgs = arrayOf(url.host)
        CookieManager.get().delete(whereClause, whereArgs)
        return true
    }

    @Synchronized
    override fun removeAllCookie(): Boolean {
        //内存移除
        cookies.clear()
        //数据库移除
        CookieManager.get().deleteAll()
        return true
    }

    /**
     * 获取所有的cookie
     */
    @get:Synchronized
    override val allCookie: List<Cookie>
        get() {
            val ret: MutableList<Cookie> = ArrayList()
            for (key in cookies.keys) {
                cookies[key]?.values?.let {
                    ret.addAll(it)
                }
            }
            return ret
        }

    @Synchronized
    override fun getCookie(url: HttpUrl): List<Cookie> {
        val ret: MutableList<Cookie> = ArrayList()
        val mapCookie: Map<String, Cookie>? = cookies[url.host]
        if (mapCookie != null) {
            ret.addAll(mapCookie.values)
        }
        return ret
    }

    companion object {
        /**
         * 当前cookie是否过期
         */
        private fun isCookieExpired(cookie: Cookie): Boolean {
            return cookie.expiresAt < System.currentTimeMillis()
        }
    }


}