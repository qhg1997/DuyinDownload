package com.qhg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 项目名：mideal
 * 包  名：PACKAGE_NAME
 * 创建者：乔回国
 * 创建时间：2022/6/1 14:16
 * 描述：
 */
public class DYDownLoad {
    final static Pattern url_compile = Pattern.compile("http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*(),]|(\\?:%[0-9a-fA-F][0-9a-fA-F]))+");
    final static String mode = "post";//like
    final static String save = "./";//like
    final static HTTP http = HTTP.builder()
            .config(builder -> builder.followRedirects(false))
            .addPreprocessor(preChain -> {
                preChain.getTask()
                        .addHeader("user-agent", "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36 Edg/87.0.664.66");
                preChain.proceed();
            })
            .build();
    static String nickname = null;
    static String max_cursor = "0";
    static File saveDir = null;

    public static void main(String[] args) throws InterruptedException {
        //用户主页链接
        final String url = findUrl("https://v.douyin.com/FKn26BL/");
        final String secId = findSecId(url);
        System.out.println("[  提示  ]:用户的sec_id=" + secId);
        //请求列表
        String dataUrl = "https://www.iesdouyin.com/web/api/v2/aweme/" + mode + "/?sec_uid=" + secId + "&count=35&max_cursor=[max_cursor]&aid=1128&_signature=[signature]";
        String signature = "RuMN1wAAJu7w0.6HdIeO2EbjDc&dytk=";
        String replace = dataUrl.replace("[max_cursor]", max_cursor).replace("[signature]", signature);
        final HttpResult.Body response = http.async(replace)
                .get().getResult().getBody().cache();
        final JSONObject result = JSONObject.parseObject(response.toString());
        nickname = result.getJSONArray("aweme_list").getJSONObject(0).getJSONObject("author").getString("nickname");
        saveDir = new File(save + "/" + mode + "/" + nickname);
        if (!saveDir.exists())
            System.out.println("目录[" + saveDir.getAbsolutePath() + "]创建" + saveDir.mkdirs());
        ArrayList<JSONObject> dataList = getDataList(dataUrl);
        ArrayList<Dyinfo> dyinfos = new ArrayList<>();
        for (JSONObject o : dataList) {
            Dyinfo dyinfo = new Dyinfo();
            JSONObject video = o.getJSONObject("video");
            JSONObject play_addr = video.getJSONObject("play_addr");
            dyinfo.author = o.getString("desc");
            dyinfo.video = play_addr.getJSONArray("url_list").getString(0);
            dyinfo.uri = play_addr.getString("uri");
            dyinfo.awemeId = o.getString("aweme_id");
            dyinfo.nickname = o.getJSONObject("author").getString("nickname");
            dyinfo.awemeType = o.getInteger("aweme_type");
            dyinfos.add(dyinfo);
        }
        toDownLoad(dyinfos);
        System.out.println("任务结束。。。");
    }


    private static void toDownLoad(ArrayList<Dyinfo> dyinfos) {
        for (Dyinfo dyinfo : dyinfos) {
            if (true) {
                if (dyinfo.awemeType == 2) {//image
                    System.out.println("图集资源");
                    List<String> links = new ArrayList<>();
                    HttpResult.Body body = http.sync("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId)
                            .get().getBody().cache();
                    JSONObject object = JSONObject.parseObject(body.toString());
                    JSONObject item_list = object.getJSONArray("item_list").getJSONObject(0);
                    JSONArray images = item_list.getJSONArray("images");
                    String author = String.join("", dyinfo.author.split("\\r?\\n")).replace(" ", "");
                    if (author.length() > 50)
                        author = author.substring(0, 45);
                    for (Object image : images) {
                        JSONObject imageobj = JSONObject.parseObject(image.toString());
                        String url = imageobj.getJSONArray("url_list").getString(3);
                        links.add(url);
                    }
                    for (int i = 0; i < links.size(); i++) {
                        System.out.println(links.get(i));
                        File file = new File(saveDir, author + "-" + (i + 1) + "-" + System.currentTimeMillis() + ".jpeg");
                        System.out.println(file.getAbsolutePath());
                        OkHttps.sync(links.get(i)).get().getBody().toFile(file).start();
                        System.out.println("图[" + file.getName() + "]下载完成!");
                    }
                    System.out.println("图集下载完毕");
                } else if (dyinfo.awemeType == 4) {//video
                    System.out.println("视频资源");
                    HttpResult.Body body = http.async("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId).get()
                            .getResult().getBody();
                    JSONObject object = JSONObject.parseObject(body.toString());
                    System.out.println(object);
                    long aLong = object.getJSONArray("item_list").getJSONObject(0).getLong("create_time") * 1000;
                    Date date = new Date(aLong);
                    String author = String.join("", dyinfo.author.split("\\r?\\n")).replace(" ", "");
                    if (author.length() > 50)
                        author = author.substring(0, 45);
                    File file = new File(saveDir, format("yyyy-MM-dd HH.mm.ss", date) + "-" + author + ".mp4");
                    if (!file.exists()) {

                        System.out.println(file.getName());
                        System.out.println(file.getAbsolutePath());
                        String s = "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + dyinfo.uri + "&radio=1080p&line=0";
                        System.out.println(s);
                        OkHttps.sync(s).get().getBody().toFile(file).start();
                        System.out.println("[" + file.getName() + "]下载完成....");
                    } else {
                        System.out.println("已经下载过....");
                    }
                } else {
                    System.out.println("位置类型：" + dyinfo.awemeType);
                }
            } else {
                HttpResult.Body body = http.sync("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId)
                        .get().getBody().cache();
                JSONObject object = JSONObject.parseObject(body.toString());
                JSONArray images = object.getJSONArray("item_list").getJSONObject(0).getJSONArray("images");
                if (images == null || images.isEmpty()) {
                    System.out.println("视频资源");
                    body = http.async("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId).get()
                            .getResult().getBody();
                    object = JSONObject.parseObject(body.toString());
                    long aLong = object.getJSONArray("item_list").getJSONObject(0).getLong("create_time") * 1000;
                    Date date = new Date(aLong);
                    String author = String.join("", dyinfo.author.split("\\r?\\n")).replace(" ", "");
                    if (author.length() > 50)
                        author = author.substring(0, 45);
                    File file = new File(saveDir, format("yyyy-MM-dd HH.mm.ss", date) + "-" + author + ".mp4");
                    if (!file.exists()) {
                        OkHttps.sync("https://aweme.snssdk.com/aweme/v1/play/?video_id=" + dyinfo.uri + "&radio=1080p&line=0")
                                .get().getBody().toFile(file).start();
                        System.out.println("[" + file.getName() + "]下载完成....");
                    } else {
                        System.out.println("已经下载过....");
                    }
                } else {
                    System.out.println("图集资源");
                    List<String> links = new ArrayList<>();
                    body = http.sync("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId)
                            .get().getBody().cache();
                    object = JSONObject.parseObject(body.toString());
                    JSONObject item_list = object.getJSONArray("item_list").getJSONObject(0);
                    String author = String.join("", dyinfo.author.split("\\r?\\n")).replace(" ", "");
                    if (author.length() > 50)
                        author = author.substring(0, 45);
                    images = item_list.getJSONArray("images");
                    for (Object image : images) {
                        JSONObject imageobj = JSONObject.parseObject(image.toString());
                        String url = imageobj.getJSONArray("url_list").getString(3);
                        links.add(url);
                    }
                    for (int i = 0; i < links.size(); i++) {
                        File file = new File(saveDir, author + "-" + (i + 1) + "-" + System.currentTimeMillis() + ".jpeg");
                        OkHttps.sync(links.get(i)).get().getBody().toFile(file).start();
                        System.out.println("图[" + file.getName() + "]下载完成!");
                    }
                    System.out.println("图集下载完毕");
                }
            }

        }

    }

    private static String format(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    private static ArrayList<JSONObject> getDataList(String url) throws InterruptedException {
        int index = 0;
        ArrayList<JSONObject> objects = new ArrayList<>();
        System.out.println("[  用户  ]: " + nickname);
        while (true) {
            index += 1;
            System.out.println("[  提示  ]:正在进行第 " + index + " 次尝试");
            TimeUnit.MILLISECONDS.sleep(300);
            String signature = "PDHVOQAAXMfFyj02QEpGaDwx1S&dytk=";
            String replace = url.replace("[max_cursor]", max_cursor).replace("[signature]", signature);
            HttpResult.Body cache = http.sync(replace)
                    .get()
                    .getBody().cache();
            JSONObject res = JSONObject.parseObject(cache.toString());
            DYDownLoad.max_cursor = res.getString("max_cursor");
            JSONArray aweme_list = res.getJSONArray("aweme_list");
            boolean has_more = res.getBoolean("has_more");
            objects.addAll(aweme_list.stream().map(i -> JSONObject.parseObject(i.toString())).collect(Collectors.toList()));
            if (!has_more) break;
            System.out.println("[  提示  ]:抓获数据成功!");
        }
        System.out.println("[  提示  ]:抓获数据完成! 共[" + objects.size() + "]条");
        return objects;
    }

    static String findUrl(String str) {
        final Matcher matcher = url_compile.matcher(str);
        if (matcher.find())
            return matcher.group();
        return "";
    }

    /**
     * 用户的sec_id
     *
     * @param url
     * @return sec_id
     */
    static String findSecId(String url) {
        final HttpResult httpResult = http.sync(url)
                .get();
        String location = httpResult.getHeader("location");
        final int start = location.lastIndexOf("/");
        final int end = location.lastIndexOf("?");
        return location.substring(start + 1, end);
    }

    public static class Dyinfo {
        public String author;
        public String video;
        public String awemeId;
        public String nickname;
        public String uri;
        public Integer awemeType;
    }

}
