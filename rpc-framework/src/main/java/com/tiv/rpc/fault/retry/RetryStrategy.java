package com.tiv.rpc.fault.retry;

import com.tiv.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {

    /**
     * 重试
     *
     * @param callable
     * @param methodName
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable, String methodName) throws Exception;
}
