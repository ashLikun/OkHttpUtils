package com.ashlikun.okhttputils.json;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/8/4.
 * gson的默认值处理
 */

public class JsonTypeAdapter {

    public static class IntegerTypeAdapter implements JsonDeserializer<Integer> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            String value = json.getAsJsonPrimitive().getAsString();
            if (TextUtils.isEmpty(value) || TextUtils.equals(value, "NULL")) {
                return null;
            } else {
                try {
                    return json.getAsJsonPrimitive().getAsInt();
                } catch (Exception e) {
                    return null;
                }
            }
        }

    }

    public static class StringTypeAdapter implements JsonDeserializer<String> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            String value = json.getAsJsonPrimitive().getAsString();
            if (value == null || TextUtils.equals(value.toUpperCase(), "NULL")) {
                return null;
            } else {
                return value;
            }
        }

    }

    public static class LongTypeAdapter implements JsonDeserializer<Long> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            String value = json.getAsJsonPrimitive().getAsString();
            if (TextUtils.isEmpty(value) || TextUtils.equals(value, "NULL")) {
                return null;
            } else {
                try {
                    return json.getAsJsonPrimitive().getAsLong();
                } catch (Exception e) {
                    return null;
                }
            }
        }


    }

    public static class DoubleTypeAdapter implements JsonDeserializer<Double> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            String value = json.getAsJsonPrimitive().getAsString();
            if (TextUtils.isEmpty(value) || TextUtils.equals(value, "NULL")) {
                return null;
            } else {
                try {
                    return json.getAsJsonPrimitive().getAsDouble();
                } catch (Exception e) {
                    return null;
                }
            }
        }


    }

    public static class FloatTypeAdapter implements JsonDeserializer<Float> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            String value = json.getAsJsonPrimitive().getAsString();
            if (TextUtils.isEmpty(value) || TextUtils.equals(value, "NULL")) {
                return null;
            } else {
                try {
                    return json.getAsJsonPrimitive().getAsFloat();
                } catch (Exception e) {
                    return null;
                }
            }
        }
    }

    public static class ShortTypeAdapter implements JsonDeserializer<Short> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public Short deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            String value = json.getAsJsonPrimitive().getAsString();
            if (TextUtils.isEmpty(value) || TextUtils.equals(value, "NULL")) {
                return null;
            } else {
                try {
                    return json.getAsJsonPrimitive().getAsShort();
                } catch (Exception e) {
                    return null;
                }
            }
        }
    }

}
