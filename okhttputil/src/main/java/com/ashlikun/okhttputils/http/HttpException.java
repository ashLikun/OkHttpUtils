package com.ashlikun.okhttputils.http;


import com.ashlikun.okhttputils.http.response.HttpErrorCode;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Call;

/**
 * 作者　　: 李坤
 * 创建时间: 2016/10/9 13:22
 * 邮箱　　：496546144@qq.com
 * 方法功能：
 */

public class HttpException extends Exception {
    private int code;
    private String message;
    private Exception originalException;//原始错误

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

    public Exception getOriginalException() {
        return originalException;
    }

    public void setOriginalException(Exception originalException) {
        this.originalException = originalException;
    }

    /**
     * 处理异常
     *
     * @param call
     * @param e
     * @return
     */
    public static HttpException handleFailureHttpException(Call call, final IOException e) {

        HttpException res;
        if (e instanceof ConnectException) {
            res = new HttpException(HttpErrorCode.HTTP_NO_CONNECT, HttpErrorCode.MSG_NO_CONNECT);
        } else if (e instanceof SocketException) {
            //java.net.SocketException: sendto failed: ECONNRESET (Connection reset by peer)
            //服务器的并发连接数超过了其承载量，服务器会将其中一些连接关闭；
            //如果知道实际连接服务器的并发客户数没有超过服务器的承载量，则有可能是中了病毒或者木马，引起网
            if ("Socket closed".equals(e.getMessage())) {
                res = new HttpException(HttpErrorCode.HTTP_CANCELED, HttpErrorCode.MSG_CANCELED);
            } else {
                res = new HttpException(HttpErrorCode.HTTP_SOCKET_ERROR, HttpErrorCode.MSG_SOCKET_ERROR);
            }
        } else if (e instanceof SocketTimeoutException) {
            res = new HttpException(HttpErrorCode.HTTP_TIME_OUT, HttpErrorCode.MSG_TIME_OUT);
        } else if (e instanceof UnknownHostException) {
            res = new HttpException(HttpErrorCode.HTTP_UNKNOWN_HOST, HttpErrorCode.MSG_UNKNOWN_HOST);
        } else {
            e.printStackTrace();
            res = new HttpException(HttpErrorCode.HTTP_UNKNOWN, HttpErrorCode.MSG_UNKNOWN);
        }
        return res;
    }

    /**
     * 处理异常
     * 方法功能：当成功的时候返回的.但是code不是200的时候
     */
    public static HttpException getOnResponseHttpException(int code, String message) {
        if (message != null && message.contains("timed out") || code == HttpURLConnection.HTTP_CLIENT_TIMEOUT || code == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
            return new HttpException(HttpErrorCode.HTTP_TIME_OUT, HttpErrorCode.MSG_TIME_OUT);
        } else if (code == HttpURLConnection.HTTP_INTERNAL_ERROR || code == 0) {//500
            return new HttpException(code, HttpErrorCode.MSG_INTERNAL_ERROR);
        } else if (code == HttpURLConnection.HTTP_FORBIDDEN) {//403
            return new HttpException(code, HttpErrorCode.MSG_FORBIDDEN);
        } else if (code == HttpURLConnection.HTTP_NOT_FOUND) {//404
            return new HttpException(code, HttpErrorCode.MSG_NOT_FOUND);
        } else if (code == HttpURLConnection.HTTP_BAD_METHOD) {//405
            return new HttpException(code, HttpErrorCode.MSG_BAD_METHOD);
        } else if (code == HttpURLConnection.HTTP_MULT_CHOICE || code == HttpURLConnection.HTTP_MOVED_PERM
                || code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_SEE_OTHER) {//300
            return new HttpException(code, HttpErrorCode.MSG_REDIRRECT);
        } else {
            //其他错误
            return new HttpException(code, HttpErrorCode.MSG_UNKNOWN_NETWORK);
        }
    }

}
