package com.tiv.provider.impl;

import com.tiv.common.model.Order;
import com.tiv.common.service.OrderService;
import com.tiv.rpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 订单服务实现类
 */
@Service
@RpcService
public class OrderServiceImpl implements OrderService {
    @Override
    public Order getOrder(Order order) {
        System.out.println("订单名: " + order.getName());
        return order;
    }
}
