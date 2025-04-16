package com.tiv.rpc.springboot.starter.bootstrap;

import com.tiv.rpc.config.RpcConfig;
import com.tiv.rpc.config.RpcConfigHolder;
import com.tiv.rpc.server.tcp.VertxTcpServer;
import com.tiv.rpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 注解驱动rpc框架自启动器
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring初始化时自动执行,初始化rpc框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        // 获取@EnableRpc的needServer属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");
        // 初始化配置
        RpcConfigHolder.init();
        RpcConfig rpcConfig = RpcConfigHolder.getRpcConfig();

        if (needServer) {
            log.info("micro-rpc server启动.");
            new VertxTcpServer().doStart(rpcConfig.getPort());
        } else {
            log.info("不启动server");
        }
    }
}
