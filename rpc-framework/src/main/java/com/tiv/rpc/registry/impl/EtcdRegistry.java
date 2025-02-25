package com.tiv.rpc.registry.impl;

import cn.hutool.json.JSONUtil;
import com.tiv.rpc.config.RegistryConfig;
import com.tiv.rpc.model.ServiceMetaInfo;
import com.tiv.rpc.registry.Registry;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Etcd注册中心
 */
public class EtcdRegistry implements Registry {

    /**
     * Etcd客户端
     */
    private Client client;

    /**
     * 存储客户端
     */
    private KV kvClient;

    /**
     * 租约客户端
     */
    private Lease leaseClient;

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/micro-rpc/";

    /**
     * 租约过期时间30s
     */
    private static final long ttl = 30L;

    @Override
    public void init(RegistryConfig registryConfig) {
        // 客户端初始化
        client = Client
                .builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        leaseClient = client.getLeaseClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 设置过期时间
        long leaseId = leaseClient.grant(ttl).get().getID();

        // 设置键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        // 存储键值对
        kvClient.put(key, value, putOption).get();
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        // 删除键值对
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscover(String serviceKey) {
        // 前缀搜索,末尾需要加上/
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> kvs = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();
            return kvs.stream().map(kv -> {
                String value = kv.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("服务发现 获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (leaseClient != null) {
            leaseClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
