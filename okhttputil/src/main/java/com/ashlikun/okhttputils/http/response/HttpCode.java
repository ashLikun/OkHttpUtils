package com.ashlikun.okhttputils.http.response;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * 作者　　: 李坤
 * 创建时间:2016/9/2　11:01
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：http的返回值
 */

public class HttpCode {
    public static final int SUCCEED = 0;//正常请求
    public static final int ERROR = 1;//请求出错
    public static final int SIGN_ERROR = 688;//签名错误
    public static final int TOKEN_ERROR = 888;//token失效

    @IntDef(value = {SUCCEED, ERROR, SIGN_ERROR, TOKEN_ERROR})
    @Retention(value = RUNTIME)
    public @interface IHttpCode {

    }

}
