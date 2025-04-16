package com.tiv.provider;

import com.tiv.common.service.OrderService;
import com.tiv.provider.impl.OrderServiceImpl;
import com.tiv.rpc.bootstrap.ProviderBootstrap;
import com.tiv.rpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者示例
 */
@Deprecated
public class ProviderApplication {
    public static void main(String[] args) {
        // 提供的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<OrderService> serviceRegisterInfo = new ServiceRegisterInfo<>(OrderService.class.getName(), OrderServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 启动服务
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
