package com.tiv.rpc.loadbalancer.impl;

import cn.hutool.core.collection.CollUtil;
import com.tiv.rpc.loadbalancer.LoadBalancer;
import com.tiv.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器.
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 虚拟节点环
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            return null;
        }

        // 构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                String virtualNodeKey = serviceMetaInfo.getServiceAddress() + "&&" + i;
                int hash = hash(virtualNodeKey);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // 本次请求的哈希值
        int hash = hash(requestParams);
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);

        // 没有大于等于该哈希值的虚拟节点时,返回首结点
        if (entry == null) {
            return virtualNodes.firstEntry().getValue();
        }

        return entry.getValue();
    }

    /**
     * 自定义哈希算法
     *
     * @param key
     * @return
     */
    private int hash(Object key) {
        return key.hashCode();
    }
}
