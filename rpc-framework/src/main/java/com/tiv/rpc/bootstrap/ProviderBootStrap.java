package com.tiv.rpc.bootstrap;

import com.tiv.rpc.config.RegistryConfig;
import com.tiv.rpc.config.RpcConfig;
import com.tiv.rpc.config.RpcConfigHolder;
import com.tiv.rpc.constant.RpcConstant;
import com.tiv.rpc.model.ServiceMetaInfo;
import com.tiv.rpc.model.ServiceRegisterInfo;
import com.tiv.rpc.registry.LocalRegistry;
import com.tiv.rpc.registry.Registry;
import com.tiv.rpc.registry.RegistryFactory;
import com.tiv.rpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者启动类
 */
public class ProviderBootstrap {

    /**
     * 初始化
     *
     * @param serviceRegisterInfoList
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // 初始化配置
        RpcConfigHolder.init();

        final RpcConfig rpcConfig = RpcConfigHolder.getRpcConfig();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
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
                throw new RuntimeException(serviceName + "服务注册失败", e);
            }
        }

        // 启动web服务器
        new VertxTcpServer().doStart(RpcConfigHolder.getRpcConfig().getPort());
    }

}
