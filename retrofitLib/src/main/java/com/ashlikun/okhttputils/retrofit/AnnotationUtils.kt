package com.ashlikun.okhttputils.retrofit

/**
 * 作者　　: 李坤
 * 创建时间: 2021/6/17　10:36
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
object AnnotationUtils {
    private val ANNOTATION_FILTER = arrayOf("java.lang.annotation", "kotlin.annotation")

    /**
     * 是否是Java或者kotlin的注解
     */
    fun isInJavaLangAnnotationPackage(annotation: Annotation): Boolean {
        ANNOTATION_FILTER.forEach {
            //包含
            if (annotation.annotationClass.qualifiedName?.startsWith(it) == true) {
                return true
            }
        }
        return false
    }
}