package com.ashlikun.okhttputils.http.download

import com.ashlikun.okhttputils.http.HttpUtils.launch
import com.ashlikun.okhttputils.http.OkHttpUtils
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 17:34
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：下载管理器,断点下载
 */
class DownloadManager private constructor(var client: OkHttpClient) {

    // 将执行结果保存
    private val currentTaskList: MutableMap<String, DownloadTask> = mutableMapOf()

    /**
     * 添加下载任务
     */
    fun addDownloadTask(downloadTask: DownloadTask, isStart: Boolean = true) {
        downloadTask.client = client
        val oldTask = currentTaskList[downloadTask.id]
        if (oldTask != null) {
            oldTask.client = client
            when {
                //正在下载不处理
                oldTask.isDownloading -> return
                //完成也不处理
                oldTask.isCompleted -> currentTaskList.remove(downloadTask.id)
                oldTask.isCancel -> currentTaskList.remove(downloadTask.id)
                else -> {
                    if (isStart) {
                        launch {
                            downloadTask.run()
                        }
                    }
                    return
                }
            }
        }
        if (!downloadTask.isDownloading) {
            downloadTask.downloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT
            // 保存下载task列表
            currentTaskList[downloadTask.id] = downloadTask
            if (isStart) {
                launch {
                    downloadTask.run()
                }
            }
        }
    }

    /**
     * 暂停下载任务
     *
     * @param id 任务id
     */
    fun pause(id: String) {
        getDownloadTask(id)?.downloadStatus = DownloadStatus.DOWNLOAD_STATUS_PAUSE
    }

    /**
     * 重新开始已经暂停的下载任务
     *
     * @param id 任务id
     */
    fun resume(id: String) {
        getDownloadTask(id)?.apply { addDownloadTask(this) }
    }

    /**
     * 取消下载任务(同时会删除已经下载的文件，和清空数据库缓存)
     *
     * @param id 任务id
     */
    fun cancel(id: String) {
        getDownloadTask(id)?.apply {
            currentTaskList.remove(this.id)
            cancel()
        }
    }

    /**
     * 实时更新manager中的task信息
     *
     * @param task 任务id
     */
    fun updateDownloadTask(task: DownloadTask) {
        val currTask = getDownloadTask(task.id)
        if (currTask != null) {
            currentTaskList[task.id] = task
        }
    }

    /**
     * 获得指定的task
     *
     * @param id 任务id
     */
    fun getDownloadTask(id: String): DownloadTask? {
        var currTask = currentTaskList[id]
        if (currTask == null) {
            // 从数据库中取出未完成的task
            val entity = DownloadEntity.queryById(id)
            if (entity != null) {
                if (entity.downloadStatus != DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                    currTask = parseEntity2Task(entity)
                    // 放入task list中
                    currentTaskList[id] = currTask
                }
            }
        }
        return currTask
    }

    /**
     * 获得所有的task
     */
    val allDownloadTasks: Map<String, DownloadTask>
        get() {
            DownloadEntity.queryAll().forEach {
                currentTaskList[it.downloadId] = parseEntity2Task(it)
            }
            return currentTaskList
        }

    /**
     * 把从数据库取出来的实体  放入任务栈
     */
    private fun parseEntity2Task(entity: DownloadEntity): DownloadTask {
        return DownloadTask().apply {
            downloadStatus = entity.downloadStatus
            fileName = entity.fileName
            saveDirPath = entity.saveDirPath
            url = entity.url
            id = entity.downloadId
            completedSize = entity.completedSize
            totalSize = entity.totalSize
        }
    }

    companion object {
        //默认的进度间隔时间
        const val DEFAULT_RATE: Long = 200

        var defaultFilePath: String = ""

        /**
         * 防止多线程操作时出现多个实例
         */
        fun initPath(defaultFilePath: String) {
            this.defaultFilePath = defaultFilePath
        }

        private val instance by lazy {
            if (defaultFilePath.isNullOrEmpty()) Exception("请调用initPath方法设置文件默认路径")
            val client = OkHttpUtils.get().okHttpClient
                .newBuilder()
                .readTimeout(
                    OkHttpUtils.DEFAULT_MILLISECONDS * 30,
                    TimeUnit.MILLISECONDS
                )
                .writeTimeout(OkHttpUtils.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .connectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            client.interceptors().clear()
            client.networkInterceptors().clear()
            DownloadManager(client.build())
        }

        fun get(): DownloadManager = instance
    }
}