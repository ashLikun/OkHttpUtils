package com.ashlikun.okhttputils.http;

import android.net.Uri;
import android.text.TextUtils;

import com.ashlikun.okhttputils.http.cache.CacheEntity;
import com.ashlikun.okhttputils.http.callback.Callback;
import com.ashlikun.okhttputils.http.response.HttpErrorCode;
import com.ashlikun.okhttputils.http.response.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 4:16
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：用到的工具方法
 */
public class HttpUtils {
    public static void runmainThread(Consumer<Integer> next) {
        runmainThread(1, next);
    }

    public static void runmainThread(int id, Consumer<Integer> next) {
        Observable.just(id).observeOn(AndroidSchedulers.mainThread())
                .subscribe(next);
    }

    /**
     * 将传递进来的参数拼接成 url
     */
    public static Uri createUrlFromParams(Uri url, Map<String, Object> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = url.buildUpon();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
        return builder.build();
    }

    /**
     * 根据响应头或者url获取文件名
     */
    public static String getNetFileName(Response response, String url) {
        String fileName = getHeaderFileName(response);
        if (TextUtils.isEmpty(fileName)) {
            fileName = getUrlFileName(url);
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = "unknownfile_" + System.currentTimeMillis();
        }
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return fileName;
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(Response response) {
        if (response == null) {
            return null;
        }
        String dispositionHeader = response.header("Content-Disposition");
        if (dispositionHeader != null) {
            //文件名可能包含双引号，需要去除
            dispositionHeader = dispositionHeader.replaceAll("\"", "");
            String split = "filename=";
            int indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                return dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
            }
            split = "filename*=";
            indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                String fileName = dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
                String encode = "UTF-8''";
                if (fileName.startsWith(encode)) {
                    fileName = fileName.substring(encode.length(), fileName.length());
                }
                return fileName;
            }
        }
        return null;
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
    private static String getUrlFileName(String url) {
        String filename = null;
        String[] strings = url.split("/");
        for (String string : strings) {
            if (string.contains("?")) {
                int endIndex = string.indexOf("?");
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex);
                    return filename;
                }
            }
        }
        if (strings.length > 0) {
            filename = strings[strings.length - 1];
        }
        return filename;
    }

    /**
     * 处理返回值
     */
    public static <T> T handerResult(Type type, final Response response, Gson gson) throws IOException {
        if (type != null) {
            if (type == Response.class) {
                return (T) response;
            } else if (type == ResponseBody.class) {
                return (T) response.body();
            } else {
                String json = response.body().string();
                if (type == String.class) {
                    return (T) json;
                } else {
                    T res = null;
                    try {
                        if (TextUtils.isEmpty(json)) {
                            throw new JsonSyntaxException("json length = 0");
                        }
                        if (gson == null) {
                            Class cls = null;
                            try {
                                if (type instanceof Class) {
                                    cls = (Class) type;
                                } else {
                                    cls = (Class) ((ParameterizedType) type).getRawType();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (cls != null) {
                                if (HttpResponse.class.isAssignableFrom(cls)) {
                                    try {
                                        HttpResponse da = (HttpResponse) cls.newInstance();
                                        gson = da.parseGson();
                                    } catch (InstantiationException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (gson == null) {
                            gson = OkHttpUtils.getInstance().getParseGson();
                        }
                        res = gson.fromJson(json, type);
                    } catch (JsonSyntaxException e) {//数据解析异常
                        throw new IOException(HttpErrorCode.MSG_DATA_ERROR2 + "  \n  原异常：" + e.toString() + "\n json = " + json);
                    }
                    if (res instanceof HttpResponse) {
                        ((HttpResponse) res).json = json;
                        ((HttpResponse) res).httpcode = response.code();
                        ((HttpResponse) res).response = response;
                    }
                    return res;
                }
            }
        }
        return null;
    }

    /**
     * 处理返回值
     */
    public static <T> T handerResult(Type type, final CacheEntity response, Gson gson) throws IOException {
        String json = response.result;
        if (type == String.class) {
            return (T) json;
        } else {
            T res = null;
            try {
                if (TextUtils.isEmpty(json)) {
                    throw new JsonSyntaxException("json length = 0");
                }
                if (gson == null) {
                    Class cls = null;
                    try {
                        if (type instanceof Class) {
                            cls = (Class) type;
                        } else {
                            cls = (Class) ((ParameterizedType) type).getRawType();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (cls != null) {
                        if (HttpResponse.class.isAssignableFrom(cls)) {
                            try {
                                HttpResponse da = (HttpResponse) cls.newInstance();
                                gson = da.parseGson();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (gson == null) {
                    gson = OkHttpUtils.getInstance().getParseGson();
                }
                res = gson.fromJson(json, type);
            } catch (JsonSyntaxException e) {//数据解析异常
                throw new IOException(HttpErrorCode.MSG_DATA_ERROR2 + "  \n  原异常：" + e.toString() + "\n json = " + json);
            }
            if (res instanceof HttpResponse) {
                ((HttpResponse) res).json = json;
                ((HttpResponse) res).httpcode = response.code;
            }
            return res;
        }
    }

    /**
     * 获取回调里面的泛型
     */
    public static Type getType(Class mClass) {
        Type types = mClass.getGenericSuperclass();
        Type[] parentypes;//泛型类型集合
        if (types instanceof ParameterizedType) {
            parentypes = ((ParameterizedType) types).getActualTypeArguments();
        } else {
            parentypes = mClass.getGenericInterfaces();
            for (Type childtype : parentypes) {
                if (childtype instanceof ParameterizedType) {
                    Type rawType = ((ParameterizedType) childtype).getRawType();
                    //实现的接口是Callback
                    if (rawType instanceof Class && Callback.class.isAssignableFrom(((Class) rawType))) {
                        //Callback里面的类型
                        parentypes = ((ParameterizedType) childtype).getActualTypeArguments();
                    }
                }
            }
        }
        if (parentypes == null || parentypes.length == 0) {
            new Throwable("HttpSubscription  ->>>  callBack回调 不能没有泛型，请查看HttpCallBack是否有泛型");
        } else {
            return parentypes[0];
        }
        return null;
    }

}