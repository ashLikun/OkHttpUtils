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
package com.ashlikun.okhttputils.http.convert;

import android.os.Environment;
import android.text.TextUtils;

import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.IOUtils;
import com.ashlikun.okhttputils.http.callback.ProgressCallBack;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.functions.Consumer;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：字符串的转换器
 * 修订历史：
 * ================================================
 */
public class FileConvert implements Converter<File> {
    //下载目标文件夹
    public static final String DM_TARGET_FOLDER = File.separator + "download" + File.separator;
    //目标文件存储的文件夹路径
    private String folder;
    //目标文件存储的文件名
    private String fileName;
    ProgressCallBack callBack;

    public FileConvert() {
        this(null);
    }

    public FileConvert(String fileName) {
        this(Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER, fileName);
    }

    public FileConvert(String folder, String fileName) {
        this.folder = folder;
        this.fileName = fileName;
    }


    @Override
    public File convertResponse(Response response, Gson gosn) throws Exception {
        String url = response.request().url().toString();
        if (TextUtils.isEmpty(folder)) {
            folder = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = HttpUtils.getNetFileName(response, url);
        }

        File dir = new File(folder);
        IOUtils.createFolder(dir);
        File file = new File(dir, fileName);
        IOUtils.delFileOrFolder(file);

        InputStream bodyStream = null;
        byte[] buffer = new byte[2 * 1024];
        FileOutputStream fileOutputStream = null;
        try {
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            long totalSize = body.contentLength();
            long completedSize = 0;
            bodyStream = body.byteStream();
            int len;
            fileOutputStream = new FileOutputStream(file);
            //上一次的时间
            long timeOld = System.currentTimeMillis();
            while ((len = bodyStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
                completedSize += len;
                //计算是否要回调
                if (Math.abs(System.currentTimeMillis() - timeOld) > callBack.getRate()) {
                    timeOld = System.currentTimeMillis();
                    //回调
                    onProgress(completedSize, totalSize, completedSize == totalSize);
                }
            }
            // 防止最后一次不足rate时间，导致percent无法达到100%
            onProgress(completedSize, totalSize, completedSize == totalSize);
            fileOutputStream.flush();
            return file;
        } finally {
            IOUtils.closeQuietly(bodyStream);
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

    private void onProgress(final long progress, final long total, final boolean done) {
        HttpUtils.runmainThread(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                callBack.onLoading(progress, total, done, false);
            }
        });
    }

    public void progressCallback(ProgressCallBack callBack) {
        this.callBack = callBack;
    }
}
