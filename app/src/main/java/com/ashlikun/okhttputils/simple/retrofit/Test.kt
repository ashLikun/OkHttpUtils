package com.ashlikun.okhttputils.simple.retrofit

import android.util.Log
import com.ashlikun.okhttputils.http.response.HttpResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 作者　　: 李坤
 * 创建时间: 2021/4/9　17:23
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
class Test {
    companion object {
        @JvmStatic
        fun start() {
            val kClass = ApiOther::class
            kClass.members.forEach {
                it.parameters.forEach {
                    Log.e("wwwwwwwww",it?.name?:"11")
                }
            }
            GlobalScope.launch {
                var aaa = ApiOther.api.testx(111)
                Log.e("aaaa", aaa?.json + "")
                aaa = ApiOther.api.test("interface", 11111)
                Log.e("bbb", aaa?.json + "")
            }

        }
    }
}