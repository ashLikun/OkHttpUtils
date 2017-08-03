package com.ashlikun.okhttputils.http.request;

import java.io.File;

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
    public File file;

    public FileInput(String key, String filename, File file) {
        this.key = key;
        this.filename = filename;
        this.file = file;
    }

    public FileInput(String key, File file) {
        this.key = key;
        this.file = file;
        if (exists()) {
            this.filename = file.getName();
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
                ", file=" + file +
                '}';
    }
}
