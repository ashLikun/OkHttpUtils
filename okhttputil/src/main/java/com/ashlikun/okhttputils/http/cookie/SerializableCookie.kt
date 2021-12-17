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
 * 创建时间: 2021.12.17 16:42
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：cookie实体存储
 */
@Table("HttpCookie")
class SerializableCookie @JvmOverloads constructor(host: String = "", cookie: Cookie? = null) :
    Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    var id: String = ""
    var cookieString: String = ""
    var host: String = ""
    var name: String = ""
    var domain: String = ""

    @Ignore
    @Transient
    var cookie: Cookie


    init {
        if (cookie != null) {
            this.cookie = cookie
            this.host = host
            name = cookie.name
            domain = cookie.domain
            if (cookie != null) {
                cookieString = encodeCookie(host, cookie)
            }
        } else {
            this.cookie = decodeCookie(cookieString)
        }
        id = "$name@$domain"
    }


    fun save() {
        LiteOrmUtil.get().save(this)
    }


    /**
     * host, name, domain 标识一个cookie是否唯一
     */
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val that = o as SerializableCookie?
        if (host != that?.host) return false
        if (name != that?.name) return false
        return domain == that?.domain
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + domain.hashCode()
        return result
    }

    companion object {
        private const val serialVersionUID = 6374381323722046732L
        const val HOST = "host"
        const val NAME = "name"
        const val DOMAIN = "domain"
        const val COOKIE = "cookie"
        fun parseCursorToBean(cursor: Cursor): SerializableCookie? {
            val host = cursor.getString(cursor.getColumnIndex(HOST))
            val cookieBytes = cursor.getBlob(cursor.getColumnIndex(COOKIE))
            val cookie = bytesToCookie(cookieBytes) ?: return null
            return SerializableCookie(host, cookie)
        }

        fun getContentValues(serializableCookie: SerializableCookie): ContentValues {
            val values = ContentValues()
            values.put(HOST, serializableCookie.host)
            values.put(NAME, serializableCookie.name)
            values.put(DOMAIN, serializableCookie.domain)
            values.put(
                COOKIE, cookieToBytes(serializableCookie.host, serializableCookie.cookie)
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

        fun cookieToBytes(host: String, cookie: Cookie): ByteArray? {
            val serializableCookie = SerializableCookie(host, cookie)
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
        fun decodeCookie(cookieString: String): Cookie {
            return bytesToCookie(hexStringToByteArray(cookieString))
        }

        fun bytesToCookie(bytes: ByteArray): Cookie {
            val byteArrayInputStream = ByteArrayInputStream(bytes)
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            return (objectInputStream.readObject() as SerializableCookie).cookie
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
                data[i / 2] = ((Character.digit(
                    hexString[i],
                    16
                ) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }
    }
}