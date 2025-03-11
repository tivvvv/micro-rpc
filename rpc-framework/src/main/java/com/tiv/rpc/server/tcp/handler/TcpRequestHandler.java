package com.tiv.rpc.server.tcp.handler;

import com.tiv.rpc.enums.ProtocolMessageTypeEnum;
import com.tiv.rpc.model.RpcRequest;
import com.tiv.rpc.model.RpcResponse;
import com.tiv.rpc.protocol.Header;
import com.tiv.rpc.protocol.ProtocolMessage;
import com.tiv.rpc.protocol.encode.ProtocolMessageDecoder;
import com.tiv.rpc.protocol.encode.ProtocolMessageEncoder;
import com.tiv.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TCP请求处理器
 */
public class TcpRequestHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接收并解析rpc请求
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("自定义协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();
            RpcResponse rpcResponse = new RpcResponse();
            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("tcp success");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getCode());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                netSocket.write(ProtocolMessageEncoder.encode(responseProtocolMessage));
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });
        netSocket.handler(tcpBufferHandlerWrapper);
    }
}
