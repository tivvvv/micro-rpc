package com.tiv.rpc.loadbalancer;

/**
 * 负载均衡器key
 */
public interface LoadBalancerKeys {
    
    String ROUND_ROBIN = "roundRobin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistentHash";
}
