package com.ashlikun.okhttputils.http.cache;

import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.request.HttpRequest;
import com.ashlikun.orm.LiteOrmUtil;

import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/11 0011　下午 4:06
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：缓存管理器
 */
public class CacheManage {
    public static void update(CacheEntity entity) {
        LiteOrmUtil.get().update(entity);
    }

    public static void delete(CacheEntity entity) {
        LiteOrmUtil.get().delete(entity);
    }

    public static void deleteAll() {
        LiteOrmUtil.get().delete(CacheEntity.class);
    }

    public static CacheEntity queryById(String id) {
        return LiteOrmUtil.get().queryById(id, CacheEntity.class);
    }

    public static List<CacheEntity> queryAll() {
        return LiteOrmUtil.get().query(CacheEntity.class);
    }
    public static String getCacheKey(HttpRequest request) {
        if (request == null) {
            return null;
        }
        if (request.getMethod().equals("GET")) {
            return request.getUrl().toString();
        } else {
            return HttpUtils.createUrlFromParams(request.getUrl(), request.getParams()).toString();
        }
    }
}
