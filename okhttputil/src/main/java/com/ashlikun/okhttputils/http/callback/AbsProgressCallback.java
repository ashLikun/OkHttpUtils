package com.ashlikun.okhttputils.http.callback;

import com.ashlikun.okhttputils.http.download.DownloadManager;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 4:31
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class AbsProgressCallback implements ProgressCallBack {

    @Override
    public void onLoading(long progress, long total, boolean done, boolean isUpdate) {

    }

    @Override
    public long getRate() {
        return DownloadManager.DEFAULT_RATE;
    }
}
