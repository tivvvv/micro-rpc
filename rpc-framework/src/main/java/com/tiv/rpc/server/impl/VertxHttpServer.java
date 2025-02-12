package com.tiv.rpc.server.impl;

import com.tiv.rpc.server.HttpServer;
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
        httpServer.requestHandler(request -> {
            System.out.println("Received request: " + request.method() + " uri: " + request.uri());
            // 响应
            request.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello From Vert.x HTTP server!");
        });
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
