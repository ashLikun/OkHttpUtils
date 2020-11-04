package com.ashlikun.okhttputils.http;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ashlikun.okhttputils.http.cache.CacheEntity;
import com.ashlikun.okhttputils.http.callback.Callback;
import com.ashlikun.okhttputils.http.request.ContentRequestBody;
import com.ashlikun.okhttputils.http.request.HttpRequest;
import com.ashlikun.okhttputils.http.request.ProgressRequestBody;
import com.ashlikun.okhttputils.http.response.HttpErrorCode;
import com.ashlikun.okhttputils.http.response.IHttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 4:16
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：用到的工具方法
 */
public class HttpUtils {
    public static Charset UTF_8 = Charset.forName("UTF-8");
    public static volatile Handler handler;

    public static Handler getMainHandler() {
        //双重校验DCL单例模式
        if (handler == null) {
            //同步代码块
            synchronized (HttpUtils.class) {
                if (handler == null) {
                    //创建一个新的实例
                    handler = new Handler(Looper.getMainLooper());
                }
            }
        }
        //返回一个实例
        return handler;
    }

    public static void runmainThread(Runnable runnable) {
        getMainHandler().post(runnable);
    }

    /**
     * 是否是主线程
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 运行到子线程
     */
    public static void runNewThread(Runnable runnable) {
        OkHttpUtils.getInstance().getOkHttpClient().dispatcher().executorService().execute(runnable);
    }

    /**
     * 获取文件的mime类型  Content-type
     */
    public static String getMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        try {
            return fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return HttpRequest.MEDIA_TYPE_STREAM.toString();
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
     * 请求里面添加一个参数
     *
     * @return 修改后的Request
     */
    public static Request.Builder addRequestParams(Request request, String key, Object value) {
        if (request == null || TextUtils.isEmpty(key) || value == null) {
            return null;
        }
        Request.Builder builder = request.newBuilder();
        String method = request.method();
        if ("GET".equals(method)) {
            builder.url(request.url()
                    .newBuilder()
                    .setEncodedQueryParameter(key, value.toString())
                    .build());
        } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method)) {
            setRequestBody(request.body(), method, builder, key, value, true);
        }
        return builder;
    }

    /**
     * 修改请求参数里面的某个字段
     *
     * @return 修改后的Request
     */
    public static Request.Builder setRequestParams(Request request, String key, Object value) {
        if (request == null || TextUtils.isEmpty(key) || value == null) {
            return null;
        }
        Request.Builder builder = request.newBuilder();
        String method = request.method();
        if ("GET".equals(method)) {
            builder.url(request.url()
                    .newBuilder()
                    .setEncodedQueryParameter(key, value.toString())
                    .build());
        } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method)) {
            setRequestBody(request.body(), method, builder, key, value, false);
        }
        return builder;
    }

    private static void setRequestBody(RequestBody body, String method, Request.Builder builder, String key, Object value, boolean isAdd) {
        if (body != null && body instanceof ContentRequestBody) {
            //data方式提交，一般是json
            ContentRequestBody contentRequestBody = (ContentRequestBody) body;
            String content = contentRequestBody.getContent();
            try {
                //处理Number数据格式化异常
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                        }.getType(), new JsonDeserializer<Map<String, Object>>() {
                            @Override
                            public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                                Map<String, Object> treeMap = new HashMap<>();
                                JsonObject jsonObject = json.getAsJsonObject();
                                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                                for (Map.Entry<String, JsonElement> entry : entrySet) {
                                    treeMap.put(entry.getKey(), entry.getValue());
                                }
                                return treeMap;
                            }
                        }).create();
                Map<String, Object> p = gson.fromJson(content, new TypeToken<Map<String, Object>>() {
                }.getType());
                if (p != null) {
                    p.put(key, value);
                }
                content = gson.toJson(p);
            } catch (Exception e) {
            }

            builder.method(method, ContentRequestBody.createNew(contentRequestBody.contentType(), content));
        } else if (body != null && body instanceof FormBody) {
            //表单提交
            FormBody formBody = (FormBody) body;
            int size = formBody.size();
            if (size > 0) {
                FormBody.Builder newFormBody = new FormBody.Builder();
                for (int i = 0; i < size; i++) {
                    if (!isAdd && formBody.name(i).equals(key)) {
                        //修改这个值
                        newFormBody.add(formBody.name(i), value.toString());
                    } else {
                        //其他不变
                        newFormBody.add(formBody.name(i), formBody.value(i));
                    }
                }
                if (isAdd) {
                    newFormBody.add(key, value.toString());
                }
                builder.method(method, newFormBody.build());
            }
        } else if (body != null && body instanceof MultipartBody) {
            //复杂表单
            MultipartBody multipartBody = (MultipartBody) body;
            int size = multipartBody.size();
            if (size > 0) {
                MultipartBody.Builder newMultipartBody = new MultipartBody.Builder();
                for (int i = 0; i < size; i++) {
                    MultipartBody.Part part = multipartBody.part(i);
                    Headers headers = part.headers();
                    String partValue = headers.get("Content-Disposition");
                    //不是文件 , 并且是当前key
                    if (!isAdd && !partValue.contains("filename") && partValue.contains(key)) {
                        newMultipartBody.addPart(headers, RequestBody.create(null, value.toString()));
                    } else {
                        //其他不变
                        newMultipartBody.addPart(headers, part.body());
                    }
                }
                if (isAdd) {
                    newMultipartBody.addFormDataPart(key, value.toString());
                }
                builder.method(method, newMultipartBody.build());
            }
        } else if (body != null && body instanceof ProgressRequestBody) {
            //带进度
            ProgressRequestBody progressRequestBody = (ProgressRequestBody) body;
            setRequestBody(progressRequestBody.getRequestBody(), method, builder, key, value, isAdd);
        }
    }

    /**
     * 获取 request 整个数据
     */
    public static String getRequestToString(Request request) {
        if (request == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Url   : " + request.url().url().toString());
        sb.append("\nMethod: " + request.method());
        sb.append("\nHeads : " + request.headers().toString());
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            try {
                Buffer bufferedSink = new Buffer();
                requestBody.writeTo(bufferedSink);
                Charset charset = requestBody.contentType().charset();
                charset = charset == null ? Charset.forName("utf-8") : charset;
                sb.append("\n");
                sb.append(bufferedSink.readString(charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * 打印返回消息
     *
     * @param response 返回的对象
     */
    public static String getResponseToString(Response response) {
        if (response == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Heads : " + response.headers().toString());
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            try {
                long contentLength = responseBody.contentLength();
                BufferedSource source = responseBody.source();
                try {
                    source.request(Long.MAX_VALUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Buffer buffer = source.buffer();
                Charset charset = responseBody.contentType().charset();
                charset = charset == null ? Charset.forName("utf-8") : charset;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset();
                }
                if (contentLength != 0) {
                    sb.append("\n");
                    sb.append(buffer.clone().readString(charset));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
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
                return handerResult(type, response, response.body().string(), gson);
            }
        }
        return null;
    }

    /**
     * 处理返回值,只解析String或者自定义类型
     */
    public static <T> T handerResult(Type type, final Response response, final String json, Gson gson) throws IOException {
        if (type == String.class) {
            return (T) json;
        } else {
            T res = null;
            try {
                if (TextUtils.isEmpty(json)) {
                    throw new JsonSyntaxException("json length = 0");
                }
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
                if (gson == null) {
                    gson = OkHttpUtils.getInstance().getParseGson();
                }
                try {
                    if (cls != null) {
                        if (IHttpResponse.class.isAssignableFrom(cls)) {
                            try {
                                IHttpResponse da = (IHttpResponse) cls.newInstance();
                                res = da.parseData(gson, json, type);
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (res == null) {
                        res = gson.fromJson(json, type);
                    }
                } catch (final JsonSyntaxException e) {
                    e.printStackTrace();
                    //数据解析异常，统一回调错误,运行到新的线程不占用当前线程
                    if (OkHttpUtils.getInstance().onDataParseError != null) {
                        runNewThread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpUtils.getInstance().onDataParseError.onError(HttpErrorCode.HTTP_DATA_ERROR, e, response, json);
                            }
                        });
                    }
                    throw new IOException(HttpErrorCode.MSG_DATA_ERROR + "  \n  原异常：" + e.toString() + "\n json = " + json);
                }
            } catch (final JsonSyntaxException e) {
                //数据解析异常，统一回调错误,运行到新的线程不占用当前线程
                if (OkHttpUtils.getInstance().onDataParseError != null) {
                    runNewThread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpUtils.getInstance().onDataParseError.onError(HttpErrorCode.HTTP_DATA_ERROR, e, response, json);
                        }
                    });
                }
                throw new IOException(HttpErrorCode.MSG_DATA_ERROR2 + "  \n  原异常：" + e.toString() + "\n json = " + json);
            }
            if (res instanceof IHttpResponse) {
                ((IHttpResponse) res).setJson(json);
                ((IHttpResponse) res).setHttpCode(response.code());
                ((IHttpResponse) res).setResponse(response);
            }
            return res;
        }
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
                if (gson == null) {
                    gson = OkHttpUtils.getInstance().getParseGson();
                }
                if (cls != null) {
                    if (IHttpResponse.class.isAssignableFrom(cls)) {
                        try {
                            IHttpResponse da = (IHttpResponse) cls.newInstance();
                            res = da.parseData(gson, json, type);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (res == null) {
                    res = gson.fromJson(json, type);
                }
            } catch (JsonSyntaxException e) {//数据解析异常
                throw new IOException(HttpErrorCode.MSG_DATA_ERROR2 + "  \n  原异常：" + e.toString() + "\n json = " + json);
            }
            if (res instanceof IHttpResponse) {
                ((IHttpResponse) res).setJson(json);
                ((IHttpResponse) res).setHttpCode(response.code);

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

    /**
     * 从Response 里面把body内容克隆出来，保证Response的body还可以继续使用
     *
     * @return 可能为null
     */
    public static String getResponseColneBody(Response response) {
        try {
            if (response.isSuccessful() && response.body() != null) {
                BufferedSource source = response.body().source();
                if (source != null) {
                    source.request(java.lang.Long.MAX_VALUE);
                    Buffer buffer = source.buffer();
                    MediaType contentType = response.body().contentType();
                    Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
                    return buffer.clone().readString(charset);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            String stringValue = (String) value;
            if ("true".equalsIgnoreCase(stringValue)) {
                return true;
            } else if ("false".equalsIgnoreCase(stringValue)) {
                return false;
            }
        }
        return null;
    }

    public static Double toDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.valueOf((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    public static Float toFloat(Object value) {
        if (value instanceof Float) {
            return (Float) value;
        } else if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof String) {
            try {
                return Float.valueOf((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    public static Integer toInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return (int) Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    public static Long toLong(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return (long) Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    public static String toString(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return String.valueOf(value);
        }
        return null;
    }
}
