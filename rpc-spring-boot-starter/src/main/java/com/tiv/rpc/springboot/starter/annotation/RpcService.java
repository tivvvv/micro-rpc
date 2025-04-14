package com.tiv.rpc.springboot.starter.annotation;

import com.tiv.rpc.constant.RpcConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供者注解
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    /**
     * 服务接口类
     *
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本
     *
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
