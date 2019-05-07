package com.ashlikun.okhttputils.http.request;


import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/5/7　17:03
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：data方式的提交，如json
 */
public class ContentRequestBody extends RequestBody {
    private MediaType mediaType;
    private final byte[] content;
    private final int byteCount;

    private ContentRequestBody(MediaType mediaType, byte[] content) {
        this.mediaType = mediaType;
        this.content = content;
        this.byteCount = content.length;
    }

    public String getContent() {
        return new String(content, mediaType.charset());
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public long contentLength() throws IOException {
        return byteCount;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.write(content, 0, byteCount);
    }

    public static RequestBody create(MediaType contentType, String content) {
        Charset charset = Util.UTF_8;
        if (contentType != null) {
            charset = contentType.charset();
            if (charset == null) {
                charset = Util.UTF_8;
                contentType = MediaType.parse(contentType + "; charset=utf-8");
            }
        }
        byte[] bytes = content.getBytes(charset);
        return new ContentRequestBody(contentType, bytes);
    }
}
