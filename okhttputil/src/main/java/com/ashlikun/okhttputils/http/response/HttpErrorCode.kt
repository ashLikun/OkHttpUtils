package com.ashlikun.okhttputils.http.response

/**
 * 作者　　: 李坤
 * 创建时间: 2017/8/9　11:07
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
object HttpErrorCode {
    //200以上的用HttpURLConnection里面的变量
    const val HTTP_NO_CONNECT = 100102 //网络未连接
    const val HTTP_TIME_OUT = 100101 //网络超时
    const val HTTP_UNKNOWN = 100100 //未知错误
    const val HTTP_UNKNOWN_HOST = 10098 //域名错误
    const val HTTP_DATA_ERROR = 10097 //数据解析错误
    const val HTTP_SOCKET_ERROR = 10096 //请求过于频繁,请稍后再试
    const val HTTP_CANCELED = 10095 //请求被取消
    const val MSG_NO_CONNECT = "请检查网络设置" //网络未连接
    const val MSG_TIME_OUT = "连接超时" //网络超时
    const val MSG_UNKNOWN = "未知错误" //未知错误
    const val MSG_UNKNOWN_NETWORK = "未知网络错误" //域名错误
    const val MSG_UNKNOWN_HOST = "请检查网络设置" //域名错误
    const val MSG_DATA_ERROR = "数据解析错误" //数据解析错误
    const val MSG_DATA_ERROR2 = "数据解析错误_NULL" //数据解析错误
    const val MSG_SOCKET_ERROR = "请求过于频繁,请稍后再试" //请求过于频繁,请稍后再试
    const val MSG_CANCELED = "请求被取消" //请求被取消
    const val MSG_INTERNAL_ERROR = "服务君累趴下啦" //服务君累趴下啦
    const val MSG_FORBIDDEN = "禁止访问" //禁止访问
    const val MSG_NOT_FOUND = "资源不存在" //资源不存在
    const val MSG_REDIRRECT = "重定向错误" //重定向错误
    const val MSG_BAD_METHOD = "请求方法错误" //请求方法错误
}