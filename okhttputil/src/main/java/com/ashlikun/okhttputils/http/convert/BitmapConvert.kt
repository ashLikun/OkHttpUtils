package com.ashlikun.okhttputils.http.convert

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView.ScaleType
import com.ashlikun.okhttputils.http.response.HttpErrorCode
import com.google.gson.Gson
import okhttp3.Response
import java.io.IOException

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.16 14:13
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：Bitmap的回调转换
 */
class BitmapConvert constructor(
    val maxWidth: Int = 1000,
    val maxHeight: Int = 1000,
    val decodeConfig: Bitmap.Config = Bitmap.Config.ARGB_8888,
    val scaleType: ScaleType = ScaleType.CENTER_INSIDE
) : Converter<Bitmap> {
    @Throws(Exception::class)
    override fun convertResponse(response: Response, gson: Gson?): Bitmap {
        val body = response.body
            ?: throw IOException("${HttpErrorCode.MSG_DATA_ERROR2}  \n bitmap create error")
        return parse(body.bytes())
    }

    @Throws(OutOfMemoryError::class)
    private fun parse(byteArray: ByteArray): Bitmap {
        val decodeOptions = BitmapFactory.Options()
        val bitmap: Bitmap
        if (maxWidth == 0 && maxHeight == 0) {
            decodeOptions.inPreferredConfig = decodeConfig
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, decodeOptions)
        } else {
            decodeOptions.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, decodeOptions)
            val actualWidth = decodeOptions.outWidth
            val actualHeight = decodeOptions.outHeight
            val desiredWidth =
                getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight, scaleType)
            val desiredHeight =
                getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth, scaleType)
            decodeOptions.inJustDecodeBounds = false
            decodeOptions.inSampleSize =
                findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight)
            val tempBitmap =
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, decodeOptions)
            if (tempBitmap != null && (tempBitmap.width > desiredWidth || tempBitmap.height > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true)
                tempBitmap.recycle()
            } else {
                bitmap = tempBitmap
            }
        }
        return bitmap
    }

    companion object {
        private fun getResizedDimension(
            maxPrimary: Int,
            maxSecondary: Int,
            actualPrimary: Int,
            actualSecondary: Int,
            scaleType: ScaleType
        ): Int {

            // If no dominant value at all, just return the actual.
            if (maxPrimary == 0 && maxSecondary == 0) {
                return actualPrimary
            }

            // If ScaleType.FIT_XY fill the whole rectangle, ignore ratio.
            if (scaleType == ScaleType.FIT_XY) {
                return if (maxPrimary == 0) {
                    actualPrimary
                } else {
                    maxPrimary
                }
            }

            // If primary is unspecified, scale primary to match secondary's scaling ratio.
            if (maxPrimary == 0) {
                val ratio = maxSecondary.toDouble() / actualSecondary.toDouble()
                return (actualPrimary * ratio).toInt()
            }
            if (maxSecondary == 0) {
                return maxPrimary
            }
            val ratio = actualSecondary.toDouble() / actualPrimary.toDouble()
            var resized = maxPrimary

            // If ScaleType.CENTER_CROP fill the whole rectangle, preserve aspect ratio.
            if (scaleType == ScaleType.CENTER_CROP) {
                if (resized * ratio < maxSecondary) {
                    resized = (maxSecondary / ratio).toInt()
                }
                return resized
            }
            if (resized * ratio > maxSecondary) {
                resized = (maxSecondary / ratio).toInt()
            }
            return resized
        }

        private fun findBestSampleSize(
            actualWidth: Int,
            actualHeight: Int,
            desiredWidth: Int,
            desiredHeight: Int
        ): Int {
            val wr = actualWidth.toDouble() / desiredWidth
            val hr = actualHeight.toDouble() / desiredHeight
            val ratio = Math.min(wr, hr)
            var n = 1.0f
            while (n * 2 <= ratio) {
                n *= 2f
            }
            return n.toInt()
        }
    }
}