package com.ashlikun.okhttputils.json;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/8/4.
 */
public class JsonTypeAdapter {

    public static class IntegerTypeAdapter implements JsonDeserializer<Integer> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsJsonPrimitive().getAsString() == null
                    || json.getAsJsonPrimitive().getAsString() == ""
                    || TextUtils.equals(json.getAsJsonPrimitive().getAsString().toUpperCase(), "NULL")) {
                return GsonHelper.DEFAULT;
            } else {
                return json.getAsJsonPrimitive().getAsInt();
            }
        }

    }

    public static class StringTypeAdapter implements JsonDeserializer<String> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsJsonPrimitive().getAsString() == null
                    || TextUtils.equals(json.getAsJsonPrimitive().getAsString().toUpperCase(), "NULL")) {
                return null;
            } else {
                return json.getAsJsonPrimitive().getAsString();
            }
        }

    }

    public static class LongTypeAdapter implements JsonDeserializer<Long> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsJsonPrimitive().getAsString() == null
                    || json.getAsJsonPrimitive().getAsString() == ""
                    || TextUtils.equals(json.getAsJsonPrimitive().getAsString().toUpperCase(), "NULL")) {
                return Long.valueOf(GsonHelper.DEFAULT);
            } else {
                return json.getAsJsonPrimitive().getAsLong();
            }
        }


    }

    public static class DoubleTypeAdapter implements JsonDeserializer<Double> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsJsonPrimitive().getAsString() == null
                    || json.getAsJsonPrimitive().getAsString() == ""
                    || TextUtils.equals(json.getAsJsonPrimitive().getAsString().toUpperCase(), "NULL")) {
                return Double.valueOf(GsonHelper.DEFAULT);
            } else {
                return json.getAsJsonPrimitive().getAsDouble();
            }
        }


    }

    public static class FloatTypeAdapter implements JsonDeserializer<Float> {
        // json转为对象时调用,实现JsonDeserializer<>接口
        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsJsonPrimitive().getAsString() == null
                    || json.getAsJsonPrimitive().getAsString() == ""
                    || TextUtils.equals(json.getAsJsonPrimitive().getAsString().toUpperCase(), "NULL")) {
                return Float.valueOf(GsonHelper.DEFAULT);
            } else {
                return json.getAsJsonPrimitive().getAsFloat();
            }
        }
    }

}
