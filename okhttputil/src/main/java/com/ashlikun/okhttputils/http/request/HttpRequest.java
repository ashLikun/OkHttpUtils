package com.ashlikun.okhttputils.http.request;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ashlikun.gson.GsonHelper;
import com.ashlikun.okhttputils.http.ExecuteCall;
import com.ashlikun.okhttputils.http.HttpException;
import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.OkHttpUtils;
import com.ashlikun.okhttputils.http.SuperHttp;
import com.ashlikun.okhttputils.http.cache.CacheMode;
import com.ashlikun.okhttputils.http.callback.Callback;
import com.ashlikun.okhttputils.http.callback.ProgressCallBack;
import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 作者　　: 李坤
 * 创建时间:2016/10/14　15:03
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍： 请求参数封装
 * 注意：一定要调用请求方法指定请求的方法，默认时get
 * 可以继承，从写方法
 */

public class HttpRequest implements Comparator<String>, SuperHttp {
    /* valid HTTP methods */
    public static final String[] methods = {
            "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"
    };
    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    protected Uri url;//请求地址
    protected String method;//请求方法
    protected Map<String, String> headers;//请求头
    protected Map<String, Object> params;//普通键值对参数  get
    protected String postContent;//请求内容，如果设置这个参数  其他的参数将不会提交  post
    protected MediaType contentType;//请求类型
    protected List<FileInput> files;//上传文件
    protected Gson gson;
    protected Request request;
    protected boolean isJson = false;
    //标识这个请求，会传递到Request里面
    protected Object tag;
    //缓存模式
    protected CacheMode cacheMode;
    //缓存超时时间
    protected long cacheTime;

    public static HttpRequest post(String url) {
        HttpRequest param = new HttpRequest(url);
        param.setMethod("POST");
        return param;
    }

    public static HttpRequest get(String url) {
        HttpRequest param = new HttpRequest(url);
        param.setMethod("GET");
        return param;
    }

    public static HttpRequest get() {
        return get("");
    }

    public HttpRequest(String url) {
        url(url);
        cacheMode = OkHttpUtils.getCacheMode();
        cacheTime = OkHttpUtils.getCacheTime();
    }


    public HttpRequest appendPath(String path) {
        if (url == null) {
            new Exception("先调用url方法");
        }
        Uri.Builder builder = url.buildUpon();
        if (!TextUtils.isEmpty(path)) {
            url = builder.appendPath(path).build();
        }
        return this;
    }

    public HttpRequest setMethod(String method) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equals(method)) {
                this.method = method;
                return this;
            }
        }
        Log.e("setMethod", "请求方法错误" + method);
        this.method = methods[0];
        return this;
    }

    /**
     * 设置请求类型
     */
    public HttpRequest setContentType(MediaType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpRequest url(String url) {
        this.url = Uri.parse(url);
        return this;
    }

    public boolean isHavaHeader() {
        return headers != null && !headers.isEmpty();
    }

    public boolean isHavaparams() {
        return params != null && !params.isEmpty();
    }

    public boolean isHavafiles() {
        return files != null && !files.isEmpty();
    }

    //添加对象参数
    private HttpRequest addParamObject(String key, Object valuse) {
        if (!isEmpty(key) && valuse != null) {
            checkParamMap();
            params.put(key, valuse);
        }
        return this;
    }

    //添加参数
    public HttpRequest addParam(String key, Object valuse) {
        addParamObject(key, valuse);
        return this;
    }

    //添加参数
    public HttpRequest addParam(String key, String valuse) {
        addParamObject(key, valuse);
        return this;
    }

    //添加参数
    public HttpRequest addParam(String key, int valuse) {
        addParamObject(key, valuse);
        return this;
    }

    //添加参数
    public HttpRequest addParam(String key, double valuse) {
        addParamObject(key, valuse);
        return this;
    }

    //添加头部
    public HttpRequest addHeader(String key, String valuse) {
        if (!isEmpty(key) && !isEmpty(valuse)) {
            if (headers == null) {
                newHeaderMap();
            }
            headers.put(key, valuse);
        }
        return this;
    }

    /**
     * 添加 公共参数
     *
     * @return
     */
    protected void addCommonParams() {
        if (OkHttpUtils.getInstance().isCommonParams()) {
            checkParamMap();
            Map<String, Object> pp = OkHttpUtils.getInstance().getCommonParams();
            if (pp != null && !pp.isEmpty() && params != null) {
                for (Map.Entry<String, Object> entry : pp.entrySet()) {
                    //如果已经有了就不添加，公共参数优先级低
                    if (!params.containsKey(entry.getKey())) {
                        addParam(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    /**
     * 添加map参数
     */
    public HttpRequest addParams(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            addParam(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 添加文件参数
     */
    public HttpRequest addParam(String key, File file) {
        return addParam(new FileInput(key, file));
    }

    /**
     * 添加文件参数
     */
    public HttpRequest addParam(FileInput param) {
        if (param.exists()) {
            if (files == null) {
                files = new ArrayList<>();
            }
            files.add(param);
        }
        return this;
    }

    /**
     * 添加文件参数
     */
    public HttpRequest addParamFilePath(String key, String filePath) {
        if (filePath == null) {
            return this;
        }
        addParam(key, new File(filePath));
        return this;
    }

    /**
     * 添加文件参数
     */
    public HttpRequest addParam(String key, List<File> files) {
        if (files == null || files.isEmpty()) {
            return this;
        }
        for (File f : files) {
            addParam(key, f);
        }
        return this;
    }

    /**
     * 添加文件参数
     */
    public HttpRequest addParamFilePath(String key, List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            return this;
        }
        for (String f : filePaths) {
            addParamFilePath(key, f);
        }
        return this;
    }

    /**
     * 设置直接提交得post
     * 不会添加公共参数
     */
    public HttpRequest setContent(String content) {
        postContent = content;
        return this;
    }

    /**
     * 设置缓存模式
     */
    public HttpRequest setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    /**
     * 设置缓存时间
     */
    public HttpRequest setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return this;
    }

    /**
     * 设置直接提交得post 并且是json
     */
    public HttpRequest setContentJson(String content) {
        //添加公共参数
        if (OkHttpUtils.getInstance().isCommonParams()) {
            try {
                Gson gson = new Gson();
                params = gson.fromJson(content, Map.class);
                addCommonParams();
                content = gson.toJson(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        postContent = content;
        isJson = true;
        setMethod("POST");
        return this;
    }

    /**
     * 方法功能：把键值对转换成json放到content里面,最后调用
     * 注意：在这个方法调用以后添加的参数将无效
     * 2：如果存在文件就不能用content提交
     */
    public HttpRequest toJson() {
        //添加公共参数
        addCommonParams();
        //转换成json
        if (params != null && !params.isEmpty() && !isHavafiles()) {
            postContent = GsonHelper.getGson().toJson(params);
            params.clear();
            isJson = true;
            setMethod("POST");
        }
        return this;
    }

    /**
     * 构建请求
     * 这里返回的对象才可以执行请求
     */
    public RequestCall buildCall() {
        return new RequestCall(this);
    }

    /**
     * 解析结果的gson
     *
     * @param gson
     * @return
     */
    public HttpRequest parseGson(Gson gson) {
        this.gson = gson;
        return this;
    }

    public Gson getParseGson() {
        return gson;
    }

    public Object getTag() {
        return tag;
    }

    public HttpRequest tag(Object tag) {
        this.tag = tag;
        return this;
    }

    /**
     * 方法功能：构建请求body    多表单数据  键值对
     */
    private void addParams(Map<String, Object> params, MultipartBody.Builder builder) {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                //Content-Disposition;form-data; name="aaa"
                builder.addFormDataPart(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
    }

    /**
     * 方法功能：构建请求body    表单数据  键值对
     */
    private void addParams(Map<String, Object> params, FormBody.Builder builder) {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
    }

    /**
     * 方法功能：构建请求body    多表单  文件数据
     */
    private void addFlieParams(MultipartBody.Builder builder) {//表单数据
        if (files != null && !files.isEmpty()) {
            for (FileInput fileInput : files) {
                builder.addFormDataPart(fileInput.key, fileInput.filename
                        , RequestBody.create(fileInput.contentType, fileInput.file));
            }
        }
    }


    public Uri getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, Object> getParams() {
        Map<String, Object> map = new HashMap<>();
        if (params != null) {
            map.putAll(params);
        }
        return map;
    }

    /**
     * 异步回调
     */
    @Override
    public <T> ExecuteCall execute(Callback<T> callback) {
        return buildCall().execute(callback);
    }

    /**
     * 同步执行
     */
    @Override
    public <ResultType> ResultType syncExecute(Type rawType, Type... typeArguments) throws HttpException {
        return buildCall().syncExecute(rawType, typeArguments);
    }

    /********************************************************************************************
     *                                           私有方法
     ********************************************************************************************/
    /**
     * 构建一个Request
     */
    protected Request bulidRequest(Callback callback, ProgressCallBack progressCallBack) {
        //

        Request.Builder builder = new Request.Builder();
        if (isEmpty(method)) {
            method = methods[0];
        }
        RequestBody requestBody = buildRequestBody(callback, progressCallBack);
        Headers.Builder header = new Headers.Builder();
        //添加公共请求头
        if (OkHttpUtils.getInstance().isCommonHeaders()) {
            for (Map.Entry<String, String> entry : OkHttpUtils.getInstance().getCommonHeaders().entrySet()) {
                header.add(entry.getKey(), entry.getValue());
            }
        }
        if (isHavaHeader()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                header.add(entry.getKey(), entry.getValue());
            }
        }
        request = builder
                .url(url.toString())
                .headers(header.build())
                .method(method, requestBody)
                .tag(tag)
                .build();
        return request;
    }

    /**
     * 构建请求body
     * postContent ->text/plain;charset=utf-8
     * 不存在文件application/x-www-form-urlencoded
     * 存在文件 multipart/form-data
     * 私有
     */
    protected RequestBody buildRequestBody(Callback callback, ProgressCallBack progressCallBack) {
        RequestBody body = null;
        onBuildRequestBody();
        //添加公共参数,只提交content的时候已经处理好了公共参数
        if (isEmpty(postContent)) {
            addCommonParams();
        }
        onBuildRequestBodyHasCommonParams();
        if (method.equals("GET")) {
            //get请求把参数放在url里面, 没有请求实体
            url = HttpUtils.createUrlFromParams(url, params);
            body = null;
        } else if (!isEmpty(postContent)) {
            //只提交content
            if (isJson) {
                body = ContentRequestBody.createNew(contentType == null ? MEDIA_TYPE_JSON :
                        contentType, postContent);
            } else {
                body = ContentRequestBody.createNew(contentType == null ? MEDIA_TYPE_PLAIN :
                        contentType, postContent);
            }
            //content方式是不能提交文件的
        } else {
            //存在文件用MultipartBody
            if (isHavafiles()) {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(contentType == null ? MultipartBody.FORM : contentType);

                addParams(params, builder);
                addFlieParams(builder);
                body = builder.build();
            } else {//不存在文件用 FormBody
                FormBody.Builder builder = new FormBody.Builder();
                addParams(params, builder);
                body = builder.build();
            }
        }
        //是否添加进度回调
        if (body != null && callback != null) {
            if (callback instanceof ProgressCallBack) {
                progressCallBack = (ProgressCallBack) callback;
            }
            if (progressCallBack != null) {
                body = new ProgressRequestBody(body, progressCallBack);
            }
        }
        return body;
    }


    /**
     * 是否正常的字符串
     * 私有
     *
     * @return
     */
    private boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }


    /**
     * 可以添加签名，在全部参数添加完毕后,如果调用toJson方法那么params没有值，content有值
     * 实现者可以继承从写
     * 这里是添加公共参数之前
     */
    public void onBuildRequestBody() {

    }

    /**
     * 可以添加签名，在全部参数添加完毕后,如果调用toJson方法那么params没有值，content有值
     * 实现者可以继承从写
     * 这里是添加公共参数之后
     */
    public void onBuildRequestBodyHasCommonParams() {

    }

    /**
     * 方便从写
     */
    public void checkParamMap() {
        if (params == null) {
            params = new TreeMap<>(this);
        }
    }

    /**
     * 方便从写
     */
    public void newHeaderMap() {
        headers = new HashMap<>();
    }

    //遍历参数的时候顺序,默认递增，结合 newParamMap 方法
    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }

    public Request getRequest() {
        return request;
    }
}
