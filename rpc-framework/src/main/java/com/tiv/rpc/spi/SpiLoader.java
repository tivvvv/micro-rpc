package com.tiv.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.tiv.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spi加载器
 */
@Slf4j
public class SpiLoader {

    /**
     * 系统spi目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 自定义spi目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * spi扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 类加载集合 接口名: (key: 实现类)
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 实例缓存集合
     */
    private static Map<String, Object> instanceMap = new ConcurrentHashMap<>();

    /**
     * 加载所有spi
     */
    public static void loadAll() {
        log.info("加载所有spi");
        for (Class<?> clazz : LOAD_CLASS_LIST) {
            load(clazz);
        }
    }

    /**
     * 加载指定类型spi
     *
     * @param loadClass
     * @return
     */
    public static Map<String, Class<?>> load(Class loadClass) {
        log.info("加载类型为{}的spi", loadClass.getName());
        Map<String, Class<?>> key2ClassMap = new HashMap<>();
        // 先扫描RPC_SYSTEM_SPI_DIR,后扫描RPC_CUSTOM_SPI_DIR,即自定义spi优先级更高
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            key2ClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    log.error("spi load error: ", e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), key2ClassMap);
        return key2ClassMap;
    }

    /**
     * 获取接口实例
     *
     * @param clazz
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<?> clazz, String key) {
        String className = clazz.getName();
        Map<String, Class<?>> key2ClassMap = loaderMap.get(className);
        if (key2ClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader未加载%s类型", className));
        }
        if (!key2ClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader %s类型下不存在key=%s的实现类", className, key));
        }
        // 实现类
        Class<?> implClass = key2ClassMap.get(key);
        String implClassName = implClass.getName();
        // 缓存实例
        if (!instanceMap.containsKey(implClassName)) {
            try {
                instanceMap.put(implClassName, implClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException(String.format("%s类实例化失败 ", implClassName), e);
            }
        }
        return (T) instanceMap.get(implClassName);
    }

}
