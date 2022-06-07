package com.qhg.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * 项目名：DuyinDownload
 * 包  名：com.qhg.utils
 * 创建者：乔回国
 * 创建时间：2022/6/7 15:35
 * 描述：
 */
public class Configs {
    static final Properties properties = new Properties();

    static {
        try {
            properties.load(Configs.class.getResourceAsStream("/config.properties"));
            properties.setProperty("", "");
        } catch (IOException ignored) {
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String _default) {
        return properties.getProperty(key, _default);
    }

    public static void main(String[] args) {
        System.out.println(get("2"));
    }
}
