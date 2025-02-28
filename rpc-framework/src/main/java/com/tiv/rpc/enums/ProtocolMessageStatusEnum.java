package com.tiv.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自定义协议 消息状态枚举
 */
@Getter
@AllArgsConstructor
public enum ProtocolMessageStatusEnum {

    OK(20, "ok"),
    BAD_REQUEST(40, "badRequest"),
    BAD_RESPONSE(50, "badResponse");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态描述
     */
    private final String desc;

    /**
     * 根据状态码获取枚举
     *
     * @param code
     * @return
     */
    public static ProtocolMessageStatusEnum getEnumByCode(int code) {
        for (ProtocolMessageStatusEnum statusEnum : values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
