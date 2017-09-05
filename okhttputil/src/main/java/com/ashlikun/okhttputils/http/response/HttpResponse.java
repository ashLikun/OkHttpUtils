package com.ashlikun.okhttputils.http.response;

import android.text.TextUtils;

import com.ashlikun.okhttputils.json.GsonHelper;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return unicode2String(message);
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
     * 方法功能：根据key获取对象,多个key代表多个等级
     */

    public <T> T getValue(Class<T> type, String... key) throws JSONException {
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

        T t = GsonHelper.getGson().fromJson(resStr.toString(), type);
        return t;
    }

    public int getIntValue(String... key) {
        try {
            Integer res = getValue(Integer.class, key);
            return res == null ? 0 : res;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getStringValue(String... key) {
        try {
            String res = getValue(String.class, key);
            return res == null ? "" : res;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean getBooleanValue(String... key) {
        try {
            Boolean res = getValue(Boolean.class, key);
            return res == null ? false : res;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public float getFloatValue(String... key) {
        try {
            Float res = getValue(Float.class, key);
            return res == null ? 0 : res;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getDoubleValue(String... key) {
        try {
            Double res = getValue(Double.class, key);
            return res == null ? 0 : res;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
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

    static final Pattern reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{2,4})");

    /**
     * unicode 转字符串
     */
    public String unicode2String(String unicode) {

        if (TextUtils.isEmpty(unicode)) {
            return null;
        }
        Matcher m = reUnicode.matcher(unicode);
        StringBuffer sb = new StringBuffer(unicode.length());
        while (m.find()) {
            m.appendReplacement(sb,
                    Character.toString((char) Integer.parseInt(m.group(1), 16)));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
