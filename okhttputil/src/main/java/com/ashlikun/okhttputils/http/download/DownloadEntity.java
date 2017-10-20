package com.ashlikun.okhttputils.http.download;


import com.ashlikun.orm.LiteOrmUtil;
import com.ashlikun.orm.db.annotation.Column;
import com.ashlikun.orm.db.annotation.PrimaryKey;
import com.ashlikun.orm.db.annotation.Table;
import com.ashlikun.orm.db.enums.AssignType;

import java.util.List;


/**
 * Entity mapped to table "download".
 */
@Table("DownloadEntity")
public class DownloadEntity {
    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("downloadId")
    private String downloadId;
    private Long totalSize;//总大小
    private Long completedSize;//完成大小
    private String url;
    private String saveDirPath;
    private String fileName;
    private Integer downloadStatus;

    public DownloadEntity() {
    }

    public DownloadEntity(String downloadId) {
        this.downloadId = downloadId;
    }

    public DownloadEntity(String downloadId, Long totalSize, Long completedSize, String url, String saveDirPath, String fileName, Integer downloadStatus) {
        this.downloadId = downloadId;
        this.totalSize = totalSize;
        this.completedSize = completedSize;
        this.url = url;
        this.saveDirPath = saveDirPath;
        this.fileName = fileName;
        this.downloadStatus = downloadStatus;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Long getCompletedSize() {
        return completedSize;
    }

    public void setCompletedSize(Long completedSize) {
        this.completedSize = completedSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSaveDirPath() {
        return saveDirPath;
    }

    public void setSaveDirPath(String saveDirPath) {
        this.saveDirPath = saveDirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(Integer downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public static DownloadEntity queryById(String id) {
        return LiteOrmUtil.get().queryById(id, DownloadEntity.class);
    }

    public static List<DownloadEntity> queryAll() {
        return LiteOrmUtil.get().query(DownloadEntity.class);
    }

    public void save() {
        LiteOrmUtil.get().save(this);
    }

    public static void update(DownloadEntity entity) {
        LiteOrmUtil.get().update(entity);
    }

    public static void delete(DownloadEntity entity) {
        LiteOrmUtil.get().delete(entity);
    }
}
