package com.ashlikun.okhttputils.http.response

import com.google.gson.annotations.SerializedName

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:34
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：http的基本类
 */
open class HttpResponse(
    //原始数据
    json: String = ""
) : AbsHttpResponse(json) {
    @SerializedName(value = CODE_KEY, alternate = [CODE_KEY2])
    override var code = ERROR

    @SerializedName(MES_KEY, alternate = [MES_KEY2])
    override var message: String = ""

    override fun setOnGsonErrorData(json: String) {
        super.setOnGsonErrorData(json)
        code = getIntValue(CODE_KEY)
        message = getStringValue(MES_KEY)
    }

    /**
     * 是否成功  success
     */
    override val isSucceed: Boolean
        get() = code == SUCCEED

    companion object {
        var SUCCEED = 1 //正常请求
        var ERROR = 0 //请求出错
        const val CODE_KEY = "code"
        const val CODE_KEY2 = "status"
        const val MES_KEY = "msg"
        const val MES_KEY2 = "message"
    }
}