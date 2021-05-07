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
            Log.e("qqq","${System.currentTimeMillis()}")
            val kClass = ApiOther::class
            Log.e("qqq aaa","${System.currentTimeMillis()}")
            kClass.members.find { it.name == "test" }?.parameters?.forEach {
                Log.e("wwwwwwwww",it?.name?:"11")
            }
//            kClass.members.forEach {
//                it.parameters.forEach {
//                    Log.e("wwwwwwwww",it?.name?:"11")
//                }
//            }

            Log.e("qqq aaa sss","${System.currentTimeMillis()}")
            GlobalScope.launch {
                var aaa = ApiOther.api.testx(111)
                Log.e("aaaa", aaa?.json + "")
                aaa = ApiOther.api.test("interface", 11111)
                Log.e("bbb", aaa?.json + "")
            }

        }
    }
}