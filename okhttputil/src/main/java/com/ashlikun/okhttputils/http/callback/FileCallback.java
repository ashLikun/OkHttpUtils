/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ashlikun.okhttputils.http.callback;

import com.ashlikun.okhttputils.http.convert.FileConvert;
import com.ashlikun.okhttputils.http.download.DownloadManager;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.Response;

/**
 * @author　　: 李坤
 * 创建时间: 2020/4/16 11:29
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：文件的回调下载进度监听
 */

public abstract class FileCallback extends AbsCallback<File> implements ProgressCallBack {

    private FileConvert convert;    //文件转换类

    public FileCallback() {
        this(null);
    }

    public FileCallback(String destFileName) {
        this(null, destFileName);
    }

    public FileCallback(String destFileDir, String destFileName) {
        convert = new FileConvert(destFileDir, destFileName);
        convert.progressCallback(this);
    }

    @Override
    public long getRate() {
        return DownloadManager.DEFAULT_RATE;
    }

    @Override
    public File convertResponse(Response response, Gson gosn) throws Exception {
        File file = convert.convertResponse(response, gosn);
        response.close();
        return file;
    }
}
