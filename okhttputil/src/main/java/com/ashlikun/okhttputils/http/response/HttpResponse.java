package com.ashlikun.okhttputils.http.response;

import android.text.TextUtils;

import com.ashlikun.gson.GsonHelper;
import com.ashlikun.okhttputils.http.HttpUtils;
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
public class HttpResponse {
    public static int SUCCEED = 0;//正常请求
    public static int ERROR = 1;//请求出错

    public final static String CODE_KEY = "code";
    public final static String MES_KEY = "msg";
    //gson不解析
    public transient Response response;
    public transient String json;
    public transient int httpcode;
    /**
     * 缓存已经实例化的JSONObject,JSONArray对象
     */
    private transient Object cache;

    @SerializedName(CODE_KEY)
    public int code = ERROR;
    @SerializedName(MES_KEY)
    public String message;

    //当Gson自动解析异常的时候会调用，由内部调用
    public void setOnGsonErrorData(String json) {
        this.json = json;
        this.code = getIntValue(HttpResponse.CODE_KEY);
        this.message = getStringValue(HttpResponse.MES_KEY);
    }

    public int getCode() {
        return code;
    }

    /**
     * 获取头部code
     *
     * @return
     */
    public int getHttpCode() {
        return httpcode;
    }

    public String getMessage() {
        return unicode2String(message);
    }

    /**
     * 获取根部json对象
     * 说明后续这个结果都是json对象
     *
     * @return
     * @throws JSONException
     */
    public JSONObject getJSONObject() throws JSONException {
        if (cache == null) {
            if (json == null) {
                json = "";
            }
            JSONObject jObj = new JSONObject(json);
            json = null;
            cache = jObj;
        }
        return (JSONObject) cache;
    }

    /**
     * 获取根部json数组
     * 说明后续这个结果都是json数组
     *
     * @return
     * @throws JSONException
     */
    public JSONArray getJSONArray() throws JSONException {
        if (cache == null) {
            if (json == null) {
                json = "";
            }
            JSONArray jsonArray = new JSONArray(json);
            json = null;
            cache = jsonArray;
        }
        return (JSONArray) cache;
    }


    /**
     * 作者　　: 李坤
     * 创建时间: 2016/9/2 11:05
     * <p>
     * 方法功能：返回成功  success
     */

    public boolean isSucceed() {
        return code == SUCCEED;
    }

    public Object getKeyToObject(String... key) throws JSONException {
        Object res = getJSONObject();
        if (key != null) {
            for (int i = 0; i < key.length; i++) {
                res = getCacheJSON(key[i], res);
            }
        }
        return res;
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
        Object o = getKeyToObject(key);
        if (o == null) {
            return null;
        }
        return GsonHelper.getGsonNotNull().fromJson(o.toString(), type);
    }

    /**
     * 基本类型的获取
     * 方法功能：根据key获取对象,多个key代表多个等级,只能获取基础数据类型
     */
    public <T> T getValueBase(String... key) throws ClassCastException, JsonParseException, JSONException {
        if (key == null || key.length == 0) {
            return null;
        }
        return (T) getKeyToObject(key);
    }

    public int getIntValue(String... key) {
        return getIntValue(0, key);
    }

    public int getIntValue(int defaultValue, String... key) {
        try {
            Integer result = HttpUtils.toInteger(getValueBase(key));
            return result == null ? defaultValue : result;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public long getLongValue(String... key) {
        return getLongValue(0, key);
    }

    public long getLongValue(long defaultValue, String... key) {
        try {
            Long res = HttpUtils.toLong(getValueBase(key));
            return res == null ? defaultValue : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }


    public String getStringValue(String... key) {
        try {
            String res = HttpUtils.toString(getValueBase(key));
            return res == null ? "" : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return "";
        } catch (ClassCastException e) {
            e.printStackTrace();
            return "";
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean getBooleanValue(String... key) {
        return getBooleanValue(false, key);
    }

    public boolean getBooleanValue(boolean defaultValue, String... key) {
        try {
            Boolean res = HttpUtils.toBoolean(getValueBase(key));
            return res == defaultValue ? false : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public float getFloatValue(String... key) {
        return getFloatValue(0, key);
    }

    public float getFloatValue(float defaultValue, String... key) {
        try {
            Float res = HttpUtils.toFloat(getValueBase(key));
            return res == null ? defaultValue : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public double getDoubleValue(String... key) {
        return getDoubleValue(0, key);
    }

    public double getDoubleValue(double defaultValue, String... key) {
        try {
            Double res = HttpUtils.toDouble(getValueBase(key));
            return res == null ? defaultValue : res;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
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

    public Gson parseGson() {
        return null;
    }
}
