/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ashlikun.okhttputils.http.cookie.store;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * @author　　: 李坤
 * 创建时间: 2019/2/27 11:20
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：CookieStore 的公共接口
 */

public interface CookieStore {

    /**
     * 保存url对应所有cookie
     */
    void saveCookie(HttpUrl url, List<Cookie> cookie);

    /**
     * 保存url对应所有cookie
     */
    void saveCookie(HttpUrl url, Cookie cookie);

    /**
     * 加载url所有的cookie
     */
    List<Cookie> loadCookie(HttpUrl url);

    /**
     * 获取当前所有保存的cookie
     */
    List<Cookie> getAllCookie();

    /**
     * 获取当前url对应的所有的cookie
     */
    List<Cookie> getCookie(HttpUrl url);

    /**
     * 根据url和cookie移除对应的cookie
     */
    boolean removeCookie(HttpUrl url, Cookie cookie);

    /**
     * 根据url移除所有的cookie
     */
    boolean removeCookie(HttpUrl url);

    /**
     * 移除所有的cookie
     */
    boolean removeAllCookie();
}
