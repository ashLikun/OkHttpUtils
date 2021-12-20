/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ashlikun.okhttputils.http.cookie

import android.content.ContentValues
import android.database.Cursor
import com.ashlikun.orm.LiteOrmUtil
import com.ashlikun.orm.db.annotation.Ignore
import com.ashlikun.orm.db.annotation.PrimaryKey
import com.ashlikun.orm.db.annotation.Table
import com.ashlikun.orm.db.enums.AssignType
import okhttp3.Cookie
import okhttp3.internal.and
import java.io.*
import java.util.*

/**
 * @author　　: 李坤
 * 创建时间: 2019/2/27 11:26
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：cookie实体存储
 */
@Table("HttpCookie")
open class SerializableCookie : Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    var id: String = ""
    var cookieString: String = ""
    var host: String = ""
    var name: String = ""
    var domain: String = ""

    @Ignore
    @Transient
    var cookieCache: Cookie? = null

    @Ignore
    @Transient
    var clientCookie: Cookie? = null

    constructor() {}
    constructor(host: String, cookie: Cookie) {
        this.cookieCache = cookie
        this.host = host
        name = cookie.name
        domain = cookie.domain
        cookieString = encodeCookie(host, cookie)
        id = cookieToken
    }

    /**
     * 这里有概率不能序列化成功
     */
    fun getCookie(): Cookie? {
        if (cookieCache == null) {
            cookieCache = decodeCookie(cookieString)
        }
        var bestCookie = cookieCache
        if (clientCookie != null) {
            bestCookie = clientCookie
        }
        return bestCookie
    }

    private val cookieToken: String
        private get() = "$name@$domain"

    fun save() {
        LiteOrmUtil.get().save(this)
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        cookieCache?.run {
            out.defaultWriteObject()
            out.writeObject(name)
            out.writeObject(value)
            out.writeLong(expiresAt)
            out.writeObject(domain)
            out.writeObject(path)
            out.writeBoolean(secure)
            out.writeBoolean(httpOnly)
            out.writeBoolean(hostOnly)
            out.writeBoolean(persistent)
        }
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(ois: ObjectInputStream) {
        ois.defaultReadObject()
        val name = ois.readObject() as String? ?: ""
        val value = ois.readObject() as String? ?: ""
        val expiresAt = ois.readLong()
        val domain = ois.readObject() as String? ?: ""
        val path = ois.readObject() as String? ?: ""
        val secure = ois.readBoolean()
        val httpOnly = ois.readBoolean()
        val hostOnly = ois.readBoolean()
        val persistent = ois.readBoolean()
        clientCookie = Cookie.Builder().let {
            it.name(name).value(value).expiresAt(expiresAt)
            if (hostOnly) it.hostOnlyDomain(domain) else it.domain(domain)
            it.path(path)
            if (secure) it.secure()
            if (httpOnly) it.httpOnly()
            it
        }.build()
    }

    /**
     * host, name, domain 标识一个cookie是否唯一
     */
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as SerializableCookie
        if (host != that.host) return false
        if (name != that.name) return false
        return domain == that.domain
    }

    override fun hashCode(): Int {
        var result = if (host != null) host.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (domain != null) domain.hashCode() else 0
        return result
    }

    companion object {
        private const val serialVersionUID = 6374381323722046732L
        const val HOST = "host"
        const val NAME = "name"
        const val DOMAIN = "domain"
        const val COOKIE = "cookie"
        fun parseCursorToBean(cursor: Cursor): SerializableCookie {
            val host = cursor.getString(cursor.getColumnIndex(HOST))
            val cookieBytes = cursor.getBlob(cursor.getColumnIndex(COOKIE))
            val cookie = bytesToCookie(cookieBytes)
            return SerializableCookie(host, cookie!!)
        }

        fun getContentValues(serializableCookie: SerializableCookie): ContentValues {
            val values = ContentValues()
            values.put(HOST, serializableCookie.host)
            values.put(NAME, serializableCookie.name)
            values.put(DOMAIN, serializableCookie.domain)
            values.put(
                COOKIE,
                cookieToBytes(serializableCookie.host, serializableCookie.getCookie())
            )
            return values
        }

        /**
         * cookies 序列化成 string
         *
         * @param cookie 要序列化
         * @return 序列化之后的string
         */
        fun encodeCookie(host: String, cookie: Cookie): String {
            val cookieBytes = cookieToBytes(host, cookie)
            return byteArrayToHexString(cookieBytes)
        }

        fun cookieToBytes(host: String, cookie: Cookie?): ByteArray? {
            val serializableCookie = SerializableCookie()
            serializableCookie.host = host
            serializableCookie.cookieCache = cookie
            val os = ByteArrayOutputStream()
            try {
                val outputStream = ObjectOutputStream(os)
                outputStream.writeObject(serializableCookie)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            return os.toByteArray()
        }

        /**
         * 将字符串反序列化成cookies
         *
         * @param cookieString cookies string
         * @return cookie object
         */
        fun decodeCookie(cookieString: String?): Cookie? {
            if (cookieString == null || cookieString.isEmpty()) {
                return null
            }
            val bytes = hexStringToByteArray(cookieString)
            return bytesToCookie(bytes)
        }

        fun bytesToCookie(bytes: ByteArray?): Cookie? {
            val byteArrayInputStream = ByteArrayInputStream(bytes)
            return try {
                val objectInputStream = ObjectInputStream(byteArrayInputStream)
                (objectInputStream.readObject() as SerializableCookie).getCookie()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 二进制数组转十六进制字符串
         *
         * @param bytes byte array to be converted
         * @return string containing hex values
         */
        private fun byteArrayToHexString(bytes: ByteArray?): String {
            val sb = StringBuilder(bytes!!.size * 2)
            for (element in bytes) {
                val v: Int = element and 0xff
                if (v < 16) {
                    sb.append('0')
                }
                sb.append(Integer.toHexString(v))
            }
            return sb.toString().uppercase(Locale.US)
        }

        /**
         * 十六进制字符串转二进制数组
         *
         * @param hexString string of hex-encoded values
         * @return decoded byte array
         */
        private fun hexStringToByteArray(hexString: String): ByteArray {
            val len = hexString.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(
                    hexString[i + 1], 16
                )).toByte()
                i += 2
            }
            return data
        }
    }
}