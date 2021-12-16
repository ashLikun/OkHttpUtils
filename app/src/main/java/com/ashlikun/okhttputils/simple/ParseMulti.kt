package com.ashlikun.okhttputils.simple

import com.ashlikun.okhttputils.retrofit.Parse

/**
 * 作者　　: 李坤
 * 创建时间: 2021/6/16　20:44
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS,AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Parse("aaaaaaa")
annotation class ParseMulti
