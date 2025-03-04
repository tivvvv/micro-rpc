package com.tiv.rpc.constant;

/**
 * 自定义协议常量
 */
public interface ProtocolConstant {

    /**
     * 协议magic number
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;

    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 消息头 magic num索引
     */
    int MAGIC_INDEX = 0;

    /**
     * 消息头 版本号索引
     */
    int VERSION_INDEX = 1;

    /**
     * 消息头 序列化器索引
     */
    int SERIALIZER_INDEX = 2;

    /**
     * 消息头 类型索引
     */
    int TYPE_INDEX = 3;

    /**
     * 消息头 状态索引
     */
    int STATUS_INDEX = 4;

    /**
     * 消息头 请求id索引
     */
    int REQUEST_ID_INDEX = 5;

    /**
     * 消息头 消息体长度索引
     */
    int BODY_LENGTH_INDEX = 13;
}
