package com.tiv.rpc.fault.tolerant;

import com.tiv.rpc.fault.tolerant.impl.FailFastTolerantStrategy;
import com.tiv.rpc.spi.SpiLoader;

/**
 * 容错策略工厂
 */
public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    /**
     * 获取容错策略
     *
     * @param key
     * @return
     */
    public static TolerantStrategy getTolerantStrategy(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
