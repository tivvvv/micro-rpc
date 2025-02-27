package com.tiv.rpc.registry;

import com.tiv.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册服务本地缓存
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    private List<ServiceMetaInfo> serviceCache;

    /**
     * 写入缓存
     *
     * @param serviceCache
     */
    public void writeCache(List<ServiceMetaInfo> serviceCache) {
        this.serviceCache = serviceCache;
    }

    /**
     * 读取缓存
     *
     * @return
     */
    public List<ServiceMetaInfo> readCache() {
        return serviceCache;
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        serviceCache = null;
    }
}
