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
        String url = "https://api5-normal-c-lf.amemv.com/aweme/v1/user/block/list/?index=290&count=10&source=0&hotsoon_filtered_count=0&hotsoon_has_more=0&iid=119187649607335&device_id=2744795635389447&ac=wifi&channel=update&aid=1128&app_name=aweme&version_code=230000&version_name=23.0.0&device_platform=android&os=android&ssmix=a&device_type=HD1910&device_brand=OnePlus&language=zh&os_api=22&os_version=5.1.1&manifest_version_code=230001&resolution=900*1600&dpi=300&update_version_code=23009900&_rticket=1667801570444&package=com.ss.android.ugc.aweme&mcc_mnc=46000&cpu_support64=false&host_abi=armeabi-v7a&ts=1667801569&appTheme=light&app_type=normal&need_personal_recommend=1&is_guest_mode=0&minor_status=0&is_android_pad=0&cdid=f9fb8792-7ea2-479f-9f7e-796d3c8d1ee5&md=0.1";
        String[] split = url.split("\\?");
        System.out.println(split[0]);
        String[] strings = split[1].split("&");
        for (String string : strings) {
            String[] strings1 = string.split("=");
            String k = strings1[0];
            String v = strings1[1];
            System.out.println(".addUrlPara(\"" + k + "\",\"" + v + "\")");
        }

        HttpCall call = OkHttps.async("https://api5-normal-c-lf.amemv.com/aweme/v1/user/block/list/")
                .addUrlPara("index", "0")
                .addUrlPara("count", "40")
                .addUrlPara("source", "0")
                .addUrlPara("hotsoon_filtered_count", "0")
                .addUrlPara("hotsoon_has_more", "0")
                .addUrlPara("iid", "119187649607335")
                .addUrlPara("device_id", "2744795635389447")
                .addUrlPara("ac", "wifi")
                .addUrlPara("channel", "update")
                .addUrlPara("aid", "1128")
                .addUrlPara("app_name", "aweme")
                .addUrlPara("version_code", "230000")
                .addUrlPara("version_name", "23.0.0")
                .addUrlPara("device_platform", "android")
                .addUrlPara("os", "android")
                .addUrlPara("ssmix", "a")
                .addUrlPara("device_type", "HD1910")
                .addUrlPara("device_brand", "OnePlus")
                .addUrlPara("language", "zh")
                .addUrlPara("os_api", "22")
                .addUrlPara("os_version", "5.1.1")
                .addUrlPara("manifest_version_code", "230001")
                .addUrlPara("resolution", "900*1600")
                .addUrlPara("dpi", "300")
                .addUrlPara("update_version_code", "23009900")
                .addUrlPara("_rticket", "1667801570444")
                .addUrlPara("package", "com.ss.android.ugc.aweme")
                .addUrlPara("mcc_mnc", "46000")
                .addUrlPara("cpu_support64", "false")
                .addUrlPara("host_abi", "armeabi-v7a")
                .addUrlPara("ts", "1667801569")
                .addUrlPara("appTheme", "light")
                .addUrlPara("app_type", "normal")
                .addUrlPara("need_personal_recommend", "1")
                .addUrlPara("is_guest_mode", "0")
                .addUrlPara("minor_status", "0")
                .addUrlPara("is_android_pad", "0")
                .addUrlPara("cdid", "f9fb8792-7ea2-479f-9f7e-796d3c8d1ee5")
                .addUrlPara("md", "0.1")
                .addHeader("Cookie", "install_id=119187649607335; " +
                        "ttreq=1$65acc1d78f7222e8aad69613fd9e354691e09d1a; " +
                        "d_ticket=6507f52c4c51a054ed62a29dbb65e7e3d4033; " +
                        "multi_sids=2057875502938608%3Abdd419502028e0bc18f45f5419453be9; " +
                        "odin_tt=f8b429c9fc66dff2242a0bd25f8b5a29d50258bc624e38c5519d2dde16f9d1361dd86237aa6d61d9ae36ec44691260c2f6d55cd4d56bb2e82aaf581f61c711a524251752afb4bc4faf8b04ec320f6179; " +
                        "n_mh=i2a1GWUt2fNfatSZl3luM-aVn3l5TOs2nf2JV1Pe-1o; " +
                        "passport_assist_user=CkGKyMFtdPLUZmL2QRZ4rnsBY-HiVJqBYTf23ue_jOX8MKof0tDT2Eqqh1Dqw0BcgIq3wnA5RbfIGs9clhk9JAtwARpICjwfNV60kWfSGvoA-25xn_gx9yPMQwbrNUxGEuqK8x-rd0toERsgsMpmkQ0LEqjGnVcdzATeOfxLYJxUL7QQ5cegDRiJr9ZUIgEDoFcgCA%3D%3D; " +
                        "sid_guard=bdd419502028e0bc18f45f5419453be9%7C1667799248%7C5184000%7CFri%2C+06-Jan-2023+05%3A34%3A08+GMT; " +
                        "uid_tt=96a2414febdcbe00c48f5df7c1245286; " +
                        "sid_tt=bdd419502028e0bc18f45f5419453be9; " +
                        "sessionid=bdd419502028e0bc18f45f5419453be9; " +
                        "passport_csrf_token_default=b32f2ad19f942c4d14059b927879f54a")
                .get();
        HttpResult.Body body = call.getResult().getBody();
        System.out.println(body);

    }
}
