package com.ashlikun.okhttputils.retrofit;

import com.ashlikun.okhttputils.http.OkHttpManage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import kotlin.coroutines.Continuation;

/**
 * 作者　　: 李坤
 * 创建时间: 2020/12/16　16:42
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
class MyInvocationHandler implements InvocationHandler {
    Retrofit retrofit;
    OkHttpManage okHttpManage;
    private final Object[] emptyArgs = new Object[0];

    public MyInvocationHandler(Retrofit retrofit, OkHttpManage okHttpManage) {
        this.retrofit = retrofit;
        this.okHttpManage = okHttpManage;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // If the method is a method from Object then defer to normal invocation.
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        args = args != null ? args : emptyArgs;
        if (args != null && args.length >= 1) {
            Object continuation = args[args.length - 1];
            if (continuation instanceof Continuation) {
                retrofit.proxyStart(method, args);
                try {
                    Object o = retrofit.loadServiceMethodSuspend(proxy, method, okHttpManage, args, (Continuation) continuation);
                    return o;
                } catch (Exception e) {
                    retrofit.proxyError(method, args, e);
                    throw e;
                }
            }
        }
        //执行普通方法
        try {
            Object o = retrofit.loadServiceMethod(proxy, method, okHttpManage).invokeNoSuspend(proxy, args);
            return o;
        } catch (Exception e) {
            retrofit.proxyError(method, args, e);
            throw e;
        }
    }
}
