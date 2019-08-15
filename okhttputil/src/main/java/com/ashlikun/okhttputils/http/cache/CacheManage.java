package com.ashlikun.okhttputils.http.cache;

import com.ashlikun.okhttputils.http.HttpUtils;
import com.ashlikun.okhttputils.http.request.HttpRequest;
import com.ashlikun.orm.LiteOrmUtil;
import com.ashlikun.orm.db.assit.WhereBuilder;

import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/11 0011　下午 4:06
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：缓存管理器
 */
public class CacheManage {
    public static boolean update(CacheEntity entity) {
        try {
            LiteOrmUtil.get().update(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean delete(CacheEntity entity) {
        try {
            LiteOrmUtil.get().delete(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean delete(String key) {
        try {
            LiteOrmUtil.get().delete(new WhereBuilder(CacheEntity.class).where("key=?", key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean delete(HttpRequest request) {
        return delete(getCacheKey(request));
    }

    public static void deleteAll() {
        try {
            LiteOrmUtil.get().delete(CacheEntity.class);
        } catch (Exception e) {

        }
    }

    public static CacheEntity queryById(String id) {
        return LiteOrmUtil.get().queryById(id, CacheEntity.class);
    }

    public static CacheEntity queryById(HttpRequest request) {
        return LiteOrmUtil.get().queryById(getCacheKey(request), CacheEntity.class);
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
