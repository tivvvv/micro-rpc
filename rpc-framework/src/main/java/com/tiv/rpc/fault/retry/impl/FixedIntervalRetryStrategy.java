package com.tiv.rpc.fault.retry.impl;

import com.github.rholder.retry.*;
import com.tiv.rpc.fault.retry.RetryStrategy;
import com.tiv.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定间隔重试
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable, String methodName) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfException()
                .withWaitStrategy(WaitStrategies.fixedWait(5L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("第{}次尝试执行方法:{}", attempt.getAttemptNumber(), methodName);
                    }
                }).build();
        return retryer.call(callable);
    }
}
