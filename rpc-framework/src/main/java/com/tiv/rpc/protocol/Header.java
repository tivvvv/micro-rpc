package com.tiv.rpc.protocol;

import lombok.Data;

/**
 * 消息头
 */
@Data
public class Header {

    /**
     * magic number
     */
    private byte magic;

    /**
     * 版本号
     */
    private byte version;

    /**
     * 序列化器
     */
    private byte serializer;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 状态
     */
    private byte status;

    /**
     * 请求id
     */
    private long requestId;

    /**
     * 消息体长度
     */
    private int bodyLength;
}
