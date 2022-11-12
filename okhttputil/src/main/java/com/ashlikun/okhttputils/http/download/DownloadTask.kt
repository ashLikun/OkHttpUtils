package com.ashlikun.okhttputils.http.download

import com.ashlikun.okhttputils.http.HttpException
import com.ashlikun.okhttputils.http.HttpUtils.getNetFileName
import com.ashlikun.okhttputils.http.HttpUtils.launchMain
import com.ashlikun.okhttputils.http.IOUtils.closeQuietly
import com.ashlikun.okhttputils.http.IOUtils.createFolder
import com.ashlikun.okhttputils.http.IOUtils.createNewFile
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import kotlin.math.abs

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 14:05
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：下载线程
 */

open class DownloadTask(
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

    open var client: OkHttpClient = DownloadManager.get().client


    // 文件保存路径
    open var saveDirPath: String =
        if (!DownloadManager.defaultFilePath.endsWith(File.separator)) "${DownloadManager.defaultFilePath}${File.separator}" else DownloadManager.defaultFilePath
        set(value) = if (!value.endsWith(File.separator)) field = "$value${File.separator}" else field = value

    //文件保存的名称,如果正在下载就会被替换掉,第一次下载如果不设置，那么就会取Http的fileName，如果没有那么就会取url
    open var fileName: String = ""

    //下载状态
    open var downloadStatus: Int = DownloadStatus.DOWNLOAD_STATUS_INIT

    //下载多少回调一次  默认200，单位毫秒;
    open var rate: Long = DownloadManager.DEFAULT_RATE

    // 总大小
    open var totalSize: Long = 0

    //  已经下载的大小
    open var completedSize: Long = 0


    //错误码
    open var errorCode = 0


    //是否取消
    open var isCancel = false


    //是否下载结束
    open protected val isDownloadFinish: Boolean
        get() {
            if (totalSize > 0 && completedSize > 0 && totalSize == completedSize) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED
                return true
            }
            return false
        }

    // 获得文件名
    open val filePath: String
        get() {
            if (fileName.isEmpty() && dbEntity != null) {
                fileName = dbEntity!!.fileName
            }
            createFolder(saveDirPath)
            val filepath = saveDirPath + fileName
            createNewFile(filepath)
            return filepath
        }

    open protected var dbEntity: DownloadEntity? = null

    /**
     * 是否正在下载
     */
    open val isDownloading: Boolean
        get() = downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING

    /**
     * 是否下载完成
     */
    open val isCompleted: Boolean
        get() = downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED

    init {
        if (rate <= 0) {
            rate = DownloadManager.DEFAULT_RATE
        }
        if (id.isNullOrEmpty()) {
            id = url
        }
    }

    /**
     * 删除数据库文件和已经下载的文件
     */
    open fun cancel() {
        totalSize = 0
        if (!isDownloading) {
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_CANCEL
            onCallBack(downloadStatus)
            if (dbEntity != null) {
                DownloadEntity.delete(dbEntity!!)
                val temp = File(filePath)
                if (temp.exists()) temp.delete()
            }
        }
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_CANCEL
        isCancel = true
    }

    /**
     * 分发回调事件到ui层
     * @param downloadStatus 必须传递参数的形式，不然状态中由于异步可能不对
     */
    open protected fun onCallBack(downloadStatus: Int) {
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
    open protected val downLoadPercent: Double
        protected get() {
            val baiy = completedSize * 1.0
            val baiz = totalSize * 1.0
            // 防止分母为0出现NoN
            return if (baiz > 0) {
                val fen = baiy / baiz * 100
                if (fen > 100) 100.0 else fen
            } else 0.0
        }


    /**
     * 真正的下载
     */
    internal open fun run() {
        //正在下载
        if (isDownloading) return
        isCancel = false
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

                //下面这些是断点下载的恢复
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
            } else {
                //设置文件名
//                if (fileName.isEmpty()) {
//                    fileName = getNetFileName(null, url)
//                }
//                downLoadFile = RandomAccessFile(filePath, FILE_MODE)
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
            //这个需要在文件名已经存在的时候使用
            if (downLoadFile == null) {
                downLoadFile = RandomAccessFile(filePath, FILE_MODE)
            }
            // 文件跳转到指定位置开始写入
            downLoadFile.seek(completedSize)
            if (!response.isSuccessful) {
                throw HttpException(response.code, "http error")
            }
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
                var length: Int
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
                    } > 0 && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_CANCEL
                    && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                    downLoadFile.write(buffer, 0, length)
                    completedSize += length.toLong()
                    //计算是否要回调
                    if (abs(System.currentTimeMillis() - timeOld) > rate) {
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
                if (!isCancel) {
                    onCallBack(downloadStatus)
                }
            }
        } catch (e: FileNotFoundException) {
            // file not found
            e.printStackTrace()
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR
            errorCode = DownloadStatus.DOWNLOAD_ERROR_FILE_NOT_FOUND
            onCallBack(downloadStatus)
        } catch (e: IOException) {
            // io exception
            e.printStackTrace()
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR
            errorCode = DownloadStatus.DOWNLOAD_ERROR_IO_ERROR
            onCallBack(downloadStatus)
        } catch (e: HttpException) {
            e.printStackTrace()
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR
            errorCode = e.code
            onCallBack(downloadStatus)
        } finally {
            if (!isCancel) {
                //改变状态
                if (isDownloadFinish) {
                    onCallBack(downloadStatus)
                }
                // 下载后新数据库
                if (dbEntity != null) {
                    dbEntity!!.completedSize = completedSize
                    dbEntity!!.downloadStatus = downloadStatus
                    DownloadEntity.update(dbEntity!!)
                }
            } else {
                onCallBack(downloadStatus)
                //取消删除
                if (dbEntity != null) {
                    DownloadEntity.delete(dbEntity!!)
                    val temp = File(filePath)
                    if (temp.exists()) temp.delete()
                }
            }
            // 回收资源
            closeQuietly(bis)
            closeQuietly(inputStream)
            closeQuietly(downLoadFile)
        }
    }

}