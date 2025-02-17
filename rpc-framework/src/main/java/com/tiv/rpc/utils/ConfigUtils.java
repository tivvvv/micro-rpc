package com.tiv.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 */
public class ConfigUtils {

    private static final String APPLICATION_PREFIX = "application";
    private static final String PROPERTIES_SUFFIX = ".properties";

    /**
     * 加载配置,默认环境
     *
     * @param clazz
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置,区分环境
     *
     * @param clazz
     * @param prefix
     * @param env
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String env) {
        StringBuilder configFileBuilder = new StringBuilder(APPLICATION_PREFIX);
        if (StrUtil.isNotBlank(env)) {
            configFileBuilder.append("-").append(env);
        }
        configFileBuilder.append(PROPERTIES_SUFFIX);
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(clazz, prefix);
    }

}
