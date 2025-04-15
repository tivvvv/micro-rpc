package com.tiv.rpc.bootstrap;

import com.tiv.rpc.config.RpcConfigHolder;

/**
 * 消费者启动类
 */
public class ConsumerBootstrap {

    /**
     * 初始化
     */
    public static void init() {
        RpcConfigHolder.init();
    }
}
