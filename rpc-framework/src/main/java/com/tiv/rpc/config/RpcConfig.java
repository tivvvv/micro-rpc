package com.tiv.rpc.config;

import com.tiv.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * rpc框架配置类
 */
@Data
public class RpcConfig {

    /**
     * rpc框架名称
     */
    private String name = "mirco-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器地址
     */
    private String host = "localhost";

    /**
     * 服务器端口号
     */
    private Integer port = 8080;

    /**
     * 是否开启mock调用
     */
    private boolean mockActive = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JSON;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}
