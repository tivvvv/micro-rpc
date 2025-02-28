package com.tiv.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义协议 消息序列化器枚举
 */
@Getter
@AllArgsConstructor
public enum ProtocolMessageSerializerEnum {

    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian");

    /**
     * 枚举码
     */
    private final int code;

    /**
     * 序列化器key
     */
    private final String key;

    /**
     * 根据枚举码获取枚举
     *
     * @param code
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByCode(int code) {
        for (ProtocolMessageSerializerEnum value : values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据序列化器key获取枚举
     *
     * @param key
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(String key) {
        for (ProtocolMessageSerializerEnum value : values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取所有序列化器key
     *
     * @return
     */
    public static List<String> getKeys() {
        return Arrays.stream(values()).map(ProtocolMessageSerializerEnum::getKey).collect(Collectors.toList());
    }
}
