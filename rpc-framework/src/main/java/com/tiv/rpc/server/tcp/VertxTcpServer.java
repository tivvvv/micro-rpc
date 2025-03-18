package com.tiv.rpc.server.tcp;

import com.tiv.rpc.server.tcp.handler.TcpRequestHandler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于Vert.x实现TCP服务器
 */
@Slf4j
public class VertxTcpServer {

    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(new TcpRequestHandler());

        // 启动TCP服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port " + port);
            } else {
                log.info("Failed to start TCP server: " + result.cause());
            }
        });
    }
}
