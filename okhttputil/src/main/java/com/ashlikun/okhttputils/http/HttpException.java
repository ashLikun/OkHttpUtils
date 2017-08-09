package com.ashlikun.okhttputils.http;


/**
 * 作者　　: 李坤
 * 创建时间: 2016/10/9 13:22
 * 邮箱　　：496546144@qq.com
 * 方法功能：
 */

public final class HttpException extends Exception {
    private int code;
    private String message;

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }



    /**
     * HTTP status code.
     */
    public int code() {
        return code;
    }

    /**
     * HTTP status message.
     */
    public String message() {
        return message;
    }

}
