package com.tiv.consumer;

import com.tiv.common.model.Order;
import com.tiv.common.service.OrderService;
import com.tiv.rpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

/**
 * 订单服务调用类
 */
@Service
public class OrderServiceCall {

    @RpcReference
    private OrderService orderService;

    public void call() {
        Order order = new Order();
        order.setId(100L);
        order.setName("order-100");
        order.setPrice(100);
        Order result = orderService.getOrder(order);
        System.out.println(result.toString());
    }
}
