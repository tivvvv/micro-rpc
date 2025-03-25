package com.tiv.rpc.fault.retry;

/**
 * 重试策略key
 */
public interface RetryStrategyKeys {

    /**
     * 不重试
     */
    String NO_RETRY = "noRetry";

    /**
     * 固定间隔重试
     */
    String FIXED_INTERVAL_RETRY = "fixedIntervalRetry";
}
