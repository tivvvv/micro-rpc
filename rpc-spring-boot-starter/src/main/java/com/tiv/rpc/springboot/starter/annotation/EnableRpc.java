package com.tiv.rpc.springboot.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用rpc注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableRpc {

    /**
     * 是否启用服务端
     *
     * @return
     */
    boolean needServer() default true;
}
