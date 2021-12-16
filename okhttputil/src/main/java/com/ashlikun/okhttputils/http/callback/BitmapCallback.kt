package com.ashlikun.okhttputils.http.callback

import android.graphics.Bitmap
import android.widget.ImageView.ScaleType
import com.ashlikun.okhttputils.http.convert.BitmapConvert
import com.google.gson.Gson
import okhttp3.Response

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 19:39
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */

abstract class BitmapCallback(
    val maxWidth: Int = 1000,
    val maxHeight: Int = 1000,
    val decodeConfig: Bitmap.Config = Bitmap.Config.ARGB_8888,
    val scaleType: ScaleType = ScaleType.CENTER_INSIDE
) : AbsCallback<Bitmap>() {
    private var convert: BitmapConvert =
        BitmapConvert(maxWidth, maxHeight, decodeConfig, scaleType)

    @Throws(Exception::class)
    override fun convertResponse(response: Response, gosn: Gson?): Bitmap {
        val bitmap = convert.convertResponse(response, gosn)
        response.close()
        return bitmap!!
    }
}