package com.tiv.rpc.springboot.starter.annotation;

import com.tiv.rpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.tiv.rpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.tiv.rpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用rpc注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 是否启用服务端
     *
     * @return
     */
    boolean needServer() default true;
}
