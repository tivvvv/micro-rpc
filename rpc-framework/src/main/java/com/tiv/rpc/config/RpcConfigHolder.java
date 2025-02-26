package com.tiv.rpc.config;

import com.tiv.rpc.constant.RpcConstant;
import com.tiv.rpc.registry.Registry;
import com.tiv.rpc.registry.RegistryFactory;
import com.tiv.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * rpc框架配置管理器
 */
@Slf4j
public class RpcConfigHolder {

    private static volatile RpcConfig rpcConfig;

    /**
     * rpc框架配置初始化(基于配置文件)
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * rpc框架配置初始化(自定义配置)
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        // 初始化rpc配置
        rpcConfig = newRpcConfig;
        log.info("micro-rpc init, config = {}", newRpcConfig.toString());
        // 初始化注册中心
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
        // JVM退出时,服务从注册中心下线
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 获取rpc框架配置
     *
     * @return
     */
    public static RpcConfig getRpcConfig() {
        // 双检索单例模式
        if (rpcConfig == null) {
            synchronized (RpcConfigHolder.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
