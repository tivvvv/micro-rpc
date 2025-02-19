package com.tiv.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * mock服务代理
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(method.getReturnType());
    }

    /**
     * 获取指定类型的默认对象
     *
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        // 基本类型返回指定值
        if (type.isPrimitive()) {
            if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            } else if (type == double.class) {
                return 0.0;
            } else if (type == boolean.class) {
                return false;
            } else if (type == char.class) {
                return 'a';
            }
        }
        // 引用类型返回null
        return null;
    }
}
