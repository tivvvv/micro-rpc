package com.tiv.rpc.fault.tolerant.impl;

import com.tiv.rpc.fault.tolerant.TolerantStrategy;
import com.tiv.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败
 */
public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务异常", e);
    }
}
