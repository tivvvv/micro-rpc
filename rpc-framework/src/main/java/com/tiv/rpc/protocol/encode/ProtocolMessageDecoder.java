package com.tiv.rpc.protocol.encode;

import com.tiv.rpc.constant.ProtocolConstant;
import com.tiv.rpc.enums.ProtocolMessageSerializerEnum;
import com.tiv.rpc.enums.ProtocolMessageTypeEnum;
import com.tiv.rpc.model.RpcRequest;
import com.tiv.rpc.model.RpcResponse;
import com.tiv.rpc.protocol.Header;
import com.tiv.rpc.protocol.ProtocolMessage;
import com.tiv.rpc.serializer.Serializer;
import com.tiv.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 自定义协议消息解码器
 */
public class ProtocolMessageDecoder {

    /**
     * 解码
     *
     * @param buffer
     * @return
     * @throws IOException
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        Header header = new Header();
        // 读取消息头结构
        byte magic = buffer.getByte(0);
        if (ProtocolConstant.PROTOCOL_MAGIC != magic) {
            throw new RuntimeException("非法消息");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        // 读取消息体
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByCode(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化器不存在");
        }
        Serializer serializer = SerializerFactory.getSerializer(serializerEnum.getKey());
        ProtocolMessageTypeEnum typeEnum = ProtocolMessageTypeEnum.getEnumByCode(header.getType());
        if (typeEnum == null) {
            throw new RuntimeException("消息类型不存在");
        }
        // 按照消息类型反序列化
        switch (typeEnum) {
            case REQUEST:
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, rpcRequest);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, rpcResponse);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("消息类型暂不支持");
        }
    }
}
