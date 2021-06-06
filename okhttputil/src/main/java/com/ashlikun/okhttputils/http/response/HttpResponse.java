package com.ashlikun.okhttputils.http.response;

import com.ashlikun.okhttputils.http.HttpUtils;
import com.google.gson.annotations.SerializedName;

/**
 * 作者　　: 李坤
 * 创建时间: 10:27 admin
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：http的基本类
 */
public class HttpResponse extends AbsHttpResponse {
    public static int SUCCEED = 1;//正常请求
    public static int ERROR = 0;//请求出错
    public final static String CODE_KEY = "code";
    public final static String MES_KEY = "msg";

    @SerializedName(value = CODE_KEY)
    public int code = ERROR;
    @SerializedName(MES_KEY)
    public String message;

    public HttpResponse() {
        super();
    }

    public HttpResponse(String json) {
        super(json);
    }

    @Override
    public void setOnGsonErrorData(String json) {
        this.code = getIntValue(HttpResponse.CODE_KEY);
        this.message = getStringValue(HttpResponse.MES_KEY);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }


    @Override
    public String getMessage() {
        return HttpUtils.unicode2String(message);
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 是否成功  success
     */
    @Override
    public boolean isSucceed() {
        return code == SUCCEED;
    }
}
