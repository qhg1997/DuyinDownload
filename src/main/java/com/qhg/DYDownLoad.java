package com.qhg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
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
            .config(builder -> {
                builder.followRedirects(false);
                builder.addInterceptor(new Interceptor() {
                    public final int maxRetry = 5;//最大重试次数
                    private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        while (!response.isSuccessful() && retryNum < maxRetry) {
                            response.close();
                            retryNum++;
                            System.out.println("正在重试:[" + retryNum + "]");
                            response = chain.proceed(request);
                        }
                        return response;
                    }
                });
            })
            .addPreprocessor(preChain -> {
                preChain.getTask()
                        .addHeader("user-agent", "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36 Edg/87.0.664.66");
                preChain.proceed();
            })
            .build();
    static String nickname = null;
    static String max_cursor = "0";
    static File saveDir = null;
    static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        //用户主页链接
        final String url = findUrl("https://v.douyin.com/FENhTVS/");
        final String secId = findSecId(url);
        System.out.println("[  提示  ]:用户的sec_id=" + secId);
        //请求列表
        String dataUrl = "https://www.iesdouyin.com/web/api/v2/aweme/" + mode + "/?sec_uid=" + secId + "&count=35&max_cursor=[max_cursor]&aid=1128&_signature=[signature]";
        String signature = "RuMN1wAAJu7w0.6HdIeO2EbjDc&dytk=";
        String replace = dataUrl.replace("[max_cursor]", max_cursor).replace("[signature]", signature);
        final JSONObject result = getResult(replace);
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
        System.out.println("任务结束。。。爬取条目[" + dyinfos.size() + "],文件下载总数[" + count + "],实际数量" + (saveDir.list().length) + "]");
    }


    private static void toDownLoad(ArrayList<Dyinfo> dyinfos) {
        for (Dyinfo dyinfo : dyinfos) {
            if (true) {
                if (dyinfo.awemeType == 2) {//image
                    System.out.println("图集资源");
                    List<String> links = new ArrayList<>();
                    JSONObject object = getResult("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId);
                    JSONObject item_list = object.getJSONArray("item_list").getJSONObject(0);
                    JSONArray images = item_list.getJSONArray("images");
                    String author = safeFileName(dyinfo.author);
                    for (Object image : images) {
                        JSONObject imageobj = JSONObject.parseObject(image.toString());
                        String url = imageobj.getJSONArray("url_list").getString(3);
                        links.add(url);
                    }
                    for (int i = 0; i < links.size(); i++) {
                        File file = new File(saveDir, author + "-" + (i + 1) + "-" + System.currentTimeMillis() + ".jpeg");
                        System.out.println("文件[" + file.getName() + "]开始下载....");
                        OkHttps.sync(links.get(i)).get().getBody().toFile(file).start();
                        System.out.println("图[" + file.getName() + "]下载完成!");
                        count++;
                    }
                    System.out.println("图集下载完毕");
                } else if (dyinfo.awemeType == 4) {//video
                    System.out.println("视频资源");
                    JSONObject object = getResult("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId);
                    long aLong;
                    try {
                        aLong = object.getJSONArray("item_list").getJSONObject(0).getLong("create_time") * 1000;
                    } catch (Exception e) {
                        aLong = System.currentTimeMillis();
                    }
                    Date date = new Date(aLong);
                    String author = safeFileName(dyinfo.author);
                    File file = new File(saveDir, format(date) + "-" + author + ".mp4");
                    if (!file.exists()) {
                        System.out.println("文件[" + file.getName() + "]开始下载....");
                        String s = "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + dyinfo.uri + "&radio=1080p&line=0";
                        OkHttps.sync(s).get().getBody().toFile(file).start();
                        System.out.println("[" + file.getName() + "]下载完成....");
                        count++;
                    } else {
                        System.out.println("已经下载过....");
                    }
                } else {
                    System.out.println("位置类型：" + dyinfo.awemeType);
                }
            } else {
                JSONObject object = getResult("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId);
                JSONArray images = object.getJSONArray("item_list").getJSONObject(0).getJSONArray("images");
                if (images == null || images.isEmpty()) {
                    System.out.println("视频资源");
                    object = getResult("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId);
                    long aLong = object.getJSONArray("item_list").getJSONObject(0).getLong("create_time") * 1000;
                    Date date = new Date(aLong);
                    String author = safeFileName(dyinfo.author);
                    File file = new File(saveDir, format(date) + "-" + author + ".mp4");
                    if (!file.exists()) {
                        System.out.println("文件[" + file.getName() + "]开始下载....");
                        OkHttps.sync("https://aweme.snssdk.com/aweme/v1/play/?video_id=" + dyinfo.uri + "&radio=1080p&line=0")
                                .get().getBody().toFile(file).start();
                        System.out.println("[" + file.getName() + "]下载完成....");
                        count++;
                    } else {
                        System.out.println("已经下载过....");
                    }
                } else {
                    System.out.println("图集资源");
                    List<String> links = new ArrayList<>();
                    object = getResult("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + dyinfo.awemeId);
                    JSONObject item_list = object.getJSONArray("item_list").getJSONObject(0);
                    String author = safeFileName(dyinfo.author);
                    images = item_list.getJSONArray("images");
                    for (Object image : images) {
                        JSONObject imageobj = JSONObject.parseObject(image.toString());
                        String url = imageobj.getJSONArray("url_list").getString(3);
                        links.add(url);
                    }
                    for (int i = 0; i < links.size(); i++) {
                        File file = new File(saveDir, author + "-" + (i + 1) + "-" + System.currentTimeMillis() + ".jpeg");
                        System.out.println("文件[" + file.getName() + "]开始下载....");
                        OkHttps.sync(links.get(i)).get().getBody().toFile(file).start();
                        System.out.println("图[" + file.getName() + "]下载完成!");
                        count++;
                    }
                    System.out.println("图集下载完毕");
                }
            }

        }

    }

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

    private static String format(Date date) {
        return dateFormat.format(date);
    }

    private static ArrayList<JSONObject> getDataList(String url) throws InterruptedException {
        int index = 0;
        ArrayList<JSONObject> objects = new ArrayList<>();
        System.out.println("[  用户  ]: " + nickname);
        while (true) {
            System.out.println("[  提示  ]:正在进行第 " + index + " 次");
            TimeUnit.MILLISECONDS.sleep(300);
            String signature = "PDHVOQAAXMfFyj02QEpGaDwx1S&dytk=";
            String replace = url.replace("[max_cursor]", max_cursor).replace("[signature]", signature);
            JSONObject res = getResult(replace);
            index += 1;
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

    static JSONObject getResult(String url) {
        while (true) {
            HttpResult.Body body = http.sync(url).get().getBody().cache();
            JSONObject object = JSONObject.parseObject(body.toString());
            body.close();
            if (object != null)
                return object;
        }
    }

    final static Pattern pattern = Pattern.compile("[\\s\\\\/:*?\"<>|]");

    static String safeFileName(String str) {
        str = String.join("", str.split("\\r?\\n"));
        Matcher matcher = pattern.matcher(str);
        str = matcher.replaceAll("");
        if (str.length() > 50)
            str = str.substring(0, 45);
        return str;
    }

}
