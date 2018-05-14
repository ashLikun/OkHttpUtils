package com.ashlikun.okhttputils.http.callback;

import com.ashlikun.okhttputils.http.HttpUtils;
import com.google.gson.Gson;

import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 2:52
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：字符串回调
 */
public abstract class StringCallback extends AbsCallback<String> {
    @Override
    public String convertResponse(Response response, Gson gosn) throws Exception {
        return HttpUtils.handerResult(String.class, response, gosn);
    }
}
