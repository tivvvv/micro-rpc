package com.tiv.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.tiv.rpc.model.RpcRequest;
import com.tiv.rpc.model.RpcResponse;
import com.tiv.rpc.serializer.Serializer;
import com.tiv.rpc.serializer.impl.JDKSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理(基于JDK动态代理)
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = new JDKSerializer();
        // 构造rpc请求
        RpcRequest rpcRequest = RpcRequest
                .builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求
            byte[] resultBytes;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bodyBytes).execute()) {
                resultBytes = httpResponse.bodyBytes();
            }
            // 解析rpc响应
            RpcResponse rpcResponse = serializer.deserialize(resultBytes, RpcResponse.class);
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
