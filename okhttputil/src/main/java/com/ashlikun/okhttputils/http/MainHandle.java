package com.ashlikun.okhttputils.http;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.SoftReference;

/**
 * 作者　　: 李坤
 * 创建时间:2017/8/27 0027　2:41
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：单例形式的hander,主线程
 */

public class MainHandle {
    Handler mainHandle;
    private static volatile MainHandle instance = null;

    private MainHandle(Looper looper) {
        mainHandle = new Handler(looper);
    }

    public static MainHandle get() {
        //双重校验DCL单例模式
        if (instance == null) {
            //同步代码块
            synchronized (MainHandle.class) {
                if (instance == null) {
                    //创建一个新的实例
                    instance = new MainHandle(Looper.getMainLooper());
                }
            }
        }
        //返回一个实例
        return instance;
    }

    public void posts(Runnable runnable) {
        mainHandle.post(new SoftRunnable(runnable));
    }

    public void postDelayeds(Runnable runnable, long delayMillis) {
        mainHandle.postDelayed(new SoftRunnable(runnable), delayMillis);
    }

    public void postDelayeds(Runnable runnable, Object token, long delayMillis) {
        Message message = Message.obtain(get().mainHandle,new SoftRunnable(runnable));
        message.obj = token;
        get().mainHandle.sendMessageDelayed(message, delayMillis);
    }


    public static void post(Runnable runnable) {
        get().mainHandle.post(new SoftRunnable(runnable));
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        get().mainHandle.postDelayed(new SoftRunnable(runnable), delayMillis);
    }

    public static void postDelayed(Runnable runnable, Object token, long delayMillis) {
        Message message = Message.obtain(get().mainHandle,new SoftRunnable(runnable));
        message.obj = token;
        get().mainHandle.sendMessageDelayed(message, delayMillis);
    }

    /**
     * 解决回调内存泄露
     */
    public static class SoftRunnable implements Runnable {
        SoftReference<Runnable> runnable;

        public SoftRunnable(Runnable runnable) {
            this.runnable = new SoftReference<Runnable>(runnable);
        }

        @Override
        public void run() {
            if (runnable != null && runnable.get() != null) {
                runnable.get().run();
            }
        }
    }

    /**
     * 是否是主线程
     */
    public static boolean isMain() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
