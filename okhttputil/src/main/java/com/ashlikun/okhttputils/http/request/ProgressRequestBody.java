package com.ashlikun.okhttputils.http.request;

import com.ashlikun.okhttputils.http.callback.ProgressCallBack;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by likun on 2016/8/3.
 * 上传进度封装
 */

public class ProgressRequestBody extends RequestBody implements Observer<Long> {
    //实际的待包装请求体
    private final RequestBody requestBody;
    //进度回调接口
    private final ProgressCallBack progressCallBack;
    //包装完成的BufferedSink
    private BufferedSink bufferedSink;
    //当前写入字节数
    long bytesWritten = 0L;
    //总字节长度，避免多次调用contentLength()方法
    long contentLength = 0L;
    Disposable disposable;

    /**
     * 构造函数，赋值
     *
     * @param requestBody      待包装的请求体
     * @param progressCallBack 回调接口
     */
    public ProgressRequestBody(RequestBody requestBody, ProgressCallBack progressCallBack) {
        this.requestBody = requestBody;
        this.progressCallBack = progressCallBack;
    }

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    /**
     * 重写进行写入
     *
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();

    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        if (progressCallBack != null && (disposable == null || !disposable.isDisposed())) {
            Observable.interval(progressCallBack.getRate(), progressCallBack.getRate(), TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);

        }

        return new ForwardingSink(sink) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
            }
        };
    }


    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(Long aLong) {
        if (bytesWritten == contentLength) {
            disposable.dispose();
            disposable = null;
        }
        progressCallBack.onLoading(bytesWritten, contentLength, bytesWritten == contentLength, true);
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
