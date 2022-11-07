package com.qhg;

import com.qhg.utils.IO;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目名：DuyinDownload
 * 包  名：com.qhg
 * 创建者：乔回国
 * 创建时间：2022/6/7 17:10
 * 描述：
 */
public class Main {
    public static void main(String[] args) {
        List<String> list;
        if (args != null && args.length > 0) {
            list = Arrays.asList(args);
        } else {
            final InputStream sharelink = Main.class.getResourceAsStream("/sharelink");
            list = IO.readLines(sharelink).stream().distinct().collect(Collectors.toList());
        }
        DYDownLoad dyDownLoad = new DYDownLoad();
        list.parallelStream().forEach(dyDownLoad::download);
    }

}
