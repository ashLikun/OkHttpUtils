package com.ashlikun.okhttputils.http

import android.text.TextUtils
import java.io.Closeable
import java.io.File
import java.io.Flushable
import java.io.IOException
import java.lang.Exception

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.17 9:30
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：io操作的相关工具
 */
object IOUtils {
    fun closeQuietly(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun flushQuietly(flushable: Flushable?) {
        try {
            flushable?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createFolder(folderPath: String): Boolean {
        return folderPath.isNotEmpty() && createFolder(File(folderPath))
    }

    fun createFolder(targetFolder: File): Boolean {
        if (targetFolder.exists()) {
            if (targetFolder.isDirectory) {
                return true
            }
            targetFolder.delete()
        }
        return targetFolder.mkdirs()
    }

    fun delFileOrFolder(path: String): Boolean {
        return path.isNotEmpty() && delFileOrFolder(File(path))
    }

    fun delFileOrFolder(file: File): Boolean {
        if (!file.exists()) {
        } else if (file.isFile) {
            file.delete()
        } else if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (sonFile in files) {
                    delFileOrFolder(sonFile)
                }
            }
            file.delete()
        }
        return true
    }

    fun createFile(targetFile: File): Boolean {
        if (targetFile.exists()) {
            if (targetFile.isFile) {
                return true
            }
            delFileOrFolder(targetFile)
        }
        return try {
            targetFile.createNewFile()
        } catch (e: IOException) {
            false
        }
    }

    fun createNewFile(filePath: String): Boolean {
        return filePath.isNotEmpty() && createNewFile(File(filePath))
    }

    fun createNewFile(targetFile: File): Boolean {
        if (targetFile.exists() && !targetFile.isFile) {
            delFileOrFolder(targetFile)
        }
        return if (!targetFile.exists()) {
            try {
                targetFile.createNewFile()
            } catch (e: IOException) {
                false
            }
        } else true
    }
}