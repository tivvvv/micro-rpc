package com.tiv.rpc.registry;

import com.tiv.rpc.config.RegistryConfig;
import com.tiv.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心接口
 */
public interface Registry {

    /**
     * 初始化
     *
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务
     *
     * @param serviceMetaInfo
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务
     *
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     *
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscover(String serviceKey);

    /**
     * 节点下线
     */
    void destroy();

    /**
     * 心跳检测
     */
    void heartBeat();
}
