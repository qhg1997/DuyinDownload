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
            "https://v.douyin.com/YRAg2M6/",
            "https://v.douyin.com/YRDJjXM/",
            "https://v.douyin.com/YRAsfWH/",
            "https://v.douyin.com/YRApYgK/",
            "https://v.douyin.com/YRAqTwn/",
            "https://v.douyin.com/YRDJ6Lm/",
            "https://v.douyin.com/YRAqXTr/",
            "https://v.douyin.com/YRA4KeL/",
            "https://v.douyin.com/YRA3aR1/",
            "https://v.douyin.com/YRAg9Q9/",
            "https://v.douyin.com/YRAGAv4/",
            "https://v.douyin.com/YRDRMVx/",
            "https://v.douyin.com/YRAVCPo/",
            "https://v.douyin.com/YRA7a3p/",
            "https://v.douyin.com/YRAXsX7/",
            "https://v.douyin.com/YRAqLpS/",
            "https://v.douyin.com/YRDda3p/",
            "https://v.douyin.com/YRAgBN6/",
            "https://v.douyin.com/YRAgyo7/",
            "https://v.douyin.com/YRAb1cW/",
            "https://v.douyin.com/YRAGrsR/",
            "https://v.douyin.com/YRA7RQB/",
            "https://v.douyin.com/YRD5QSG/",
            "https://v.douyin.com/YRD2kUr/",
            "https://v.douyin.com/YRDaPRH/",
            "https://v.douyin.com/YRDyWNg/",
            "https://v.douyin.com/YRDYdmk/",
            "https://v.douyin.com/YRDHuBW/",
            "https://v.douyin.com/YRDfUwQ/",
            "https://v.douyin.com/YRDmjXq/",
            "https://v.douyin.com/YRDft5p/",
            "https://v.douyin.com/YRDyLyY/",
            "https://v.douyin.com/YRDrGEV/",
            "https://v.douyin.com/YRDj79n/",
            "https://v.douyin.com/YRDrYL6/",
            "https://v.douyin.com/YRDhqpm/",
            "https://v.douyin.com/YRDDuCT/",
            "https://v.douyin.com/YRDr9XA/",
            "https://v.douyin.com/YRDfQhg/",
            "https://v.douyin.com/YRS3e2f/",
            "https://v.douyin.com/YRAFtLo/",
            "https://v.douyin.com/YRSsjne/",
            "https://v.douyin.com/YRStqDe/",
            "https://v.douyin.com/YRSwX4C/",
            "https://v.douyin.com/YRSodfv/",
            "https://v.douyin.com/YRSvsC6/",
            "https://v.douyin.com/YRSgPSr/",
            "https://v.douyin.com/YRAFQsA/",
            "https://v.douyin.com/YRSnPGV/",
            "https://v.douyin.com/YRALsyt/",
            "https://v.douyin.com/YRA1PEW/",
            "https://v.douyin.com/YRSg7HN/",
            "https://v.douyin.com/YRA1AuY/",
            "https://v.douyin.com/YRSwQro/",
            "https://v.douyin.com/YRSfskA/",
            "https://v.douyin.com/YRSq661/",
            "https://v.douyin.com/YRSfQsv/",
            "https://v.douyin.com/YRSAEB7/",
            "https://v.douyin.com/YRSUyQL/"
    };

    static String[] arr2 = {
            "https://v.douyin.com/YRyab4d/",
            "https://v.douyin.com/YRfNuTv/",
            "https://v.douyin.com/YRf6Ycq/",
            "https://v.douyin.com/YRfReA2/",
            "https://v.douyin.com/YRfLcd1/"
    };

    public static void main(String[] args) throws InterruptedException {
        for (String s : arr2) {
            new Thread(() -> {
                try {
                    new DYDownLoad().download(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        System.out.println("线程启动全部完成...");
    }

}
