package com.ashlikun.okhttputils.http.response;

import android.util.Log;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * 作者　　: 李坤
 * 创建时间: 2020/6/8　13:48
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class TypeAdapterHttpResponse extends TypeAdapter {
    @Override
    public void write(JsonWriter out, Object value) throws IOException {

    }

    @Override
    public Object read(JsonReader in) throws IOException {
        Log.e("aaa", "aaaa");
        return null;
    }
}
