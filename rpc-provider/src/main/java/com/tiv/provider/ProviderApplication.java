package com.tiv.provider;

import com.tiv.rpc.server.impl.VertxHttpServer;

/**
 * 服务提供者启动类
 */
public class ProviderApplication {
    public static void main(String[] args) {
        new VertxHttpServer().doStart(8080);
    }
}
