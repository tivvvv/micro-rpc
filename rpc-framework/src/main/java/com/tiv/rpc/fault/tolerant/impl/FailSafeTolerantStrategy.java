package com.tiv.rpc.fault.tolerant.impl;

import com.tiv.rpc.fault.tolerant.TolerantStrategy;
import com.tiv.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("服务异常, 静默处理", e);
        return new RpcResponse();
    }
}
