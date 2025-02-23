package com.tiv.rpc.serializer;

import com.tiv.rpc.serializer.impl.JDKSerializer;
import com.tiv.rpc.spi.SpiLoader;

/**
 * 序列化器工厂
 */
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JDKSerializer();

    /**
     * 获取序列化器
     *
     * @param key
     * @return
     */
    public static Serializer getSerializer(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
