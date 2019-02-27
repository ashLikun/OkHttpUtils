package com.ashlikun.okhttputils.http.response;

/**
 * 作者　　: 李坤
 * 创建时间:2016/9/2　11:01
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：http的返回值
 * 可以改变
 */

public class HttpCode {
    public static int SUCCEED = 0;//正常请求
    public static int ERROR = 1;//请求出错
    public static int SIGN_ERROR = 688;//签名错误
    public static int TOKEN_ERROR = 888;//token失效
}
