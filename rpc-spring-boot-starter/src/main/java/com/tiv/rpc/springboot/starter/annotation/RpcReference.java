package com.tiv.rpc.springboot.starter.annotation;

import com.tiv.rpc.constant.RpcConstant;
import com.tiv.rpc.fault.retry.RetryStrategyKeys;
import com.tiv.rpc.fault.tolerant.TolerantStrategyKeys;
import com.tiv.rpc.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消费者注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

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

    /**
     * 负载均衡器
     *
     * @return
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     *
     * @return
     */
    String retryStrategy() default RetryStrategyKeys.NO_RETRY;

    /**
     * 容错策略
     *
     * @return
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 是否开启mock调用
     *
     * @return
     */
    boolean mockActive() default false;

}
