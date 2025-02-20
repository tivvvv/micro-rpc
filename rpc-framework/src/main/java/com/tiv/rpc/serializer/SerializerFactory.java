package com.tiv.rpc.serializer;

import com.tiv.rpc.serializer.impl.JDKSerializer;
import com.tiv.rpc.serializer.impl.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂
 */
public class SerializerFactory {

    /**
     * 序列化器映射
     */
    private static final Map<String, Serializer> SERIALIZER_MAP = new HashMap<>() {
        {
            put(SerializerKeys.JDK, new JDKSerializer());
            put(SerializerKeys.JSON, new JsonSerializer());
        }
    };

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = SERIALIZER_MAP.get(SerializerKeys.JDK);

    /**
     * 获取序列化器
     *
     * @param key
     * @return
     */
    public static Serializer getSerializer(String key) {
        return SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
    }
}
