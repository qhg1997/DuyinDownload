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

    public static final String COOKIES = "sid_guard=8637d0ca8a962302edc59c185b55a6ea%7C1675907276%7C5184000%7CMon%2C+10-Apr-2023+01%3A47%3A56+GMT; ";

    public static void blockList() {
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
                    .addHeader("Cookie", COOKIES)
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

    public static void main(String[] args) {
//        putFollow();
        postFollow();
    }

    public static void parseUrl() {
        String url = "/aweme/v1/commit/follow/user/?detail_type=0&from_pre=0&sec_user_id=MS4wLjABAAAA4aBvHTQbaSm4qFrpLr4oiCRCTPbkbOpDNGPrQ9v2i4M&city=610100&channel_id=3&address_book_access=1&from_action=19001&from=19&type=0&iid=119187649607335&device_id=2744795635389447&ac=wifi&channel=update&aid=1128&app_name=aweme&version_code=230000&version_name=23.0.0&device_platform=android&os=android&ssmix=a&device_type=HD1910&device_brand=OnePlus&language=zh&os_api=22&os_version=5.1.1&manifest_version_code=230001&resolution=900*1600&dpi=300&update_version_code=23009900&_rticket=1675911845406&package=com.ss.android.ugc.aweme&mcc_mnc=46000&cpu_support64=false&host_abi=armeabi&ts=1675911846&appTheme=light&app_type=normal&need_personal_recommend=1&is_guest_mode=0&minor_status=0&is_android_pad=0&cdid=f9fb8792-7ea2-479f-9f7e-796d3c8d1ee5&md=0";
        String[] split = url.split("\\?");
        System.out.println(split[0]);
        String[] strings = split[1].split("&");
        for (String string : strings) {
            String[] strings1 = string.split("=");
            String k = strings1[0];
            String v = strings1[1];
            System.out.println(".addUrlPara(\"" + k + "\",\"" + v + "\")");
        }
    }

    /**
     * 移除黑名单
     */
    public static void postBlock() {
        HttpCall call = OkHttps.async("https://api5-normal-c-lf.amemv.com/aweme/v1/user/block/")
                .addUrlPara("aid", "1128")
                .addBodyPara("user_id", "74953502089")
                .addBodyPara("sec_user_id", "MS4wLjABAAAA4aBvHTQbaSm4qFrpLr4oiCRCTPbkbOpDNGPrQ9v2i4M")
                .addBodyPara("block_type", "0")
                .addBodyPara("source", "0")
                .addHeader("Cookie", COOKIES)
                .post();
        HttpResult.Body body = call.getResult().getBody().cache();
        System.out.println(body);
    }

    /**
     * 拉黑
     */
    public static void putBlock() {
        HttpCall call = OkHttps.async("https://api5-normal-c-lf.amemv.com/aweme/v1/user/block/")
                .addUrlPara("aid", "1128")
                .addBodyPara("user_id", "74953502089")
                .addBodyPara("sec_user_id", "MS4wLjABAAAA4aBvHTQbaSm4qFrpLr4oiCRCTPbkbOpDNGPrQ9v2i4M")
                .addBodyPara("block_type", "1")
                .addBodyPara("source", "0")
                .addHeader("Cookie", COOKIES)
                .post();
        HttpResult.Body body = call.getResult().getBody().cache();
        System.out.println(body);
    }

    /**
     * 关注
     */
    public static void postFollow() {
        HttpCall call = OkHttps.async("https://api5-normal-c-lf.amemv.com/aweme/v1/commit/follow/user/")
                .addUrlPara("sec_user_id", "MS4wLjABAAAA4aBvHTQbaSm4qFrpLr4oiCRCTPbkbOpDNGPrQ9v2i4M")

                .addUrlPara("type", "1")
                .addUrlPara("need_mark_friend", "0")

                .addUrlPara("aid", "1128")
                .addUrlPara("from_pre", "0")
                .addUrlPara("detail_type", "0")
                .addUrlPara("city", "610100")
                .addUrlPara("channel_id", "3")
                .addUrlPara("address_book_access", "0")
                .addUrlPara("from_action", "19001")
                .addUrlPara("from", "19")
                .addHeader("Cookie", COOKIES)
                .get();
        HttpResult.Body body = call.getResult().getBody().cache();
        System.out.println(body);
    }

    /**
     * 取消关注
     */
    public static void putFollow() {
        HttpCall call = OkHttps.async("https://api5-normal-c-lf.amemv.com/aweme/v1/commit/follow/user/")
                .addUrlPara("sec_user_id", "MS4wLjABAAAA4aBvHTQbaSm4qFrpLr4oiCRCTPbkbOpDNGPrQ9v2i4M")

                .addUrlPara("type", "0")

                .addUrlPara("aid", "1128")
                .addUrlPara("from_pre", "0")
                .addUrlPara("detail_type", "0")
                .addUrlPara("city", "610100")
                .addUrlPara("channel_id", "3")
                .addUrlPara("address_book_access", "0")
                .addUrlPara("from_action", "19001")
                .addUrlPara("from", "19")
                .addHeader("Cookie", COOKIES)
                .get();
        HttpResult.Body body = call.getResult().getBody().cache();
        System.out.println(body);
    }

}
