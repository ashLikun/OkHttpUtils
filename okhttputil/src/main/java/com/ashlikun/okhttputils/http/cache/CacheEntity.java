package com.ashlikun.okhttputils.http.cache;

import com.ashlikun.okhttputils.http.request.HttpRequest;
import com.ashlikun.okhttputils.http.response.HttpResponse;
import com.ashlikun.orm.LiteOrmUtil;
import com.ashlikun.orm.db.annotation.PrimaryKey;
import com.ashlikun.orm.db.annotation.Table;
import com.ashlikun.orm.db.enums.AssignType;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/11 0011　上午 11:05
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：缓存的实体类,只保存数据的String
 */
@Table("HttpCacheEntity")
public class CacheEntity {
    /**
     * 缓存永不过期
     */
    public static final long CACHE_NEVER_EXPIRE = -1;
    @PrimaryKey(AssignType.BY_MYSELF)
    /**缓存的key**/
    public String key;
    /**
     * 缓存的时间
     */
    public long cacheTime;
    /**
     * 缓存的头部json
     */
    public String head;
    /**
     * 缓存的实体数据
     */
    public String result;
    public int code;
    public String message;

    public CacheEntity() {

    }

    /**
     * 根据返回值，取String,只缓存这些
     * 或者实现tostring方法
     *
     * @param resultType
     * @param <ResultType>
     * @return
     */
    public static <ResultType> String getHanderResult(ResultType resultType) {
        if (resultType == null) {
            return null;
        }
        if (resultType instanceof HttpResponse) {
            return ((HttpResponse) resultType).json;
        } else {
            return resultType.toString();
        }
    }

    public static CacheEntity createCacheEntity(HttpRequest request, Response response, String result) {
        CacheEntity cacheEntity = new CacheEntity();
        cacheEntity.key = CacheManage.getCacheKey(request);
        cacheEntity.cacheTime = System.currentTimeMillis();
        Map<String, String> headMap = new HashMap<>();
        for (String headerName : response.headers().names()) {
            headMap.put(headerName, response.headers().get(headerName));
        }
        cacheEntity.head = new Gson().toJson(headMap);
        cacheEntity.result = result;
        cacheEntity.code = response.code();
        cacheEntity.message = response.message();
        return cacheEntity;
    }


    public void save() {
        LiteOrmUtil.get().save(this);
    }
}
