package com.tiv.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 调用测试
 */
@SpringBootTest
public class OrderServiceCallTest {

    @Resource
    private OrderServiceCall orderServiceCall;

    @Test
    void testCall() {
        orderServiceCall.call();
    }
}
