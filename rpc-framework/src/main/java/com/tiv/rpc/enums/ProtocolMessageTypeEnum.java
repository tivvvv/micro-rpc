package com.tiv.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自定义协议 消息类型枚举
 */
@Getter
@AllArgsConstructor
public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    /**
     * 类型码
     */
    private final int code;

    /**
     * 根据类型码获取枚举
     *
     * @param code
     * @return
     */
    public static ProtocolMessageTypeEnum getEnumByCode(int code) {
        for (ProtocolMessageTypeEnum typeEnum : values()) {
            if (typeEnum.getCode() == code) {
                return typeEnum;
            }
        }
        return null;
    }
}
