package com.ashlikun.okhttputils.http

import com.ashlikun.okhttputils.http.response.HttpErrorCode
import okhttp3.Call
import java.io.IOException
import java.net.*

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:30
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：异常的封装
 */

open class HttpException(
    val code: Int,
    override val message: String,
    //原始错误
    var throwable: Throwable? = null
) : Exception(message) {


    companion object {
        /**
         * 处理异常
         */
        fun handleFailureHttpException(call: Call, e: IOException): HttpException {
            val res: HttpException
            res = if (e is ConnectException) {
                HttpException(HttpErrorCode.HTTP_NO_CONNECT, HttpErrorCode.MSG_NO_CONNECT, e)
            } else if (e is SocketException) {
                //java.net.SocketException: sendto failed: ECONNRESET (Connection reset by peer)
                //服务器的并发连接数超过了其承载量，服务器会将其中一些连接关闭；
                //如果知道实际连接服务器的并发客户数没有超过服务器的承载量，则有可能是中了病毒或者木马，引起网
                if ("Socket closed" == e.message) {
                    HttpException(HttpErrorCode.HTTP_CANCELED, HttpErrorCode.MSG_CANCELED, e)
                } else {
                    HttpException(
                        HttpErrorCode.HTTP_SOCKET_ERROR,
                        HttpErrorCode.MSG_SOCKET_ERROR,
                        e
                    )
                }
            } else if (e is SocketTimeoutException) {
                HttpException(HttpErrorCode.HTTP_TIME_OUT, HttpErrorCode.MSG_TIME_OUT, e)
            } else if (e is UnknownHostException) {
                HttpException(HttpErrorCode.HTTP_UNKNOWN_HOST, HttpErrorCode.MSG_UNKNOWN_HOST, e)
            } else {
                e.printStackTrace()
                HttpException(HttpErrorCode.HTTP_UNKNOWN, HttpErrorCode.MSG_UNKNOWN, e)
            }
            return res
        }

        /**
         * 处理异常
         * 方法功能：当成功的时候返回的.但是code不是200的时候
         */
        fun getOnResponseHttpException(code: Int, message: String?): HttpException {
            return if (message != null && message.contains("timed out") || code == HttpURLConnection.HTTP_CLIENT_TIMEOUT || code == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                HttpException(HttpErrorCode.HTTP_TIME_OUT, HttpErrorCode.MSG_TIME_OUT)
            } else if (code == HttpURLConnection.HTTP_INTERNAL_ERROR || code == 0) { //500
                HttpException(code, HttpErrorCode.MSG_INTERNAL_ERROR)
            } else if (code == HttpURLConnection.HTTP_FORBIDDEN) { //403
                HttpException(code, HttpErrorCode.MSG_FORBIDDEN)
            } else if (code == HttpURLConnection.HTTP_NOT_FOUND) { //404
                HttpException(code, HttpErrorCode.MSG_NOT_FOUND)
            } else if (code == HttpURLConnection.HTTP_BAD_METHOD) { //405
                HttpException(code, HttpErrorCode.MSG_BAD_METHOD)
            } else if (code == HttpURLConnection.HTTP_MULT_CHOICE || code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_SEE_OTHER
            ) { //300
                HttpException(code, HttpErrorCode.MSG_REDIRRECT)
            } else {
                //其他错误
                HttpException(code, HttpErrorCode.MSG_UNKNOWN_NETWORK)
            }
        }
    }
}