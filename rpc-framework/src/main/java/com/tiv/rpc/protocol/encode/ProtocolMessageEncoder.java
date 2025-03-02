package com.tiv.rpc.protocol.encode;

import com.tiv.rpc.enums.ProtocolMessageSerializerEnum;
import com.tiv.rpc.protocol.Header;
import com.tiv.rpc.protocol.ProtocolMessage;
import com.tiv.rpc.serializer.Serializer;
import com.tiv.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 自定义协议消息编码器
 */
public class ProtocolMessageEncoder {

    /**
     * 编码
     *
     * @param protocolMessage
     * @return
     * @throws IOException
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        Header header = protocolMessage.getHeader();

        Buffer buffer = Buffer.buffer();
        // 按照自定义协议消息的结构,依次写入字节
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByCode(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化器不存在");
        }
        Serializer serializer = SerializerFactory.getSerializer(serializerEnum.getKey());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // 依次写入消息体长度和消息体
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
