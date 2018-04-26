package com.ashlikun.okhttputils.http.response;

import android.text.TextUtils;

import com.ashlikun.okhttputils.json.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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
public abstract class HttpResponse {
    public final static String CODE_KEY = "code";
    public final static String MES_KEY = "msg";
    //gson不解析
    public transient Response response;
    public transient String json;
    public transient int httpcode;
    //缓存已经实例化的JSONObject,JSONArray对象
    private transient JSONObject cache;

    @SerializedName(CODE_KEY)
    public int code = HttpCode.ERROR;
    @SerializedName(MES_KEY)
    public String message;

    //当Gson自动解析异常的时候会调用，由内部调用
    public void setOnGsonErrorData(String json) {
        this.json = json;
        this.code = getIntValue(HttpResponse.CODE_KEY);
        this.message = getStringValue(HttpResponse.MES_KEY);
    }

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
            if (json == null) {
                json = "";
            }
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

    public String getKeyToString(String... key) throws JSONException {
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
        return resStr.toString();
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/5/19 17:04
     * 方法功能：根据key获取对象,多个key代表多个等级,不能获取数组
     */
    public <T> T getValue(Type type, String... key) throws JsonParseException, JSONException {
        if (key == null || key.length == 0) {
            return null;
        }
        return GsonHelper.getGson().fromJson(getKeyToString(key), type);
    }

    /**
     * 基本类型的获取
     */
    public <T> T getValueBase(Type type, String... key) throws JsonParseException, JSONException {
        if (key == null || key.length == 0) {
            return null;
        }
        if (key.length == 1) {
            return (T) (getJSONObject().get(key[0]));
        }
        return GsonHelper.getGson().fromJson(getKeyToString(key), type);
    }

    public int getIntValue(String... key) {
        try {
            Integer res = getValueBase(Integer.class, key);
            return res == null ? 0 : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getStringValue(String... key) {
        try {
            String res = getValueBase(String.class, key);
            return res == null ? "" : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return "";
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean getBooleanValue(String... key) {
        try {
            Boolean res = getValueBase(Boolean.class, key);
            return res == null ? false : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public float getFloatValue(String... key) {
        try {
            Float res = getValueBase(Float.class, key);
            return res == null ? 0 : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getDoubleValue(String... key) {
        try {
            Double res = getValueBase(Double.class, key);
            return res == null ? 0 : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return 0;
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
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        if (content == null) {
            return cache;
        }

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

    public abstract Gson parseGson();
}
