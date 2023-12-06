package com.ashlikun.okhttputils.simple.retrofit

import android.util.Log
import com.ashlikun.okhttputils.simple.data.WeiJinModel
import kotlinx.coroutines.*

/**
 * 作者　　: 李坤
 * 创建时间: 2021/4/9　17:23
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
class RetrofitTest {
    companion object {
        @JvmStatic
        fun start() {
            val aa = 11111;
            Log.e("qqq aaa sss", (aa is Number).toString())
//            val aa = ApiOther.api.testx(1)
//            Log.e("aaaaaaaa", "$aa")
            GlobalScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                throwable.printStackTrace()
            }) {
                GlobalScope.async {
                    delay(2000)
                    Log.e("ggggggggg", "2")
                    delay(2000)
                }
                runCatching {
                   withContext(Dispatchers.Main){
                       var aaa2 = ApiOther.api.test(111, WeiJinModel())
                       Log.e("bbb", aaa2?.dataX?.toString())
                       Log.e("bbb2", aaa2?.newData?.toString())
                   }
                }.onFailure {
                    it.printStackTrace()
                    Log.e("sssssss22222222ss", "")
                }
            }
//            GlobalScope.launch {
//                var aaa = ApiOther.api.testx(111)
//                Log.e("aaaa", aaa?.toString() + "")
////                aaa = ApiOther.api.test( 11111, WeiJinModel())
////                Log.e("bbb", aaa?.json + "")
////                var aaa = ApiOther.api.test7(111, "asdasdsad")
//                runCatching {
//                    var aaa2 = ApiOther.api.test(111, WeiJinModel())
//                    Log.e("bbb", aaa2?.json + "")
//                }.onFailure {
//                    Log.e("sssssssss", "")
//                }
//
//            }

        }
    }
}