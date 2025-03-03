package com.tiv.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.tiv.rpc.config.RpcConfig;
import com.tiv.rpc.config.RpcConfigHolder;
import com.tiv.rpc.constant.ProtocolConstant;
import com.tiv.rpc.constant.RpcConstant;
import com.tiv.rpc.enums.ProtocolMessageSerializerEnum;
import com.tiv.rpc.enums.ProtocolMessageTypeEnum;
import com.tiv.rpc.model.RpcRequest;
import com.tiv.rpc.model.RpcResponse;
import com.tiv.rpc.model.ServiceMetaInfo;
import com.tiv.rpc.protocol.Header;
import com.tiv.rpc.protocol.ProtocolMessage;
import com.tiv.rpc.protocol.encode.ProtocolMessageDecoder;
import com.tiv.rpc.protocol.encode.ProtocolMessageEncoder;
import com.tiv.rpc.registry.Registry;
import com.tiv.rpc.registry.RegistryFactory;
import com.tiv.rpc.serializer.Serializer;
import com.tiv.rpc.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理(基于JDK动态代理)
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getSerializer(RpcConfigHolder.getRpcConfig().getSerializer());

        String serviceName = method.getDeclaringClass().getName();
        // 构造rpc请求
        RpcRequest rpcRequest = RpcRequest
                .builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 获取rpc配置
            RpcConfig rpcConfig = RpcConfigHolder.getRpcConfig();
            // 获取注册中心
            Registry registry = RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo
                    .builder()
                    .serviceName(serviceName)
                    .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION)
                    .build();

            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscover(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("未发现服务");
            }
            serviceMetaInfo = serviceMetaInfoList.get(0);

            // 发送请求
            Vertx vertx = Vertx.vertx();
            NetClient netClient = vertx.createNetClient();
            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
            netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), res -> {
                if (res.succeeded()) {
                    System.out.println("Connected to TCP server.");
                    NetSocket socket = res.result();
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    Header header = new Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByKey(RpcConfigHolder.getRpcConfig().getSerializer()).getCode());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getCode());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);
                    try {
                        Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encode);
                    } catch (IOException e) {
                        throw new RuntimeException("自定义协议消息编码错误");
                    }
                    socket.handler(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            responseFuture.complete(responseProtocolMessage.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException("自定义协议消息解码错误");
                        }
                    });
                } else {
                    System.err.println("Failed to connect to TCP server.");
                }
            });
            RpcResponse rpcResponse = responseFuture.get();
            netClient.close();
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
