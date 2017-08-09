package com.ashlikun.okhttputils.http.request;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ashlikun.okhttputils.http.Callback;
import com.ashlikun.okhttputils.json.GsonHelper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
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
 */

public class RequestParam implements Comparator<String> {
    protected Uri url;//请求地址
    private String method;//请求方法
    protected Map<String, String> headers;//请求头

    protected Map<String, String> params;//普通键值对参数  get
    private String postContent;//请求内容，如果设置这个参数  其他的参数将不会提交  post
    private List<FileInput> files;//上传文件

    private boolean isJson = false;

    public RequestParam(String url) {
        url(url);
    }

    /* valid HTTP methods */
    private static final String[] methods = {
            "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"
    };

    public void appendPath(String path) {
        if (url == null) new Exception("先调用url方法");
        Uri.Builder builder = url.buildUpon();
        if (!TextUtils.isEmpty(path)) {
            url = builder.appendPath(path).build();
        }
    }

    public void setMethod(String method) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equals(method)) {
                this.method = method;
                return;
            }
        }
        Log.e("setMethod", "请求方法错误" + method);
        this.method = methods[0];
    }

    public void post() {
        setMethod("POST");
    }

    public void get() {
        setMethod("GET");
    }

    public void url(String url) {
        this.url = Uri.parse(url);
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

    public void addParam(String key, String valuse) {
        if (!isEmpty(key) && !isEmpty(valuse)) {
            if (params == null) newParamMap();
            params.put(key, valuse);
        }
    }

    public void addHeader(String key, String valuse) {
        if (!isEmpty(key) && !isEmpty(valuse)) {
            if (headers == null) newHeaderMap();
            headers.put(key, valuse);
        }
    }

    public void addParam(String key, int valuse) {
        addParam(key, String.valueOf(valuse));
    }

    public void addParam(String key, double valuse) {
        addParam(key, String.valueOf(valuse));
    }

    public void addParam(String key, File file) {
        FileInput param = new FileInput(key, file);
        if (param.exists()) {
            if (files == null) files = new ArrayList<>();
            files.add(param);
        }
    }

    public void addParamFile(String key, String filePath) {
        if (filePath == null) return;
        addParam(key, new File(filePath));
    }

    public void addParam(String key, List<File> files) {
        if (files == null || files.isEmpty()) return;
        for (File f : files) {
            addParam(key, f);
        }
    }

    public void addParamFile(String key, List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) return;
        for (String f : filePaths) {
            addParamFile(key, f);
        }
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/4/12 0012 17:22
     * <p>
     * 方法功能：把键值对转换成json放到content里面,最后调用
     * 注意：在这个方法调用以后添加的参数将无效
     */

    public void toJson() {
        if (params != null && !params.isEmpty()) {
            postContent = GsonHelper.getGson().toJson(params);
            params.clear();
            isJson = true;
            post();
        }
    }

    public void setContent(String content) {
        postContent = content;
    }

    //构建一个Request
    public Request bulidRequest(Callback callback) {
        Request.Builder builder = new Request.Builder();
        if (isEmpty(method))
            method = methods[0];
        RequestBody requestBody = buildRequestBody(callback);
        Headers.Builder header = new Headers.Builder();
        if (isHavaHeader()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                header.add(entry.getKey(), entry.getValue());
            }
        }
        String urlStr = url.toString().replaceAll("//", "/");
        return builder
                .url(urlStr)
                .headers(header.build())
                .method(method, requestBody)
                .build();
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/4/12 0012 17:31
     * <p>
     * 方法功能：构建请求body
     * postContent ->text/plain;charset=utf-8
     * 不存在文件application/x-www-form-urlencoded
     * 存在文件 multipart/form-data
     */

    public RequestBody buildRequestBody(Callback callback) {
        RequestBody body = null;
        addSign();
        if (method.equals("GET")) {
            //get请求把参数放在url里面, 没有请求实体
            url = appendQueryParams(url, params);
            body = null;
        } else if (!isEmpty(postContent)) {//只提交content
            if (isJson) {
                body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), postContent);
            } else {
                body = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), postContent);
            }
        } else {

            if (isHavafiles()) {//存在文件用MultipartBody
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                addParams(builder);
                addFlieParams(builder);
                body = builder.build();
            } else {//不存在文件用 FormBody
                FormBody.Builder builder = new FormBody.Builder();
                addParams(builder);
                body = builder.build();
            }
        }
        //是否添加进度回调
        if (body != null && callback != null && callback instanceof ProgressCallBack) {
            body = new ProgressRequestBody(body, (ProgressCallBack) callback);
        }
        return body;
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/3/21 10:00
     * <p>
     * 方法功能：get请求构建url
     */

    private Uri appendQueryParams(Uri url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = url.buildUpon();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 是否正常的字符串
     *
     * @return
     */
    public boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }


    /**
     * 作者　　: 李坤
     * 创建时间: 2017/3/21 9:55
     * <p>
     * 方法功能：构建请求body    多表单数据  键值对
     */
    private void addParams(MultipartBody.Builder builder) {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());//Content-Disposition;form-data; name="aaa"
            }
        }
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/3/21 9:55
     * <p>
     * 方法功能：构建请求body    表单数据  键值对
     */
    private void addParams(FormBody.Builder builder) {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addEncoded(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/3/21 9:55
     * <p>
     * 方法功能：构建请求body    多表单  文件数据
     */
    private void addFlieParams(MultipartBody.Builder builder) {//表单数据
        if (files != null && !files.isEmpty()) {
            for (FileInput fileInput : files) {
                builder.addFormDataPart(fileInput.key, fileInput.filename
                        , RequestBody.create(MediaType.parse(getMimeType(fileInput.file.getAbsolutePath())), fileInput.file));
            }
        }
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/3/21 9:56
     * <p>
     * 方法功能：获取文件的mime类型  Content-type
     */
    private String getMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        try {
            return fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "application/octet-stream";
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/8/8 9:45
     * 邮箱　　：496546144@qq.com
     * 方法功能：添加签名，在全部参数添加完毕后,如果调用toJson方法那么params没有值，content有值
     * 实现者可以继承从写
     */
    public void addSign() {

    }

    public void newParamMap() {
        params = new TreeMap<>(this);
    }

    public void newHeaderMap() {
        headers = new HashMap<>();
    }

    //遍历参数的时候顺序,默认递增，结合 newParamMap 方法
    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}
