package com.ashlikun.okhttputils.http.response;

import android.text.TextUtils;

import com.ashlikun.okhttputils.json.GsonHelper;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 10:27 admin
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：http的基本类
 */
public class HttpResponse {
    //gson不解析
    public transient Response response;
    public transient String json;
    public transient int httpcode;
    //缓存已经实例化的JSONObject,JSONArray对象
    private transient JSONObject cache;

    @HttpCode.IHttpCode
    @SerializedName("code")
    public int code;
    @SerializedName("msg")
    public String message;


    @HttpCode.IHttpCode
    public int getCode() {
        return code;
    }

    //获取头部code
    public int getHttpcode() {
        return httpcode;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject getJSONObject() throws JSONException {
        if (cache == null) {
            JSONObject jObj = new JSONObject(json);
            json = null;
            cache = jObj;
        }
        return cache;
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/5/25 11:07
     * <p>
     * 方法功能：设置当前类的解析json
     */

    public void setJson(String json) {
        GsonHelper.getGson().fromJson(json, HttpResponse.class);
    }

    public void setResponse(Response response) throws IOException {
        setJson(response.body().string());
        this.response = response;
        httpcode = response.code();
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2016/9/2 11:05
     * <p>
     * 方法功能：返回成功  success
     */

    public boolean isSucceed() {
        return code == HttpCode.SUCCEED;
    }



    /**
     * 作者　　: 李坤
     * 创建时间: 2017/5/19 17:04
     * 方法功能：根据key获取对象
     */

    public <T> T getTypeToObject(Class<T> type, String... key) throws JSONException {
        Object resStr = getJSONObject();
        if (key != null) {
            for (int i = 0; i < key.length; i++) {
                String currKey = key[i];//当前的key
                resStr = getCacheJSON(currKey, resStr);
            }
        }
        if (resStr == null) {
            return null;
        }
        if (type.isAssignableFrom(String.class)) {

        }
        T t = GsonHelper.getGson().fromJson(resStr.toString(), type);
        return t;
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/5/25 11:27
     * <p>
     * 方法功能：获取指定key的值
     */

    private Object getCacheJSON(String key, Object content) {
        if (TextUtils.isEmpty(key)) return null;

        if (content == null) return cache;

        if (content == null) {
            try {
                return getJSONObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (content instanceof JSONObject) {
            return ((JSONObject) content).opt(key);
        } else if (content instanceof JSONArray) {
            Object aaa = ((JSONArray) content).opt(0);
            if (aaa == null) {
                return aaa;
            }
            return getCacheJSON(key, aaa);
        }
        return cache;
    }


}
