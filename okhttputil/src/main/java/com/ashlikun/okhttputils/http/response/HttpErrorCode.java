package com.ashlikun.okhttputils.http.response;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/8/9　11:07
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public interface HttpErrorCode {
    //200以上的用HttpURLConnection里面的变量
    public static int HTTP_NO_CONNECT = 102;//网络未连接
    public static int HTTP_TIME_OUT = 101;//网络超时
    public static int HTTP_UNKNOWN = 100;//未知错误
    public static int HTTP_UNKNOWN_HOST = 98;//域名错误
    public static int HTTP_DATA_ERROR = 97;//数据解析错误
    public static int HTTP_SOCKET_ERROR = 96;//请求过于频繁,请稍后再试
    public static int HTTP_CANCELED = 95;//请求被取消


    public static String MSG_NO_CONNECT = "请检查网络设置";//网络未连接
    public static String MSG_TIME_OUT = "连接超时";//网络超时
    public static String MSG_UNKNOWN = "未知错误";//未知错误
    public static String MSG_UNKNOWN_NETWORK = "未知网络错误";//域名错误
    public static String MSG_UNKNOWN_HOST = "请检查网络设置";//域名错误
    public static String MSG_DATA_ERROR = "数据解析错误";//数据解析错误
    public static String MSG_SOCKET_ERROR = "请求过于频繁,请稍后再试";//请求过于频繁,请稍后再试
    public static String MSG_CANCELED = "请求被取消";//请求被取消


    public static String MSG_INTERNAL_ERROR = "服务君累趴下啦";//服务君累趴下啦
    public static String MSG_FORBIDDEN = "禁止访问";//禁止访问
    public static String MSG_NOT_FOUND = "资源不存在";//资源不存在
    public static String MSG_REDIRRECT = "重定向错误";//重定向错误
    public static String MSG_BAD_METHOD = "请求方法错误";//请求方法错误


}
