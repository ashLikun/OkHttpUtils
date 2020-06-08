package com.ashlikun.okhttputils.http.response;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2020/6/5　14:15
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public interface IHttpResponse {
    public int getCode();

    public void setCode(int code);

    public String getMessage();

    public void setJson(String json);

    public String getJson();

    public void setResponse(Response response);

    /**
     * 获取头部code
     *
     * @return
     */
    public int getHttpCode();

    public void setHttpCode(int httpCode);

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSucceed();

    /**
     * 解析Json的Json
     *
     * @return
     */
    public <T> T parseData(Gson gson, String json, Type type);
}
