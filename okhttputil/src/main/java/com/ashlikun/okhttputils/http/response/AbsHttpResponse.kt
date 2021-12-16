package com.ashlikun.okhttputils.http.response

import com.ashlikun.okhttputils.http.response.IHttpResponse
import org.json.JSONObject
import kotlin.Throws
import org.json.JSONException
import org.json.JSONArray
import android.text.TextUtils
import com.google.gson.JsonParseException
import com.ashlikun.gson.GsonHelper
import com.ashlikun.okhttputils.http.HttpUtils
import okhttp3.Response
import java.lang.Exception
import java.lang.reflect.Type

/**
 * 作者　　: 李坤
 * 创建时间: 2021/6/6　14:24
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：IHttpResponse的基础实现类
 */
internal abstract class AbsHttpResponse : IHttpResponse {
    /**
     * 原始数据
     */
    @Transient
    override var json: String

    /**
     * 缓存已经实例化的JSONObject,JSONArray对象
     */
    @Transient
    private var cache: Any? = null

    /**
     * gson不解析,当是缓存的时候是空的
     */
    @Transient
    var response: Response? = null

    /**
     * 获取头部code
     */
    @Transient
    override var httpCode = 0

    constructor() {}
    constructor(json: String?) {
        this.json = json
    }

    /**
     * 当Gson自动解析异常的时候会调用，由内部调用
     *
     * @param json
     */
    open fun setOnGsonErrorData(json: String?) {
        this.json = json
    }

    override fun setResponse(response: Response) {
        this.response = response
    }

    /**
     * 获取根部json对象
     * 说明后续这个结果都是json对象
     *
     * @return
     * @throws JSONException
     */
    val jSONObject: JSONObject?
        get() {
            try {
                if (cache == null) {
                    if (json == null) {
                        json = ""
                    }
                    val jObj = JSONObject(json)
                    json = null
                    cache = jObj
                }
            } catch (e: Exception) {
                return null
            }
            return cache as JSONObject?
        }

    /**
     * 获取根部json数组
     * 说明后续这个结果都是json数组
     *
     * @return
     * @throws JSONException
     */
    @get:Throws(JSONException::class)
    val jSONArray: JSONArray?
        get() {
            if (cache == null) {
                if (json == null) {
                    json = ""
                }
                val jsonArray = JSONArray(json)
                json = null
                cache = jsonArray
            }
            return cache as JSONArray?
        }

    fun getKeyToObject(vararg key: String?): Any? {
        var res: Any? = jSONObject ?: return null
        if (key != null) {
            for (i in 0 until key.size) {
                res = getCacheJSON(key[i], res)
                if (res == null) {
                    return null
                }
            }
        }
        return res
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/5/25 11:27
     *
     *
     * 方法功能：获取指定key的值
     */
    private fun getCacheJSON(key: String?, content: Any?): Any? {
        if (TextUtils.isEmpty(key)) {
            return null
        }
        if (content == null) {
            return null
        }
        if (content is JSONObject) {
            return content.opt(key)
        } else if (content is JSONArray) {
            val aaa = content.opt(0)
            return aaa?.let { getCacheJSON(key, it) } ?: aaa
        }
        return cache
    }

    /**
     * 方法功能：根据key获取对象,多个key代表多个等级,不能获取数组
     */
    @Throws(JsonParseException::class, JSONException::class)
    fun <T> getValue(type: Type?, vararg key: String?): T? {
        if (key == null || key.size == 0) {
            return null
        }
        val o = getKeyToObject(*key) ?: return null
        return GsonHelper.getGsonNotNull().fromJson(o.toString(), type)
    }

    /**
     * 基本类型的获取
     * 方法功能：根据key获取对象,多个key代表多个等级,只能获取基础数据类型
     */
    fun <T> getValueBase(vararg key: String?): T? {
        return if (key == null || key.size == 0) {
            null
        } else getKeyToObject(*key) as T?
    }

    fun getIntValue(vararg key: String?): Int {
        return getIntValue(0, *key)
    }

    fun getIntValue(defaultValue: Int, vararg key: String?): Int {
        val result: Int = HttpUtils.toInteger(getValueBase<T>(*key))
        return result ?: defaultValue
    }

    fun getLongValue(vararg key: String?): Long {
        return getLongValue(0, *key)
    }

    fun getLongValue(defaultValue: Long, vararg key: String?): Long {
        val res: Long = HttpUtils.toLong(getValueBase<T>(*key))
        return res ?: defaultValue
    }

    fun getStringValue(vararg key: String?): String {
        val res = toString(getValueBase<Any>(*key))
        return res ?: ""
    }

    fun getStringValueDefault(defaultValue: String, vararg key: String?): String {
        val res = toString(getValueBase<Any>(*key))
        return res ?: defaultValue
    }

    fun getBooleanValue(vararg key: String?): Boolean {
        return getBooleanValue(false, *key)
    }

    fun getBooleanValue(defaultValue: Boolean, vararg key: String?): Boolean {
        val res: Boolean = HttpUtils.toBoolean(getValueBase<T>(*key))
        return res ?: defaultValue
    }

    fun getFloatValue(vararg key: String?): Float {
        return getFloatValue(0f, *key)
    }

    fun getFloatValue(defaultValue: Float, vararg key: String?): Float {
        val res: Float = HttpUtils.toFloat(getValueBase<T>(*key))
        return res ?: defaultValue
    }

    fun getDoubleValue(vararg key: String?): Double {
        return getDoubleValue(0.0, *key)
    }

    fun getDoubleValue(defaultValue: Double, vararg key: String?): Double {
        val res: Double = HttpUtils.toDouble(getValueBase<T>(*key))
        return res ?: defaultValue
    }
}