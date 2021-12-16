package com.ashlikun.okhttputils.simple.retrofit

import android.util.Log
import com.ashlikun.okhttputils.simple.data.WeiJinModel
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
            Log.e("qqq aaa sss", "${System.currentTimeMillis()}")
            GlobalScope.launch {
//                var aaa = ApiOther.api.testx(111)
//                Log.e("aaaa", aaa?.json + "")
//                aaa = ApiOther.api.test( 11111, WeiJinModel())
//                Log.e("bbb", aaa?.json + "")
//                var aaa = ApiOther.api.test7(111, "asdasdsad")
                var aaa = ApiOther.api.test(111, WeiJinModel())
                Log.e("bbb", aaa?.json + "")
            }

        }
    }
}