package com.tiv.rpc.model;

import lombok.Data;

/**
 * 服务元信息
 */
@Data
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
    public String getServiceNode() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }
}
