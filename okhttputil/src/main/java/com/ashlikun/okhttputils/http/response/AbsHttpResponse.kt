package com.ashlikun.okhttputils.http.response

import com.ashlikun.gson.GsonHelper
import com.google.gson.JsonParseException
import com.google.gson.annotations.Expose
import okhttp3.Response
import org.json.JSONException
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:34
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：IHttpResponse的基础实现类
 */
abstract class AbsHttpResponse(
    //原始数据
    @Transient
    override var json: String = ""
) : IHttpResponse {


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
    @Transient
    var jsonMap: MutableMap<String, Any?>? = null
        get() {
            if (field != null || json.isNullOrEmpty()) return field
            field = GsonHelper.getGsonNotNull()
                .fromJson(json, MutableMap::class.java) as MutableMap<String, Any?>
            return field
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
    fun <T : Any> getValue(type: Type, vararg key: String): T? {
        if (key.isEmpty()) {
            return null
        }
        val o = getKeyToObject(*key) ?: return null
        if (type.toString().contains("java.lang.Boolean")) return o.toString().toBoolean() as T?
        return when (type) {
            String::class.java -> when (o) {
                is Map<*,*>, is List<*> -> GsonHelper.getGsonNotNull()
                    .toJson(o)
                else -> o.toString()
            }
            Int::class.java -> o.toString().toIntOrNull()
            Long::class.java -> o.toString().toLongOrNull()
            Float::class.java -> o.toString().toFloatOrNull()
            Double::class.java -> o.toString().toDoubleOrNull()
            is Map<*,*>, is List<*> -> o
            else -> {
                //转换成json
                val str: String = GsonHelper.getGsonNotNull().toJson(o)
                GsonHelper.getGsonNotNull().fromJson(str, type)
            }
        } as T?
    }

    /**
     * 获取指定类型数据
     * 根据key获取对象,多个key代表多个等级
     */
    inline fun <reified T> getValue(vararg key: String): T? {
        return getValue(T::class.java, *key)
    }


    fun getIntValue(vararg key: String) = getIntValue(0, *key)
    fun getIntValue(defaultValue: Int, vararg key: String) =
        getValue<Int>(*key) ?: defaultValue


    fun getLongValue(vararg key: String) = getLongValue(0, *key)
    fun getLongValue(defaultValue: Long, vararg key: String) =
        getValue<Long>(*key) ?: defaultValue


    fun getStringValue(vararg key: String) = getStringValueDef("", *key)
    fun getStringValueDef(defaultValue: String, vararg key: String): String =
        getValue<String>(*key) ?: defaultValue


    fun getBooleanValue(vararg key: String) = getBooleanValue(false, *key)
    fun getBooleanValue(defaultValue: Boolean, vararg key: String) =
        getValue<Boolean>(*key) ?: defaultValue


    fun getFloatValue(vararg key: String) = getFloatValue(0f, *key)
    fun getFloatValue(defaultValue: Float, vararg key: String) =
        getValue<Float>(*key) ?: defaultValue


    fun getDoubleValue(vararg key: String) = getDoubleValue(0.0, *key)
    fun getDoubleValue(defaultValue: Double, vararg key: String) =
        getValue<Double>(*key) ?: defaultValue
}