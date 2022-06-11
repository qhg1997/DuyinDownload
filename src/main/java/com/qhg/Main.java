package com.qhg;

/**
 * 项目名：DuyinDownload
 * 包  名：com.qhg
 * 创建者：乔回国
 * 创建时间：2022/6/7 17:10
 * 描述：
 */
public class Main {
    static String[] arr = {
            "https://v.douyin.com/YFMXTTg/",
            "https://v.douyin.com/YFMyuTJ/",
            "https://v.douyin.com/YFMrXgp/",
            "https://v.douyin.com/YFMAohY/",
            "https://v.douyin.com/YFM4T67/",
            "https://v.douyin.com/YFMxF6V/",
            "https://v.douyin.com/YFMr9Sm/",
            "https://v.douyin.com/YFMuhbV/",
            "https://v.douyin.com/YFM9oMr/",
            "https://v.douyin.com/YFM3scX/",
            "https://v.douyin.com/YFMTmKW/",
            "https://v.douyin.com/YFMw2MP/",
            "https://v.douyin.com/YFMw3KP/",
            "https://v.douyin.com/YFMEYk9/",
            "https://v.douyin.com/YFrRFKs/",
            "https://v.douyin.com/YFME7Cr/"
    };

    public static void main(String[] args)  {
        for (String s : arr) {
            new Thread(() -> new DYDownLoad().download(s)).start();
        }
        System.out.println("线程启动全部完成...");
    }

}
