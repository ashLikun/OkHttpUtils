package com.ashlikun.okhttputils.http.callback;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.ashlikun.okhttputils.http.convert.BitmapConvert;
import com.google.gson.Gson;

import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 2:59
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public abstract class BitmapCallback extends AbsCallback<Bitmap> {

    private BitmapConvert convert;

    public BitmapCallback() {
        convert = new BitmapConvert();
    }

    public BitmapCallback(int maxWidth, int maxHeight) {
        convert = new BitmapConvert(maxWidth, maxHeight);
    }

    public BitmapCallback(int maxWidth, int maxHeight, Bitmap.Config decodeConfig, ImageView.ScaleType scaleType) {
        convert = new BitmapConvert(maxWidth, maxHeight, decodeConfig, scaleType);
    }

    @Override
    public Bitmap convertResponse(Response response, Gson gosn) throws Exception {
        Bitmap bitmap = convert.convertResponse(response, gosn);
        response.close();
        return bitmap;
    }
}

