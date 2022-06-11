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

    };


    public static void main(String[] args) {
        for (String s : arr) {
            new Thread(() -> new DYDownLoad().download(s)).start();
        }
        System.out.println("线程启动全部完成...");
    }

}
