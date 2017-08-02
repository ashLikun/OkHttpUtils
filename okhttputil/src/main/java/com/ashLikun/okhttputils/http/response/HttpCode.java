package com.ashLikun.okhttputils.http.response;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * 作者　　: 李坤
 * 创建时间:2016/9/2　11:01
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：http的返回值
 */

public class HttpCode {
    public static final int SUCCEED = 200;//正常请求
    public static final int LOGIN_ERROR = 401;//需要重新重新登录
    public static final int CODE_403 = 403;//非法请求

    /**
     * 请求码 code [600, 700)  表示要弹窗提示。  [700, 800]  表示跳到结果页面，  702：到无门店web页面
     */
    public static final int CODE_600 = 600;
    public static final int CODE_700 = 700;
    public static final int CODE_702 = 702;
    public static final int CODE_800 = 800;
    public static final int CODE_APP_UPDATA = 601;//强制更新
    public static final int CODE_SERVICE_PAUSE = 886;//服务器暂停服务错误码
    /**
     * 服务器api_version不匹配
     */
    public static final int CODE_API_ERROR = 888;

    @IntDef(value = {SUCCEED, LOGIN_ERROR, CODE_403, CODE_600, CODE_700, CODE_702, CODE_800, CODE_APP_UPDATA, CODE_SERVICE_PAUSE, CODE_API_ERROR})
    @Retention(value = RUNTIME)
    public @interface IHttpCode {

    }

}
