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
        byte magic = buffer.getByte(ProtocolConstant.MAGIC_INDEX);
        if (ProtocolConstant.PROTOCOL_MAGIC != magic) {
            throw new RuntimeException("非法消息");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(ProtocolConstant.VERSION_INDEX));
        header.setSerializer(buffer.getByte(ProtocolConstant.SERIALIZER_INDEX));
        header.setType(buffer.getByte(ProtocolConstant.TYPE_INDEX));
        header.setStatus(buffer.getByte(ProtocolConstant.STATUS_INDEX));
        header.setRequestId(buffer.getLong(ProtocolConstant.REQUEST_ID_INDEX));
        header.setBodyLength(buffer.getInt(ProtocolConstant.BODY_LENGTH_INDEX));
        // 读取消息体
        byte[] bodyBytes = buffer.getBytes(ProtocolConstant.MESSAGE_HEADER_LENGTH, ProtocolConstant.MESSAGE_HEADER_LENGTH + header.getBodyLength());
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
