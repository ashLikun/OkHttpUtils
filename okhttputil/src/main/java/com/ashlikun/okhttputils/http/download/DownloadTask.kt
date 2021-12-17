package com.ashlikun.okhttputils.http.download

import android.text.TextUtils
import com.ashlikun.okhttputils.http.HttpUtils.getNetFileName
import com.ashlikun.okhttputils.http.HttpUtils.launchMain
import com.ashlikun.okhttputils.http.IOUtils.closeQuietly
import com.ashlikun.okhttputils.http.IOUtils.createFolder
import com.ashlikun.okhttputils.http.IOUtils.createNewFile
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 14:05
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：下载线程
 */

class DownloadTask(
    // 任务id，断点下载
    var id: String = "",
    // 下载url
    var url: String = "",
    //下载监听
    var listener: DownloadTaskListener? = null
) {
    companion object {
        //这个大小影响下载的速度
        private const val BUFFER_SIZE = 1024 * 16
        private const val FILE_MODE = "rwd"
    }

    lateinit var client: OkHttpClient

    // 文件保存路径
    var saveDirPath: String = DownloadManager.defaultFilePath
        set(value) = if (!value.endsWith("/")) field = "$value/" else field = value

    //文件保存的名称,如果正在下载就会被替换掉
    var fileName: String = ""

    //下载状态
    var downloadStatus: Int = DownloadStatus.DOWNLOAD_STATUS_INIT

    //下载多少回调一次  默认200，单位毫秒;
    var rate: Long = DownloadManager.DEFAULT_RATE

    // 总大小
    var totalSize: Long = 0

    //  已经下载的大小
    var completedSize: Long = 0


    //错误码
    var errorCode = 0


    //是否取消
    var isCancel = false


    //是否下载结束
    val isDownloadFinish: Boolean
        get() {
            if (totalSize > 0 && completedSize > 0 && totalSize == completedSize) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED
                return true
            }
            return false
        }

    // 获得文件名
    val filePath: String
        get() {
            if (fileName.isEmpty() && dbEntity != null) {
                fileName = dbEntity!!.fileName
            }
            createFolder(saveDirPath)
            val filepath = saveDirPath + fileName
            createNewFile(filepath)
            return filepath
        }

    private var dbEntity: DownloadEntity? = null

    /**
     * 是否正在下载
     */
    val isDownloading: Boolean
        get() = downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING

    /**
     * 是否下载完成
     */
    val isCompleted: Boolean
        get() = downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED

    init {
        if (rate <= 0) {
            rate = DownloadManager.DEFAULT_RATE
        }
    }

    /**
     * 删除数据库文件和已经下载的文件
     */
    fun cancel() {
        totalSize = 0
        listener?.onCancel(this)
        if (dbEntity != null) {
            DownloadEntity.delete(dbEntity!!)
            val temp = File(filePath)
            if (temp.exists()) temp.delete()
        }
    }

    /**
     * 分发回调事件到ui层
     */
    private fun onCallBack(downloadStatus: Int) {
        launchMain {
            when (downloadStatus) {
                DownloadStatus.DOWNLOAD_STATUS_ERROR -> listener?.onError(this, errorCode)
                DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING -> listener?.onDownloading(
                    this, completedSize, totalSize, downLoadPercent
                )
                DownloadStatus.DOWNLOAD_STATUS_CANCEL -> cancel()
                DownloadStatus.DOWNLOAD_STATUS_COMPLETED -> listener?.onDownloadSuccess(
                    this, File(filePath)
                )
                DownloadStatus.DOWNLOAD_STATUS_PAUSE -> listener?.onPause(
                    this, completedSize, totalSize, downLoadPercent
                )
            }
        }
        // 同步manager中的task信息
        DownloadManager.get().updateDownloadTask(this)
    }

    // 防止分母为0出现NoN
    private val downLoadPercent: Double
        private get() {
            val baiy = completedSize * 1.0
            val baiz = totalSize * 1.0
            // 防止分母为0出现NoN
            return if (baiz > 0) {
                val fen = baiy / baiz * 100
                if (fen > 100) 100.0 else fen
            } else 0.0
        }


    fun onComplete() {
        //下载完成取消订阅
        if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
            isCancel = true
        }
    }

    fun setCancel() {
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_CANCEL
        isCancel = true
    }

    /**
     * 真正的下载
     */
    fun run() {
        var inputStream: InputStream? = null
        var bis: BufferedInputStream? = null
        var downLoadFile: RandomAccessFile? = null
        try {
            // 数据库中加载数据
            dbEntity = DownloadEntity.queryById(id)
            if (dbEntity != null) {
                //存在历史下载
                completedSize = dbEntity!!.completedSize
                totalSize = dbEntity!!.totalSize
                //替换文件名
                fileName = dbEntity!!.fileName
                downLoadFile = RandomAccessFile(filePath, FILE_MODE)
            } else {
                //设置文件名
                if (fileName.isEmpty()) {
                    fileName = getNetFileName(null, url)
                }
                downLoadFile = RandomAccessFile(filePath, FILE_MODE)
            }
            //调节已完成的长度与文件一致
            val fileLength = downLoadFile.length()
            if (fileLength < completedSize) completedSize = downLoadFile.length()
            // 下载完成，更新数据库数据
            if (fileLength != 0L && totalSize <= fileLength) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED
                completedSize = fileLength
                totalSize = completedSize
                dbEntity = DownloadEntity(
                    id, totalSize, totalSize,
                    url, saveDirPath, fileName, downloadStatus
                )
                dbEntity?.save()
                // 执行finally中的回调
                return
            }

            // 开始下载
            val request: Request = Request.Builder().url(url).header(
                "RANGE", "bytes=$completedSize-"
            )
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body
            //设置response文件名
            if (fileName.isEmpty()) {
                fileName = getNetFileName(response, url)
            }
            // 文件跳转到指定位置开始写入
            downLoadFile.seek(completedSize)
            if (responseBody != null) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING
                if (totalSize < completedSize + responseBody.contentLength()) {
                    totalSize = completedSize + responseBody.contentLength()
                    dbEntity?.totalSize = totalSize
                }
                // 获得文件流
                inputStream = responseBody.byteStream()
                bis = BufferedInputStream(inputStream, BUFFER_SIZE)
                val buffer = ByteArray(BUFFER_SIZE)
                var length = 0
                // 开始下载数据库中插入下载信息
                if (dbEntity == null) {
                    dbEntity = DownloadEntity(
                        id, totalSize, 0L,
                        url, saveDirPath, fileName, downloadStatus
                    )
                    dbEntity?.save()
                }
                //上一次的时间
                var timeOld = System.currentTimeMillis()
                while (bis.read(buffer).also {
                        length = it
                    } > 0 && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_CANCEL && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                    downLoadFile.write(buffer, 0, length)
                    completedSize += length.toLong()
                    //计算是否要回调
                    if (Math.abs(System.currentTimeMillis() - timeOld) > rate) {
                        timeOld = System.currentTimeMillis()
                        //写入数据库
                        dbEntity!!.completedSize = completedSize
                        DownloadEntity.update(dbEntity!!)
                        //回调
                        onCallBack(downloadStatus)
                    }
                    if (isCancel) {
                        break
                    }
                }
                // 防止最后一次不足rate时间，导致percent无法达到100%
                onCallBack(downloadStatus)
            }
        } catch (e: FileNotFoundException) {
            // file not found
            e.printStackTrace()
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR
            errorCode = DownloadStatus.DOWNLOAD_ERROR_FILE_NOT_FOUND
        } catch (e: IOException) {
            // io exception
            e.printStackTrace()
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR
            errorCode = DownloadStatus.DOWNLOAD_ERROR_IO_ERROR
        } finally {
            if (!isCancel) {
                if (isDownloadFinish) {
                    onCallBack(downloadStatus)
                }
                // 下载后新数据库
                if (dbEntity != null) {
                    dbEntity!!.completedSize = completedSize
                    dbEntity!!.downloadStatus = downloadStatus
                    DownloadEntity.update(dbEntity!!)
                }
            }
            // 回收资源
            closeQuietly(bis)
            closeQuietly(inputStream)
            closeQuietly(downLoadFile)
        }
    }

}