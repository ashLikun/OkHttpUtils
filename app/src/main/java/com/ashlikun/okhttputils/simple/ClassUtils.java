package com.ashlikun.okhttputils.simple;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/8/7　14:17
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：Class反射工具
 */

public class ClassUtils {
    /**
     * 获取全部Field，包括父类
     *
     * @param claxx
     * @return
     */
    public static LinkedList<Field> getAllDeclaredFields(Class<?> claxx) {
        // find all field.
        LinkedList<Field> fieldList = new LinkedList<Field>();
        while (claxx != null && claxx != Object.class) {
            Field[] fs = claxx.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                if (!isInvalid(f)) {
                    fieldList.addLast(f);
                }
            }
            claxx = claxx.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 获取指定的字段
     */
    public static Field getAllDeclaredField(Class<?> claxx, String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return null;
        }

        while (claxx != null && claxx != Object.class) {
            try {
                Field f = claxx.getDeclaredField(fieldName);
                if (f != null) {
                    return f;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            claxx = claxx.getSuperclass();
        }
        return null;
    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     */
    public static Object getField(Object object, String fieldName) {
        if (object == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }
        try {
            Field field = getAllDeclaredField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     */
    public static Field setField(Object object, String fieldName, Object value) {
        if (object == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }
        try {
            Field field = getAllDeclaredField(object.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(object, value);
                return field;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取全部Method，包括父类
     *
     * @param claxx
     * @return
     */
    public static LinkedList<Method> getAllDeclaredMethods(Class<?> claxx) {
        // find all field.
        LinkedList<Method> fieldList = new LinkedList<Method>();
        while (claxx != null && claxx != Object.class) {
            Method[] fs = claxx.getDeclaredMethods();
            for (int i = 0; i < fs.length; i++) {
                Method f = fs[i];
                fieldList.addLast(f);
            }
            claxx = claxx.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 获取指定的方法
     */
    public static Method getAllDeclaredMethod(Class<?> claxx, String methodName, Class<?>... parameterTypes) {
        if (methodName == null || methodName.isEmpty()) {
            return null;
        }

        while (claxx != null && claxx != Object.class) {
            try {
                Method f = claxx.getDeclaredMethod(methodName, parameterTypes);
                if (f != null) {
                    return f;
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            claxx = claxx.getSuperclass();
        }
        return null;
    }

    /**
     * 反射方法
     *
     * @param object      要反射的对象
     * @param methodNames 要反射的方法名称
     */
    public static Object getMethod(Object object, String methodNames) {
        return getMethod(object, methodNames, null);
    }

    public static Object getMethod(Object object, String methodName, Class<?>[] parameterTypes, Object... args) {
        if (object == null || methodName == null || methodName.isEmpty()) {
            return null;
        }
        try {
            Method method = getAllDeclaredMethod(object.getClass(), methodName, parameterTypes);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(object, args);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取域的泛型类型，如果不带泛型返回null
     */
    public static Class<?> getGenericType(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (type instanceof Class<?>) {
                return (Class<?>) type;
            }
        } else if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }


    /**
     * 是静态常量或者内部结构属性
     */
    public static boolean isInvalid(Field f) {
        return (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())) || f.isSynthetic();
    }

    /**
     * 是否有Class注解
     *
     * @param clazz           a {@link Class} object.
     * @param annotationClass a {@link Class} object.
     * @return a boolean.
     */
    public static boolean hasClassAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return getClassAnnotation(clazz, annotationClass) != null;
    }

    /**
     * 是否有字段注解
     *
     * @param clazz           a {@link Class} object.
     * @param annotationClass a {@link Class} object.
     * @param fieldName       a {@link String} object.
     * @return a boolean.
     */
    public static boolean hasFieldAnnotation(Class<?> clazz,
                                             Class<? extends Annotation> annotationClass, String fieldName) throws Exception {
        return getFieldAnnotation(clazz, annotationClass, fieldName) != null;
    }

    /**
     * 是否有字段注解
     *
     * @param clazz           a {@link Class} object.
     * @param annotationClass a {@link Class} object.
     * @param methodName      a {@link String} object.
     * @param paramType       a {@link Class} object.
     * @return a boolean.
     */
    public static boolean hasMethodAnnotation(Class<?> clazz,
                                              Class<? extends Annotation> annotationClass, String methodName, Class<?>... paramType) throws Exception {
        return getMethodAnnotation(clazz, annotationClass, methodName, paramType) != null;
    }

    /**
     * 获取类注解
     *
     * @param clazz           类
     * @param annotationClass 注解类
     * @return a A object.
     */


    public static <A extends Annotation> A getClassAnnotation(Class<?> clazz, Class<A> annotationClass) {
        return clazz.getAnnotation(annotationClass);

    }

    /**
     * 获取类成员注解
     *
     * @param clazz           类
     * @param annotationClass 注解类
     * @param fieldName       成员属性名
     * @return a A object.
     */


    public static <A extends Annotation> A getFieldAnnotation(Class<?> clazz,
                                                              Class<A> annotationClass, String fieldName) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field == null) {

                throw new Exception("no such field[" + fieldName + "] in " + clazz.getCanonicalName());

            }
            return field.getAnnotation(annotationClass);

        } catch (SecurityException e) {
            e.printStackTrace();

            throw new Exception("access error: field[" + fieldName + "] in " + clazz.getCanonicalName(), e);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();

            throw new Exception("no such field[" + fieldName + "] in " + clazz.getCanonicalName());

        }

    }

    /**
     * 获取类方法上的注解
     *
     * @param clazz           类
     * @param annotationClass 注解类
     * @param methodName      方法名
     * @param paramType       方法参数
     * @return a A object.
     */
    public static <A extends Annotation> A getMethodAnnotation(Class<?> clazz,
                                                               Class<A> annotationClass, String methodName, Class<?>... paramType)
            throws Exception {
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramType);
            if (method == null) {
                throw new Exception("access error: method[" + methodName + "] in " + clazz.getCanonicalName());
            }
            return method.getAnnotation(annotationClass);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new Exception("access error: method[" + methodName + "] in " + clazz.getCanonicalName(), e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new Exception("no such method[" + methodName + "] in " + clazz.getCanonicalName(), e);
        }
    }
}

