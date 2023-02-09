package com.qhg.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HttpCall;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import com.qhg.utils.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目名：renting
 * 包  名：controllers.common
 * 创建者：乔回国
 * 创建时间：2022/11/7 13:49
 * 描述：
 */
public class App {

    /*解析抓包导出文件 拿到黑名单列表  格式化json*/
    public static void analyze() {
        String content = IO.readContentAsString(new File("C:\\Users\\40477\\Desktop\\1_Full.txt"));
        String[] split = content.split("------------------------------------------------------------------");
        List<String> collect = Arrays.stream(split).filter(i -> i.contains("aweme/v1/user/block/list/")).collect(Collectors.toList());
        System.out.println(collect.size());
        JSONArray objects = new JSONArray();
        for (String str : collect) {
            String[] strings = str.split("status_code");
            System.out.println("{\"status_code" + strings[1]);
            JSONObject object = JSONObject.parseObject("{\"status_code" + strings[1].replace("�?,\"", ",\",\""));
            objects.add(object);
            System.out.println(object);
        }
        IO.writeContent(JSON.toJSONString(objects, true), new File("C:\\Users\\40477\\Desktop\\blockList.json"));
    }

    /**
     * 提取分享链接
     */
    public static void extract() {
        String link = "https://www.iesdouyin.com/share/user/";
        String content = IO.readContentAsString(new File("C:\\Users\\40477\\Desktop\\blockList.json"));
        JSONArray jsonArray = JSONArray.parseArray(content);
        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            JSONArray block_list = object.getJSONArray("block_list");
            for (int i1 = 0; i1 < block_list.size(); i1++) {
                JSONObject blockListJSONObject = block_list.getJSONObject(i1);
                String nickname = blockListJSONObject.getString("nickname");
                String sec_uid = blockListJSONObject.getString("sec_uid");
                System.out.println(nickname + "        " + link + sec_uid);
                strings.add(nickname + "        " + link + sec_uid);
            }
        }
        IO.writeContent(String.join("\n", strings), new File("C:\\Users\\40477\\Desktop\\blockList.list"));

    }

    public static void main(String[] args) {
        int index = 0;
        int count = 10;
        int sum = 0;
        HttpCall call;
        ArrayList<Object> objects = new ArrayList<>();
        while (true) {
            call = OkHttps.async("https://api5-normal-c-lf.amemv.com/aweme/v1/user/block/list/")
                    .addUrlPara("index", index)
                    .addUrlPara("count", count)
                    .addUrlPara("aid", "1128")
                    .addHeader("Cookie", "sid_guard=8637d0ca8a962302edc59c185b55a6ea%7C1675907276%7C5184000%7CMon%2C+10-Apr-2023+01%3A47%3A56+GMT; ")
                    .get();
            HttpResult.Body body = call.getResult().getBody().cache();
            objects.add(body.toString());
            JSONObject object = JSONObject.parseObject(body.toString());
            System.out.println(index + "," + count + " : " + object.getInteger("status_code"));
            sum += object.getJSONArray("block_list").size();
            if (!object.getBoolean("has_more")) {
                break;
            }
            index += count;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String jsons = JSON.toJSONString(objects, true);
        IO.writeContent(jsons, new File("C:\\Users\\40477\\Desktop\\黑名单.json"));
        System.out.println("共 " + sum + " 条");
    }

}
