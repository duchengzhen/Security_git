package com.calvin.security.utils;

import android.text.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by calvin on 2014/6/19.
 */
public class HtmlParse {

    public static List<String> getJokes() {
        List<String> jokes=new ArrayList<String>();
        String url = "http://news.163.com/special/qingsongyike/";
        int count = 0;
        do {
            try {
                Document doc = Jsoup.connect(url).timeout(5000).get();
                Elements select = doc.select(".f_center");
                for (Element text : select) {
                    String con = text.nextElementSibling().text();
                    if(!TextUtils.isEmpty(con))
                        jokes.add(con);
                }
                return jokes;
            } catch (IOException e) {
                count++;
                if (count < 5) {
                    System.err.println("正在重试:" + count);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                e.printStackTrace();
                // 如果有严重问题,就是无法联网,抛出自定义异常
                // throw new Exception("严重问题");
                System.out.println("错误");
            }
        } while (count < 5);
        return jokes;
    }
}
