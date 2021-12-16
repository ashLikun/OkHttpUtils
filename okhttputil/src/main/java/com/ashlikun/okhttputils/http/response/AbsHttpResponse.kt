package com.ashlikun.okhttputils.http.response

import com.ashlikun.gson.GsonHelper
import com.google.gson.JsonParseException
import okhttp3.Response
import org.json.JSONException
import java.lang.reflect.Type

/**
 * 作者　　: 李坤
 * 创建时间: 2021/6/6　14:24
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：IHttpResponse的基础实现类
 */
abstract class AbsHttpResponse : IHttpResponse {
    //原始数据
    @Transient
    override var json: String = ""

    /**
     * 获取头部code
     */
    @Transient
    override var httpCode = 0

    /**
     * gson不解析,当是缓存的时候是空的
     */
    @Transient
    override var response: Response? = null

    /**
     * 当Gson自动解析异常的时候会调用，由内部调用
     */
    open fun setOnGsonErrorData(json: String) {
        this.json = json
    }

    /**
     * 获取根部json对象
     * 说明后续这个结果都是json对象
     */
    val jsonMap: MutableMap<String, Any?>? by lazy {
        try {
            GsonHelper.getGsonNotNull()
                .fromJson(json, MutableMap::class.java) as MutableMap<String, Any?>
        } catch (e: Exception) {
            null
        }
    }


    fun getKeyToObject(vararg key: String): Any? {
        var res: Any? = jsonMap ?: return null
        for (k in key) {
            res = getCacheJSON(k, res!!)
            if (res == null) {
                return null
            }
        }
        return res
    }

    /**
     * 获取指定key的值
     * @param content  JSONObject 或者 JSONArray
     */
    private fun getCacheJSON(key: String, content: Any): Any? {
        if (key.isEmpty()) {
            return null
        }
        if (content is Map<*, *>) {
            return content[key]
        } else if (content is List<*>) {
            val aaa = content.getOrNull(0)
            return aaa?.let { getCacheJSON(key, it) } ?: aaa
        }
        return jsonMap
    }

    /**
     * 根据key获取对象,多个key代表多个等级,不能获取数组
     */
    @Throws(JsonParseException::class, JSONException::class)
    fun <T> getValue(type: Type, vararg key: String): T? {
        if (key.isEmpty()) {
            return null
        }
        val o = getKeyToObject(*key) ?: return null
        //转换成json
        val str: String = GsonHelper.getGsonNotNull().toJson(o)
        return GsonHelper.getGsonNotNull().fromJson(str, type)
    }

    /**
     * 基本类型的获取
     * 方法功能：根据key获取对象,多个key代表多个等级,只能获取基础数据类型
     */
    fun <T> getValueBase(vararg key: String): T? {
        return if (key.isEmpty()) null else getKeyToObject(*key) as T?
    }


    fun getIntValue(vararg key: String) = getIntValue(0, *key)
    fun getIntValue(defaultValue: Int, vararg key: String) =
        getValueBase<Any>(*key)?.toString()?.toIntOrNull() ?: defaultValue


    fun getLongValue(vararg key: String) = getLongValue(0, *key)
    fun getLongValue(defaultValue: Long, vararg key: String) =
        getValueBase<Any>(*key)?.toString()?.toLongOrNull() ?: defaultValue


    fun getStringValue(vararg key: String) = getStringValueDef("", *key)
    fun getStringValueDef(defaultValue: String, vararg key: String) =
        getValueBase<Any>(*key)?.toString() ?: defaultValue


    fun getBooleanValue(vararg key: String) = getBooleanValue(false, *key)
    fun getBooleanValue(defaultValue: Boolean, vararg key: String) =
        getValueBase<Any>(*key)?.toString()?.toBoolean() ?: defaultValue


    fun getFloatValue(vararg key: String) = getFloatValue(0f, *key)
    fun getFloatValue(defaultValue: Float, vararg key: String) =
        getValueBase<Any>(*key)?.toString()?.toFloatOrNull() ?: defaultValue


    fun getDoubleValue(vararg key: String) = getDoubleValue(0.0, *key)
    fun getDoubleValue(defaultValue: Double, vararg key: String) =
        getValueBase<Any>(*key)?.toString()?.toDoubleOrNull() ?: defaultValue
}