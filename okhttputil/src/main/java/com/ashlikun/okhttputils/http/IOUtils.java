package com.ashlikun.okhttputils.http;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/5/10 0010　下午 4:19
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class IOUtils {
    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void flushQuietly(Flushable flushable) {
        if (flushable == null) {
            return;
        }
        try {
            flushable.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean createFolder(String folderPath) {
        if (!TextUtils.isEmpty(folderPath)) {
            File folder = new File(folderPath);
            return createFolder(folder);
        }
        return false;
    }

    public static boolean createFolder(File targetFolder) {
        if (targetFolder.exists()) {
            if (targetFolder.isDirectory()) {
                return true;
            }
            targetFolder.delete();
        }
        return targetFolder.mkdirs();
    }

    public static boolean delFileOrFolder(String path) {
        return !TextUtils.isEmpty(path) && delFileOrFolder(new File(path));
    }


    public static boolean delFileOrFolder(File file) {
        if (file == null || !file.exists()) {
            // do nothing
        } else if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File sonFile : files) {
                    delFileOrFolder(sonFile);
                }
            }
            file.delete();
        }
        return true;
    }

    public static boolean createFile(File targetFile) {
        if (targetFile.exists()) {
            if (targetFile.isFile()) {
                return true;
            }
            delFileOrFolder(targetFile);
        }
        try {
            return targetFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }


    public static boolean createNewFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return createNewFile(file);
        }
        return false;
    }

    public static boolean createNewFile(File targetFile) {
        if (targetFile.exists() && !targetFile.isFile()) {
            delFileOrFolder(targetFile);
        }
        if (!targetFile.exists()) {
            try {
                return targetFile.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }
}
