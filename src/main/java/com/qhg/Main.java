package com.qhg;

import java.util.Arrays;

/**
 * 项目名：DuyinDownload
 * 包  名：com.qhg
 * 创建者：乔回国
 * 创建时间：2022/6/7 17:10
 * 描述：
 */
public class Main {
    static String[] arr = {
            "https://v.douyin.com/YFM9oMr/",
            "https://v.douyin.com/YFM3scX/",
            "https://v.douyin.com/YFMTmKW/",
            "https://v.douyin.com/YFMw2MP/",
            "https://v.douyin.com/YFMw3KP/",
            "https://v.douyin.com/YFMEYk9/",
            "https://v.douyin.com/YFrRFKs/",
            "https://v.douyin.com/YFME7Cr/",
            "https://v.douyin.com/Y2RnJMJ/",
            "https://v.douyin.com/Y2RTFGW/",
            "https://v.douyin.com/Y2RGX3c/",
            "https://v.douyin.com/Y2RbPPq/",
            "https://v.douyin.com/Y2RCCY1/",
            "https://v.douyin.com/YYTh8rd/"
    };


    public static void main(String[] args) {
        Arrays.stream(arr).parallel().forEach(item -> new DYDownLoad().download(item));
    }

}
