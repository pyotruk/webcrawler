package ru.webcrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class Page {

    private String url;
    private int deep;
    private int httpCode;
    private String content;
    private List<Page> childrenPages = new ArrayList<Page>();

    public Page(String url, int deep) {
        this.url = url;
        this.deep = deep;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (Page page : childrenPages) {
            sb.append("\n\t").append(page);
        }
        return "Page[deep:" + deep + "][httpCode:" + httpCode + "][url:" + url + "][children:" + sb + "]";
    }

    //TODO thread-safe ??
    void load() {
        try {
            Connection.Response response = Jsoup.connect(url).execute(); //TODO add timeout
            httpCode = response.statusCode();
            Document doc = response.parse();
            content = doc.text();
            for (Element a : doc.select("a")) {
                childrenPages.add(new Page(a.attr("href"), deep + 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
