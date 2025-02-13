package com.tiv.rpc.server.http.impl;

import com.tiv.rpc.server.http.HttpServer;
import com.tiv.rpc.server.http.handler.HttpRequestHandler;
import io.vertx.core.Vertx;

/**
 * Vert.x实现HTTP服务器
 */
public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();
        // 创建HTTP服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();
        // 处理请求
        httpServer.requestHandler(new HttpRequestHandler());
        // 启动服务器, 监听指定端口
        httpServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.err.println("Failed to start server: " + result.cause());
            }
        });
    }
}
