package com.ashlikun.okhttputils.http.request

import android.net.Uri
import android.util.Log
import com.ashlikun.gson.GsonHelper
import com.ashlikun.okhttputils.http.*
import com.ashlikun.okhttputils.http.HttpUtils.createUrlFromParams
import com.ashlikun.okhttputils.http.cache.CacheMode
import com.ashlikun.okhttputils.http.callback.Callback
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.HashMap

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 10:08
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：请求参数封装
 *          注意：一定要调用请求方法指定请求的方法，默认时get
 *          可以继承，从写方法
 */
open class HttpRequest(url: String) : Comparator<String>, SuperHttp {
    //请求地址
    open var url: Uri = Uri.parse(url)

    //请求方法
    open var method: String = methods[0]

    //请求头
    open var headers = mutableMapOf<String, String>()

    //普通键值对参数  get
    open var params: MutableMap<String, Any> = TreeMap<String, Any>(this)

    //请求内容，如果设置这个参数  其他的参数将不会提交  post
    open var postContent: String = ""

    //请求类型
    open var contentType: MediaType? = null

    //上传文件
    open var files = mutableListOf<FileInput>()


    open var parseGson: Gson = OkHttpUtils.get().parseGson

    lateinit var request: Request
        protected set

    /**
     * 是否json形式提交
     */
    open var isJson: Boolean? = OkHttpUtils.get().isJsonRequest

    //标识这个请求，会传递到Request里面
    open var tag: Any? = null

    //缓存模式
    open var cacheMode: CacheMode? = null

    //缓存超时时间
    open var cacheTime: Long = OkHttpUtils.get().cacheTime

    //缓存的key，默认内部自动获取url，但是如果参数里面有动态的参数，那么可能失效，如时间戳,所以开放自己设置
    open var cacheKey: String = ""
        get() {
            if (field.isNullOrEmpty()) {
                return if (request.method == "GET") request.url.toString()
                else return createUrlFromParams(url, paramsNotCommonParams).toString()
            } else return field
        }

    open fun appendPath(path: String): HttpRequest {
        val builder = url.buildUpon()
        if (path.isNotEmpty()) {
            url = builder.appendPath(path).build()
        }
        return this
    }

    open fun setMethod(method: String): HttpRequest {
        for (i in methods.indices) {
            if (methods[i] == method) {
                this.method = method
                return this
            }
        }
        Log.e("setMethod", "请求方法错误$method")
        this.method = methods[0]
        return this
    }

    /**
     * 设置请求类型
     */
    open fun setContentType(contentType: MediaType?): HttpRequest {
        this.contentType = contentType
        return this
    }

    /**
     * 获取去除公共参数
     */
    open val paramsNotCommonParams: Map<String, Any>
        get() {
            var value: Map<String, Any> = emptyMap()
            if (isJson == true) {
                runCatching {
                    value = parseGson.fromJson(postContent, Map::class.java) as Map<String, Any>
                }
            } else {
                value = params.toMap()
            }
            //去除公共参数
            if (OkHttpUtils.get().isCommonParams)
                value = value.filter { !OkHttpUtils.get().commonParams.containsKey(it.key) }
            return value
        }


    open val isHavaHeader: Boolean
        get() = headers.isNotEmpty()
    open val isHavaparams: Boolean
        get() = params.isNotEmpty()
    open val isHavafiles: Boolean
        get() = files.isNotEmpty()

    /**
     * 添加对象参数
     *  @param isValueJson  对象中的对象是否归根到根节点
     */
    open fun addParamObject(key: String, valuse: Any?, isValueJson: Boolean = true): HttpRequest {
        fun other(key: String, valuse: Any, isValueJson: Boolean = true) {
            try {
                val gson = GsonHelper.getGson()
                val map = gson.fromJson(gson.toJson(valuse), Map::class.java)
                addParams(map, isValueJson)
            } catch (e: Exception) {
                Log.e("OkhttpUtils ", "addParamObject error  key = ${key},valuse = ${valuse},${valuse.javaClass}")
                e.printStackTrace()
                //普通键值对
                if (key.isNotEmpty()) params[key] = valuse
            }
        }
        if (valuse != null) {
            when {

                //普通键值对
                (valuse is String || valuse is Number || valuse is Boolean || valuse is Boolean) -> if (key.isNotEmpty()) params[key] = valuse
                //如果是文件
                valuse is File -> if (key.isNotEmpty()) addParam(key, valuse)
                //如果是列表
                valuse is List<*> -> {
                    if (valuse.getOrNull(0) is File) {
                        addParam(key, valuse as List<File>)
                    } else if (key.isNotEmpty()) params[key] = valuse
                }
                //如果跳过释放，就直接添加
                !isValueJson -> if (key.isNotEmpty()) params[key] = valuse
                //如果参数是Map类型，就直接释放
                valuse is Map<*, *> -> addParams(valuse, false)
                //其他值序列化成json  Map
                else -> {
                    other(key, valuse, false)
                }
            }

        }
        return this
    }

    /**
     * 添加参数
     * @param isValueJson 对象中的对象是否归根到根节点
     */
    open fun addParam(key: String, valuse: Any?, isValueJson: Boolean = true): HttpRequest {
        addParamObject(key, valuse, isValueJson)
        return this
    }

    /**
     * 添加map参数
     * @param isValueJson 对象中的对象是否归根到根节点
     */
    open fun addParams(map: Map<*, *>?, isValueJson: Boolean = true): HttpRequest {
        map?.forEach {
            if (it.key != null && it.value != null) {
                addParam(it.key!!.toString(), it.value!!, isValueJson)
            }
        }
        return this
    }

    /**
     * 添加头部
     */
    open fun addHeader(key: String, valuse: String?): HttpRequest {
        if (key.isNotEmpty() && !valuse.isNullOrEmpty()) {
            headers[key] = valuse
        }
        return this
    }

    /**
     * 添加 公共参数
     */
    open protected fun addCommonParams() {
        OkHttpUtils.get().commonParams.forEach { t ->
            //如果已经有了就不添加，公共参数优先级低
            if (!params.containsKey(t.key)) {
                addParam(t.key, t.value)
            }
        }
    }


    /**
     * 添加文件参数
     */
    open fun addParam(key: String, file: File?) =
        if (file == null) this else addParam(FileInput(key, file))

    /**
     * 添加文件参数
     */
    open fun addParam(param: FileInput?): HttpRequest {
        if (param?.exists() == true) files.add(param)
        return this
    }

    /**
     * 添加文件参数
     */
    open fun addParamFilePath(key: String, filePath: String?) =
        if (filePath.isNullOrEmpty()) this else addParam(key, File(filePath))

    /**
     * 添加文件参数
     */
    open fun addParam(key: String, files: List<File>?): HttpRequest {
        files?.forEach { addParam(key, it) }
        return this
    }

    /**
     * 添加文件参数
     */
    open fun addParamFilePath(key: String, filePaths: List<String>?): HttpRequest {
        filePaths?.forEach { addParamFilePath(key, it) }
        return this
    }

    /**
     * 设置直接提交得post
     * 不会添加公共参数
     */
    open fun setContent(content: String): HttpRequest {
        //添加公共参数
        var content = content
        if (OkHttpUtils.get().isCommonParams) {
            runCatching {
                params = parseGson.fromJson(content, TreeMap::class.java) as TreeMap<String, Any>
                addCommonParams()
                content = parseGson.toJson(params)
            }
        }
        postContent = content
        isJson = postContent.isJson()
        if (method == methods[0]) {
            setMethod("POST")
        }
        return this
    }

    /**
     * 方法功能：把键值对转换成json放到content里面,最后调用
     * 注意：在这个方法调用以后添加的参数将无效
     * 2：如果存在文件就不能用content提交
     */
    open fun toJson(): HttpRequest {
        if (isJson == false) {
            return this
        }
        if (method == methods[0]) {
            setMethod("POST")
        }
        //添加公共参数
        addCommonParams()
        //转换成json
        if (params.isNotEmpty() && !isHavafiles) {
            if (postContent.isNotEmpty()) {
                runCatching {
                    params = (params + (parseGson.fromJson(postContent, HashMap::class.java) as MutableMap<String, Any>)).toMutableMap()
                }
            }
            //如果参数只有一个，并且是List，那么json就是数组
            if (params.size == 1 && params.values.find { it != null } is List<*>) {
                postContent = GsonHelper.getGson().toJson(params.values.find { it != null })
            } else {
                postContent = GsonHelper.getGson().toJson(params)
            }
            params.clear()
        }

        isJson = postContent.isJson()
        return this
    }

    /**
     * 构建请求
     * 这里返回的对象才可以执行请求
     */
    open fun buildCall(): RequestCall {
        return RequestCall(this)
    }

    /**
     * 解析结果的gson
     */
    open fun parseGson(gson: Gson): HttpRequest {
        parseGson = gson
        return this
    }

    open fun tag(tag: Any?): HttpRequest {
        this.tag = tag
        return this
    }

    /**
     * 构建请求body    多表单数据  键值对
     */
    open protected fun addParams(params: Map<String, Any>, builder: MultipartBody.Builder) {
        for ((key, value) in params) {
            //Content-Disposition;form-data; name="aaa"
            builder.addFormDataPart(key, value.toString())
        }
    }

    /**
     * 构建请求body    表单数据  键值对
     */
    open protected fun addParams(params: Map<String, Any>, builder: FormBody.Builder) {
        for ((key, value) in params) {
            builder.add(key, value?.toString())
        }
    }

    /**
     * 方法功能：构建请求body    多表单  文件数据
     */
    open protected fun addFlieParams(builder: MultipartBody.Builder) {
        //表单数据
        for (fileInput in files) {
            builder.addFormDataPart(
                fileInput.key,
                fileInput.filename,
                fileInput.file.asRequestBody(fileInput.contentType)
            )
        }
    }

    /**
     * 异步回调
     */
    override fun <T> execute(callback: Callback<T>): ExecuteCall {
        return buildCall().execute(callback)
    }

    /**
     * 同步执行
     */
    @Throws(HttpException::class)
    override fun <ResultType> syncExecute(rawType: Type, vararg typeArguments: Type): ResultType {
        return buildCall().syncExecute(rawType, *typeArguments)
    }
    /********************************************************************************************
     * 私有方法
     */
    /**
     * 第一步调用
     * 构建一个Request
     */
    open fun bulidRequest(
        callback: Callback<*>?
    ): Request {
        val builder = Request.Builder()
        if (method.isEmpty()) method = methods[0]
        val requestBody = buildRequestBody(callback)
        val header = Headers.Builder()
        //添加公共请求头
        OkHttpUtils.get().commonHeaders.forEach {
            //直接set
            header[it.key] = it.value
        }
        //如果在公共请求头有的要替换
        headers.forEach {
            //直接set
            header[it.key] = it.value
        }
        request = builder
            .url(url.toString())
            .headers(header.build())
            .method(method, requestBody)
            .tag(tag)
            .build()
        return request
    }

    /**
     * 构建请求body
     * postContent ->text/plain;charset=utf-8
     * 不存在文件application/x-www-form-urlencoded
     * 存在文件 multipart/form-data
     * 私有
     */
    open protected fun buildRequestBody(callback: Callback<*>?): RequestBody? {

        var body: RequestBody?
        onBuildRequestBody()
        //添加公共参数,只提交content的时候已经处理好了公共参数
        if (postContent.isEmpty()) {
            addCommonParams()
        }
        if (isJson == true) {
            toJson()
        }
        onBuildRequestBodyHasCommonParams()
        if (method == "GET") {
            //get请求把参数放在url里面, 没有请求实体
            url = createUrlFromParams(url, params)
            body = null
        } else if (postContent.isNotEmpty()) {
            //只提交content
            body = if (isJson == true) {
                ContentRequestBody.createNew(
                    if (contentType == null) MEDIA_TYPE_JSON else contentType,
                    postContent
                )
            } else {
                ContentRequestBody.createNew(
                    if (contentType == null) MEDIA_TYPE_PLAIN else contentType,
                    postContent
                )
            }
            //content方式是不能提交文件的
        } else {
            //存在文件用MultipartBody
            body = if (isHavafiles) {
                val builder = MultipartBody.Builder()
                builder.setType(contentType ?: MultipartBody.FORM)
                addParams(params, builder)
                addFlieParams(builder)
                builder.build()
            } else { //不存在文件用 FormBody
                val builder = FormBody.Builder()
                addParams(params, builder)
                builder.build()
            }
        }
        //是否添加进度回调
        if (body != null && callback?.progressCallBack != null) {
            body = ProgressRequestBody(body, callback.progressRate, callback.progressCallBack)
        }
        return body
    }

    /**
     * 可以添加签名，在全部参数添加完毕后,如果调用toJson方法那么params没有值，content有值
     * 实现者可以继承从写
     * 这里是添加公共参数之前
     */
    open fun onBuildRequestBody() {}

    /**
     * 可以添加签名，在全部参数添加完毕后,如果调用toJson方法那么params没有值，content有值
     * 实现者可以继承从写
     * 这里是添加公共参数之后
     */
    open fun onBuildRequestBodyHasCommonParams() {}


    //遍历参数的时候顺序,默认递增，结合 newParamMap 方法
    override fun compare(o1: String, o2: String): Int {
        return o1.compareTo(o2)
    }

    companion object {
        /* valid HTTP methods */
        val methods = arrayOf(
            "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"
        )
        val MEDIA_TYPE_PLAIN: MediaType = "text/plain;charset=utf-8".toMediaType()
        val MEDIA_TYPE_JSON: MediaType = "application/json;charset=utf-8".toMediaType()
        val MEDIA_TYPE_STREAM: MediaType = "application/octet-stream".toMediaType()

        fun post(url: String = ""): HttpRequest {
            val param = HttpRequest(url)
            param.setMethod("POST")
            return param
        }

        operator fun get(url: String = ""): HttpRequest {
            val param = HttpRequest(url)
            param.setMethod("GET")
            return param
        }
    }

}