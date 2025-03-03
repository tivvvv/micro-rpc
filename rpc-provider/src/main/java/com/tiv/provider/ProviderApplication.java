package com.tiv.provider;

import com.tiv.common.service.OrderService;
import com.tiv.provider.impl.OrderServiceImpl;
import com.tiv.rpc.config.RegistryConfig;
import com.tiv.rpc.config.RpcConfig;
import com.tiv.rpc.config.RpcConfigHolder;
import com.tiv.rpc.constant.RpcConstant;
import com.tiv.rpc.model.ServiceMetaInfo;
import com.tiv.rpc.registry.LocalRegistry;
import com.tiv.rpc.registry.Registry;
import com.tiv.rpc.registry.RegistryFactory;
import com.tiv.rpc.server.tcp.VertxTcpServer;

/**
 * 服务提供者启动类
 */
public class ProviderApplication {
    public static void main(String[] args) {
        // 初始化配置
        RpcConfigHolder.init();

        // 注册服务
        String serviceName = OrderService.class.getName();
        LocalRegistry.register(serviceName, OrderServiceImpl.class);

        // 注册服务
        RpcConfig rpcConfig = RpcConfigHolder.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo
                .builder()
                .serviceName(serviceName)
                .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION)
                .serviceHost(rpcConfig.getHost())
                .servicePort(rpcConfig.getPort())
                .build();
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动web服务器
        new VertxTcpServer().doStart(RpcConfigHolder.getRpcConfig().getPort());
    }
}
