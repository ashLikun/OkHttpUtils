package com.ashlikun.okhttputils.http.download

import com.ashlikun.orm.db.annotation.PrimaryKey
import com.ashlikun.orm.db.enums.AssignType
import com.ashlikun.orm.db.annotation.Column
import com.ashlikun.orm.LiteOrmUtil
import com.ashlikun.okhttputils.http.download.DownloadEntity
import com.ashlikun.orm.db.annotation.Table

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 17:34
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：下载实体
 */

@Table("DownloadEntity")
class DownloadEntity(
    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("downloadId")
    var downloadId: String = "",
    var totalSize: Long = 0,
    var completedSize: Long = 0,
    var url: String = "",
    var saveDirPath: String = "",
    var fileName: String = "",
    var downloadStatus: Int = DownloadStatus.DOWNLOAD_STATUS_INIT
) {
    fun save() {
        LiteOrmUtil.get().save(this)
    }

    companion object {
        fun queryById(id: String): DownloadEntity {
            return LiteOrmUtil.get().queryById(id, DownloadEntity::class.java)
        }

        fun queryAll(): List<DownloadEntity> {
            return LiteOrmUtil.get().query(DownloadEntity::class.java)
        }

        fun update(entity: DownloadEntity) {
            LiteOrmUtil.get().update(entity)
        }

        fun delete(entity: DownloadEntity) {
            LiteOrmUtil.get().delete(entity)
        }
    }
}