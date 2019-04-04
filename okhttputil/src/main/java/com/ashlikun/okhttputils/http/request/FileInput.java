package com.ashlikun.okhttputils.http.request;

import com.ashlikun.okhttputils.http.HttpUtils;

import java.io.File;

import okhttp3.MediaType;

/**
 * 作者　　: 李坤
 * 创建时间:2017/3/17　15:56
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public class FileInput {
    public String key;
    public String filename;
    public MediaType contentType;
    public File file;

    public FileInput(String key, String filename, File file) {
        this.key = key;
        this.filename = filename;
        this.file = file;
        if (exists()) {
            contentType = MediaType.parse(HttpUtils.getMimeType(file.getAbsolutePath()));
        }
    }

    public FileInput(String key, File file) {
        this.key = key;
        this.file = file;
        if (exists()) {
            this.filename = file.getName();
            contentType = MediaType.parse(HttpUtils.getMimeType(file.getAbsolutePath()));
        }

    }

    public boolean exists() {
        try {
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String toString() {
        return "FileInput{" +
                "key='" + key + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", file=" + file +
                '}';
    }

}
