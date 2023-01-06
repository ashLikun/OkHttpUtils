package com.ashlikun.okhttputils.http.response

import com.ashlikun.okhttputils.R
import com.ashlikun.okhttputils.http.OkHttpManage

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:34
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：Http 错误的Code
 */
object HttpErrorCode {
    //200以上的用HttpURLConnection里面的变量
    const val HTTP_NO_CONNECT = 100102 //网络未连接
    const val HTTP_TIME_OUT = 100101 //网络超时
    const val HTTP_UNKNOWN = 100100 //未知错误
    const val HTTP_CALL_ERROR = 100103 //处理结果错误
    const val HTTP_UNKNOWN_HOST = 10098 //域名错误
    const val HTTP_DATA_ERROR: Int = 10097 //数据解析错误
    const val HTTP_SOCKET_ERROR = 10096 //请求过于频繁,请稍后再试
    const val HTTP_CANCELED = 10095 //请求被取消
    fun getMessage(resId: Int) = OkHttpManage.app!!.getString(R.string.okhttp_message_common).ifEmpty { OkHttpManage.app!!.getString(resId) }

    //网络未连接
    val MSG_NO_CONNECT
        get() = getMessage(R.string.okhttp_message_no_conect)

    //连接超时
    val MSG_TIME_OUT
        get() = getMessage(R.string.okhttp_message_time_out)

    //未知错误
    val MSG_UNKNOWN
        get() = getMessage(R.string.okhttp_message_unknown)

    //处理结果错误
    val MSG_CALL_ERROR
        get() = getMessage(R.string.okhttp_message_call_error)

    //未知网络错误
    val MSG_UNKNOWN_NETWORK
        get() = getMessage(R.string.okhttp_message_unknown_network)

    //数据解析错误
    val MSG_DATA_ERROR
        get() = getMessage(R.string.okhttp_message_data_error)

    //数据解析错误_NULL
    val MSG_DATA_ERROR2
        get() = getMessage(R.string.okhttp_message_data_error2)

    //请求过于频繁,请稍后再试
    val MSG_SOCKET_ERROR
        get() = getMessage(R.string.okhttp_message_socket_error)

    //请求被取消
    val MSG_CANCELED
        get() = getMessage(R.string.okhttp_message_canceled)

    //服务君累趴下啦
    val MSG_500
        get() = getMessage(R.string.okhttp_message_internal_error)

    //禁止访问
    val MSG_FORBIDDEN
        get() = getMessage(R.string.okhttp_message_forbidden)

    //资源不存在
    val MSG_NOT_FIND
        get() = getMessage(R.string.okhttp_message_no_find)

    //重定向错误
    val MSG_REDIRRECT
        get() = getMessage(R.string.okhttp_message_redirrect)

    //请求方法错误
    val MSG_BAD_METHOD
        get() = getMessage(R.string.okhttp_message_bad_method)

}