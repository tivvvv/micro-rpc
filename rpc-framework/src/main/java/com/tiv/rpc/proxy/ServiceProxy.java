package com.tiv.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.tiv.rpc.config.RpcConfig;
import com.tiv.rpc.config.RpcConfigHolder;
import com.tiv.rpc.constant.RpcConstant;
import com.tiv.rpc.model.RpcRequest;
import com.tiv.rpc.model.RpcResponse;
import com.tiv.rpc.model.ServiceMetaInfo;
import com.tiv.rpc.registry.Registry;
import com.tiv.rpc.registry.RegistryFactory;
import com.tiv.rpc.serializer.Serializer;
import com.tiv.rpc.serializer.SerializerFactory;
import com.tiv.rpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

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
            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, serviceMetaInfo);
            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("客户端调用服务器失败");
        }
    }
}
