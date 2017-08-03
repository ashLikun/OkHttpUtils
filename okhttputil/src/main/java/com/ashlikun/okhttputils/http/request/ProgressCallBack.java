package com.ashlikun.okhttputils.http.request;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/5/23 10:29
 * <p>
 * 方法功能：进度回调
 */
public interface ProgressCallBack {


    /**
     * @param progress 已经下载或上传字节数
     * @param total    总字节数
     * @param done     是否完成
     * @param isUpdate 是否是上传
     */
    void onLoading(long progress, long total, boolean done, boolean isUpdate);

    /**
     * @return
     */
    long getRate();

    /**
     * @param rate
     */
    void setRate(long rate);


}
