package com.ashlikun.okhttputils.http.cookie

import android.content.ContentValues
import android.database.Cursor
import com.ashlikun.orm.LiteOrmUtil
import com.ashlikun.orm.db.assit.QueryBuilder
import com.ashlikun.orm.db.assit.WhereBuilder

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 14:58
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：Cookie 管理器
 * .cookieJar(CookieJarImpl(DBCookieStore()))
 */

class CookieManager private constructor() {
    fun queryAll(): List<SerializableCookie> {
        return LiteOrmUtil.get().query(SerializableCookie::class.java)
    }

    fun parseCursorToBean(cursor: Cursor): SerializableCookie? {
        return SerializableCookie.parseCursorToBean(cursor)
    }

    fun getContentValues(serializableCookie: SerializableCookie): ContentValues {
        return SerializableCookie.getContentValues(serializableCookie)
    }

    /**
     * 按条件查询对象并返回集合
     */
    fun query(selection: String, selectionArgs: Array<String>): List<SerializableCookie> {
        return LiteOrmUtil.get().query(
            QueryBuilder.create(SerializableCookie::class.java)
                .where(selection, *selectionArgs)
        )
    }

    fun delete(whereClause: String, whereArgs: Array<String>) {
        LiteOrmUtil.get().delete(
            WhereBuilder.create(SerializableCookie::class.java)
                .where(whereClause, *whereArgs)
        )
    }

    fun deleteAll() {
        LiteOrmUtil.get().deleteAll(SerializableCookie::class.java)
    }

    companion object {
        private val instance by lazy { CookieManager() }
        fun get(): CookieManager = instance
    }
}