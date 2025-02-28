package com.tiv.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.tiv.common.model.Order;
import com.tiv.common.service.OrderService;
import com.tiv.rpc.config.RpcConfigHolder;
import com.tiv.rpc.model.RpcRequest;
import com.tiv.rpc.model.RpcResponse;
import com.tiv.rpc.serializer.Serializer;
import com.tiv.rpc.serializer.SerializerFactory;

/**
 * 订单服务静态代理
 */
@Deprecated
public class OrderServiceProxy implements OrderService {
    @Override
    public Order getOrder(Order order) {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getSerializer(RpcConfigHolder.getRpcConfig().getSerializer());

        // 构造rpc请求
        RpcRequest rpcRequest = RpcRequest
                .builder()
                .serviceName(OrderService.class.getName())
                .methodName("getOrder")
                .parameterTypes(new Class<?>[]{Order.class})
                .args(new Object[]{order})
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求
            byte[] resultBytes;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bodyBytes).execute()) {
                resultBytes = httpResponse.bodyBytes();
            }
            // 解析rpc响应
            RpcResponse rpcResponse = serializer.deserialize(resultBytes, RpcResponse.class);
            return (Order) rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
