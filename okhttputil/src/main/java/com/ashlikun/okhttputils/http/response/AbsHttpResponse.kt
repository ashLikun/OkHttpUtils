package com.ashlikun.okhttputils.http.response

import com.ashlikun.gson.GsonHelper
import com.ashlikun.gson.JsonHelper
import com.ashlikun.okhttputils.http.JSONHelp
import com.google.gson.reflect.TypeToken
import okhttp3.Response
import java.lang.reflect.Type

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:34
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：IHttpResponse的基础实现类
 */
abstract class AbsHttpResponse(
    json: String = ""
) : IHttpResponse {
    //原始数据
    @Transient
    override var json: String = ""
        set(value) {
            field == value
            jsonHelper = JSONHelp(value)
        }

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
    var jsonHelper = JSONHelp(json)

    init {
        this.json = json
    }

    fun getKeyToObject(vararg key: String) = jsonHelper.getKeyToObject(*key)

    /**
     * 获取指定key的值
     * @param content  JSONObject 或者 JSONArray
     */
    private fun getCacheJSON(key: String, content: Any) = jsonHelper.getCacheJSON(key, content)

    /**
     * 根据key获取对象,多个key代表多个等级,不能获取数组
     */
    fun <T : Any> getValue(type: Type, vararg key: String) = jsonHelper.getValue<T>(type, *key)

    /**
     * 获取指定类型数据
     * 根据key获取对象,多个key代表多个等级
     */
    inline fun <reified T> getValue(vararg key: String) = jsonHelper.getValue<T>(*key)


    fun getIntValue(vararg key: String) = jsonHelper.getIntValue(*key)
    fun getIntValue(defaultValue: Int, vararg key: String) = jsonHelper.getIntValue(defaultValue, *key)

    fun getLongValue(vararg key: String) = jsonHelper.getLongValue(*key)
    fun getLongValue(defaultValue: Long, vararg key: String) = jsonHelper.getLongValue(defaultValue, *key)

    fun getStringValue(vararg key: String) = jsonHelper.getStringValue(*key)
    fun getStringValueDef(defaultValue: String, vararg key: String) = jsonHelper.getStringValueDef(defaultValue, *key)

    fun getBooleanValue(vararg key: String) = jsonHelper.getBooleanValue(*key)
    fun getBooleanValue(defaultValue: Boolean, vararg key: String) = jsonHelper.getBooleanValue(defaultValue, *key)

    fun getFloatValue(vararg key: String) = jsonHelper.getFloatValue(*key)
    fun getFloatValue(defaultValue: Float, vararg key: String) = jsonHelper.getFloatValue(defaultValue, *key)


    fun getDoubleValue(vararg key: String) = jsonHelper.getDoubleValue(*key)
    fun getDoubleValue(defaultValue: Double, vararg key: String) = jsonHelper.getDoubleValue(defaultValue, *key)
}