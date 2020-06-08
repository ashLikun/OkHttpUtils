package com.ashlikun.okhttputils.simple;

import com.ashlikun.okhttputils.http.response.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/4/26 0026　上午 10:14
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class HomeHttpResult<T> extends HttpResponse {
    @Override
    public <T> T parseData(Gson gson, String json, Type type) {
        return new GsonBuilder().create().fromJson(json, type);
    }
}
