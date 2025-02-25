package com.tiv.rpc.registry;

import com.tiv.rpc.registry.impl.EtcdRegistry;
import com.tiv.rpc.spi.SpiLoader;

/**
 * 注册中心工厂
 */
public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取注册中心
     *
     * @param key
     * @return
     */
    public static Registry getRegistry(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
