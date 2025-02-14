package com.tiv.consumer;

import com.tiv.common.model.Order;
import com.tiv.common.service.OrderService;

/**
 * 消费者启动类
 */
public class ConsumerApplication {
    public static void main(String[] args) {
        // 创建静态代理对象
        OrderService orderService = new OrderServiceProxy();
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
