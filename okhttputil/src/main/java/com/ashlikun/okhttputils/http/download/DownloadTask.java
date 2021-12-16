/*
 * Copyright 2014-2016 wjokhttp
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
package com.ashlikun.okhttputils.http.download;

import static com.ashlikun.okhttputils.http.download.DownloadManager.defaultFilePath;

import android.text.TextUtils;

import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * 下载线程
 */
public class DownloadTask implements Runnable {
    private static final int BUFFER_SIZE = 1024 * 16;
    private static String FILE_MODE = "rwd";
    private OkHttpClient mClient;

    private RandomAccessFile mDownLoadFile;
    private DownloadEntity dbEntity;
    private DownloadTaskListener mListener;
    // 任务id，断点下载
    private String id;
    // 总大小
    private long totalSize;
    //  已经下载的大小
    private long completedSize;
    // 下载url
    private String url;
    // 文件保存路径
    private String saveDirPath;
    // 文件保存的名称
    private String fileName;
    //下载状态
    private int downloadStatus;
    //错误码
    private int errorCode;
    //下载多少回调一次  默认200，单位毫秒;
    private long rate;
    //是否取消
    private boolean isCancel = false;

    private DownloadTask(Builder builder) {
        mClient = new OkHttpClient();
        this.id = builder.id;
        this.url = builder.url;
        this.saveDirPath = builder.saveDirPath;
        this.fileName = builder.fileName;
        this.downloadStatus = builder.downloadStatus;
        this.mListener = builder.listener;
        this.rate = builder.rate;
        if (this.rate <= 0) {
            this.rate = DownloadManager.DEFAULT_RATE;
        }
    }

    private boolean isDownloadFinish() {
        boolean finish = false;
        if (totalSize > 0 && completedSize > 0 && totalSize == completedSize) {
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
            finish = true;
        }
        return finish;
    }

    /**
     * 删除数据库文件和已经下载的文件
     */
    protected void cancel() {
        totalSize = 0;
        mListener.onCancel(DownloadTask.this);
        if (dbEntity != null) {
            DownloadEntity.delete(dbEntity);
            File temp = new File(getFilePath());
            if (temp.exists()) {
                temp.delete();
            }
        }
    }

    /**
     * 分发回调事件到ui层
     */
    private void onCallBack(int downloadStatus) {
        HttpUtils.launchMain(new Runnable() {
            @Override
            public void run() {
                switch (downloadStatus) {
                    // 下载失败
                    case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                        mListener.onError(DownloadTask.this, errorCode);
                        break;
                    // 正在下载
                    case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                        mListener.onDownloading(DownloadTask.this, completedSize, totalSize, getDownLoadPercent());
                        break;
                    // 取消
                    case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                        cancel();
                        break;
                    // 完成
                    case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                        mListener.onDownloadSuccess(DownloadTask.this, new File(getFilePath()));
                        break;
                    // 停止
                    case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                        mListener.onPause(DownloadTask.this, completedSize, totalSize, getDownLoadPercent());
                        break;
                }
            }
        });
        // 同步manager中的task信息
        DownloadManager.getInstance().updateDownloadTask(this);
    }


    private double getDownLoadPercent() {
        double baiy = completedSize * 1.0;
        double baiz = totalSize * 1.0;

        // 防止分母为0出现NoN
        if (baiz > 0) {
            double fen = ((baiy / baiz) * 100);
            return fen > 100 ? 100 : fen;
        } else {
            return 0;
        }
    }

    private String getFilePath() {
        // 获得文件名
        if (TextUtils.isEmpty(fileName)) {
            fileName = HttpUtils.getNetFileName(null, url);
        }
        if (TextUtils.isEmpty(saveDirPath)) {
            saveDirPath = defaultFilePath;
        }
        if (!saveDirPath.endsWith("/")) {
            saveDirPath = saveDirPath + "/";
        }
        IOUtils.createFolder(saveDirPath);
        String filepath = saveDirPath + fileName;
        IOUtils.createNewFile(filepath);
        return filepath;
    }


    public void setClient(OkHttpClient mClient) {
        this.mClient = mClient;
    }


    public String getId() {
        if (!TextUtils.isEmpty(id)) {
        } else {
            id = url;
        }
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getSaveDirPath() {
        return saveDirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }


    public void onComplete() {
        //下载完成取消订阅
        if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
            isCancel = true;
        }
    }

    public void setCancel() {
        setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        isCancel = true;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            // 数据库中加载数据
            dbEntity = DownloadEntity.queryById(id);
            if (dbEntity != null) {
                completedSize = dbEntity.getCompletedSize();
                totalSize = dbEntity.getTotalSize();
            }

            // 获得文件路径
            String filepath = getFilePath();
            // 获得下载保存文件
            mDownLoadFile = new RandomAccessFile(filepath, FILE_MODE);

            //调节已完成的长度与文件一致
            long fileLength = mDownLoadFile.length();
            if (fileLength < completedSize) {
                completedSize = mDownLoadFile.length();
            }
            // 下载完成，更新数据库数据
            if (fileLength != 0 && totalSize <= fileLength) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
                totalSize = completedSize = fileLength;
                dbEntity = new DownloadEntity(id, totalSize, totalSize, url, saveDirPath, fileName, downloadStatus);
                dbEntity.save();
                // 执行finally中的回调
                return;
            }

            // 开始下载
            Request request = new Request.Builder().url(url).header("RANGE",
                    "bytes=" + completedSize + "-")
                    .build();

            // 文件跳转到指定位置开始写入
            mDownLoadFile.seek(completedSize);
            Response response = mClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            //设置文件名
            if (TextUtils.isEmpty(fileName)) {
                fileName = HttpUtils.getNetFileName(response, url);
            }
            if (responseBody != null) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING;
                if (totalSize < completedSize + responseBody.contentLength()) {
                    totalSize = completedSize + responseBody.contentLength();
                    if (dbEntity != null) {
                        dbEntity.setTotalSize(totalSize);
                    }
                }
                // 获得文件流
                inputStream = responseBody.byteStream();
                bis = new BufferedInputStream(inputStream, BUFFER_SIZE);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length = 0;
                // 开始下载数据库中插入下载信息
                if (dbEntity == null) {
                    dbEntity = new DownloadEntity(id, totalSize, 0L, url, saveDirPath, fileName, downloadStatus);
                    dbEntity.save();
                }
                //上一次的时间
                long timeOld = System.currentTimeMillis();
                while ((length = bis.read(buffer)) > 0 && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_CANCEL
                        && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                    mDownLoadFile.write(buffer, 0, length);
                    completedSize += length;
                    //计算是否要回调
                    if (Math.abs(System.currentTimeMillis() - timeOld) > rate) {
                        timeOld = System.currentTimeMillis();
                        //写入数据库
                        dbEntity.setCompletedSize(completedSize);
                        DownloadEntity.update(dbEntity);
                        //回调
                        onCallBack(downloadStatus);
                    }
                    if (isCancel) {
                        break;
                    }
                }
                // 防止最后一次不足rate时间，导致percent无法达到100%
                onCallBack(downloadStatus);
            }
        } catch (FileNotFoundException e) {
            // file not found
            e.printStackTrace();
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            errorCode = DownloadStatus.DOWNLOAD_ERROR_FILE_NOT_FOUND;
        } catch (IOException e) {
            // io exception
            e.printStackTrace();
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            errorCode = DownloadStatus.DOWNLOAD_ERROR_IO_ERROR;
        } finally {
            if (!isCancel) {
                if (isDownloadFinish()) {
                    onCallBack(downloadStatus);
                }
                // 下载后新数据库
                if (dbEntity != null) {
                    dbEntity.setCompletedSize(completedSize);
                    dbEntity.setDownloadStatus(downloadStatus);
                    DownloadEntity.update(dbEntity);
                }
            }
            // 回收资源
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(mDownLoadFile);
        }
    }

    /**
     * 是否正在下载
     */
    public boolean isDownloading() {
        return getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING;
    }

    /**
     * 是否下载完成
     */
    public boolean isCompleted() {
        return getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
    }

    public static class Builder {
        private String id;// task id
        private String url;// file url
        private String saveDirPath;// 保存的路径,默认时myApp的sdPath
        private String fileName; // 保存的文件名
        private int downloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;
        private long rate;//下载多少回调一次  默认200，单位毫秒;

        private DownloadTaskListener listener;

        /**
         * 作为下载task开始、删除、停止的key值，如果为空则默认是url
         *
         * @param id
         * @return
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * 下载url（not null）
         *
         * @param url
         * @return
         */
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置保存地址
         *
         * @param saveDirPath
         * @return
         */
        public Builder setSaveDirPath(String saveDirPath) {
            this.saveDirPath = saveDirPath;
            return this;
        }

        /**
         * 设置下载状态
         *
         * @param downloadStatus
         * @return
         */
        public Builder setDownloadStatus(int downloadStatus) {
            this.downloadStatus = downloadStatus;
            return this;
        }

        /**
         * 设置文件名
         *
         * @param fileName
         * @return
         */
        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 设置回调时间   毫秒
         *
         * @param rate
         * @return
         */
        public Builder setRate(long rate) {
            this.rate = rate;
            return this;
        }

        /**
         * 设置下载回调
         *
         * @param listener
         * @return
         */
        public Builder setListener(DownloadTaskListener listener) {
            this.listener = listener;
            return this;
        }

        public DownloadTask build() {
            return new DownloadTask(this);
        }


    }

}
