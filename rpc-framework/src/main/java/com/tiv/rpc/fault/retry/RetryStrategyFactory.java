package com.tiv.rpc.fault.retry;

import com.tiv.rpc.fault.retry.impl.NoRetryStrategy;
import com.tiv.rpc.spi.SpiLoader;

/**
 * 重试策略工厂
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试策略
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
     * 获取重试策略
     *
     * @param key
     * @return
     */
    public static RetryStrategy getRetryStrategy(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
