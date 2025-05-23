package com.tiv.consumer;

import com.tiv.common.model.Order;
import com.tiv.common.service.OrderService;
import com.tiv.rpc.config.RpcConfigHolder;
import com.tiv.rpc.proxy.ServiceProxyFactory;

/**
 * 消费者示例
 */
@Deprecated
public class ConsumerApplication {
    public static void main(String[] args) {
        RpcConfigHolder.init();

        // 创建动态代理对象
        OrderService orderService = ServiceProxyFactory.getProxy(OrderService.class);
        Order order = new Order();
        order.setName("测试订单");

        Order newOrder = orderService.getOrder(order);
        if (newOrder != null) {
            System.out.println(newOrder.getName());
        } else {
            System.out.println("获取订单失败");
        }
    }
}
