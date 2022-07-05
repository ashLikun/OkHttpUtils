package com.ashlikun.okhttputils.retrofit

import com.ashlikun.okhttputils.http.request.HttpRequest
import java.io.File

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/16　15:36
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：对参数的描述
 * @param index  方法里面的第几个参数  -1:代表固定参数,这时候的key就是 "key1:value1"
 * @param key 对应字段名，提交后台的Key
 */
class ParameterHandler(
    var index: Int,
    var key: String,
    var valueDefault: String = "",
    var isFile: Boolean = false,
    //是否作为一个data提交，当只有一个参数的时候生效
    var isBody: Boolean = false,
    var isHeader: Boolean = false,
    //如果是多个文件是否用同一个key变成数组
    var isFileArray: Boolean = false
) {


    //吧这个注解信息填入到HttpRequest里面
    internal fun apply(request: HttpRequest, args: Array<Any?>?) {
        val value = if (index == -1) valueDefault else args?.getOrNull(index)
        when {
            isHeader -> request.addHeader(key, value?.toString().orEmpty())
            isBody -> request.setContent(value?.toString().orEmpty())
            //如果是文件
            isFile || value is File -> {
                if (value is List<*>) {
                    value.forEachIndexed { nIndex, s ->
                        if (s is String) {
                            if (isFileArray) {
                                request.addParamFilePath("${key}[${nIndex}]", s)
                            } else {
                                request.addParamFilePath(key, s)
                            }
                        } else if (s is File) {
                            if (isFileArray) {
                                request.addParam("${key}[${nIndex}]", s)
                            } else {
                                request.addParam(key, s)
                            }
                        }
                    }
                } else {
                    if (value is String) {
                        request.addParamFilePath(key, value)
                    } else if (value is File) {
                        request.addParam(key, value)
                    }
                }
            }
            //普通的键值对,内部会各种处理
            else -> request.addParam(key, value)
        }
    }
}