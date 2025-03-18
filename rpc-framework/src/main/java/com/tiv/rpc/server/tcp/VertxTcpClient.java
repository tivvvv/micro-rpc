package com.tiv.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.tiv.rpc.config.RpcConfigHolder;
import com.tiv.rpc.constant.ProtocolConstant;
import com.tiv.rpc.enums.ProtocolMessageSerializerEnum;
import com.tiv.rpc.enums.ProtocolMessageTypeEnum;
import com.tiv.rpc.model.RpcRequest;
import com.tiv.rpc.model.RpcResponse;
import com.tiv.rpc.model.ServiceMetaInfo;
import com.tiv.rpc.protocol.Header;
import com.tiv.rpc.protocol.ProtocolMessage;
import com.tiv.rpc.protocol.encode.ProtocolMessageDecoder;
import com.tiv.rpc.protocol.encode.ProtocolMessageEncoder;
import com.tiv.rpc.server.tcp.handler.TcpBufferHandlerWrapper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * 基于Vert.x实现TCP客户端
 */
public class VertxTcpClient {
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) {
        // 发送TCP请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), res -> {
            if (!res.succeeded()) {
                System.err.println("Failed to connect TCP server.");
                return;
            }
            NetSocket socket = res.result();
            // 构造消息
            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
            Header header = new Header();
            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByKey(RpcConfigHolder.getRpcConfig().getSerializer()).getCode());
            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getCode());
            header.setRequestId(IdUtil.getSnowflakeNextId());
            protocolMessage.setHeader(header);
            protocolMessage.setBody(rpcRequest);

            // 发送请求
            try {
                Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
                socket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("自定义协议消息编码错误");
            }

            // 接收响应
            TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                try {
                    ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                    responseFuture.complete(responseProtocolMessage.getBody());
                } catch (IOException e) {
                    throw new RuntimeException("自定义协议消息解码错误");
                }
            });
            socket.handler(tcpBufferHandlerWrapper);
        });
        try {
            return responseFuture.get();
        } catch (Exception e) {
            throw new RuntimeException("调用失败");
        } finally {
            netClient.close();
        }
    }
}
