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
package com.ashlikun.okhttputils.http.cookie;

import android.content.ContentValues;
import android.database.Cursor;

import com.ashlikun.orm.LiteOrmUtil;
import com.ashlikun.orm.db.assit.QueryBuilder;
import com.ashlikun.orm.db.assit.WhereBuilder;

import java.util.List;

/**
 * @author　　: 李坤
 * 创建时间: 2019/2/27 11:26
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public class CookieManager {

    private volatile static CookieManager instance;

    public static CookieManager getInstance() {
        if (instance == null) {
            synchronized (CookieManager.class) {
                if (instance == null) {
                    instance = new CookieManager();
                }
            }
        }
        return instance;
    }

    private CookieManager() {
    }


    public List<SerializableCookie> queryAll() {
        return LiteOrmUtil.get().query(SerializableCookie.class);
    }

    public SerializableCookie parseCursorToBean(Cursor cursor) {
        return SerializableCookie.parseCursorToBean(cursor);
    }

    public ContentValues getContentValues(SerializableCookie serializableCookie) {
        return SerializableCookie.getContentValues(serializableCookie);
    }

    /**
     * 按条件查询对象并返回集合
     */
    public List<SerializableCookie> query(String selection, String[] selectionArgs) {
        return LiteOrmUtil.get().query(QueryBuilder.create(SerializableCookie.class).where(selection, (Object[]) selectionArgs));
    }

    public void delete(String whereClause, String[] whereArgs) {
        LiteOrmUtil.get().delete(WhereBuilder.create(SerializableCookie.class).where(whereClause, (Object[]) whereArgs));
    }

    public void deleteAll() {
        LiteOrmUtil.get().deleteAll(SerializableCookie.class);
    }
}
