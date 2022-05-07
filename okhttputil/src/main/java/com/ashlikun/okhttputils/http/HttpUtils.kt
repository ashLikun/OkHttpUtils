package com.ashlikun.okhttputils.http

import android.net.Uri
import android.os.Looper
import android.text.TextUtils
import com.ashlikun.okhttputils.http.cache.CacheEntity
import com.ashlikun.okhttputils.http.callback.Callback
import com.ashlikun.okhttputils.http.request.ContentRequestBody
import com.ashlikun.okhttputils.http.request.HttpRequest
import com.ashlikun.okhttputils.http.request.ProgressRequestBody
import com.ashlikun.okhttputils.http.response.HttpErrorCode
import com.ashlikun.okhttputils.http.response.IHttpResponse
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URLConnection
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:30
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：用到的工具方法
 */

object HttpUtils {
    var UTF_8 = Charset.forName("UTF-8")

    val reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{2,4})")//创建一个新的实例

    fun launchMain(job: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            job()
        }
    }

    /**
     * 是否是主线程
     */
    val isMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()

    /**
     * 运行到携程
     */
    fun launch(job: suspend () -> Unit) {
        GlobalScope.launch {
            job()
        }
    }

    /**
     * 获取文件的mime类型  Content-type
     */
    fun getMimeType(path: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        try {
            return fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return HttpRequest.MEDIA_TYPE_STREAM.toString()
    }

    /**
     * 将传递进来的参数拼接成 url
     */
    fun createUrlFromParams(url: Uri, params: Map<String, Any>): Uri {
        if (params.isEmpty()) return url
        return url.buildUpon().apply {
            params.forEach {
                appendQueryParameter(it.key, (it.value.toString()))
            }
        }.build()
    }

    /**
     * 根据响应头或者url获取文件名
     */
    fun getNetFileName(response: Response?, url: String): String {
        var fileName = if (response != null) getHeaderFileName(response) else ""
        if (fileName.isEmpty()) {
            fileName = getUrlFileName(url)
        }
        if (fileName.isEmpty()) {
            fileName = "unknownfile_" + System.currentTimeMillis()
        }
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
        }
        return fileName!!
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private fun getHeaderFileName(response: Response): String {
        var dispositionHeader = response.header("Content-Disposition")
        if (dispositionHeader != null) {
            //文件名可能包含双引号，需要去除
            dispositionHeader = dispositionHeader.replace("\"".toRegex(), "")
            var split = "filename="
            var indexOf = dispositionHeader.indexOf(split)
            if (indexOf != -1) {
                return dispositionHeader.substring(indexOf + split.length, dispositionHeader.length)
            }
            split = "filename*="
            indexOf = dispositionHeader.indexOf(split)
            if (indexOf != -1) {
                var fileName =
                    dispositionHeader.substring(indexOf + split.length, dispositionHeader.length)
                val encode = "UTF-8''"
                if (fileName.startsWith(encode)) {
                    fileName = fileName.substring(encode.length, fileName.length)
                }
                return fileName
            }
        }
        return ""
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
    private fun getUrlFileName(url: String): String {
        var filename = ""
        val strings = url.split("/").toTypedArray()
        for (string in strings) {
            if (string.contains("?")) {
                val endIndex = string.indexOf("?")
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex)
                    return filename
                }
            }
        }
        if (strings.isNotEmpty()) {
            filename = strings[strings.size - 1]
        }
        return filename
    }

    /**
     * 请求里面添加一个参数
     *
     * @return 修改后的Request
     */
    fun addRequestParams(request: Request, key: String, value: Any): Request.Builder {
        val builder: Request.Builder = request.newBuilder()
        val method = request.method
        if ("GET" == method) {
            builder.url(
                request.url
                    .newBuilder()
                    .setEncodedQueryParameter(key, value.toString())
                    .build()
            )
        } else if ("POST" == method || "PUT" == method || "DELETE" == method || "PATCH" == method) {
            setRequestBody(request.body, method, builder, key, value, true)
        }
        return builder
    }

    /**
     * 修改请求参数里面的某个字段
     *
     * @return 修改后的Request
     */
    fun setRequestParams(request: Request, key: String, value: Any): Request.Builder {
        val builder: Request.Builder = request.newBuilder()
        val method = request.method
        if ("GET" == method) {
            builder.url(
                request.url
                    .newBuilder()
                    .setEncodedQueryParameter(key, value.toString())
                    .build()
            )
        } else if ("POST" == method || "PUT" == method || "DELETE" == method || "PATCH" == method) {
            setRequestBody(request.body, method, builder, key, value, false)
        }
        return builder
    }

    private fun setRequestBody(
        body: RequestBody?,
        method: String,
        builder: Request.Builder,
        key: String,
        value: Any,
        isAdd: Boolean
    ) {
        if (body == null) return
        if (body is ContentRequestBody) {
            //data方式提交，一般是json
            var content = body.getContent()
            try {
                //处理Number数据格式化异常
                val gson = GsonBuilder()
                    .registerTypeAdapter(
                        object : TypeToken<Map<String?, Any?>?>() {}.type,
                        JsonDeserializer<Map<String, Any>> { json, typeOfT, context ->
                            val treeMap: MutableMap<String, Any> = HashMap()
                            val jsonObject = json.asJsonObject
                            val entrySet = jsonObject.entrySet()
                            for ((key1, value1) in entrySet) {
                                treeMap[key1] = value1
                            }
                            treeMap
                        }).create()
                val p = gson.fromJson<MutableMap<String, Any>>(
                    content,
                    object : TypeToken<Map<String?, Any?>?>() {}.type
                )
                if (p != null) {
                    p[key] = value
                }
                content = gson.toJson(p) ?: ""
            } catch (e: Exception) {
            }
            builder.method(
                method,
                ContentRequestBody.createNew(body.contentType(), content)
            )
        } else if (body is FormBody) {
            //表单提交
            val size = body.size
            if (size > 0) {
                val newFormBody = FormBody.Builder()
                for (i in 0 until size) {
                    if (!isAdd && body.name(i) == key) {
                        //修改这个值
                        newFormBody.add(body.name(i), value.toString())
                    } else {
                        //其他不变
                        newFormBody.add(body.name(i), body.value(i))
                    }
                }
                if (isAdd) {
                    newFormBody.add(key, value.toString())
                }
                builder.method(method, newFormBody.build())
            }
        } else if (body is MultipartBody) {
            //复杂表单
            val size = body.size
            if (size > 0) {
                val newMultipartBody = MultipartBody.Builder()
                for (i in 0 until size) {
                    val part = body.part(i)
                    val headers = part.headers
                    val partValue = headers?.get("Content-Disposition") ?: ""
                    //不是文件 , 并且是当前key
                    if (!isAdd && !partValue.contains("filename") && partValue.contains(key)) {
                        newMultipartBody.addPart(
                            headers,
                            value.toString().toRequestBody(null)
                        )
                    } else {
                        //其他不变
                        newMultipartBody.addPart(headers, part.body)
                    }
                }
                if (isAdd) {
                    newMultipartBody.addFormDataPart(key, value.toString())
                }
                builder.method(method, newMultipartBody.build())
            }
        } else if (body != null && body is ProgressRequestBody) {
            //带进度
            setRequestBody(body.requestBody, method, builder, key, value, isAdd)
        }
    }

    /**
     * 获取 request 整个数据
     */
    fun getRequestToString(request: Request): String {
        val sb = StringBuilder()
        sb.append("Url   : " + request.url.toUrl().toString())
        sb.append("\nMethod: ${request.method}")
        sb.append("\nHeads : ${request.headers}")
        val requestBody = request.body
        if (requestBody != null) {
            try {
                val bufferedSink = Buffer()
                requestBody.writeTo(bufferedSink)
                sb.append("\n")
                sb.append(
                    bufferedSink.readString(
                        requestBody.contentType()?.charset() ?: Charset.forName("utf-8")
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    /**
     * 打印返回消息
     *
     * @param response 返回的对象
     */
    fun getResponseToString(response: Response): String {
        val sb = StringBuilder()
        sb.append("Heads : " + response.headers.toString())
        val responseBody = response.body
        if (responseBody != null) {
            try {
                val contentLength = responseBody.contentLength()
                val source = responseBody.source()
                try {
                    source.request(Long.MAX_VALUE)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val buffer = source.buffer()
                var charset = responseBody.contentType()?.charset() ?: Charset.forName("utf-8")
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(Charset.forName("utf-8"))
                }
                if (contentLength != 0L) {
                    sb.append("\n")
                    sb.append(buffer.clone().readString(charset))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    /**
     * 处理返回值
     */
    @Throws(IOException::class)
    fun <T> handerResult(type: Type, response: Response, gson: Gson): T {
        return if (type === Response::class.java) {
            response as T
        } else if (type === ResponseBody::class.java) {
            response.body as T
        } else {
            handerResult(
                type,
                response,
                response.body?.string() ?: "",
                gson
            ) as T
        }
    }

    /**
     * 处理返回值,只解析String或者自定义类型
     */
    @Throws(IOException::class)
    fun <T> handerResult(type: Type, response: Response, json: String, gson: Gson): T {
        return if (type === String::class.java) {
            json as T
        } else {
            var res: T? = null
            try {
                if (json.isEmpty()) throw JsonSyntaxException("json length = 0")
                try {
                    var cls = if (type is Class<*>) type
                    else (type as ParameterizedType).rawType as Class<*>
                    if (IHttpResponse::class.java.isAssignableFrom(cls)) {
                        res = (cls.newInstance() as IHttpResponse).parseData(
                            gson, json, type, response
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (res == null) {
                    res = gson.fromJson(json, type)
                }
            } catch (e: Exception) {
                //数据解析异常，统一回调错误,运行到新的线程不占用当前线程
                launch {
                    OkHttpUtils.sendOnDataParseError(
                        HttpErrorCode.HTTP_DATA_ERROR, e, response, json
                    )
                }
                throw IOException("${HttpErrorCode.MSG_DATA_ERROR2} \n  原异常：$e  \n json = $json")
            }
            if (res is IHttpResponse) {
                res.json = json
                res.httpCode = response.code
                res.response = response
            }
            if (res == null) {
                throw IOException("${HttpErrorCode.MSG_DATA_ERROR2}  \n json = $json")
            }
            res!!
        }
    }

    /**
     * 处理返回值,缓存用的
     */
    @JvmStatic
    @Throws(IOException::class)
    fun <T> handerResult(type: Type, response: CacheEntity, gson: Gson): T {
        val json = response.result
        return if (type === String::class.java) {
            json as T
        } else {
            var res: T? = null
            try {
                if (json.isEmpty()) throw JsonSyntaxException("json length = 0")
                try {
                    var cls = if (type is Class<*>) type
                    else (type as ParameterizedType).rawType as Class<*>
                    if (IHttpResponse::class.java.isAssignableFrom(cls)) {
                        res = (cls.newInstance() as IHttpResponse).parseData(
                            gson, json, type, null
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (res == null) {
                    res = gson.fromJson(json, type)
                }
            } catch (e: Exception) { //数据解析异常
                throw IOException("${HttpErrorCode.MSG_DATA_ERROR2} \n  原异常：$e  \n json = $json")
            }
            if (res is IHttpResponse) {
                (res as IHttpResponse).json = json
                (res as IHttpResponse).httpCode = response.code
            }
            if (res == null) {
                throw IOException("${HttpErrorCode.MSG_DATA_ERROR2}  \n json = $json")
            }
            res!!
        }
    }

    /**
     * 获取回调里面的泛型
     */
    fun getType(mClass: Class<*>): Type {
        val types = mClass.genericSuperclass
        var parentypes: Array<Type?> //泛型类型集合
        if (types is ParameterizedType) {
            parentypes = types.actualTypeArguments
        } else {
            parentypes = mClass.genericInterfaces
            for (childtype in parentypes) {
                if (childtype is ParameterizedType) {
                    val rawType = childtype.rawType
                    //实现的接口是Callback
                    if (rawType is Class<*> && Callback::class.java.isAssignableFrom(
                            rawType
                        )
                    ) {
                        //Callback里面的类型
                        parentypes = childtype.actualTypeArguments
                    }
                }
            }
        }
        return parentypes.getOrNull(0)
            ?: throw RuntimeException("HttpSubscription  ->>>  ，请查看 " + mClass + "是否有泛型")
    }

    /**
     * 从Response 里面把body内容克隆出来，保证Response的body还可以继续使用
     *
     * @return 可能为null
     */
    fun getResponseColneBody(response: Response): String {
        try {
            if (response.isSuccessful) {
                val source = response.body?.source()
                if (source != null) {
                    source.request(Long.MAX_VALUE)
                    val buffer = source.buffer
                    val contentType = response.body!!.contentType()
                    val charset = if (contentType != null) contentType.charset(UTF_8) else UTF_8
                    return buffer.clone().readString(charset!!)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * unicode 转字符串
     */
    @JvmStatic
    fun unicode2String(unicode: String): String {
        if (unicode.isEmpty()) {
            return ""
        }
        val m = reUnicode.matcher(unicode)
        val sb = StringBuffer(unicode.length)
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toInt(16).toChar().toString())
        }
        m.appendTail(sb)
        return sb.toString()
    }

    fun isJson(content: String) = jsonParser(content)?.run { true } ?: false

    fun jsonParser(content: String) = runCatching {
        JsonParser().parse(content)
    }.getOrNull()
}

inline fun String.isJson() = HttpUtils.isJson(this)
inline fun String.jsonParser() = HttpUtils.jsonParser(this)