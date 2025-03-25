package com.tiv.rpc.fault.retry.impl;

import com.tiv.rpc.fault.retry.RetryStrategy;
import com.tiv.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试
 */
public class NoRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable, String methodName) throws Exception {
        return callable.call();
    }
}
