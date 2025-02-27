package com.tiv.rpc.registry.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.tiv.rpc.config.RegistryConfig;
import com.tiv.rpc.model.ServiceMetaInfo;
import com.tiv.rpc.registry.Registry;
import com.tiv.rpc.registry.RegistryServiceCache;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Etcd注册中心
 */
@Slf4j
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
     * 本地注册节点key集合
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 已监听的key集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

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
        heartBeat();
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
        log.info("服务注册成功: {},key: {},value: {}", serviceMetaInfo, key, value);

        // 本地缓存节点信息
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        // 删除键值对
        kvClient.delete(ByteSequence.from(registryKey, StandardCharsets.UTF_8));
        localRegisterNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscover(String serviceKey) {
        // 优先从缓存中获取服务列表
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (CollUtil.isNotEmpty(cachedServiceMetaInfoList)) {
            return cachedServiceMetaInfoList;
        }

        // 前缀搜索,末尾需要加上/
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> kvs = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();
            List<ServiceMetaInfo> serviceMetaInfoList = kvs.stream().map(kv -> {
                String key = kv.getKey().toString(StandardCharsets.UTF_8);
                // 监听key
                watch(key);
                String value = kv.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
            // 写入缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("服务发现 获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(String.format("%s节点下线失败", key), e);
            }
        }
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

    @Override
    public void heartBeat() {
        // 每15秒执行一次续约
        CronUtil.schedule("15 * * * * ? *", new Task() {
            @Override
            public void execute() {
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> kvs = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
                        if (CollUtil.isEmpty(kvs)) {
                            // 节点已过期
                            continue;
                        }
                        // 节点未过期,重新注册续约
                        KeyValue kv = kvs.get(0);
                        String value = kv.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("key: %s续约失败", key), e);
                    }
                }
            }
        });
        // 打开秒级设置
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean add = watchingKeySet.add(serviceNodeKey);
        if (add) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE:
                            // 监听到key被删除时清空缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }
}
