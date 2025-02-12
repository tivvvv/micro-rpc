package com.tiv.common.service;

import com.tiv.common.model.Order;

/**
 * 订单服务
 */
public interface OrderService {

    /**
     * 获取订单
     *
     * @param order
     * @return
     */
    Order getOrder(Order order);
}
