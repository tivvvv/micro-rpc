package com.tiv.provider;

import com.tiv.common.service.OrderService;
import com.tiv.provider.impl.OrderServiceImpl;
import com.tiv.rpc.config.RpcConfigHolder;
import com.tiv.rpc.registry.LocalRegistry;
import com.tiv.rpc.server.http.impl.VertxHttpServer;

/**
 * 服务提供者启动类
 */
public class ProviderApplication {
    public static void main(String[] args) {
        // 初始化配置
        RpcConfigHolder.init();
        // 注册服务
        LocalRegistry.register(OrderService.class.getName(), OrderServiceImpl.class);
        // 启动web服务器
        new VertxHttpServer().doStart(RpcConfigHolder.getRpcConfig().getPort());
    }
}
