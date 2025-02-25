package com.tiv.rpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务元信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMetaInfo {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion;

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口号
     */
    private Integer servicePort;

    /**
     * HTTP前缀常量
     */
    private final static String HTTP_PREFIX = "http";

    /**
     * 获取服务注册key
     *
     * @return
     */
    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务注册节点
     *
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    /**
     * 获取完整的服务地址
     *
     * @return
     */
    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, HTTP_PREFIX)) {
            return String.format("%s://%s:%s", HTTP_PREFIX, serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
